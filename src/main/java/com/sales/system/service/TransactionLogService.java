package com.sales.system.service;

import com.sales.system.service.dto.TransactionLogDTO;
import java.util.Optional;

/**
 * Service Interface for managing {@link com.sales.system.domain.TransactionLog}.
 */
public interface TransactionLogService {
    /**
     * Save a transactionLog.
     *
     * @param transactionLogDTO the entity to save.
     * @return the persisted entity.
     */
    TransactionLogDTO save(TransactionLogDTO transactionLogDTO);

    /**
     * Updates a transactionLog.
     *
     * @param transactionLogDTO the entity to update.
     * @return the persisted entity.
     */
    TransactionLogDTO update(TransactionLogDTO transactionLogDTO);

    /**
     * Partially updates a transactionLog.
     *
     * @param transactionLogDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<TransactionLogDTO> partialUpdate(TransactionLogDTO transactionLogDTO);

    /**
     * Get the "id" transactionLog.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<TransactionLogDTO> findOne(Long id);

    /**
     * Delete the "id" transactionLog.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
