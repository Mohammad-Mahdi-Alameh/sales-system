package com.sales.system.web.rest;

import com.sales.system.repository.TransactionLogRepository;
import com.sales.system.service.TransactionLogQueryService;
import com.sales.system.service.TransactionLogService;
import com.sales.system.service.criteria.TransactionLogCriteria;
import com.sales.system.service.dto.TransactionLogDTO;
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
 * REST controller for managing {@link com.sales.system.domain.TransactionLog}.
 */
@RestController
@RequestMapping("/api/transaction-logs")
public class TransactionLogResource {

    private static final Logger LOG = LoggerFactory.getLogger(TransactionLogResource.class);

    private static final String ENTITY_NAME = "transactionLog";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final TransactionLogService transactionLogService;

    private final TransactionLogRepository transactionLogRepository;

    private final TransactionLogQueryService transactionLogQueryService;

    public TransactionLogResource(
        TransactionLogService transactionLogService,
        TransactionLogRepository transactionLogRepository,
        TransactionLogQueryService transactionLogQueryService
    ) {
        this.transactionLogService = transactionLogService;
        this.transactionLogRepository = transactionLogRepository;
        this.transactionLogQueryService = transactionLogQueryService;
    }

    /**
     * {@code POST  /transaction-logs} : Create a new transactionLog.
     *
     * @param transactionLogDTO the transactionLogDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new transactionLogDTO, or with status {@code 400 (Bad Request)} if the transactionLog has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<TransactionLogDTO> createTransactionLog(@Valid @RequestBody TransactionLogDTO transactionLogDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save TransactionLog : {}", transactionLogDTO);
        if (transactionLogDTO.getId() != null) {
            throw new BadRequestAlertException("A new transactionLog cannot already have an ID", ENTITY_NAME, "idexists");
        }
        transactionLogDTO = transactionLogService.save(transactionLogDTO);
        return ResponseEntity.created(new URI("/api/transaction-logs/" + transactionLogDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, transactionLogDTO.getId().toString()))
            .body(transactionLogDTO);
    }

    /**
     * {@code PUT  /transaction-logs/:id} : Updates an existing transactionLog.
     *
     * @param id the id of the transactionLogDTO to save.
     * @param transactionLogDTO the transactionLogDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated transactionLogDTO,
     * or with status {@code 400 (Bad Request)} if the transactionLogDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the transactionLogDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<TransactionLogDTO> updateTransactionLog(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody TransactionLogDTO transactionLogDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update TransactionLog : {}, {}", id, transactionLogDTO);
        if (transactionLogDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, transactionLogDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!transactionLogRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        transactionLogDTO = transactionLogService.update(transactionLogDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, transactionLogDTO.getId().toString()))
            .body(transactionLogDTO);
    }

    /**
     * {@code PATCH  /transaction-logs/:id} : Partial updates given fields of an existing transactionLog, field will ignore if it is null
     *
     * @param id the id of the transactionLogDTO to save.
     * @param transactionLogDTO the transactionLogDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated transactionLogDTO,
     * or with status {@code 400 (Bad Request)} if the transactionLogDTO is not valid,
     * or with status {@code 404 (Not Found)} if the transactionLogDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the transactionLogDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<TransactionLogDTO> partialUpdateTransactionLog(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody TransactionLogDTO transactionLogDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update TransactionLog partially : {}, {}", id, transactionLogDTO);
        if (transactionLogDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, transactionLogDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!transactionLogRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<TransactionLogDTO> result = transactionLogService.partialUpdate(transactionLogDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, transactionLogDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /transaction-logs} : get all the transactionLogs.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of transactionLogs in body.
     */
    @GetMapping("")
    public ResponseEntity<List<TransactionLogDTO>> getAllTransactionLogs(
        TransactionLogCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get TransactionLogs by criteria: {}", criteria);

        Page<TransactionLogDTO> page = transactionLogQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /transaction-logs/count} : count all the transactionLogs.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countTransactionLogs(TransactionLogCriteria criteria) {
        LOG.debug("REST request to count TransactionLogs by criteria: {}", criteria);
        return ResponseEntity.ok().body(transactionLogQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /transaction-logs/:id} : get the "id" transactionLog.
     *
     * @param id the id of the transactionLogDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the transactionLogDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<TransactionLogDTO> getTransactionLog(@PathVariable("id") Long id) {
        LOG.debug("REST request to get TransactionLog : {}", id);
        Optional<TransactionLogDTO> transactionLogDTO = transactionLogService.findOne(id);
        return ResponseUtil.wrapOrNotFound(transactionLogDTO);
    }

    /**
     * {@code DELETE  /transaction-logs/:id} : delete the "id" transactionLog.
     *
     * @param id the id of the transactionLogDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTransactionLog(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete TransactionLog : {}", id);
        transactionLogService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
