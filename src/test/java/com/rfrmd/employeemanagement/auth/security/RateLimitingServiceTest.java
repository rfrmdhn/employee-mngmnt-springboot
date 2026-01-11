package com.rfrmd.employeemanagement.auth.security;

import io.github.bucket4j.Bucket;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RateLimitingServiceTest {

    private RateLimitingService rateLimitingService;

    @BeforeEach
    void setUp() {
        rateLimitingService = new RateLimitingService();
    }

    @Test
    void resolveBucket_ShouldReturnBucket() {
        Bucket bucket = rateLimitingService.resolveBucket("user@example.com");
        assertNotNull(bucket);
    }

    @Test
    void resolveBucket_ShouldReturnSameBucketForKey() {
        Bucket bucket1 = rateLimitingService.resolveBucket("user@example.com");
        Bucket bucket2 = rateLimitingService.resolveBucket("user@example.com");

        assertSame(bucket1, bucket2);
    }

    @Test
    void resolveBucket_ShouldReturnDifferentBucketsForDifferentKeys() {
        Bucket bucket1 = rateLimitingService.resolveBucket("user1@example.com");
        Bucket bucket2 = rateLimitingService.resolveBucket("user2@example.com");

        assertNotSame(bucket1, bucket2);
    }

    @Test
    void bucket_ShouldAllowRequestsUpToLimit() {
        Bucket bucket = rateLimitingService.resolveBucket("test@test.com");

        // Detailed check based on configured limit (5 per minute)
        assertTrue(bucket.tryConsume(1));
        assertTrue(bucket.tryConsume(1));
        assertTrue(bucket.tryConsume(1));
        assertTrue(bucket.tryConsume(1));
        assertTrue(bucket.tryConsume(1));

        // 6th should fail
        assertFalse(bucket.tryConsume(1));
    }
}
