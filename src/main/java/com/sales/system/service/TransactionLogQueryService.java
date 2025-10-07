package com.sales.system.service;

import com.sales.system.domain.*; // for static metamodels
import com.sales.system.domain.TransactionLog;
import com.sales.system.repository.TransactionLogRepository;
import com.sales.system.service.criteria.TransactionLogCriteria;
import com.sales.system.service.dto.TransactionLogDTO;
import com.sales.system.service.mapper.TransactionLogMapper;
import jakarta.persistence.criteria.JoinType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link TransactionLog} entities in the database.
 * The main input is a {@link TransactionLogCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link TransactionLogDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class TransactionLogQueryService extends QueryService<TransactionLog> {

    private static final Logger LOG = LoggerFactory.getLogger(TransactionLogQueryService.class);

    private final TransactionLogRepository transactionLogRepository;

    private final TransactionLogMapper transactionLogMapper;

    public TransactionLogQueryService(TransactionLogRepository transactionLogRepository, TransactionLogMapper transactionLogMapper) {
        this.transactionLogRepository = transactionLogRepository;
        this.transactionLogMapper = transactionLogMapper;
    }

    /**
     * Return a {@link Page} of {@link TransactionLogDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<TransactionLogDTO> findByCriteria(TransactionLogCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<TransactionLog> specification = createSpecification(criteria);
        return transactionLogRepository.findAll(specification, page).map(transactionLogMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(TransactionLogCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<TransactionLog> specification = createSpecification(criteria);
        return transactionLogRepository.count(specification);
    }

    /**
     * Function to convert {@link TransactionLogCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<TransactionLog> createSpecification(TransactionLogCriteria criteria) {
        Specification<TransactionLog> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = Specification.allOf(
                Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : null,
                buildRangeSpecification(criteria.getId(), TransactionLog_.id),
                buildRangeSpecification(criteria.getTimestamp(), TransactionLog_.timestamp),
                buildStringSpecification(criteria.getOperationType(), TransactionLog_.operationType),
//                buildStringSpecification(criteria.getFieldChanged(), TransactionLog_.fieldChanged),
//                buildStringSpecification(criteria.getOldValue(), TransactionLog_.oldValue),
//                buildStringSpecification(criteria.getNewValue(), TransactionLog_.newValue),
                buildStringSpecification(criteria.getModifiedBy(), TransactionLog_.modifiedBy),
                buildSpecification(criteria.getSaleTransactionId(), root ->
                    root.join(TransactionLog_.saleTransaction, JoinType.LEFT).get(SaleTransaction_.id)
                )
            );
        }
        return specification;
    }
}
