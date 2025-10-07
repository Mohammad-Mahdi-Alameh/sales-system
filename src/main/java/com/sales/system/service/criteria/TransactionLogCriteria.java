package com.sales.system.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.sales.system.domain.TransactionLog} entity. This class is used
 * in {@link com.sales.system.web.rest.TransactionLogResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /transaction-logs?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class TransactionLogCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private InstantFilter timestamp;

    private StringFilter operationType;

    private StringFilter fieldChanged;

    private StringFilter oldValue;

    private StringFilter newValue;

    private StringFilter modifiedBy;

    private LongFilter saleTransactionId;

    private Boolean distinct;

    public TransactionLogCriteria() {}

    public TransactionLogCriteria(TransactionLogCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.timestamp = other.optionalTimestamp().map(InstantFilter::copy).orElse(null);
        this.operationType = other.optionalOperationType().map(StringFilter::copy).orElse(null);
        this.fieldChanged = other.optionalFieldChanged().map(StringFilter::copy).orElse(null);
        this.oldValue = other.optionalOldValue().map(StringFilter::copy).orElse(null);
        this.newValue = other.optionalNewValue().map(StringFilter::copy).orElse(null);
        this.modifiedBy = other.optionalModifiedBy().map(StringFilter::copy).orElse(null);
        this.saleTransactionId = other.optionalSaleTransactionId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public TransactionLogCriteria copy() {
        return new TransactionLogCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public Optional<LongFilter> optionalId() {
        return Optional.ofNullable(id);
    }

    public LongFilter id() {
        if (id == null) {
            setId(new LongFilter());
        }
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public InstantFilter getTimestamp() {
        return timestamp;
    }

    public Optional<InstantFilter> optionalTimestamp() {
        return Optional.ofNullable(timestamp);
    }

    public InstantFilter timestamp() {
        if (timestamp == null) {
            setTimestamp(new InstantFilter());
        }
        return timestamp;
    }

    public void setTimestamp(InstantFilter timestamp) {
        this.timestamp = timestamp;
    }

    public StringFilter getOperationType() {
        return operationType;
    }

    public Optional<StringFilter> optionalOperationType() {
        return Optional.ofNullable(operationType);
    }

    public StringFilter operationType() {
        if (operationType == null) {
            setOperationType(new StringFilter());
        }
        return operationType;
    }

    public void setOperationType(StringFilter operationType) {
        this.operationType = operationType;
    }

    public StringFilter getFieldChanged() {
        return fieldChanged;
    }

    public Optional<StringFilter> optionalFieldChanged() {
        return Optional.ofNullable(fieldChanged);
    }

    public StringFilter fieldChanged() {
        if (fieldChanged == null) {
            setFieldChanged(new StringFilter());
        }
        return fieldChanged;
    }

    public void setFieldChanged(StringFilter fieldChanged) {
        this.fieldChanged = fieldChanged;
    }

    public StringFilter getOldValue() {
        return oldValue;
    }

    public Optional<StringFilter> optionalOldValue() {
        return Optional.ofNullable(oldValue);
    }

    public StringFilter oldValue() {
        if (oldValue == null) {
            setOldValue(new StringFilter());
        }
        return oldValue;
    }

    public void setOldValue(StringFilter oldValue) {
        this.oldValue = oldValue;
    }

    public StringFilter getNewValue() {
        return newValue;
    }

    public Optional<StringFilter> optionalNewValue() {
        return Optional.ofNullable(newValue);
    }

    public StringFilter newValue() {
        if (newValue == null) {
            setNewValue(new StringFilter());
        }
        return newValue;
    }

    public void setNewValue(StringFilter newValue) {
        this.newValue = newValue;
    }

    public StringFilter getModifiedBy() {
        return modifiedBy;
    }

    public Optional<StringFilter> optionalModifiedBy() {
        return Optional.ofNullable(modifiedBy);
    }

    public StringFilter modifiedBy() {
        if (modifiedBy == null) {
            setModifiedBy(new StringFilter());
        }
        return modifiedBy;
    }

    public void setModifiedBy(StringFilter modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    public LongFilter getSaleTransactionId() {
        return saleTransactionId;
    }

    public Optional<LongFilter> optionalSaleTransactionId() {
        return Optional.ofNullable(saleTransactionId);
    }

    public LongFilter saleTransactionId() {
        if (saleTransactionId == null) {
            setSaleTransactionId(new LongFilter());
        }
        return saleTransactionId;
    }

    public void setSaleTransactionId(LongFilter saleTransactionId) {
        this.saleTransactionId = saleTransactionId;
    }

    public Boolean getDistinct() {
        return distinct;
    }

    public Optional<Boolean> optionalDistinct() {
        return Optional.ofNullable(distinct);
    }

    public Boolean distinct() {
        if (distinct == null) {
            setDistinct(true);
        }
        return distinct;
    }

    public void setDistinct(Boolean distinct) {
        this.distinct = distinct;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final TransactionLogCriteria that = (TransactionLogCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(timestamp, that.timestamp) &&
            Objects.equals(operationType, that.operationType) &&
            Objects.equals(fieldChanged, that.fieldChanged) &&
            Objects.equals(oldValue, that.oldValue) &&
            Objects.equals(newValue, that.newValue) &&
            Objects.equals(modifiedBy, that.modifiedBy) &&
            Objects.equals(saleTransactionId, that.saleTransactionId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, timestamp, operationType, fieldChanged, oldValue, newValue, modifiedBy, saleTransactionId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TransactionLogCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalTimestamp().map(f -> "timestamp=" + f + ", ").orElse("") +
            optionalOperationType().map(f -> "operationType=" + f + ", ").orElse("") +
            optionalFieldChanged().map(f -> "fieldChanged=" + f + ", ").orElse("") +
            optionalOldValue().map(f -> "oldValue=" + f + ", ").orElse("") +
            optionalNewValue().map(f -> "newValue=" + f + ", ").orElse("") +
            optionalModifiedBy().map(f -> "modifiedBy=" + f + ", ").orElse("") +
            optionalSaleTransactionId().map(f -> "saleTransactionId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
