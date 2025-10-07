package com.sales.system.domain;

import static com.sales.system.domain.ProductTestSamples.*;
import static com.sales.system.domain.SaleTestSamples.*;
import static com.sales.system.domain.SaleTransactionTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.sales.system.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class SaleTransactionTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(SaleTransaction.class);
        SaleTransaction saleTransaction1 = getSaleTransactionSample1();
        SaleTransaction saleTransaction2 = new SaleTransaction();
        assertThat(saleTransaction1).isNotEqualTo(saleTransaction2);

        saleTransaction2.setId(saleTransaction1.getId());
        assertThat(saleTransaction1).isEqualTo(saleTransaction2);

        saleTransaction2 = getSaleTransactionSample2();
        assertThat(saleTransaction1).isNotEqualTo(saleTransaction2);
    }

    @Test
    void productTest() {
        SaleTransaction saleTransaction = getSaleTransactionRandomSampleGenerator();
        Product productBack = getProductRandomSampleGenerator();

        saleTransaction.setProduct(productBack);
        assertThat(saleTransaction.getProduct()).isEqualTo(productBack);

        saleTransaction.product(null);
        assertThat(saleTransaction.getProduct()).isNull();
    }

    @Test
    void saleTest() {
        SaleTransaction saleTransaction = getSaleTransactionRandomSampleGenerator();
        Sale saleBack = getSaleRandomSampleGenerator();

        saleTransaction.setSale(saleBack);
        assertThat(saleTransaction.getSale()).isEqualTo(saleBack);

        saleTransaction.sale(null);
        assertThat(saleTransaction.getSale()).isNull();
    }
}
