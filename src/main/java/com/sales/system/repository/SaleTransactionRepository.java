package com.sales.system.repository;

import com.sales.system.domain.SaleTransaction;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the SaleTransaction entity.
 */
@Repository
public interface SaleTransactionRepository extends JpaRepository<SaleTransaction, Long> {
    default Optional<SaleTransaction> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<SaleTransaction> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<SaleTransaction> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select saleTransaction from SaleTransaction saleTransaction left join fetch saleTransaction.product",
        countQuery = "select count(saleTransaction) from SaleTransaction saleTransaction"
    )
    Page<SaleTransaction> findAllWithToOneRelationships(Pageable pageable);

    @Query("select saleTransaction from SaleTransaction saleTransaction left join fetch saleTransaction.product")
    List<SaleTransaction> findAllWithToOneRelationships();

    @Query(
        "select saleTransaction from SaleTransaction saleTransaction left join fetch saleTransaction.product where saleTransaction.id =:id"
    )
    Optional<SaleTransaction> findOneWithToOneRelationships(@Param("id") Long id);
}
