package com.sales.system.web.rest;

import com.sales.system.repository.SaleTransactionRepository;
import com.sales.system.service.SaleTransactionService;
import com.sales.system.service.dto.SaleTransactionDTO;
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
 * REST controller for managing {@link com.sales.system.domain.SaleTransaction}.
 */
@RestController
@RequestMapping("/api/sale-transactions")
public class SaleTransactionResource {

    private static final Logger LOG = LoggerFactory.getLogger(SaleTransactionResource.class);

    private static final String ENTITY_NAME = "saleTransaction";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final SaleTransactionService saleTransactionService;

    private final SaleTransactionRepository saleTransactionRepository;

    public SaleTransactionResource(SaleTransactionService saleTransactionService, SaleTransactionRepository saleTransactionRepository) {
        this.saleTransactionService = saleTransactionService;
        this.saleTransactionRepository = saleTransactionRepository;
    }

    /**
     * {@code POST  /sale-transactions} : Create a new saleTransaction.
     *
     * @param saleTransactionDTO the saleTransactionDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new saleTransactionDTO, or with status {@code 400 (Bad Request)} if the saleTransaction has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<SaleTransactionDTO> createSaleTransaction(@Valid @RequestBody SaleTransactionDTO saleTransactionDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save SaleTransaction : {}", saleTransactionDTO);
        if (saleTransactionDTO.getId() != null) {
            throw new BadRequestAlertException("A new saleTransaction cannot already have an ID", ENTITY_NAME, "idexists");
        }
        saleTransactionDTO = saleTransactionService.save(saleTransactionDTO);
        return ResponseEntity.created(new URI("/api/sale-transactions/" + saleTransactionDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, saleTransactionDTO.getId().toString()))
            .body(saleTransactionDTO);
    }

    /**
     * {@code PUT  /sale-transactions/:id} : Updates an existing saleTransaction.
     *
     * @param id the id of the saleTransactionDTO to save.
     * @param saleTransactionDTO the saleTransactionDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated saleTransactionDTO,
     * or with status {@code 400 (Bad Request)} if the saleTransactionDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the saleTransactionDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<SaleTransactionDTO> updateSaleTransaction(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody SaleTransactionDTO saleTransactionDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update SaleTransaction : {}, {}", id, saleTransactionDTO);
        if (saleTransactionDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, saleTransactionDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!saleTransactionRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        saleTransactionDTO = saleTransactionService.update(saleTransactionDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, saleTransactionDTO.getId().toString()))
            .body(saleTransactionDTO);
    }

    /**
     * {@code PATCH  /sale-transactions/:id} : Partial updates given fields of an existing saleTransaction, field will ignore if it is null
     *
     * @param id the id of the saleTransactionDTO to save.
     * @param saleTransactionDTO the saleTransactionDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated saleTransactionDTO,
     * or with status {@code 400 (Bad Request)} if the saleTransactionDTO is not valid,
     * or with status {@code 404 (Not Found)} if the saleTransactionDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the saleTransactionDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<SaleTransactionDTO> partialUpdateSaleTransaction(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody SaleTransactionDTO saleTransactionDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update SaleTransaction partially : {}, {}", id, saleTransactionDTO);
        if (saleTransactionDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, saleTransactionDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!saleTransactionRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<SaleTransactionDTO> result = saleTransactionService.partialUpdate(saleTransactionDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, saleTransactionDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /sale-transactions} : get all the saleTransactions.
     *
     * @param pageable the pagination information.
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of saleTransactions in body.
     */
    @GetMapping("")
    public ResponseEntity<List<SaleTransactionDTO>> getAllSaleTransactions(
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        @RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload
    ) {
        LOG.debug("REST request to get a page of SaleTransactions");
        Page<SaleTransactionDTO> page;
        if (eagerload) {
            page = saleTransactionService.findAllWithEagerRelationships(pageable);
        } else {
            page = saleTransactionService.findAll(pageable);
        }
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /sale-transactions/:id} : get the "id" saleTransaction.
     *
     * @param id the id of the saleTransactionDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the saleTransactionDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<SaleTransactionDTO> getSaleTransaction(@PathVariable("id") Long id) {
        LOG.debug("REST request to get SaleTransaction : {}", id);
        Optional<SaleTransactionDTO> saleTransactionDTO = saleTransactionService.findOne(id);
        return ResponseUtil.wrapOrNotFound(saleTransactionDTO);
    }

    /**
     * {@code DELETE  /sale-transactions/:id} : delete the "id" saleTransaction.
     *
     * @param id the id of the saleTransactionDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSaleTransaction(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete SaleTransaction : {}", id);
        saleTransactionService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
