package com.sales.system.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class SellerTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Seller getSellerSample1() {
        return new Seller().id(1L).name("name1").lastName("lastName1").employeeCode("employeeCode1");
    }

    public static Seller getSellerSample2() {
        return new Seller().id(2L).name("name2").lastName("lastName2").employeeCode("employeeCode2");
    }

    public static Seller getSellerRandomSampleGenerator() {
        return new Seller()
            .id(longCount.incrementAndGet())
            .name(UUID.randomUUID().toString())
            .lastName(UUID.randomUUID().toString())
            .employeeCode(UUID.randomUUID().toString());
    }
}
