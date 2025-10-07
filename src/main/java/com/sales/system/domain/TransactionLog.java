package com.sales.system.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;

/**
 * A TransactionLog.
 */
@Entity
@Table(name = "transaction_log")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class TransactionLog implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "timestamp", nullable = false)
    private Instant timestamp;

    @NotNull
    @Size(max = 50)
    @Column(name = "operation_type", length = 50, nullable = false)
    private String operationType;

    @NotNull
    @Size(max = 100)
    @Column(name = "field_changed", length = 100, nullable = false)
    private String fieldChanged;

    @Size(max = 500)
    @Column(name = "old_value", length = 500)
    private String oldValue;

    @Size(max = 500)
    @Column(name = "new_value", length = 500)
    private String newValue;

    @NotNull
    @Size(max = 100)
    @Column(name = "modified_by", length = 100, nullable = false)
    private String modifiedBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "product", "sale" }, allowSetters = true)
    private SaleTransaction saleTransaction;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public TransactionLog id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getTimestamp() {
        return this.timestamp;
    }

    public TransactionLog timestamp(Instant timestamp) {
        this.setTimestamp(timestamp);
        return this;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public String getOperationType() {
        return this.operationType;
    }

    public TransactionLog operationType(String operationType) {
        this.setOperationType(operationType);
        return this;
    }

    public void setOperationType(String operationType) {
        this.operationType = operationType;
    }

    public String getFieldChanged() {
        return this.fieldChanged;
    }

    public TransactionLog fieldChanged(String fieldChanged) {
        this.setFieldChanged(fieldChanged);
        return this;
    }

    public void setFieldChanged(String fieldChanged) {
        this.fieldChanged = fieldChanged;
    }

    public String getOldValue() {
        return this.oldValue;
    }

    public TransactionLog oldValue(String oldValue) {
        this.setOldValue(oldValue);
        return this;
    }

    public void setOldValue(String oldValue) {
        this.oldValue = oldValue;
    }

    public String getNewValue() {
        return this.newValue;
    }

    public TransactionLog newValue(String newValue) {
        this.setNewValue(newValue);
        return this;
    }

    public void setNewValue(String newValue) {
        this.newValue = newValue;
    }

    public String getModifiedBy() {
        return this.modifiedBy;
    }

    public TransactionLog modifiedBy(String modifiedBy) {
        this.setModifiedBy(modifiedBy);
        return this;
    }

    public void setModifiedBy(String modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    public SaleTransaction getSaleTransaction() {
        return this.saleTransaction;
    }

    public void setSaleTransaction(SaleTransaction saleTransaction) {
        this.saleTransaction = saleTransaction;
    }

    public TransactionLog saleTransaction(SaleTransaction saleTransaction) {
        this.setSaleTransaction(saleTransaction);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TransactionLog)) {
            return false;
        }
        return getId() != null && getId().equals(((TransactionLog) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TransactionLog{" +
            "id=" + getId() +
            ", timestamp='" + getTimestamp() + "'" +
            ", operationType='" + getOperationType() + "'" +
            ", fieldChanged='" + getFieldChanged() + "'" +
            ", oldValue='" + getOldValue() + "'" +
            ", newValue='" + getNewValue() + "'" +
            ", modifiedBy='" + getModifiedBy() + "'" +
            "}";
    }
}
