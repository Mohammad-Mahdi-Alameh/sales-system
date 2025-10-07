package com.sales.system.service.dto;

import jakarta.validation.constraints.*;

import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.sales.system.domain.TransactionLog} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class TransactionLogDTO implements Serializable {

    private Long id;

    @NotNull
    private Instant timestamp;

    @NotNull
    @Size(max = 50)
    private String operationType;

//    @NotNull
//    @Size(max = 100)
//    private String fieldChanged;
//
//    @Size(max = 500)
//    private String oldValue;
//
//    @Size(max = 500)
//    private String newValue;
//
//    @NotNull
//    @Size(max = 100)
//    private String modifiedBy;

//    private SaleTransactionDTO saleTransaction;
    private Long saleTransactionId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSaleTransactionId() {
        return saleTransactionId;
    }

    public void setSaleTransactionId(Long saleTransactionId) {
        this.saleTransactionId = saleTransactionId;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public String getOperationType() {
        return operationType;
    }

    public void setOperationType(String operationType) {
        this.operationType = operationType;
    }

//    public String getFieldChanged() {
//        return fieldChanged;
//    }

//    public void setFieldChanged(String fieldChanged) {
//        this.fieldChanged = fieldChanged;
//    }

//    public String getOldValue() {
//        return oldValue;
//    }

//    public void setOldValue(String oldValue) {
//        this.oldValue = oldValue;
//    }
//
//    public String getNewValue() {
//        return newValue;
//    }
//
//    public void setNewValue(String newValue) {
//        this.newValue = newValue;
//    }
//
//    public String getModifiedBy() {
//        return modifiedBy;
//    }
//
//    public void setModifiedBy(String modifiedBy) {
//        this.modifiedBy = modifiedBy;
//    }

//    public SaleTransactionDTO getSaleTransaction() {
//        return saleTransaction;
//    }
//
//    public void setSaleTransaction(SaleTransactionDTO saleTransaction) {
//        this.saleTransaction = saleTransaction;
//    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TransactionLogDTO)) {
            return false;
        }

        TransactionLogDTO transactionLogDTO = (TransactionLogDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, transactionLogDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TransactionLogDTO{" +
            "id=" + getId() +
            ", timestamp='" + getTimestamp() + "'" +
            ", operationType='" + getOperationType() + "'" +
//            ", fieldChanged='" + getFieldChanged() + "'" +
//            ", oldValue='" + getOldValue() + "'" +
//            ", newValue='" + getNewValue() + "'" +
//            ", modifiedBy='" + getModifiedBy() + "'" +
//            ", saleTransaction=" + getSaleTransaction() +
            "}";
    }
}
