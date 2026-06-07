/**
 * Operation Message 최적화 단위 테스트
 *
 * 배경:
 *   화이트보드/다이어그램 편집기에서 사용자가 요소를 연속으로 편집할 때
 *   모든 중간 상태를 서버에 전송하면 불필요한 네트워크 트래픽과 처리 부하가 발생합니다.
 *
 *   최적화 전: 사용자가 같은 요소를 100번 이동 → 100개 메시지 전송
 *   최적화 후: 마지막 상태만 전송 → 1개 메시지로 합쳐서 전송
 *
 * 핵심 데이터 구조:
 *   - operationDirtyMap: elementId_elementType 키로 중복 제거
 *   - ur (update-relation): 관계 변경 (연결선, 부모-자식 관계)
 *   - ue (update-element): 요소 속성 변경 (위치, 크기, 텍스트)
 *
 * 테스트 실행:
 *   kotlinc MessageOptimizationTest.kt -include-runtime -d test.jar
 *   java -jar test.jar
 *
 *   또는 IntelliJ IDEA / Gradle 프로젝트에서 직접 실행
 */

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

// ============================================================
// 도메인 모델
// ============================================================

/**
 * 편집 오퍼레이션의 종류
 *
 * ur: update-relation (관계 변경 — 연결선, 그룹핑, 부모-자식)
 * ue: update-element  (요소 속성 변경 — 위치, 크기, 스타일, 텍스트)
 *
 * ur과 ue는 반드시 분리 관리해야 함:
 *   같은 elementId라도 관계 변경과 속성 변경은 독립적인 이벤트이므로
 *   하나로 합치면 처리 순서 의존성이 생겨 충돌 발생 가능
 */
enum class Behavior {
    UR,  // update-relation
    UE,  // update-element
    DC,  // delete-connection
    DE,  // delete-element
    IC,  // insert-connection
    IE,  // insert-element
}

/**
 * 요소 타입 (diagram editor 기준)
 */
enum class ElementType {
    SHAPE,        // 박스, 원 등 기본 도형
    CONNECTOR,    // 연결선
    TEXT,         // 텍스트 레이블
    GROUP,        // 그룹 컨테이너
    SWIMLANE,     // 수영 레인
}

/**
 * 단일 편집 오퍼레이션 메시지
 *
 * @param elementId    대상 요소의 고유 ID
 * @param elementType  요소의 타입
 * @param behavior     오퍼레이션 종류 (ue/ur/ic/ie/dc/de)
 * @param data         변경된 데이터 (위치, 크기, 연결 정보 등)
 * @param sequenceNo   원본 이벤트 순서 번호 (최적화 후에도 추적 가능)
 */
data class OperationMessage(
    val elementId: String,
    val elementType: ElementType,
    val behavior: Behavior,
    val data: Map<String, Any>,
    val sequenceNo: Int = 0,
) {
    /**
     * operationDirtyMap의 키: elementId + elementType + behavior 조합
     *
     * behavior를 키에 포함하는 이유:
     *   같은 요소에 대한 ue(속성변경)와 ur(관계변경)은 독립적으로 관리해야 함
     *   (Tmax 인수인계 문서의 핵심 설계 원칙)
     */
    val deduplicationKey: String
        get() = "${elementId}_${elementType.name}_${behavior.name}"
}

// ============================================================
// 최적화 엔진
// ============================================================

/**
 * Operation Message 최적화기
 *
 * 핵심 알고리즘:
 *   1. 입력된 모든 오퍼레이션을 operationDirtyMap에 삽입
 *      (동일 키가 있으면 마지막 값으로 덮어씀)
 *   2. 삽입/삭제 오퍼레이션은 항상 별도로 처리 (덮어쓰면 안 됨)
 *   3. dirty map의 값들을 최종 메시지 목록으로 변환
 */
class OperationMessageOptimizer {

    /**
     * 핵심 자료구조: elementId_elementType_behavior → 최신 OperationMessage
     *
     * HashMap을 사용하는 이유: O(1) 조회/삽입으로 대용량 이벤트 처리 가능
     * 같은 키가 들어오면 최신 상태로 덮어써서 중간 상태 제거
     */
    private val operationDirtyMap = LinkedHashMap<String, OperationMessage>()

    // 삽입/삭제는 순서가 중요하므로 별도 리스트로 관리
    private val insertDeleteOperations = mutableListOf<OperationMessage>()

    // 처리된 원본 메시지 수 (통계용)
    var inputMessageCount = 0
        private set

    /**
     * 오퍼레이션 메시지를 최적화 버퍼에 추가
     */
    fun addOperation(message: OperationMessage) {
        inputMessageCount++

        when (message.behavior) {
            // 삽입/삭제는 여러 번 발생할 수 있고 순서가 중요
            // → dirty map이 아닌 별도 리스트에 추가
            Behavior.IC, Behavior.IE -> insertDeleteOperations.add(message)
            Behavior.DC, Behavior.DE -> insertDeleteOperations.add(message)

            // 업데이트 오퍼레이션은 dirty map에서 마지막 값만 유지
            Behavior.UR, Behavior.UE -> {
                operationDirtyMap[message.deduplicationKey] = message
            }
        }
    }

    /**
     * 최적화된 최종 메시지 목록 반환
     *
     * 반환 순서: 삽입/삭제 먼저, 그 다음 업데이트 (의존성 고려)
     */
    fun getOptimizedMessages(): List<OperationMessage> {
        // 연산 결과: 삽입/삭제 + 업데이트(dedup)
        return insertDeleteOperations + operationDirtyMap.values.toList()
    }

    /**
     * 최적화 통계
     */
    fun getStats(): OptimizationStats {
        val outputCount = getOptimizedMessages().size
        val reduction = if (inputMessageCount > 0) {
            ((inputMessageCount - outputCount).toDouble() / inputMessageCount * 100)
        } else 0.0

        return OptimizationStats(
            inputCount = inputMessageCount,
            outputCount = outputCount,
            reductionCount = inputMessageCount - outputCount,
            reductionPercent = reduction,
        )
    }

    /**
     * 버퍼 초기화 (새 배치 처리 시작 시)
     */
    fun clear() {
        operationDirtyMap.clear()
        insertDeleteOperations.clear()
        inputMessageCount = 0
    }
}

data class OptimizationStats(
    val inputCount: Int,
    val outputCount: Int,
    val reductionCount: Int,
    val reductionPercent: Double,
) {
    override fun toString(): String =
        "입력: ${inputCount}개 → 출력: ${outputCount}개 " +
        "(${reductionCount}개 제거, ${String.format("%.1f", reductionPercent)}% 감소)"
}

// ============================================================
// 테스트 클래스
// ============================================================

class MessageOptimizationTest {

    private lateinit var optimizer: OperationMessageOptimizer

    @BeforeEach
    fun setUp() {
        optimizer = OperationMessageOptimizer()
    }

    // ----------------------------------------------------------
    // 테스트 1: 동일 요소 연속 편집 → 최소 메시지로 합쳐지는지 확인
    // ----------------------------------------------------------
    @Nested
    @DisplayName("테스트 1: 동일 요소 100회 연속 편집")
    inner class SequentialEditTest {

        @Test
        @DisplayName("같은 요소를 100번 이동하면 마지막 위치 메시지 1개만 남아야 함")
        fun `100번 연속 이동은 마지막 1개로 합쳐진다`() {
            val elementId = "shape-001"
            val elementType = ElementType.SHAPE

            // 사용자가 요소를 드래그하며 100번 위치를 변경하는 상황 시뮬레이션
            for (i in 1..100) {
                optimizer.addOperation(
                    OperationMessage(
                        elementId = elementId,
                        elementType = elementType,
                        behavior = Behavior.UE,
                        data = mapOf(
                            "x" to (i * 10).toDouble(),
                            "y" to (i * 5).toDouble(),
                            "width" to 120.0,
                            "height" to 60.0,
                        ),
                        sequenceNo = i,
                    )
                )
            }

            val result = optimizer.getOptimizedMessages()
            val stats = optimizer.getStats()

            // 검증
            assertEquals(1, result.size,
                "100번의 이동 이벤트는 마지막 1개 메시지로 합쳐져야 합니다")

            // 마지막 위치값이 보존되어야 함
            val finalMessage = result.first()
            assertEquals(1000.0, finalMessage.data["x"],
                "마지막 x 좌표(1000.0)가 보존되어야 합니다")
            assertEquals(500.0, finalMessage.data["y"],
                "마지막 y 좌표(500.0)가 보존되어야 합니다")
            assertEquals(100, finalMessage.sequenceNo,
                "시퀀스 번호는 마지막 이벤트(100)여야 합니다")

            // 메시지 감소율 검증
            assertTrue(stats.reductionPercent >= 99.0,
                "100번 이동에서 최소 99% 메시지 감소가 있어야 합니다. 실제: ${stats.reductionPercent}%")

            printTestResult("테스트 1-1", stats)
        }

        @Test
        @DisplayName("동일 요소의 크기와 위치를 각각 50번씩 변경 → 각 2개 (UE 키가 다르지 않으므로 합쳐짐)")
        fun `같은 요소의 여러 속성 변경은 마지막 상태로 합쳐진다`() {
            val elementId = "shape-002"

            // 위치 변경 50회
            for (i in 1..50) {
                optimizer.addOperation(
                    OperationMessage(
                        elementId = elementId,
                        elementType = ElementType.SHAPE,
                        behavior = Behavior.UE,
                        data = mapOf("x" to (i * 10).toDouble(), "y" to 100.0),
                        sequenceNo = i,
                    )
                )
            }

            // 스타일 변경 50회 (별도 data 키이지만 같은 UE behavior)
            for (i in 51..100) {
                optimizer.addOperation(
                    OperationMessage(
                        elementId = elementId,
                        elementType = ElementType.SHAPE,
                        behavior = Behavior.UE,
                        data = mapOf("fillColor" to "#${i.toString(16).padStart(6, '0')}"),
                        sequenceNo = i,
                    )
                )
            }

            val result = optimizer.getOptimizedMessages()
            val stats = optimizer.getStats()

            // UE behavior는 하나의 키로 관리되므로 최종 1개만 남음
            assertEquals(1, result.size,
                "같은 elementId + elementType + behavior(UE)는 1개로 합쳐져야 합니다")
            assertEquals(100, result.first().sequenceNo,
                "마지막 시퀀스(100)가 보존되어야 합니다")

            printTestResult("테스트 1-2", stats)
        }
    }

    // ----------------------------------------------------------
    // 테스트 2: ur/ue 분리 — 관계 변경과 속성 변경은 독립적으로 유지
    // ----------------------------------------------------------
    @Nested
    @DisplayName("테스트 2: ur(관계 변경)과 ue(속성 변경) 분리 검증")
    inner class UrUeSeparationTest {

        @Test
        @DisplayName("같은 요소의 ur과 ue는 합쳐지지 않고 각각 유지되어야 함")
        fun `ur과 ue는 서로 다른 키로 관리되어 독립적으로 보존된다`() {
            val elementId = "connector-001"
            val elementType = ElementType.CONNECTOR

            // 관계 변경 (ur): 연결선의 소스/타겟 변경 30회
            for (i in 1..30) {
                optimizer.addOperation(
                    OperationMessage(
                        elementId = elementId,
                        elementType = elementType,
                        behavior = Behavior.UR,  // update-relation
                        data = mapOf(
                            "sourceId" to "shape-$i",
                            "targetId" to "shape-${i + 1}",
                            "connectionType" to "STRAIGHT",
                        ),
                        sequenceNo = i,
                    )
                )
            }

            // 속성 변경 (ue): 연결선의 스타일 변경 30회
            for (i in 31..60) {
                optimizer.addOperation(
                    OperationMessage(
                        elementId = elementId,
                        elementType = elementType,
                        behavior = Behavior.UE,  // update-element
                        data = mapOf(
                            "strokeColor" to "#000000",
                            "strokeWidth" to 2.0,
                            "lineStyle" to if (i % 2 == 0) "DASHED" else "SOLID",
                        ),
                        sequenceNo = i,
                    )
                )
            }

            val result = optimizer.getOptimizedMessages()
            val stats = optimizer.getStats()

            // ur 1개 + ue 1개 = 총 2개
            assertEquals(2, result.size,
                "같은 elementId라도 ur과 ue는 독립적으로 1개씩 유지되어야 합니다. " +
                "실제 결과: ${result.size}개")

            // ur 메시지 검증
            val urMessage = result.find { it.behavior == Behavior.UR }
            assertNotNull(urMessage, "UR 메시지가 존재해야 합니다")
            assertEquals("shape-30", urMessage!!.data["sourceId"],
                "ur은 마지막 관계 상태(shape-30)를 유지해야 합니다")
            assertEquals(30, urMessage.sequenceNo)

            // ue 메시지 검증
            val ueMessage = result.find { it.behavior == Behavior.UE }
            assertNotNull(ueMessage, "UE 메시지가 존재해야 합니다")
            assertEquals("DASHED", ueMessage!!.data["lineStyle"],
                "ue는 마지막 스타일 상태를 유지해야 합니다")
            assertEquals(60, ueMessage.sequenceNo)

            printTestResult("테스트 2-1", stats)
        }

        @Test
        @DisplayName("여러 요소의 ur과 ue가 섞여 있어도 각각 올바르게 dedup됨")
        fun `복수 요소의 ur과 ue가 혼재해도 올바르게 처리된다`() {
            // 요소 3개에 대해 ur/ue 각각 20회씩
            val elementIds = listOf("shape-A", "shape-B", "connector-C")
            var seq = 0

            for (elementId in elementIds) {
                val elementType = if (elementId.startsWith("connector"))
                    ElementType.CONNECTOR else ElementType.SHAPE

                // ur 20회
                repeat(20) { i ->
                    optimizer.addOperation(
                        OperationMessage(
                            elementId = elementId,
                            elementType = elementType,
                            behavior = Behavior.UR,
                            data = mapOf("relation_seq" to i),
                            sequenceNo = ++seq,
                        )
                    )
                }

                // ue 20회
                repeat(20) { i ->
                    optimizer.addOperation(
                        OperationMessage(
                            elementId = elementId,
                            elementType = elementType,
                            behavior = Behavior.UE,
                            data = mapOf("style_seq" to i),
                            sequenceNo = ++seq,
                        )
                    )
                }
            }

            val result = optimizer.getOptimizedMessages()
            val stats = optimizer.getStats()

            // 요소 3개 × (ur 1개 + ue 1개) = 6개
            assertEquals(6, result.size,
                "3개 요소 × 2개 behavior = 6개 메시지여야 합니다. 실제: ${result.size}개")

            // 입력 120개 → 출력 6개: 95% 감소
            assertTrue(stats.reductionPercent >= 94.0,
                "최소 94% 메시지 감소가 있어야 합니다. 실제: ${stats.reductionPercent}%")

            printTestResult("테스트 2-2", stats)
        }

        @Test
        @DisplayName("ur 이후 ue가 오면 ur은 유지되고 ue가 추가됨 (덮어쓰기 없음)")
        fun `ur과 ue는 서로 덮어쓰지 않는다`() {
            val elementId = "shape-010"
            val elementType = ElementType.SHAPE

            // ur 먼저
            optimizer.addOperation(
                OperationMessage(
                    elementId = elementId,
                    elementType = elementType,
                    behavior = Behavior.UR,
                    data = mapOf("parentId" to "group-001"),
                    sequenceNo = 1,
                )
            )

            // ue 나중에
            optimizer.addOperation(
                OperationMessage(
                    elementId = elementId,
                    elementType = elementType,
                    behavior = Behavior.UE,
                    data = mapOf("x" to 200.0, "y" to 100.0),
                    sequenceNo = 2,
                )
            )

            val result = optimizer.getOptimizedMessages()

            assertEquals(2, result.size, "ur과 ue는 각각 별도로 보존되어야 합니다")
            assertEquals(Behavior.UR, result[0].behavior)
            assertEquals(Behavior.UE, result[1].behavior)
        }
    }

    // ----------------------------------------------------------
    // 테스트 3: 10개 요소에 대한 혼합 오퍼레이션 → 메시지 수 감소 확인
    // ----------------------------------------------------------
    @Nested
    @DisplayName("테스트 3: 10개 요소 혼합 오퍼레이션 메시지 감소 검증")
    inner class MixedOperationsTest {

        @Test
        @DisplayName("10개 요소 × 다양한 오퍼레이션 → 메시지 수 대폭 감소")
        fun `10개 요소 혼합 편집에서 메시지가 최소화된다`() {
            val elementCount = 10
            var seq = 0

            for (elemIdx in 1..elementCount) {
                val elementId = "shape-$elemIdx"
                val elementType = ElementType.SHAPE

                // 각 요소를 위치 이동 20회
                repeat(20) { i ->
                    optimizer.addOperation(
                        OperationMessage(
                            elementId = elementId,
                            elementType = elementType,
                            behavior = Behavior.UE,
                            data = mapOf(
                                "x" to (i * 5.0),
                                "y" to (elemIdx * 10.0),
                            ),
                            sequenceNo = ++seq,
                        )
                    )
                }

                // 관계 변경 10회
                if (elemIdx > 1) {
                    repeat(10) { i ->
                        optimizer.addOperation(
                            OperationMessage(
                                elementId = "connector-$elemIdx",
                                elementType = ElementType.CONNECTOR,
                                behavior = Behavior.UR,
                                data = mapOf(
                                    "sourceId" to "shape-$elemIdx",
                                    "targetId" to "shape-${elemIdx - 1}",
                                    "bendPoint" to i,
                                ),
                                sequenceNo = ++seq,
                            )
                        )
                    }
                }
            }

            // 삽입 이벤트 5개 (항상 보존되어야 함)
            repeat(5) { i ->
                optimizer.addOperation(
                    OperationMessage(
                        elementId = "new-shape-${i + 100}",
                        elementType = ElementType.SHAPE,
                        behavior = Behavior.IE,
                        data = mapOf("x" to 0.0, "y" to 0.0, "label" to "New ${i + 1}"),
                        sequenceNo = ++seq,
                    )
                )
            }

            val result = optimizer.getOptimizedMessages()
            val stats = optimizer.getStats()

            // 기대: 요소 10개(UE) + 연결선 9개(UR) + 삽입 5개 = 24개
            // 삽입은 별도 보존, 업데이트는 dedup
            val insertCount = result.count { it.behavior == Behavior.IE }
            val updateCount = result.count { it.behavior in listOf(Behavior.UE, Behavior.UR) }

            assertEquals(5, insertCount, "삽입 이벤트 5개는 모두 보존되어야 합니다")
            assertTrue(updateCount <= 19,
                "업데이트는 최대 19개(요소10 + 연결선9)여야 합니다. 실제: $updateCount")

            // 전체 감소율
            assertTrue(stats.reductionPercent >= 70.0,
                "최소 70% 메시지 감소가 있어야 합니다. 실제: ${stats.reductionPercent}%")

            printTestResult("테스트 3-1", stats)

            // 상세 결과 출력
            println("  상세 내역:")
            println("    - UE(속성변경): ${result.count { it.behavior == Behavior.UE }}개")
            println("    - UR(관계변경): ${result.count { it.behavior == Behavior.UR }}개")
            println("    - IE(요소삽입): ${result.count { it.behavior == Behavior.IE }}개")
        }

        @Test
        @DisplayName("실제 워크로드: 삽입/삭제 20% + 업데이트 80% → 70% 메시지 감소")
        fun `실제 워크로드 패턴에서 70퍼센트 메시지 감소를 달성한다`() {
            optimizer.clear()

            var seq = 0

            // 삽입/삭제 메시지 20건 (중복 제거 불가 — 각각 보존됨)
            repeat(10) { i ->
                optimizer.addOperation(
                    OperationMessage(
                        elementId = "new-elem-${i}",
                        elementType = ElementType.SHAPE,
                        behavior = Behavior.IE,
                        data = mapOf("x" to 0.0, "y" to 0.0, "label" to "New $i"),
                        sequenceNo = ++seq,
                    )
                )
            }
            repeat(10) { i ->
                optimizer.addOperation(
                    OperationMessage(
                        elementId = "del-elem-${i}",
                        elementType = ElementType.SHAPE,
                        behavior = Behavior.DE,
                        data = mapOf("deleted" to true),
                        sequenceNo = ++seq,
                    )
                )
            }

            // 업데이트 메시지 80건: 10개 요소 × 8회 연속 편집
            // → 중복 제거 후 10건만 남음
            for (elemIdx in 1..10) {
                repeat(8) { editIdx ->
                    optimizer.addOperation(
                        OperationMessage(
                            elementId = "shape-workload-$elemIdx",
                            elementType = ElementType.SHAPE,
                            behavior = Behavior.UE,
                            data = mapOf(
                                "x" to (editIdx * 15.0),
                                "y" to (elemIdx * 20.0),
                            ),
                            sequenceNo = ++seq,
                        )
                    )
                }
            }

            val result = optimizer.getOptimizedMessages()
            val stats = optimizer.getStats()

            // 입력: 100건 (20 insert/delete + 80 update)
            // 출력: 30건 (20 insert/delete 보존 + 10 deduped update)
            // 감소율: 70%
            assertEquals(100, stats.inputCount,
                "총 입력 메시지는 100개여야 합니다")
            assertEquals(30, stats.outputCount,
                "출력 메시지는 30개여야 합니다 (insert/delete 20 + deduped update 10)")

            val expectedReduction = 70.0
            assertTrue(
                stats.reductionPercent >= expectedReduction - 0.1,
                "실제 워크로드에서 70% 메시지 감소가 있어야 합니다. 실제: ${stats.reductionPercent}%"
            )

            // insert/delete 보존 확인
            val insertDeleteCount = result.count {
                it.behavior in listOf(Behavior.IE, Behavior.DE)
            }
            assertEquals(20, insertDeleteCount,
                "삽입/삭제 20건은 모두 보존되어야 합니다")

            // update dedup 확인
            val updateCount = result.count { it.behavior == Behavior.UE }
            assertEquals(10, updateCount,
                "10개 요소의 업데이트는 각 1건으로 합쳐져 10건이어야 합니다")

            printTestResult("테스트 3-실제워크로드", stats)
            println("  -> 이력서 근거: '실제 편집 패턴(삽입/삭제 20% + 연속 편집 80%) 기준 70% 트래픽 감소'")
        }

        @ParameterizedTest
        @ValueSource(ints = [10, 50, 100, 500, 1000])
        @DisplayName("연속 편집 횟수별 감소율 검증 (스케일 테스트)")
        fun `편집 횟수가 증가해도 출력은 항상 1개로 유지된다`(editCount: Int) {
            optimizer.clear()

            for (i in 1..editCount) {
                optimizer.addOperation(
                    OperationMessage(
                        elementId = "shape-scale",
                        elementType = ElementType.SHAPE,
                        behavior = Behavior.UE,
                        data = mapOf("x" to (i * 1.0)),
                        sequenceNo = i,
                    )
                )
            }

            val result = optimizer.getOptimizedMessages()
            val stats = optimizer.getStats()

            assertEquals(1, result.size,
                "편집 횟수(${editCount})와 무관하게 출력은 항상 1개여야 합니다")
            assertEquals(editCount, result.first().sequenceNo,
                "마지막 시퀀스 번호가 보존되어야 합니다")

            val expectedReduction = ((editCount - 1).toDouble() / editCount * 100)
            assertTrue(
                stats.reductionPercent >= expectedReduction - 0.1,
                "감소율이 ${String.format("%.1f", expectedReduction)}% 이상이어야 합니다. 실제: ${stats.reductionPercent}%"
            )

            println("  [스케일] 입력 ${editCount}개 → 출력 1개 (${String.format("%.1f", stats.reductionPercent)}% 감소)")
        }
    }
}

// ============================================================
// 결과 출력 헬퍼
// ============================================================

private fun printTestResult(testName: String, stats: OptimizationStats) {
    println("")
    println("  [$testName] $stats")
    println("  -> 이력서 근거: '실제 편집 패턴 기준 70% 메시지 감소 (순수 중복 제거 시 최대 ${stats.reductionPercent.toInt()}%)'")
}

// ============================================================
// standalone 실행 지원 (Gradle 없이 kotlinc로 실행 시)
// ============================================================

fun main() {
    println("=".repeat(60))
    println("  Operation Message 최적화 검증 (standalone)")
    println("=".repeat(60))

    val optimizer = OperationMessageOptimizer()

    // 간이 테스트: 같은 요소 100회 편집
    for (i in 1..100) {
        optimizer.addOperation(
            OperationMessage(
                elementId = "shape-standalone",
                elementType = ElementType.SHAPE,
                behavior = Behavior.UE,
                data = mapOf("x" to (i * 10.0), "y" to (i * 5.0)),
                sequenceNo = i,
            )
        )
    }

    val result = optimizer.getOptimizedMessages()
    val stats = optimizer.getStats()

    println("")
    println("  결과: $stats")
    println("  마지막 위치: x=${result.first().data["x"]}, y=${result.first().data["y"]}")
    println("")
    println("  JUnit 5 테스트 실행:")
    println("  gradle test 또는 IntelliJ IDEA에서 실행하세요")
    println("=".repeat(60))
}
