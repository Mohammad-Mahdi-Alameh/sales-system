package com.sales.system.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

/**
 * A Sale.
 */
@Entity
@Table(name = "sale")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Sale implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "creation_date", nullable = false)
    private Instant creationDate;

    @NotNull
    @DecimalMin(value = "0")
    @Column(name = "total", precision = 21, scale = 2, nullable = false)
    private BigDecimal total;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "sale")
    @JsonIgnoreProperties(value = { "product", "sale" }, allowSetters = true)
    private Set<SaleTransaction> transactions = new HashSet<>();

    @ManyToOne(optional = false)
    @NotNull
    private Client client;

    @ManyToOne(optional = false)
    @NotNull
    private Seller seller;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Sale id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getCreationDate() {
        return this.creationDate;
    }

    public Sale creationDate(Instant creationDate) {
        this.setCreationDate(creationDate);
        return this;
    }

    public void setCreationDate(Instant creationDate) {
        this.creationDate = creationDate;
    }

    public BigDecimal getTotal() {
        return this.total;
    }

    public Sale total(BigDecimal total) {
        this.setTotal(total);
        return this;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public Set<SaleTransaction> getTransactions() {
        return this.transactions;
    }

    public void setTransactions(Set<SaleTransaction> saleTransactions) {
        if (this.transactions != null) {
            this.transactions.forEach(i -> i.setSale(null));
        }
        if (saleTransactions != null) {
            saleTransactions.forEach(i -> i.setSale(this));
        }
        this.transactions = saleTransactions;
    }

    public Sale transactions(Set<SaleTransaction> saleTransactions) {
        this.setTransactions(saleTransactions);
        return this;
    }

    public Sale addTransactions(SaleTransaction saleTransaction) {
        this.transactions.add(saleTransaction);
        saleTransaction.setSale(this);
        return this;
    }

    public Sale removeTransactions(SaleTransaction saleTransaction) {
        this.transactions.remove(saleTransaction);
        saleTransaction.setSale(null);
        return this;
    }

    public Client getClient() {
        return this.client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public Sale client(Client client) {
        this.setClient(client);
        return this;
    }

    public Seller getSeller() {
        return this.seller;
    }

    public void setSeller(Seller seller) {
        this.seller = seller;
    }

    public Sale seller(Seller seller) {
        this.setSeller(seller);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Sale)) {
            return false;
        }
        return getId() != null && getId().equals(((Sale) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Sale{" +
            "id=" + getId() +
            ", creationDate='" + getCreationDate() + "'" +
            ", total=" + getTotal() +
            "}";
    }
}
