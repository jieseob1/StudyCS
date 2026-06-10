package theory;

interface AbstractFactory { // 추상 팩토리 인터페이스 => 해당 기능은 비슷한 제품군을 생성하는 팩토리를 정의하는 인터페이스
    AbstractProductA createProductA(); // 제품 A 생성 메서드
    AbstractProductB createProductB(); // 제품 B 생성 메서드
}

// product a1, b1을 생성하는 팩토리
class ConcreteFactory1 implements AbstractFactory {
    public AbstractProductA createProductA() {
        return new ConcreteProductA1(); // theory.ConcreteProductA1 객체 생성
    }

    @Override
    public AbstractProductB createProductB() {
        return new ConcreteProductB1(); // theory.ConcreteProductB1 객체 생성
    }
}

// product a2, b2를 생성하는 팩토리
class ConcreteFactory2 implements AbstractFactory {
    public AbstractProductA createProductA() {
        return new ConcreteProductA2(); // theory.ConcreteProductA2 객체 생성
    }

    @Override
    public AbstractProductB createProductB() {
        return new ConcreteProductB2(); // theory.ConcreteProductB2 객체 생성
    }
}


