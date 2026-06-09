package studycs.javalab.algorithms.dp;

import java.util.Arrays;

public final class CoinChange {
    private CoinChange() {
    }

    public static int minimumCoins(int[] coins, int amount) {
        int impossible = amount + 1;
        int[] dp = new int[amount + 1];
        Arrays.fill(dp, impossible);
        dp[0] = 0;

        for (int value = 1; value <= amount; value++) {
            for (int coin : coins) {
                if (value >= coin) {
                    dp[value] = Math.min(dp[value], dp[value - coin] + 1);
                }
            }
        }

        return dp[amount] == impossible ? -1 : dp[amount];
    }

    public static void runDemo() {
        System.out.println("CoinChange [1,2,5], 11 -> " + minimumCoins(new int[] {1, 2, 5}, 11));
    }
}
