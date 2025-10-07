package com.sales.system.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.sales.system.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class SaleTransactionDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(SaleTransactionDTO.class);
        SaleTransactionDTO saleTransactionDTO1 = new SaleTransactionDTO();
        saleTransactionDTO1.setId(1L);
        SaleTransactionDTO saleTransactionDTO2 = new SaleTransactionDTO();
        assertThat(saleTransactionDTO1).isNotEqualTo(saleTransactionDTO2);
        saleTransactionDTO2.setId(saleTransactionDTO1.getId());
        assertThat(saleTransactionDTO1).isEqualTo(saleTransactionDTO2);
        saleTransactionDTO2.setId(2L);
        assertThat(saleTransactionDTO1).isNotEqualTo(saleTransactionDTO2);
        saleTransactionDTO1.setId(null);
        assertThat(saleTransactionDTO1).isNotEqualTo(saleTransactionDTO2);
    }
}
