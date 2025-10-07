package com.sales.system.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.sales.system.domain.Sale} entity. This class is used
 * in {@link com.sales.system.web.rest.SaleResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /sales?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class SaleCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private InstantFilter creationDate;

    private BigDecimalFilter total;

    private LongFilter transactionsId;

    private LongFilter clientId;

    private LongFilter sellerId;

    private Boolean distinct;

    public SaleCriteria() {}

    public SaleCriteria(SaleCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.creationDate = other.optionalCreationDate().map(InstantFilter::copy).orElse(null);
        this.total = other.optionalTotal().map(BigDecimalFilter::copy).orElse(null);
        this.transactionsId = other.optionalTransactionsId().map(LongFilter::copy).orElse(null);
        this.clientId = other.optionalClientId().map(LongFilter::copy).orElse(null);
        this.sellerId = other.optionalSellerId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public SaleCriteria copy() {
        return new SaleCriteria(this);
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

    public InstantFilter getCreationDate() {
        return creationDate;
    }

    public Optional<InstantFilter> optionalCreationDate() {
        return Optional.ofNullable(creationDate);
    }

    public InstantFilter creationDate() {
        if (creationDate == null) {
            setCreationDate(new InstantFilter());
        }
        return creationDate;
    }

    public void setCreationDate(InstantFilter creationDate) {
        this.creationDate = creationDate;
    }

    public BigDecimalFilter getTotal() {
        return total;
    }

    public Optional<BigDecimalFilter> optionalTotal() {
        return Optional.ofNullable(total);
    }

    public BigDecimalFilter total() {
        if (total == null) {
            setTotal(new BigDecimalFilter());
        }
        return total;
    }

    public void setTotal(BigDecimalFilter total) {
        this.total = total;
    }

    public LongFilter getTransactionsId() {
        return transactionsId;
    }

    public Optional<LongFilter> optionalTransactionsId() {
        return Optional.ofNullable(transactionsId);
    }

    public LongFilter transactionsId() {
        if (transactionsId == null) {
            setTransactionsId(new LongFilter());
        }
        return transactionsId;
    }

    public void setTransactionsId(LongFilter transactionsId) {
        this.transactionsId = transactionsId;
    }

    public LongFilter getClientId() {
        return clientId;
    }

    public Optional<LongFilter> optionalClientId() {
        return Optional.ofNullable(clientId);
    }

    public LongFilter clientId() {
        if (clientId == null) {
            setClientId(new LongFilter());
        }
        return clientId;
    }

    public void setClientId(LongFilter clientId) {
        this.clientId = clientId;
    }

    public LongFilter getSellerId() {
        return sellerId;
    }

    public Optional<LongFilter> optionalSellerId() {
        return Optional.ofNullable(sellerId);
    }

    public LongFilter sellerId() {
        if (sellerId == null) {
            setSellerId(new LongFilter());
        }
        return sellerId;
    }

    public void setSellerId(LongFilter sellerId) {
        this.sellerId = sellerId;
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
        final SaleCriteria that = (SaleCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(creationDate, that.creationDate) &&
            Objects.equals(total, that.total) &&
            Objects.equals(transactionsId, that.transactionsId) &&
            Objects.equals(clientId, that.clientId) &&
            Objects.equals(sellerId, that.sellerId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, creationDate, total, transactionsId, clientId, sellerId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "SaleCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalCreationDate().map(f -> "creationDate=" + f + ", ").orElse("") +
            optionalTotal().map(f -> "total=" + f + ", ").orElse("") +
            optionalTransactionsId().map(f -> "transactionsId=" + f + ", ").orElse("") +
            optionalClientId().map(f -> "clientId=" + f + ", ").orElse("") +
            optionalSellerId().map(f -> "sellerId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
