package com.sales.system.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class TransactionLogTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static TransactionLog getTransactionLogSample1() {
        return new TransactionLog()
            .id(1L)
            .operationType("operationType1")
            .fieldChanged("fieldChanged1")
            .oldValue("oldValue1")
            .newValue("newValue1")
            .modifiedBy("modifiedBy1");
    }

    public static TransactionLog getTransactionLogSample2() {
        return new TransactionLog()
            .id(2L)
            .operationType("operationType2")
            .fieldChanged("fieldChanged2")
            .oldValue("oldValue2")
            .newValue("newValue2")
            .modifiedBy("modifiedBy2");
    }

    public static TransactionLog getTransactionLogRandomSampleGenerator() {
        return new TransactionLog()
            .id(longCount.incrementAndGet())
            .operationType(UUID.randomUUID().toString())
            .fieldChanged(UUID.randomUUID().toString())
            .oldValue(UUID.randomUUID().toString())
            .newValue(UUID.randomUUID().toString())
            .modifiedBy(UUID.randomUUID().toString());
    }
}
