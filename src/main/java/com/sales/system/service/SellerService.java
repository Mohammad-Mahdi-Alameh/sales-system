package com.sales.system.service;

import com.sales.system.service.dto.SellerDTO;
import java.util.Optional;

/**
 * Service Interface for managing {@link com.sales.system.domain.Seller}.
 */
public interface SellerService {
    /**
     * Save a seller.
     *
     * @param sellerDTO the entity to save.
     * @return the persisted entity.
     */
    SellerDTO save(SellerDTO sellerDTO);

    /**
     * Updates a seller.
     *
     * @param sellerDTO the entity to update.
     * @return the persisted entity.
     */
    SellerDTO update(SellerDTO sellerDTO);

    /**
     * Partially updates a seller.
     *
     * @param sellerDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<SellerDTO> partialUpdate(SellerDTO sellerDTO);

    /**
     * Get the "id" seller.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<SellerDTO> findOne(Long id);

    /**
     * Delete the "id" seller.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
