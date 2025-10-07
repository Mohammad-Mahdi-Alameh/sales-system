package com.sales.system.repository;

import com.sales.system.domain.Seller;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Seller entity.
 */
@SuppressWarnings("unused")
@Repository
public interface SellerRepository extends JpaRepository<Seller, Long>, JpaSpecificationExecutor<Seller> {}
