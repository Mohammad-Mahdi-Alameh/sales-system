package com.sales.system.web.rest;

import static com.sales.system.domain.SaleAsserts.*;
import static com.sales.system.web.rest.TestUtil.createUpdateProxyForBean;
import static com.sales.system.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sales.system.IntegrationTest;
import com.sales.system.domain.Client;
import com.sales.system.domain.Sale;
import com.sales.system.domain.Seller;
import com.sales.system.repository.SaleRepository;
import com.sales.system.service.SaleService;
import com.sales.system.service.dto.SaleDTO;
import com.sales.system.service.mapper.SaleMapper;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
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
 * Integration tests for the {@link SaleResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class SaleResourceIT {

    private static final Instant DEFAULT_CREATION_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATION_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final BigDecimal DEFAULT_TOTAL = new BigDecimal(0);
    private static final BigDecimal UPDATED_TOTAL = new BigDecimal(1);
    private static final BigDecimal SMALLER_TOTAL = new BigDecimal(0 - 1);

    private static final String ENTITY_API_URL = "/api/sales";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private SaleRepository saleRepository;

    @Mock
    private SaleRepository saleRepositoryMock;

    @Autowired
    private SaleMapper saleMapper;

    @Mock
    private SaleService saleServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restSaleMockMvc;

    private Sale sale;

    private Sale insertedSale;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Sale createEntity(EntityManager em) {
        Sale sale = new Sale().creationDate(DEFAULT_CREATION_DATE).total(DEFAULT_TOTAL);
        // Add required entity
        Client client;
        if (TestUtil.findAll(em, Client.class).isEmpty()) {
            client = ClientResourceIT.createEntity();
            em.persist(client);
            em.flush();
        } else {
            client = TestUtil.findAll(em, Client.class).get(0);
        }
        sale.setClient(client);
        // Add required entity
        Seller seller;
        if (TestUtil.findAll(em, Seller.class).isEmpty()) {
            seller = SellerResourceIT.createEntity();
            em.persist(seller);
            em.flush();
        } else {
            seller = TestUtil.findAll(em, Seller.class).get(0);
        }
        sale.setSeller(seller);
        return sale;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Sale createUpdatedEntity(EntityManager em) {
        Sale updatedSale = new Sale().creationDate(UPDATED_CREATION_DATE).total(UPDATED_TOTAL);
        // Add required entity
        Client client;
        if (TestUtil.findAll(em, Client.class).isEmpty()) {
            client = ClientResourceIT.createUpdatedEntity();
            em.persist(client);
            em.flush();
        } else {
            client = TestUtil.findAll(em, Client.class).get(0);
        }
        updatedSale.setClient(client);
        // Add required entity
        Seller seller;
        if (TestUtil.findAll(em, Seller.class).isEmpty()) {
            seller = SellerResourceIT.createUpdatedEntity();
            em.persist(seller);
            em.flush();
        } else {
            seller = TestUtil.findAll(em, Seller.class).get(0);
        }
        updatedSale.setSeller(seller);
        return updatedSale;
    }

    @BeforeEach
    void initTest() {
        sale = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedSale != null) {
            saleRepository.delete(insertedSale);
            insertedSale = null;
        }
    }

    @Test
    @Transactional
    void createSale() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Sale
        SaleDTO saleDTO = saleMapper.toDto(sale);
        var returnedSaleDTO = om.readValue(
            restSaleMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(saleDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            SaleDTO.class
        );

        // Validate the Sale in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedSale = saleMapper.toEntity(returnedSaleDTO);
        assertSaleUpdatableFieldsEquals(returnedSale, getPersistedSale(returnedSale));

        insertedSale = returnedSale;
    }

    @Test
    @Transactional
    void createSaleWithExistingId() throws Exception {
        // Create the Sale with an existing ID
        sale.setId(1L);
        SaleDTO saleDTO = saleMapper.toDto(sale);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restSaleMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(saleDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Sale in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkCreationDateIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        sale.setCreationDate(null);

        // Create the Sale, which fails.
        SaleDTO saleDTO = saleMapper.toDto(sale);

        restSaleMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(saleDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkTotalIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        sale.setTotal(null);

        // Create the Sale, which fails.
        SaleDTO saleDTO = saleMapper.toDto(sale);

        restSaleMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(saleDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllSales() throws Exception {
        // Initialize the database
        insertedSale = saleRepository.saveAndFlush(sale);

        // Get all the saleList
        restSaleMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(sale.getId().intValue())))
            .andExpect(jsonPath("$.[*].creationDate").value(hasItem(DEFAULT_CREATION_DATE.toString())))
            .andExpect(jsonPath("$.[*].total").value(hasItem(sameNumber(DEFAULT_TOTAL))));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllSalesWithEagerRelationshipsIsEnabled() throws Exception {
        when(saleServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restSaleMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(saleServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllSalesWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(saleServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restSaleMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(saleRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getSale() throws Exception {
        // Initialize the database
        insertedSale = saleRepository.saveAndFlush(sale);

        // Get the sale
        restSaleMockMvc
            .perform(get(ENTITY_API_URL_ID, sale.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(sale.getId().intValue()))
            .andExpect(jsonPath("$.creationDate").value(DEFAULT_CREATION_DATE.toString()))
            .andExpect(jsonPath("$.total").value(sameNumber(DEFAULT_TOTAL)));
    }

    @Test
    @Transactional
    void getSalesByIdFiltering() throws Exception {
        // Initialize the database
        insertedSale = saleRepository.saveAndFlush(sale);

        Long id = sale.getId();

        defaultSaleFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultSaleFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultSaleFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllSalesByCreationDateIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedSale = saleRepository.saveAndFlush(sale);

        // Get all the saleList where creationDate equals to
        defaultSaleFiltering("creationDate.equals=" + DEFAULT_CREATION_DATE, "creationDate.equals=" + UPDATED_CREATION_DATE);
    }

    @Test
    @Transactional
    void getAllSalesByCreationDateIsInShouldWork() throws Exception {
        // Initialize the database
        insertedSale = saleRepository.saveAndFlush(sale);

        // Get all the saleList where creationDate in
        defaultSaleFiltering(
            "creationDate.in=" + DEFAULT_CREATION_DATE + "," + UPDATED_CREATION_DATE,
            "creationDate.in=" + UPDATED_CREATION_DATE
        );
    }

    @Test
    @Transactional
    void getAllSalesByCreationDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedSale = saleRepository.saveAndFlush(sale);

        // Get all the saleList where creationDate is not null
        defaultSaleFiltering("creationDate.specified=true", "creationDate.specified=false");
    }

    @Test
    @Transactional
    void getAllSalesByTotalIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedSale = saleRepository.saveAndFlush(sale);

        // Get all the saleList where total equals to
        defaultSaleFiltering("total.equals=" + DEFAULT_TOTAL, "total.equals=" + UPDATED_TOTAL);
    }

    @Test
    @Transactional
    void getAllSalesByTotalIsInShouldWork() throws Exception {
        // Initialize the database
        insertedSale = saleRepository.saveAndFlush(sale);

        // Get all the saleList where total in
        defaultSaleFiltering("total.in=" + DEFAULT_TOTAL + "," + UPDATED_TOTAL, "total.in=" + UPDATED_TOTAL);
    }

    @Test
    @Transactional
    void getAllSalesByTotalIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedSale = saleRepository.saveAndFlush(sale);

        // Get all the saleList where total is not null
        defaultSaleFiltering("total.specified=true", "total.specified=false");
    }

    @Test
    @Transactional
    void getAllSalesByTotalIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedSale = saleRepository.saveAndFlush(sale);

        // Get all the saleList where total is greater than or equal to
        defaultSaleFiltering("total.greaterThanOrEqual=" + DEFAULT_TOTAL, "total.greaterThanOrEqual=" + UPDATED_TOTAL);
    }

    @Test
    @Transactional
    void getAllSalesByTotalIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedSale = saleRepository.saveAndFlush(sale);

        // Get all the saleList where total is less than or equal to
        defaultSaleFiltering("total.lessThanOrEqual=" + DEFAULT_TOTAL, "total.lessThanOrEqual=" + SMALLER_TOTAL);
    }

    @Test
    @Transactional
    void getAllSalesByTotalIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedSale = saleRepository.saveAndFlush(sale);

        // Get all the saleList where total is less than
        defaultSaleFiltering("total.lessThan=" + UPDATED_TOTAL, "total.lessThan=" + DEFAULT_TOTAL);
    }

    @Test
    @Transactional
    void getAllSalesByTotalIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedSale = saleRepository.saveAndFlush(sale);

        // Get all the saleList where total is greater than
        defaultSaleFiltering("total.greaterThan=" + SMALLER_TOTAL, "total.greaterThan=" + DEFAULT_TOTAL);
    }

    @Test
    @Transactional
    void getAllSalesByClientIsEqualToSomething() throws Exception {
        Client client;
        if (TestUtil.findAll(em, Client.class).isEmpty()) {
            saleRepository.saveAndFlush(sale);
            client = ClientResourceIT.createEntity();
        } else {
            client = TestUtil.findAll(em, Client.class).get(0);
        }
        em.persist(client);
        em.flush();
        sale.setClient(client);
        saleRepository.saveAndFlush(sale);
        Long clientId = client.getId();
        // Get all the saleList where client equals to clientId
        defaultSaleShouldBeFound("clientId.equals=" + clientId);

        // Get all the saleList where client equals to (clientId + 1)
        defaultSaleShouldNotBeFound("clientId.equals=" + (clientId + 1));
    }

    @Test
    @Transactional
    void getAllSalesBySellerIsEqualToSomething() throws Exception {
        Seller seller;
        if (TestUtil.findAll(em, Seller.class).isEmpty()) {
            saleRepository.saveAndFlush(sale);
            seller = SellerResourceIT.createEntity();
        } else {
            seller = TestUtil.findAll(em, Seller.class).get(0);
        }
        em.persist(seller);
        em.flush();
        sale.setSeller(seller);
        saleRepository.saveAndFlush(sale);
        Long sellerId = seller.getId();
        // Get all the saleList where seller equals to sellerId
        defaultSaleShouldBeFound("sellerId.equals=" + sellerId);

        // Get all the saleList where seller equals to (sellerId + 1)
        defaultSaleShouldNotBeFound("sellerId.equals=" + (sellerId + 1));
    }

    private void defaultSaleFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultSaleShouldBeFound(shouldBeFound);
        defaultSaleShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultSaleShouldBeFound(String filter) throws Exception {
        restSaleMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(sale.getId().intValue())))
            .andExpect(jsonPath("$.[*].creationDate").value(hasItem(DEFAULT_CREATION_DATE.toString())))
            .andExpect(jsonPath("$.[*].total").value(hasItem(sameNumber(DEFAULT_TOTAL))));

        // Check, that the count call also returns 1
        restSaleMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultSaleShouldNotBeFound(String filter) throws Exception {
        restSaleMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restSaleMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingSale() throws Exception {
        // Get the sale
        restSaleMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingSale() throws Exception {
        // Initialize the database
        insertedSale = saleRepository.saveAndFlush(sale);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the sale
        Sale updatedSale = saleRepository.findById(sale.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedSale are not directly saved in db
        em.detach(updatedSale);
        updatedSale.creationDate(UPDATED_CREATION_DATE).total(UPDATED_TOTAL);
        SaleDTO saleDTO = saleMapper.toDto(updatedSale);

        restSaleMockMvc
            .perform(put(ENTITY_API_URL_ID, saleDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(saleDTO)))
            .andExpect(status().isOk());

        // Validate the Sale in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedSaleToMatchAllProperties(updatedSale);
    }

    @Test
    @Transactional
    void putNonExistingSale() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        sale.setId(longCount.incrementAndGet());

        // Create the Sale
        SaleDTO saleDTO = saleMapper.toDto(sale);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSaleMockMvc
            .perform(put(ENTITY_API_URL_ID, saleDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(saleDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Sale in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchSale() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        sale.setId(longCount.incrementAndGet());

        // Create the Sale
        SaleDTO saleDTO = saleMapper.toDto(sale);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSaleMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(saleDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Sale in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamSale() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        sale.setId(longCount.incrementAndGet());

        // Create the Sale
        SaleDTO saleDTO = saleMapper.toDto(sale);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSaleMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(saleDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Sale in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateSaleWithPatch() throws Exception {
        // Initialize the database
        insertedSale = saleRepository.saveAndFlush(sale);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the sale using partial update
        Sale partialUpdatedSale = new Sale();
        partialUpdatedSale.setId(sale.getId());

        partialUpdatedSale.creationDate(UPDATED_CREATION_DATE);

        restSaleMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSale.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedSale))
            )
            .andExpect(status().isOk());

        // Validate the Sale in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertSaleUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedSale, sale), getPersistedSale(sale));
    }

    @Test
    @Transactional
    void fullUpdateSaleWithPatch() throws Exception {
        // Initialize the database
        insertedSale = saleRepository.saveAndFlush(sale);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the sale using partial update
        Sale partialUpdatedSale = new Sale();
        partialUpdatedSale.setId(sale.getId());

        partialUpdatedSale.creationDate(UPDATED_CREATION_DATE).total(UPDATED_TOTAL);

        restSaleMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSale.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedSale))
            )
            .andExpect(status().isOk());

        // Validate the Sale in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertSaleUpdatableFieldsEquals(partialUpdatedSale, getPersistedSale(partialUpdatedSale));
    }

    @Test
    @Transactional
    void patchNonExistingSale() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        sale.setId(longCount.incrementAndGet());

        // Create the Sale
        SaleDTO saleDTO = saleMapper.toDto(sale);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSaleMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, saleDTO.getId()).contentType("application/merge-patch+json").content(om.writeValueAsBytes(saleDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Sale in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchSale() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        sale.setId(longCount.incrementAndGet());

        // Create the Sale
        SaleDTO saleDTO = saleMapper.toDto(sale);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSaleMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(saleDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Sale in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamSale() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        sale.setId(longCount.incrementAndGet());

        // Create the Sale
        SaleDTO saleDTO = saleMapper.toDto(sale);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSaleMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(saleDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Sale in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteSale() throws Exception {
        // Initialize the database
        insertedSale = saleRepository.saveAndFlush(sale);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the sale
        restSaleMockMvc
            .perform(delete(ENTITY_API_URL_ID, sale.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return saleRepository.count();
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

    protected Sale getPersistedSale(Sale sale) {
        return saleRepository.findById(sale.getId()).orElseThrow();
    }

    protected void assertPersistedSaleToMatchAllProperties(Sale expectedSale) {
        assertSaleAllPropertiesEquals(expectedSale, getPersistedSale(expectedSale));
    }

    protected void assertPersistedSaleToMatchUpdatableProperties(Sale expectedSale) {
        assertSaleAllUpdatablePropertiesEquals(expectedSale, getPersistedSale(expectedSale));
    }
}
