package com.sales.system.service;

import com.sales.system.domain.*; // for static metamodels
import com.sales.system.domain.Seller;
import com.sales.system.repository.SellerRepository;
import com.sales.system.service.criteria.SellerCriteria;
import com.sales.system.service.dto.SellerDTO;
import com.sales.system.service.mapper.SellerMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link Seller} entities in the database.
 * The main input is a {@link SellerCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link SellerDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class SellerQueryService extends QueryService<Seller> {

    private static final Logger LOG = LoggerFactory.getLogger(SellerQueryService.class);

    private final SellerRepository sellerRepository;

    private final SellerMapper sellerMapper;

    public SellerQueryService(SellerRepository sellerRepository, SellerMapper sellerMapper) {
        this.sellerRepository = sellerRepository;
        this.sellerMapper = sellerMapper;
    }

    /**
     * Return a {@link Page} of {@link SellerDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<SellerDTO> findByCriteria(SellerCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Seller> specification = createSpecification(criteria);
        return sellerRepository.findAll(specification, page).map(sellerMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(SellerCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<Seller> specification = createSpecification(criteria);
        return sellerRepository.count(specification);
    }

    /**
     * Function to convert {@link SellerCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Seller> createSpecification(SellerCriteria criteria) {
        Specification<Seller> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = Specification.allOf(
                Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : null,
                buildRangeSpecification(criteria.getId(), Seller_.id),
                buildStringSpecification(criteria.getName(), Seller_.name),
                buildStringSpecification(criteria.getLastName(), Seller_.lastName),
                buildStringSpecification(criteria.getEmployeeCode(), Seller_.employeeCode)
            );
        }
        return specification;
    }
}
