package com.sales.system.service;

import com.sales.system.domain.*; // for static metamodels
import com.sales.system.domain.Sale;
import com.sales.system.repository.SaleRepository;
import com.sales.system.service.criteria.SaleCriteria;
import com.sales.system.service.dto.SaleDTO;
import com.sales.system.service.mapper.SaleMapper;
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
 * Service for executing complex queries for {@link Sale} entities in the database.
 * The main input is a {@link SaleCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link SaleDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class SaleQueryService extends QueryService<Sale> {

    private static final Logger LOG = LoggerFactory.getLogger(SaleQueryService.class);

    private final SaleRepository saleRepository;

    private final SaleMapper saleMapper;

    public SaleQueryService(SaleRepository saleRepository, SaleMapper saleMapper) {
        this.saleRepository = saleRepository;
        this.saleMapper = saleMapper;
    }

    /**
     * Return a {@link Page} of {@link SaleDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<SaleDTO> findByCriteria(SaleCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Sale> specification = createSpecification(criteria);
        return saleRepository.findAll(specification, page).map(saleMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(SaleCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<Sale> specification = createSpecification(criteria);
        return saleRepository.count(specification);
    }

    /**
     * Function to convert {@link SaleCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Sale> createSpecification(SaleCriteria criteria) {
        Specification<Sale> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = Specification.allOf(
                Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : null,
                buildRangeSpecification(criteria.getId(), Sale_.id),
                buildRangeSpecification(criteria.getCreationDate(), Sale_.creationDate),
                buildRangeSpecification(criteria.getTotal(), Sale_.total),
                buildSpecification(criteria.getTransactionsId(), root ->
                    root.join(Sale_.transactions, JoinType.LEFT).get(SaleTransaction_.id)
                ),
                buildSpecification(criteria.getClientId(), root -> root.join(Sale_.client, JoinType.LEFT).get(Client_.id)),
                buildSpecification(criteria.getSellerId(), root -> root.join(Sale_.seller, JoinType.LEFT).get(Seller_.id))
            );
        }
        return specification;
    }
}
