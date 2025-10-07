package com.sales.system.repository;

import com.sales.system.domain.Sale;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Sale entity.
 */
@Repository
public interface SaleRepository extends JpaRepository<Sale, Long>, JpaSpecificationExecutor<Sale> {
    default Optional<Sale> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<Sale> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<Sale> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select sale from Sale sale left join fetch sale.client left join fetch sale.seller",
        countQuery = "select count(sale) from Sale sale"
    )
    Page<Sale> findAllWithToOneRelationships(Pageable pageable);

    @Query("select sale from Sale sale left join fetch sale.client left join fetch sale.seller")
    List<Sale> findAllWithToOneRelationships();

    @Query("select sale from Sale sale left join fetch sale.client left join fetch sale.seller where sale.id =:id")
    Optional<Sale> findOneWithToOneRelationships(@Param("id") Long id);
}
