package com.sales.system.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class ClientTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Client getClientSample1() {
        return new Client().id(1L).name("name1").lastName("lastName1").mobile("mobile1");
    }

    public static Client getClientSample2() {
        return new Client().id(2L).name("name2").lastName("lastName2").mobile("mobile2");
    }

    public static Client getClientRandomSampleGenerator() {
        return new Client()
            .id(longCount.incrementAndGet())
            .name(UUID.randomUUID().toString())
            .lastName(UUID.randomUUID().toString())
            .mobile(UUID.randomUUID().toString());
    }
}
