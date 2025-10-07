package com.sales.system.domain;

import static com.sales.system.domain.ClientTestSamples.*;
import static com.sales.system.domain.SaleTestSamples.*;
import static com.sales.system.domain.SaleTransactionTestSamples.*;
import static com.sales.system.domain.SellerTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.sales.system.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class SaleTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Sale.class);
        Sale sale1 = getSaleSample1();
        Sale sale2 = new Sale();
        assertThat(sale1).isNotEqualTo(sale2);

        sale2.setId(sale1.getId());
        assertThat(sale1).isEqualTo(sale2);

        sale2 = getSaleSample2();
        assertThat(sale1).isNotEqualTo(sale2);
    }

    @Test
    void transactionsTest() {
        Sale sale = getSaleRandomSampleGenerator();
        SaleTransaction saleTransactionBack = getSaleTransactionRandomSampleGenerator();

        sale.addTransactions(saleTransactionBack);
        assertThat(sale.getTransactions()).containsOnly(saleTransactionBack);
        assertThat(saleTransactionBack.getSale()).isEqualTo(sale);

        sale.removeTransactions(saleTransactionBack);
        assertThat(sale.getTransactions()).doesNotContain(saleTransactionBack);
        assertThat(saleTransactionBack.getSale()).isNull();

        sale.transactions(new HashSet<>(Set.of(saleTransactionBack)));
        assertThat(sale.getTransactions()).containsOnly(saleTransactionBack);
        assertThat(saleTransactionBack.getSale()).isEqualTo(sale);

        sale.setTransactions(new HashSet<>());
        assertThat(sale.getTransactions()).doesNotContain(saleTransactionBack);
        assertThat(saleTransactionBack.getSale()).isNull();
    }

    @Test
    void clientTest() {
        Sale sale = getSaleRandomSampleGenerator();
        Client clientBack = getClientRandomSampleGenerator();

        sale.setClient(clientBack);
        assertThat(sale.getClient()).isEqualTo(clientBack);

        sale.client(null);
        assertThat(sale.getClient()).isNull();
    }

    @Test
    void sellerTest() {
        Sale sale = getSaleRandomSampleGenerator();
        Seller sellerBack = getSellerRandomSampleGenerator();

        sale.setSeller(sellerBack);
        assertThat(sale.getSeller()).isEqualTo(sellerBack);

        sale.seller(null);
        assertThat(sale.getSeller()).isNull();
    }
}
