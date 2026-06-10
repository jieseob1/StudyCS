package studycs.javalab.algorithms.math;

public final class GcdLcm {
    private GcdLcm() {
    }

    public static int gcd(int a, int b) {
        int x = Math.abs(a);
        int y = Math.abs(b);

        while (y != 0) {
            int remainder = x % y;
            x = y;
            y = remainder;
        }

        return x;
    }

    public static int lcm(int a, int b) {
        if (a == 0 || b == 0) {
            return 0;
        }
        return Math.abs(a / gcd(a, b) * b);
    }

    public static void runDemo() {
        System.out.println("GCD(54,24): " + gcd(54, 24));
        System.out.println("LCM(54,24): " + lcm(54, 24));
    }
}
