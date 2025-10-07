package com.sales.system.web.rest;

import static com.sales.system.domain.SaleTransactionAsserts.*;
import static com.sales.system.web.rest.TestUtil.createUpdateProxyForBean;
import static com.sales.system.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sales.system.IntegrationTest;
import com.sales.system.domain.Product;
import com.sales.system.domain.Sale;
import com.sales.system.domain.SaleTransaction;
import com.sales.system.repository.SaleTransactionRepository;
import com.sales.system.service.SaleTransactionService;
import com.sales.system.service.dto.SaleTransactionDTO;
import com.sales.system.service.mapper.SaleTransactionMapper;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link SaleTransactionResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class SaleTransactionResourceIT {

    private static final Integer DEFAULT_QUANTITY = 1;
    private static final Integer UPDATED_QUANTITY = 2;

    private static final BigDecimal DEFAULT_UNIT_PRICE = new BigDecimal(0);
    private static final BigDecimal UPDATED_UNIT_PRICE = new BigDecimal(1);

    private static final BigDecimal DEFAULT_SUBTOTAL = new BigDecimal(0);
    private static final BigDecimal UPDATED_SUBTOTAL = new BigDecimal(1);

    private static final String ENTITY_API_URL = "/api/sale-transactions";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private SaleTransactionRepository saleTransactionRepository;

    @Mock
    private SaleTransactionRepository saleTransactionRepositoryMock;

    @Autowired
    private SaleTransactionMapper saleTransactionMapper;

    @Mock
    private SaleTransactionService saleTransactionServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restSaleTransactionMockMvc;

    private SaleTransaction saleTransaction;

    private SaleTransaction insertedSaleTransaction;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static SaleTransaction createEntity(EntityManager em) {
        SaleTransaction saleTransaction = new SaleTransaction()
            .quantity(DEFAULT_QUANTITY)
            .unitPrice(DEFAULT_UNIT_PRICE)
            .subtotal(DEFAULT_SUBTOTAL);
        // Add required entity
        Product product;
        if (TestUtil.findAll(em, Product.class).isEmpty()) {
            product = ProductResourceIT.createEntity();
            em.persist(product);
            em.flush();
        } else {
            product = TestUtil.findAll(em, Product.class).get(0);
        }
        saleTransaction.setProduct(product);
        // Add required entity
        Sale sale;
        if (TestUtil.findAll(em, Sale.class).isEmpty()) {
            sale = SaleResourceIT.createEntity(em);
            em.persist(sale);
            em.flush();
        } else {
            sale = TestUtil.findAll(em, Sale.class).get(0);
        }
        saleTransaction.setSale(sale);
        return saleTransaction;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static SaleTransaction createUpdatedEntity(EntityManager em) {
        SaleTransaction updatedSaleTransaction = new SaleTransaction()
            .quantity(UPDATED_QUANTITY)
            .unitPrice(UPDATED_UNIT_PRICE)
            .subtotal(UPDATED_SUBTOTAL);
        // Add required entity
        Product product;
        if (TestUtil.findAll(em, Product.class).isEmpty()) {
            product = ProductResourceIT.createUpdatedEntity();
            em.persist(product);
            em.flush();
        } else {
            product = TestUtil.findAll(em, Product.class).get(0);
        }
        updatedSaleTransaction.setProduct(product);
        // Add required entity
        Sale sale;
        if (TestUtil.findAll(em, Sale.class).isEmpty()) {
            sale = SaleResourceIT.createUpdatedEntity(em);
            em.persist(sale);
            em.flush();
        } else {
            sale = TestUtil.findAll(em, Sale.class).get(0);
        }
        updatedSaleTransaction.setSale(sale);
        return updatedSaleTransaction;
    }

    @BeforeEach
    void initTest() {
        saleTransaction = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedSaleTransaction != null) {
            saleTransactionRepository.delete(insertedSaleTransaction);
            insertedSaleTransaction = null;
        }
    }

    @Test
    @Transactional
    void createSaleTransaction() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the SaleTransaction
        SaleTransactionDTO saleTransactionDTO = saleTransactionMapper.toDto(saleTransaction);
        var returnedSaleTransactionDTO = om.readValue(
            restSaleTransactionMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(saleTransactionDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            SaleTransactionDTO.class
        );

        // Validate the SaleTransaction in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedSaleTransaction = saleTransactionMapper.toEntity(returnedSaleTransactionDTO);
        assertSaleTransactionUpdatableFieldsEquals(returnedSaleTransaction, getPersistedSaleTransaction(returnedSaleTransaction));

        insertedSaleTransaction = returnedSaleTransaction;
    }

    @Test
    @Transactional
    void createSaleTransactionWithExistingId() throws Exception {
        // Create the SaleTransaction with an existing ID
        saleTransaction.setId(1L);
        SaleTransactionDTO saleTransactionDTO = saleTransactionMapper.toDto(saleTransaction);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restSaleTransactionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(saleTransactionDTO)))
            .andExpect(status().isBadRequest());

        // Validate the SaleTransaction in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkQuantityIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        saleTransaction.setQuantity(null);

        // Create the SaleTransaction, which fails.
        SaleTransactionDTO saleTransactionDTO = saleTransactionMapper.toDto(saleTransaction);

        restSaleTransactionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(saleTransactionDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkUnitPriceIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        saleTransaction.setUnitPrice(null);

        // Create the SaleTransaction, which fails.
        SaleTransactionDTO saleTransactionDTO = saleTransactionMapper.toDto(saleTransaction);

        restSaleTransactionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(saleTransactionDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkSubtotalIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        saleTransaction.setSubtotal(null);

        // Create the SaleTransaction, which fails.
        SaleTransactionDTO saleTransactionDTO = saleTransactionMapper.toDto(saleTransaction);

        restSaleTransactionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(saleTransactionDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllSaleTransactions() throws Exception {
        // Initialize the database
        insertedSaleTransaction = saleTransactionRepository.saveAndFlush(saleTransaction);

        // Get all the saleTransactionList
        restSaleTransactionMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(saleTransaction.getId().intValue())))
            .andExpect(jsonPath("$.[*].quantity").value(hasItem(DEFAULT_QUANTITY)))
            .andExpect(jsonPath("$.[*].unitPrice").value(hasItem(sameNumber(DEFAULT_UNIT_PRICE))))
            .andExpect(jsonPath("$.[*].subtotal").value(hasItem(sameNumber(DEFAULT_SUBTOTAL))));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllSaleTransactionsWithEagerRelationshipsIsEnabled() throws Exception {
        when(saleTransactionServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restSaleTransactionMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(saleTransactionServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllSaleTransactionsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(saleTransactionServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restSaleTransactionMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(saleTransactionRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getSaleTransaction() throws Exception {
        // Initialize the database
        insertedSaleTransaction = saleTransactionRepository.saveAndFlush(saleTransaction);

        // Get the saleTransaction
        restSaleTransactionMockMvc
            .perform(get(ENTITY_API_URL_ID, saleTransaction.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(saleTransaction.getId().intValue()))
            .andExpect(jsonPath("$.quantity").value(DEFAULT_QUANTITY))
            .andExpect(jsonPath("$.unitPrice").value(sameNumber(DEFAULT_UNIT_PRICE)))
            .andExpect(jsonPath("$.subtotal").value(sameNumber(DEFAULT_SUBTOTAL)));
    }

    @Test
    @Transactional
    void getNonExistingSaleTransaction() throws Exception {
        // Get the saleTransaction
        restSaleTransactionMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingSaleTransaction() throws Exception {
        // Initialize the database
        insertedSaleTransaction = saleTransactionRepository.saveAndFlush(saleTransaction);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the saleTransaction
        SaleTransaction updatedSaleTransaction = saleTransactionRepository.findById(saleTransaction.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedSaleTransaction are not directly saved in db
        em.detach(updatedSaleTransaction);
        updatedSaleTransaction.quantity(UPDATED_QUANTITY).unitPrice(UPDATED_UNIT_PRICE).subtotal(UPDATED_SUBTOTAL);
        SaleTransactionDTO saleTransactionDTO = saleTransactionMapper.toDto(updatedSaleTransaction);

        restSaleTransactionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, saleTransactionDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(saleTransactionDTO))
            )
            .andExpect(status().isOk());

        // Validate the SaleTransaction in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedSaleTransactionToMatchAllProperties(updatedSaleTransaction);
    }

    @Test
    @Transactional
    void putNonExistingSaleTransaction() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        saleTransaction.setId(longCount.incrementAndGet());

        // Create the SaleTransaction
        SaleTransactionDTO saleTransactionDTO = saleTransactionMapper.toDto(saleTransaction);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSaleTransactionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, saleTransactionDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(saleTransactionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the SaleTransaction in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchSaleTransaction() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        saleTransaction.setId(longCount.incrementAndGet());

        // Create the SaleTransaction
        SaleTransactionDTO saleTransactionDTO = saleTransactionMapper.toDto(saleTransaction);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSaleTransactionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(saleTransactionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the SaleTransaction in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamSaleTransaction() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        saleTransaction.setId(longCount.incrementAndGet());

        // Create the SaleTransaction
        SaleTransactionDTO saleTransactionDTO = saleTransactionMapper.toDto(saleTransaction);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSaleTransactionMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(saleTransactionDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the SaleTransaction in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateSaleTransactionWithPatch() throws Exception {
        // Initialize the database
        insertedSaleTransaction = saleTransactionRepository.saveAndFlush(saleTransaction);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the saleTransaction using partial update
        SaleTransaction partialUpdatedSaleTransaction = new SaleTransaction();
        partialUpdatedSaleTransaction.setId(saleTransaction.getId());

        partialUpdatedSaleTransaction.quantity(UPDATED_QUANTITY);

        restSaleTransactionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSaleTransaction.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedSaleTransaction))
            )
            .andExpect(status().isOk());

        // Validate the SaleTransaction in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertSaleTransactionUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedSaleTransaction, saleTransaction),
            getPersistedSaleTransaction(saleTransaction)
        );
    }

    @Test
    @Transactional
    void fullUpdateSaleTransactionWithPatch() throws Exception {
        // Initialize the database
        insertedSaleTransaction = saleTransactionRepository.saveAndFlush(saleTransaction);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the saleTransaction using partial update
        SaleTransaction partialUpdatedSaleTransaction = new SaleTransaction();
        partialUpdatedSaleTransaction.setId(saleTransaction.getId());

        partialUpdatedSaleTransaction.quantity(UPDATED_QUANTITY).unitPrice(UPDATED_UNIT_PRICE).subtotal(UPDATED_SUBTOTAL);

        restSaleTransactionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSaleTransaction.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedSaleTransaction))
            )
            .andExpect(status().isOk());

        // Validate the SaleTransaction in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertSaleTransactionUpdatableFieldsEquals(
            partialUpdatedSaleTransaction,
            getPersistedSaleTransaction(partialUpdatedSaleTransaction)
        );
    }

    @Test
    @Transactional
    void patchNonExistingSaleTransaction() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        saleTransaction.setId(longCount.incrementAndGet());

        // Create the SaleTransaction
        SaleTransactionDTO saleTransactionDTO = saleTransactionMapper.toDto(saleTransaction);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSaleTransactionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, saleTransactionDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(saleTransactionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the SaleTransaction in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchSaleTransaction() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        saleTransaction.setId(longCount.incrementAndGet());

        // Create the SaleTransaction
        SaleTransactionDTO saleTransactionDTO = saleTransactionMapper.toDto(saleTransaction);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSaleTransactionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(saleTransactionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the SaleTransaction in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamSaleTransaction() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        saleTransaction.setId(longCount.incrementAndGet());

        // Create the SaleTransaction
        SaleTransactionDTO saleTransactionDTO = saleTransactionMapper.toDto(saleTransaction);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSaleTransactionMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(saleTransactionDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the SaleTransaction in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteSaleTransaction() throws Exception {
        // Initialize the database
        insertedSaleTransaction = saleTransactionRepository.saveAndFlush(saleTransaction);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the saleTransaction
        restSaleTransactionMockMvc
            .perform(delete(ENTITY_API_URL_ID, saleTransaction.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return saleTransactionRepository.count();
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

    protected SaleTransaction getPersistedSaleTransaction(SaleTransaction saleTransaction) {
        return saleTransactionRepository.findById(saleTransaction.getId()).orElseThrow();
    }

    protected void assertPersistedSaleTransactionToMatchAllProperties(SaleTransaction expectedSaleTransaction) {
        assertSaleTransactionAllPropertiesEquals(expectedSaleTransaction, getPersistedSaleTransaction(expectedSaleTransaction));
    }

    protected void assertPersistedSaleTransactionToMatchUpdatableProperties(SaleTransaction expectedSaleTransaction) {
        assertSaleTransactionAllUpdatablePropertiesEquals(expectedSaleTransaction, getPersistedSaleTransaction(expectedSaleTransaction));
    }
}
