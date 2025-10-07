package com.sales.system.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

/**
 * A DTO for the {@link com.sales.system.domain.SaleTransaction} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class SaleTransactionDTO implements Serializable {

    private Long id;

    @NotNull
    @Min(value = 1)
    private Integer quantity;

    @NotNull
    @DecimalMin(value = "0")
    private BigDecimal unitPrice;

    @NotNull
    @DecimalMin(value = "0")
    private BigDecimal subtotal;

    @NotNull
    private ProductDTO product;

    @NotNull
    private SaleDTO sale;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    public ProductDTO getProduct() {
        return product;
    }

    public void setProduct(ProductDTO product) {
        this.product = product;
    }

    public SaleDTO getSale() {
        return sale;
    }

    public void setSale(SaleDTO sale) {
        this.sale = sale;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SaleTransactionDTO)) {
            return false;
        }

        SaleTransactionDTO saleTransactionDTO = (SaleTransactionDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, saleTransactionDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "SaleTransactionDTO{" +
            "id=" + getId() +
            ", quantity=" + getQuantity() +
            ", unitPrice=" + getUnitPrice() +
            ", subtotal=" + getSubtotal() +
            ", product=" + getProduct() +
            ", sale=" + getSale() +
            "}";
    }
}
