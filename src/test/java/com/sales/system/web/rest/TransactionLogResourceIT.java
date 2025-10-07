package com.sales.system.web.rest;

import static com.sales.system.domain.TransactionLogAsserts.*;
import static com.sales.system.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sales.system.IntegrationTest;
import com.sales.system.domain.SaleTransaction;
import com.sales.system.domain.TransactionLog;
import com.sales.system.repository.TransactionLogRepository;
import com.sales.system.service.dto.TransactionLogDTO;
import com.sales.system.service.mapper.TransactionLogMapper;
import jakarta.persistence.EntityManager;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link TransactionLogResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class TransactionLogResourceIT {

    private static final Instant DEFAULT_TIMESTAMP = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_TIMESTAMP = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String DEFAULT_OPERATION_TYPE = "AAAAAAAAAA";
    private static final String UPDATED_OPERATION_TYPE = "BBBBBBBBBB";

    private static final String DEFAULT_FIELD_CHANGED = "AAAAAAAAAA";
    private static final String UPDATED_FIELD_CHANGED = "BBBBBBBBBB";

    private static final String DEFAULT_OLD_VALUE = "AAAAAAAAAA";
    private static final String UPDATED_OLD_VALUE = "BBBBBBBBBB";

    private static final String DEFAULT_NEW_VALUE = "AAAAAAAAAA";
    private static final String UPDATED_NEW_VALUE = "BBBBBBBBBB";

    private static final String DEFAULT_MODIFIED_BY = "AAAAAAAAAA";
    private static final String UPDATED_MODIFIED_BY = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/transaction-logs";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private TransactionLogRepository transactionLogRepository;

    @Autowired
    private TransactionLogMapper transactionLogMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restTransactionLogMockMvc;

    private TransactionLog transactionLog;

    private TransactionLog insertedTransactionLog;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TransactionLog createEntity() {
        return new TransactionLog()
            .timestamp(DEFAULT_TIMESTAMP)
            .operationType(DEFAULT_OPERATION_TYPE)
            .fieldChanged(DEFAULT_FIELD_CHANGED)
            .oldValue(DEFAULT_OLD_VALUE)
            .newValue(DEFAULT_NEW_VALUE)
            .modifiedBy(DEFAULT_MODIFIED_BY);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TransactionLog createUpdatedEntity() {
        return new TransactionLog()
            .timestamp(UPDATED_TIMESTAMP)
            .operationType(UPDATED_OPERATION_TYPE)
            .fieldChanged(UPDATED_FIELD_CHANGED)
            .oldValue(UPDATED_OLD_VALUE)
            .newValue(UPDATED_NEW_VALUE)
            .modifiedBy(UPDATED_MODIFIED_BY);
    }

    @BeforeEach
    void initTest() {
        transactionLog = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedTransactionLog != null) {
            transactionLogRepository.delete(insertedTransactionLog);
            insertedTransactionLog = null;
        }
    }

    @Test
    @Transactional
    void createTransactionLog() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the TransactionLog
        TransactionLogDTO transactionLogDTO = transactionLogMapper.toDto(transactionLog);
        var returnedTransactionLogDTO = om.readValue(
            restTransactionLogMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(transactionLogDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            TransactionLogDTO.class
        );

        // Validate the TransactionLog in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedTransactionLog = transactionLogMapper.toEntity(returnedTransactionLogDTO);
        assertTransactionLogUpdatableFieldsEquals(returnedTransactionLog, getPersistedTransactionLog(returnedTransactionLog));

        insertedTransactionLog = returnedTransactionLog;
    }

    @Test
    @Transactional
    void createTransactionLogWithExistingId() throws Exception {
        // Create the TransactionLog with an existing ID
        transactionLog.setId(1L);
        TransactionLogDTO transactionLogDTO = transactionLogMapper.toDto(transactionLog);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restTransactionLogMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(transactionLogDTO)))
            .andExpect(status().isBadRequest());

        // Validate the TransactionLog in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkTimestampIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        transactionLog.setTimestamp(null);

        // Create the TransactionLog, which fails.
        TransactionLogDTO transactionLogDTO = transactionLogMapper.toDto(transactionLog);

        restTransactionLogMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(transactionLogDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkOperationTypeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        transactionLog.setOperationType(null);

        // Create the TransactionLog, which fails.
        TransactionLogDTO transactionLogDTO = transactionLogMapper.toDto(transactionLog);

        restTransactionLogMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(transactionLogDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkFieldChangedIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        transactionLog.setFieldChanged(null);

        // Create the TransactionLog, which fails.
        TransactionLogDTO transactionLogDTO = transactionLogMapper.toDto(transactionLog);

        restTransactionLogMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(transactionLogDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkModifiedByIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        transactionLog.setModifiedBy(null);

        // Create the TransactionLog, which fails.
        TransactionLogDTO transactionLogDTO = transactionLogMapper.toDto(transactionLog);

        restTransactionLogMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(transactionLogDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllTransactionLogs() throws Exception {
        // Initialize the database
        insertedTransactionLog = transactionLogRepository.saveAndFlush(transactionLog);

        // Get all the transactionLogList
        restTransactionLogMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(transactionLog.getId().intValue())))
            .andExpect(jsonPath("$.[*].timestamp").value(hasItem(DEFAULT_TIMESTAMP.toString())))
            .andExpect(jsonPath("$.[*].operationType").value(hasItem(DEFAULT_OPERATION_TYPE)))
            .andExpect(jsonPath("$.[*].fieldChanged").value(hasItem(DEFAULT_FIELD_CHANGED)))
            .andExpect(jsonPath("$.[*].oldValue").value(hasItem(DEFAULT_OLD_VALUE)))
            .andExpect(jsonPath("$.[*].newValue").value(hasItem(DEFAULT_NEW_VALUE)))
            .andExpect(jsonPath("$.[*].modifiedBy").value(hasItem(DEFAULT_MODIFIED_BY)));
    }

    @Test
    @Transactional
    void getTransactionLog() throws Exception {
        // Initialize the database
        insertedTransactionLog = transactionLogRepository.saveAndFlush(transactionLog);

        // Get the transactionLog
        restTransactionLogMockMvc
            .perform(get(ENTITY_API_URL_ID, transactionLog.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(transactionLog.getId().intValue()))
            .andExpect(jsonPath("$.timestamp").value(DEFAULT_TIMESTAMP.toString()))
            .andExpect(jsonPath("$.operationType").value(DEFAULT_OPERATION_TYPE))
            .andExpect(jsonPath("$.fieldChanged").value(DEFAULT_FIELD_CHANGED))
            .andExpect(jsonPath("$.oldValue").value(DEFAULT_OLD_VALUE))
            .andExpect(jsonPath("$.newValue").value(DEFAULT_NEW_VALUE))
            .andExpect(jsonPath("$.modifiedBy").value(DEFAULT_MODIFIED_BY));
    }

    @Test
    @Transactional
    void getTransactionLogsByIdFiltering() throws Exception {
        // Initialize the database
        insertedTransactionLog = transactionLogRepository.saveAndFlush(transactionLog);

        Long id = transactionLog.getId();

        defaultTransactionLogFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultTransactionLogFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultTransactionLogFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllTransactionLogsByTimestampIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedTransactionLog = transactionLogRepository.saveAndFlush(transactionLog);

        // Get all the transactionLogList where timestamp equals to
        defaultTransactionLogFiltering("timestamp.equals=" + DEFAULT_TIMESTAMP, "timestamp.equals=" + UPDATED_TIMESTAMP);
    }

    @Test
    @Transactional
    void getAllTransactionLogsByTimestampIsInShouldWork() throws Exception {
        // Initialize the database
        insertedTransactionLog = transactionLogRepository.saveAndFlush(transactionLog);

        // Get all the transactionLogList where timestamp in
        defaultTransactionLogFiltering("timestamp.in=" + DEFAULT_TIMESTAMP + "," + UPDATED_TIMESTAMP, "timestamp.in=" + UPDATED_TIMESTAMP);
    }

    @Test
    @Transactional
    void getAllTransactionLogsByTimestampIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedTransactionLog = transactionLogRepository.saveAndFlush(transactionLog);

        // Get all the transactionLogList where timestamp is not null
        defaultTransactionLogFiltering("timestamp.specified=true", "timestamp.specified=false");
    }

    @Test
    @Transactional
    void getAllTransactionLogsByOperationTypeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedTransactionLog = transactionLogRepository.saveAndFlush(transactionLog);

        // Get all the transactionLogList where operationType equals to
        defaultTransactionLogFiltering("operationType.equals=" + DEFAULT_OPERATION_TYPE, "operationType.equals=" + UPDATED_OPERATION_TYPE);
    }

    @Test
    @Transactional
    void getAllTransactionLogsByOperationTypeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedTransactionLog = transactionLogRepository.saveAndFlush(transactionLog);

        // Get all the transactionLogList where operationType in
        defaultTransactionLogFiltering(
            "operationType.in=" + DEFAULT_OPERATION_TYPE + "," + UPDATED_OPERATION_TYPE,
            "operationType.in=" + UPDATED_OPERATION_TYPE
        );
    }

    @Test
    @Transactional
    void getAllTransactionLogsByOperationTypeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedTransactionLog = transactionLogRepository.saveAndFlush(transactionLog);

        // Get all the transactionLogList where operationType is not null
        defaultTransactionLogFiltering("operationType.specified=true", "operationType.specified=false");
    }

    @Test
    @Transactional
    void getAllTransactionLogsByOperationTypeContainsSomething() throws Exception {
        // Initialize the database
        insertedTransactionLog = transactionLogRepository.saveAndFlush(transactionLog);

        // Get all the transactionLogList where operationType contains
        defaultTransactionLogFiltering(
            "operationType.contains=" + DEFAULT_OPERATION_TYPE,
            "operationType.contains=" + UPDATED_OPERATION_TYPE
        );
    }

    @Test
    @Transactional
    void getAllTransactionLogsByOperationTypeNotContainsSomething() throws Exception {
        // Initialize the database
        insertedTransactionLog = transactionLogRepository.saveAndFlush(transactionLog);

        // Get all the transactionLogList where operationType does not contain
        defaultTransactionLogFiltering(
            "operationType.doesNotContain=" + UPDATED_OPERATION_TYPE,
            "operationType.doesNotContain=" + DEFAULT_OPERATION_TYPE
        );
    }

    @Test
    @Transactional
    void getAllTransactionLogsByFieldChangedIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedTransactionLog = transactionLogRepository.saveAndFlush(transactionLog);

        // Get all the transactionLogList where fieldChanged equals to
        defaultTransactionLogFiltering("fieldChanged.equals=" + DEFAULT_FIELD_CHANGED, "fieldChanged.equals=" + UPDATED_FIELD_CHANGED);
    }

    @Test
    @Transactional
    void getAllTransactionLogsByFieldChangedIsInShouldWork() throws Exception {
        // Initialize the database
        insertedTransactionLog = transactionLogRepository.saveAndFlush(transactionLog);

        // Get all the transactionLogList where fieldChanged in
        defaultTransactionLogFiltering(
            "fieldChanged.in=" + DEFAULT_FIELD_CHANGED + "," + UPDATED_FIELD_CHANGED,
            "fieldChanged.in=" + UPDATED_FIELD_CHANGED
        );
    }

    @Test
    @Transactional
    void getAllTransactionLogsByFieldChangedIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedTransactionLog = transactionLogRepository.saveAndFlush(transactionLog);

        // Get all the transactionLogList where fieldChanged is not null
        defaultTransactionLogFiltering("fieldChanged.specified=true", "fieldChanged.specified=false");
    }

    @Test
    @Transactional
    void getAllTransactionLogsByFieldChangedContainsSomething() throws Exception {
        // Initialize the database
        insertedTransactionLog = transactionLogRepository.saveAndFlush(transactionLog);

        // Get all the transactionLogList where fieldChanged contains
        defaultTransactionLogFiltering("fieldChanged.contains=" + DEFAULT_FIELD_CHANGED, "fieldChanged.contains=" + UPDATED_FIELD_CHANGED);
    }

    @Test
    @Transactional
    void getAllTransactionLogsByFieldChangedNotContainsSomething() throws Exception {
        // Initialize the database
        insertedTransactionLog = transactionLogRepository.saveAndFlush(transactionLog);

        // Get all the transactionLogList where fieldChanged does not contain
        defaultTransactionLogFiltering(
            "fieldChanged.doesNotContain=" + UPDATED_FIELD_CHANGED,
            "fieldChanged.doesNotContain=" + DEFAULT_FIELD_CHANGED
        );
    }

    @Test
    @Transactional
    void getAllTransactionLogsByOldValueIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedTransactionLog = transactionLogRepository.saveAndFlush(transactionLog);

        // Get all the transactionLogList where oldValue equals to
        defaultTransactionLogFiltering("oldValue.equals=" + DEFAULT_OLD_VALUE, "oldValue.equals=" + UPDATED_OLD_VALUE);
    }

    @Test
    @Transactional
    void getAllTransactionLogsByOldValueIsInShouldWork() throws Exception {
        // Initialize the database
        insertedTransactionLog = transactionLogRepository.saveAndFlush(transactionLog);

        // Get all the transactionLogList where oldValue in
        defaultTransactionLogFiltering("oldValue.in=" + DEFAULT_OLD_VALUE + "," + UPDATED_OLD_VALUE, "oldValue.in=" + UPDATED_OLD_VALUE);
    }

    @Test
    @Transactional
    void getAllTransactionLogsByOldValueIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedTransactionLog = transactionLogRepository.saveAndFlush(transactionLog);

        // Get all the transactionLogList where oldValue is not null
        defaultTransactionLogFiltering("oldValue.specified=true", "oldValue.specified=false");
    }

    @Test
    @Transactional
    void getAllTransactionLogsByOldValueContainsSomething() throws Exception {
        // Initialize the database
        insertedTransactionLog = transactionLogRepository.saveAndFlush(transactionLog);

        // Get all the transactionLogList where oldValue contains
        defaultTransactionLogFiltering("oldValue.contains=" + DEFAULT_OLD_VALUE, "oldValue.contains=" + UPDATED_OLD_VALUE);
    }

    @Test
    @Transactional
    void getAllTransactionLogsByOldValueNotContainsSomething() throws Exception {
        // Initialize the database
        insertedTransactionLog = transactionLogRepository.saveAndFlush(transactionLog);

        // Get all the transactionLogList where oldValue does not contain
        defaultTransactionLogFiltering("oldValue.doesNotContain=" + UPDATED_OLD_VALUE, "oldValue.doesNotContain=" + DEFAULT_OLD_VALUE);
    }

    @Test
    @Transactional
    void getAllTransactionLogsByNewValueIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedTransactionLog = transactionLogRepository.saveAndFlush(transactionLog);

        // Get all the transactionLogList where newValue equals to
        defaultTransactionLogFiltering("newValue.equals=" + DEFAULT_NEW_VALUE, "newValue.equals=" + UPDATED_NEW_VALUE);
    }

    @Test
    @Transactional
    void getAllTransactionLogsByNewValueIsInShouldWork() throws Exception {
        // Initialize the database
        insertedTransactionLog = transactionLogRepository.saveAndFlush(transactionLog);

        // Get all the transactionLogList where newValue in
        defaultTransactionLogFiltering("newValue.in=" + DEFAULT_NEW_VALUE + "," + UPDATED_NEW_VALUE, "newValue.in=" + UPDATED_NEW_VALUE);
    }

    @Test
    @Transactional
    void getAllTransactionLogsByNewValueIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedTransactionLog = transactionLogRepository.saveAndFlush(transactionLog);

        // Get all the transactionLogList where newValue is not null
        defaultTransactionLogFiltering("newValue.specified=true", "newValue.specified=false");
    }

    @Test
    @Transactional
    void getAllTransactionLogsByNewValueContainsSomething() throws Exception {
        // Initialize the database
        insertedTransactionLog = transactionLogRepository.saveAndFlush(transactionLog);

        // Get all the transactionLogList where newValue contains
        defaultTransactionLogFiltering("newValue.contains=" + DEFAULT_NEW_VALUE, "newValue.contains=" + UPDATED_NEW_VALUE);
    }

    @Test
    @Transactional
    void getAllTransactionLogsByNewValueNotContainsSomething() throws Exception {
        // Initialize the database
        insertedTransactionLog = transactionLogRepository.saveAndFlush(transactionLog);

        // Get all the transactionLogList where newValue does not contain
        defaultTransactionLogFiltering("newValue.doesNotContain=" + UPDATED_NEW_VALUE, "newValue.doesNotContain=" + DEFAULT_NEW_VALUE);
    }

    @Test
    @Transactional
    void getAllTransactionLogsByModifiedByIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedTransactionLog = transactionLogRepository.saveAndFlush(transactionLog);

        // Get all the transactionLogList where modifiedBy equals to
        defaultTransactionLogFiltering("modifiedBy.equals=" + DEFAULT_MODIFIED_BY, "modifiedBy.equals=" + UPDATED_MODIFIED_BY);
    }

    @Test
    @Transactional
    void getAllTransactionLogsByModifiedByIsInShouldWork() throws Exception {
        // Initialize the database
        insertedTransactionLog = transactionLogRepository.saveAndFlush(transactionLog);

        // Get all the transactionLogList where modifiedBy in
        defaultTransactionLogFiltering(
            "modifiedBy.in=" + DEFAULT_MODIFIED_BY + "," + UPDATED_MODIFIED_BY,
            "modifiedBy.in=" + UPDATED_MODIFIED_BY
        );
    }

    @Test
    @Transactional
    void getAllTransactionLogsByModifiedByIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedTransactionLog = transactionLogRepository.saveAndFlush(transactionLog);

        // Get all the transactionLogList where modifiedBy is not null
        defaultTransactionLogFiltering("modifiedBy.specified=true", "modifiedBy.specified=false");
    }

    @Test
    @Transactional
    void getAllTransactionLogsByModifiedByContainsSomething() throws Exception {
        // Initialize the database
        insertedTransactionLog = transactionLogRepository.saveAndFlush(transactionLog);

        // Get all the transactionLogList where modifiedBy contains
        defaultTransactionLogFiltering("modifiedBy.contains=" + DEFAULT_MODIFIED_BY, "modifiedBy.contains=" + UPDATED_MODIFIED_BY);
    }

    @Test
    @Transactional
    void getAllTransactionLogsByModifiedByNotContainsSomething() throws Exception {
        // Initialize the database
        insertedTransactionLog = transactionLogRepository.saveAndFlush(transactionLog);

        // Get all the transactionLogList where modifiedBy does not contain
        defaultTransactionLogFiltering(
            "modifiedBy.doesNotContain=" + UPDATED_MODIFIED_BY,
            "modifiedBy.doesNotContain=" + DEFAULT_MODIFIED_BY
        );
    }

    @Test
    @Transactional
    void getAllTransactionLogsBySaleTransactionIsEqualToSomething() throws Exception {
        SaleTransaction saleTransaction;
        if (TestUtil.findAll(em, SaleTransaction.class).isEmpty()) {
            transactionLogRepository.saveAndFlush(transactionLog);
            saleTransaction = SaleTransactionResourceIT.createEntity(em);
        } else {
            saleTransaction = TestUtil.findAll(em, SaleTransaction.class).get(0);
        }
        em.persist(saleTransaction);
        em.flush();
        transactionLog.setSaleTransaction(saleTransaction);
        transactionLogRepository.saveAndFlush(transactionLog);
        Long saleTransactionId = saleTransaction.getId();
        // Get all the transactionLogList where saleTransaction equals to saleTransactionId
        defaultTransactionLogShouldBeFound("saleTransactionId.equals=" + saleTransactionId);

        // Get all the transactionLogList where saleTransaction equals to (saleTransactionId + 1)
        defaultTransactionLogShouldNotBeFound("saleTransactionId.equals=" + (saleTransactionId + 1));
    }

    private void defaultTransactionLogFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultTransactionLogShouldBeFound(shouldBeFound);
        defaultTransactionLogShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultTransactionLogShouldBeFound(String filter) throws Exception {
        restTransactionLogMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(transactionLog.getId().intValue())))
            .andExpect(jsonPath("$.[*].timestamp").value(hasItem(DEFAULT_TIMESTAMP.toString())))
            .andExpect(jsonPath("$.[*].operationType").value(hasItem(DEFAULT_OPERATION_TYPE)))
            .andExpect(jsonPath("$.[*].fieldChanged").value(hasItem(DEFAULT_FIELD_CHANGED)))
            .andExpect(jsonPath("$.[*].oldValue").value(hasItem(DEFAULT_OLD_VALUE)))
            .andExpect(jsonPath("$.[*].newValue").value(hasItem(DEFAULT_NEW_VALUE)))
            .andExpect(jsonPath("$.[*].modifiedBy").value(hasItem(DEFAULT_MODIFIED_BY)));

        // Check, that the count call also returns 1
        restTransactionLogMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultTransactionLogShouldNotBeFound(String filter) throws Exception {
        restTransactionLogMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restTransactionLogMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingTransactionLog() throws Exception {
        // Get the transactionLog
        restTransactionLogMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingTransactionLog() throws Exception {
        // Initialize the database
        insertedTransactionLog = transactionLogRepository.saveAndFlush(transactionLog);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the transactionLog
        TransactionLog updatedTransactionLog = transactionLogRepository.findById(transactionLog.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedTransactionLog are not directly saved in db
        em.detach(updatedTransactionLog);
        updatedTransactionLog
            .timestamp(UPDATED_TIMESTAMP)
            .operationType(UPDATED_OPERATION_TYPE)
            .fieldChanged(UPDATED_FIELD_CHANGED)
            .oldValue(UPDATED_OLD_VALUE)
            .newValue(UPDATED_NEW_VALUE)
            .modifiedBy(UPDATED_MODIFIED_BY);
        TransactionLogDTO transactionLogDTO = transactionLogMapper.toDto(updatedTransactionLog);

        restTransactionLogMockMvc
            .perform(
                put(ENTITY_API_URL_ID, transactionLogDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(transactionLogDTO))
            )
            .andExpect(status().isOk());

        // Validate the TransactionLog in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedTransactionLogToMatchAllProperties(updatedTransactionLog);
    }

    @Test
    @Transactional
    void putNonExistingTransactionLog() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        transactionLog.setId(longCount.incrementAndGet());

        // Create the TransactionLog
        TransactionLogDTO transactionLogDTO = transactionLogMapper.toDto(transactionLog);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTransactionLogMockMvc
            .perform(
                put(ENTITY_API_URL_ID, transactionLogDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(transactionLogDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TransactionLog in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchTransactionLog() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        transactionLog.setId(longCount.incrementAndGet());

        // Create the TransactionLog
        TransactionLogDTO transactionLogDTO = transactionLogMapper.toDto(transactionLog);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTransactionLogMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(transactionLogDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TransactionLog in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamTransactionLog() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        transactionLog.setId(longCount.incrementAndGet());

        // Create the TransactionLog
        TransactionLogDTO transactionLogDTO = transactionLogMapper.toDto(transactionLog);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTransactionLogMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(transactionLogDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the TransactionLog in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateTransactionLogWithPatch() throws Exception {
        // Initialize the database
        insertedTransactionLog = transactionLogRepository.saveAndFlush(transactionLog);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the transactionLog using partial update
        TransactionLog partialUpdatedTransactionLog = new TransactionLog();
        partialUpdatedTransactionLog.setId(transactionLog.getId());

        partialUpdatedTransactionLog.operationType(UPDATED_OPERATION_TYPE).oldValue(UPDATED_OLD_VALUE).modifiedBy(UPDATED_MODIFIED_BY);

        restTransactionLogMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTransactionLog.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedTransactionLog))
            )
            .andExpect(status().isOk());

        // Validate the TransactionLog in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertTransactionLogUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedTransactionLog, transactionLog),
            getPersistedTransactionLog(transactionLog)
        );
    }

    @Test
    @Transactional
    void fullUpdateTransactionLogWithPatch() throws Exception {
        // Initialize the database
        insertedTransactionLog = transactionLogRepository.saveAndFlush(transactionLog);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the transactionLog using partial update
        TransactionLog partialUpdatedTransactionLog = new TransactionLog();
        partialUpdatedTransactionLog.setId(transactionLog.getId());

        partialUpdatedTransactionLog
            .timestamp(UPDATED_TIMESTAMP)
            .operationType(UPDATED_OPERATION_TYPE)
            .fieldChanged(UPDATED_FIELD_CHANGED)
            .oldValue(UPDATED_OLD_VALUE)
            .newValue(UPDATED_NEW_VALUE)
            .modifiedBy(UPDATED_MODIFIED_BY);

        restTransactionLogMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTransactionLog.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedTransactionLog))
            )
            .andExpect(status().isOk());

        // Validate the TransactionLog in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertTransactionLogUpdatableFieldsEquals(partialUpdatedTransactionLog, getPersistedTransactionLog(partialUpdatedTransactionLog));
    }

    @Test
    @Transactional
    void patchNonExistingTransactionLog() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        transactionLog.setId(longCount.incrementAndGet());

        // Create the TransactionLog
        TransactionLogDTO transactionLogDTO = transactionLogMapper.toDto(transactionLog);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTransactionLogMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, transactionLogDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(transactionLogDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TransactionLog in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchTransactionLog() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        transactionLog.setId(longCount.incrementAndGet());

        // Create the TransactionLog
        TransactionLogDTO transactionLogDTO = transactionLogMapper.toDto(transactionLog);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTransactionLogMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(transactionLogDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TransactionLog in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamTransactionLog() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        transactionLog.setId(longCount.incrementAndGet());

        // Create the TransactionLog
        TransactionLogDTO transactionLogDTO = transactionLogMapper.toDto(transactionLog);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTransactionLogMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(transactionLogDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the TransactionLog in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteTransactionLog() throws Exception {
        // Initialize the database
        insertedTransactionLog = transactionLogRepository.saveAndFlush(transactionLog);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the transactionLog
        restTransactionLogMockMvc
            .perform(delete(ENTITY_API_URL_ID, transactionLog.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return transactionLogRepository.count();
    }

    protected void assertIncrementedRepositoryCount(long countBefore) {
        assertThat(countBefore + 1).isEqualTo(getRepositoryCount());
    }

    protected void assertDecrementedRepositoryCount(long countBefore) {
        assertThat(countBefore - 1).isEqualTo(getRepositoryCount());
    }

    protected void assertSameRepositoryCount(long countBefore) {
        assertThat(countBefore).isEqualTo(getRepositoryCount());
    }

    protected TransactionLog getPersistedTransactionLog(TransactionLog transactionLog) {
        return transactionLogRepository.findById(transactionLog.getId()).orElseThrow();
    }

    protected void assertPersistedTransactionLogToMatchAllProperties(TransactionLog expectedTransactionLog) {
        assertTransactionLogAllPropertiesEquals(expectedTransactionLog, getPersistedTransactionLog(expectedTransactionLog));
    }

    protected void assertPersistedTransactionLogToMatchUpdatableProperties(TransactionLog expectedTransactionLog) {
        assertTransactionLogAllUpdatablePropertiesEquals(expectedTransactionLog, getPersistedTransactionLog(expectedTransactionLog));
    }
}
