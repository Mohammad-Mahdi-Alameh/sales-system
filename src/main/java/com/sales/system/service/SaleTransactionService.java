package com.sales.system.service;

import com.sales.system.service.dto.SaleTransactionDTO;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.sales.system.domain.SaleTransaction}.
 */
public interface SaleTransactionService {
    /**
     * Save a saleTransaction.
     *
     * @param saleTransactionDTO the entity to save.
     * @return the persisted entity.
     */
    SaleTransactionDTO save(SaleTransactionDTO saleTransactionDTO);

    /**
     * Updates a saleTransaction.
     *
     * @param saleTransactionDTO the entity to update.
     * @return the persisted entity.
     */
    SaleTransactionDTO update(SaleTransactionDTO saleTransactionDTO);

    /**
     * Partially updates a saleTransaction.
     *
     * @param saleTransactionDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<SaleTransactionDTO> partialUpdate(SaleTransactionDTO saleTransactionDTO);

    /**
     * Get all the saleTransactions.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<SaleTransactionDTO> findAll(Pageable pageable);

    /**
     * Get all the saleTransactions with eager load of many-to-many relationships.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<SaleTransactionDTO> findAllWithEagerRelationships(Pageable pageable);

    /**
     * Get the "id" saleTransaction.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<SaleTransactionDTO> findOne(Long id);

    /**
     * Delete the "id" saleTransaction.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
