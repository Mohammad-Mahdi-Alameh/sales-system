package com.sales.system.service.mapper;

import static com.sales.system.domain.TransactionLogAsserts.*;
import static com.sales.system.domain.TransactionLogTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TransactionLogMapperTest {

    private TransactionLogMapper transactionLogMapper;

    @BeforeEach
    void setUp() {
        transactionLogMapper = new TransactionLogMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getTransactionLogSample1();
        var actual = transactionLogMapper.toEntity(transactionLogMapper.toDto(expected));
        assertTransactionLogAllPropertiesEquals(expected, actual);
    }
}
