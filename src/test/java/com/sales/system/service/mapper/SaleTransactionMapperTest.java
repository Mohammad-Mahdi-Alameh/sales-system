package com.sales.system.service.mapper;

import static com.sales.system.domain.SaleTransactionAsserts.*;
import static com.sales.system.domain.SaleTransactionTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SaleTransactionMapperTest {

    private SaleTransactionMapper saleTransactionMapper;

    @BeforeEach
    void setUp() {
        saleTransactionMapper = new SaleTransactionMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getSaleTransactionSample1();
        var actual = saleTransactionMapper.toEntity(saleTransactionMapper.toDto(expected));
        assertSaleTransactionAllPropertiesEquals(expected, actual);
    }
}
