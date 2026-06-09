package studycs.javalab.algorithms.math;

import java.util.ArrayList;
import java.util.List;

public final class SieveOfEratosthenes {
    private SieveOfEratosthenes() {
    }

    public static List<Integer> primesUpTo(int n) {
        boolean[] composite = new boolean[n + 1];
        for (int number = 2; number * number <= n; number++) {
            if (composite[number]) {
                continue;
            }
            for (int multiple = number * number; multiple <= n; multiple += number) {
                composite[multiple] = true;
            }
        }

        List<Integer> primes = new ArrayList<>();
        for (int number = 2; number <= n; number++) {
            if (!composite[number]) {
                primes.add(number);
            }
        }
        return primes;
    }

    public static void runDemo() {
        System.out.println("Sieve primes up to 13 -> " + primesUpTo(13));
    }
}
