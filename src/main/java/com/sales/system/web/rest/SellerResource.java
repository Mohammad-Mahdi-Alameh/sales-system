package com.sales.system.web.rest;

import com.sales.system.repository.SellerRepository;
import com.sales.system.service.SellerQueryService;
import com.sales.system.service.SellerService;
import com.sales.system.service.criteria.SellerCriteria;
import com.sales.system.service.dto.SellerDTO;
import com.sales.system.web.rest.errors.BadRequestAlertException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.sales.system.domain.Seller}.
 */
@RestController
@RequestMapping("/api/sellers")
public class SellerResource {

    private static final Logger LOG = LoggerFactory.getLogger(SellerResource.class);

    private static final String ENTITY_NAME = "seller";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final SellerService sellerService;

    private final SellerRepository sellerRepository;

    private final SellerQueryService sellerQueryService;

    public SellerResource(SellerService sellerService, SellerRepository sellerRepository, SellerQueryService sellerQueryService) {
        this.sellerService = sellerService;
        this.sellerRepository = sellerRepository;
        this.sellerQueryService = sellerQueryService;
    }

    /**
     * {@code POST  /sellers} : Create a new seller.
     *
     * @param sellerDTO the sellerDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new sellerDTO, or with status {@code 400 (Bad Request)} if the seller has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<SellerDTO> createSeller(@Valid @RequestBody SellerDTO sellerDTO) throws URISyntaxException {
        LOG.debug("REST request to save Seller : {}", sellerDTO);
        if (sellerDTO.getId() != null) {
            throw new BadRequestAlertException("A new seller cannot already have an ID", ENTITY_NAME, "idexists");
        }
        sellerDTO = sellerService.save(sellerDTO);
        return ResponseEntity.created(new URI("/api/sellers/" + sellerDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, sellerDTO.getId().toString()))
            .body(sellerDTO);
    }

    /**
     * {@code PUT  /sellers/:id} : Updates an existing seller.
     *
     * @param id the id of the sellerDTO to save.
     * @param sellerDTO the sellerDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated sellerDTO,
     * or with status {@code 400 (Bad Request)} if the sellerDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the sellerDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<SellerDTO> updateSeller(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody SellerDTO sellerDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update Seller : {}, {}", id, sellerDTO);
        if (sellerDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, sellerDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!sellerRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        sellerDTO = sellerService.update(sellerDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, sellerDTO.getId().toString()))
            .body(sellerDTO);
    }

    /**
     * {@code PATCH  /sellers/:id} : Partial updates given fields of an existing seller, field will ignore if it is null
     *
     * @param id the id of the sellerDTO to save.
     * @param sellerDTO the sellerDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated sellerDTO,
     * or with status {@code 400 (Bad Request)} if the sellerDTO is not valid,
     * or with status {@code 404 (Not Found)} if the sellerDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the sellerDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<SellerDTO> partialUpdateSeller(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody SellerDTO sellerDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Seller partially : {}, {}", id, sellerDTO);
        if (sellerDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, sellerDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!sellerRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<SellerDTO> result = sellerService.partialUpdate(sellerDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, sellerDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /sellers} : get all the sellers.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of sellers in body.
     */
    @GetMapping("")
    public ResponseEntity<List<SellerDTO>> getAllSellers(
        SellerCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get Sellers by criteria: {}", criteria);

        Page<SellerDTO> page = sellerQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /sellers/count} : count all the sellers.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countSellers(SellerCriteria criteria) {
        LOG.debug("REST request to count Sellers by criteria: {}", criteria);
        return ResponseEntity.ok().body(sellerQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /sellers/:id} : get the "id" seller.
     *
     * @param id the id of the sellerDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the sellerDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<SellerDTO> getSeller(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Seller : {}", id);
        Optional<SellerDTO> sellerDTO = sellerService.findOne(id);
        return ResponseUtil.wrapOrNotFound(sellerDTO);
    }

    /**
     * {@code DELETE  /sellers/:id} : delete the "id" seller.
     *
     * @param id the id of the sellerDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSeller(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Seller : {}", id);
        sellerService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
