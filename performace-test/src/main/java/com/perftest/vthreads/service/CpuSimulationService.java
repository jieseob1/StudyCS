package com.perftest.vthreads.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

@Service
public class CpuSimulationService {

    private static final Logger log = LoggerFactory.getLogger(CpuSimulationService.class);

    /**
     * Computes prime numbers up to the given iteration count using trial division.
     *
     * This is a pure CPU-bound workload. Virtual threads do NOT provide an advantage here
     * because CPU-bound tasks do not yield the carrier thread - they keep it busy.
     * This benchmark exists to demonstrate exactly this characteristic: when the workload
     * is CPU-intensive, virtual threads perform similarly to platform threads (or worse
     * at very high concurrency due to context-switch overhead on the fixed carrier pool).
     *
     * @param iterations upper bound for prime search (i.e., find all primes <= iterations)
     * @return list of primes found
     */
    public List<Long> computePrimes(long iterations) {
        Thread current = Thread.currentThread();
        log.debug("computePrimes: thread={}, isVirtual={}, iterations={}",
                current.getName(), current.isVirtual(), iterations);

        List<Long> primes = new ArrayList<>();
        for (long candidate = 2; candidate <= iterations; candidate++) {
            if (isPrime(candidate)) {
                primes.add(candidate);
            }
        }

        log.debug("computePrimes found {} primes up to {}", primes.size(), iterations);
        return primes;
    }

    /**
     * Repeatedly hashes a payload using SHA-256, simulating CPU-intensive cryptographic work.
     *
     * Each round feeds the result of the previous hash back into the next hash,
     * preventing the JIT from optimising away the loop.
     *
     * @param payload initial string to hash
     * @param rounds  number of successive SHA-256 rounds to perform
     * @return hex-encoded final hash
     */
    public String hashPayload(String payload, int rounds) {
        Thread current = Thread.currentThread();
        log.debug("hashPayload: thread={}, isVirtual={}, rounds={}",
                current.getName(), current.isVirtual(), rounds);

        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] data = payload.getBytes(StandardCharsets.UTF_8);

            for (int i = 0; i < rounds; i++) {
                data = digest.digest(data);
                digest.reset();
            }

            return bytesToHex(data);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 algorithm not available", e);
        }
    }

    // -------------------------------------------------------------------------
    // Private helpers
    // -------------------------------------------------------------------------

    private boolean isPrime(long n) {
        if (n < 2) return false;
        if (n == 2) return true;
        if (n % 2 == 0) return false;
        long limit = (long) Math.sqrt(n);
        for (long i = 3; i <= limit; i += 2) {
            if (n % i == 0) return false;
        }
        return true;
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}
