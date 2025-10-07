package com.sales.system.domain;

import static com.sales.system.domain.SaleTransactionTestSamples.*;
import static com.sales.system.domain.TransactionLogTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.sales.system.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class TransactionLogTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(TransactionLog.class);
        TransactionLog transactionLog1 = getTransactionLogSample1();
        TransactionLog transactionLog2 = new TransactionLog();
        assertThat(transactionLog1).isNotEqualTo(transactionLog2);

        transactionLog2.setId(transactionLog1.getId());
        assertThat(transactionLog1).isEqualTo(transactionLog2);

        transactionLog2 = getTransactionLogSample2();
        assertThat(transactionLog1).isNotEqualTo(transactionLog2);
    }

    @Test
    void saleTransactionTest() {
        TransactionLog transactionLog = getTransactionLogRandomSampleGenerator();
        SaleTransaction saleTransactionBack = getSaleTransactionRandomSampleGenerator();

        transactionLog.setSaleTransaction(saleTransactionBack);
        assertThat(transactionLog.getSaleTransaction()).isEqualTo(saleTransactionBack);

        transactionLog.saleTransaction(null);
        assertThat(transactionLog.getSaleTransaction()).isNull();
    }
}
