package com.sales.system.web.rest;

import static com.sales.system.domain.SellerAsserts.*;
import static com.sales.system.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sales.system.IntegrationTest;
import com.sales.system.domain.Seller;
import com.sales.system.repository.SellerRepository;
import com.sales.system.service.dto.SellerDTO;
import com.sales.system.service.mapper.SellerMapper;
import jakarta.persistence.EntityManager;
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
 * Integration tests for the {@link SellerResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class SellerResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_LAST_NAME = "AAAAAAAAAA";
    private static final String UPDATED_LAST_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_EMPLOYEE_CODE = "AAAAAAAAAA";
    private static final String UPDATED_EMPLOYEE_CODE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/sellers";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private SellerRepository sellerRepository;

    @Autowired
    private SellerMapper sellerMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restSellerMockMvc;

    private Seller seller;

    private Seller insertedSeller;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Seller createEntity() {
        return new Seller().name(DEFAULT_NAME).lastName(DEFAULT_LAST_NAME).employeeCode(DEFAULT_EMPLOYEE_CODE);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Seller createUpdatedEntity() {
        return new Seller().name(UPDATED_NAME).lastName(UPDATED_LAST_NAME).employeeCode(UPDATED_EMPLOYEE_CODE);
    }

    @BeforeEach
    void initTest() {
        seller = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedSeller != null) {
            sellerRepository.delete(insertedSeller);
            insertedSeller = null;
        }
    }

    @Test
    @Transactional
    void createSeller() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Seller
        SellerDTO sellerDTO = sellerMapper.toDto(seller);
        var returnedSellerDTO = om.readValue(
            restSellerMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(sellerDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            SellerDTO.class
        );

        // Validate the Seller in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedSeller = sellerMapper.toEntity(returnedSellerDTO);
        assertSellerUpdatableFieldsEquals(returnedSeller, getPersistedSeller(returnedSeller));

        insertedSeller = returnedSeller;
    }

    @Test
    @Transactional
    void createSellerWithExistingId() throws Exception {
        // Create the Seller with an existing ID
        seller.setId(1L);
        SellerDTO sellerDTO = sellerMapper.toDto(seller);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restSellerMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(sellerDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Seller in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        seller.setName(null);

        // Create the Seller, which fails.
        SellerDTO sellerDTO = sellerMapper.toDto(seller);

        restSellerMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(sellerDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkLastNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        seller.setLastName(null);

        // Create the Seller, which fails.
        SellerDTO sellerDTO = sellerMapper.toDto(seller);

        restSellerMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(sellerDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkEmployeeCodeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        seller.setEmployeeCode(null);

        // Create the Seller, which fails.
        SellerDTO sellerDTO = sellerMapper.toDto(seller);

        restSellerMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(sellerDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllSellers() throws Exception {
        // Initialize the database
        insertedSeller = sellerRepository.saveAndFlush(seller);

        // Get all the sellerList
        restSellerMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(seller.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].lastName").value(hasItem(DEFAULT_LAST_NAME)))
            .andExpect(jsonPath("$.[*].employeeCode").value(hasItem(DEFAULT_EMPLOYEE_CODE)));
    }

    @Test
    @Transactional
    void getSeller() throws Exception {
        // Initialize the database
        insertedSeller = sellerRepository.saveAndFlush(seller);

        // Get the seller
        restSellerMockMvc
            .perform(get(ENTITY_API_URL_ID, seller.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(seller.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.lastName").value(DEFAULT_LAST_NAME))
            .andExpect(jsonPath("$.employeeCode").value(DEFAULT_EMPLOYEE_CODE));
    }

    @Test
    @Transactional
    void getSellersByIdFiltering() throws Exception {
        // Initialize the database
        insertedSeller = sellerRepository.saveAndFlush(seller);

        Long id = seller.getId();

        defaultSellerFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultSellerFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultSellerFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllSellersByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedSeller = sellerRepository.saveAndFlush(seller);

        // Get all the sellerList where name equals to
        defaultSellerFiltering("name.equals=" + DEFAULT_NAME, "name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllSellersByNameIsInShouldWork() throws Exception {
        // Initialize the database
        insertedSeller = sellerRepository.saveAndFlush(seller);

        // Get all the sellerList where name in
        defaultSellerFiltering("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME, "name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllSellersByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedSeller = sellerRepository.saveAndFlush(seller);

        // Get all the sellerList where name is not null
        defaultSellerFiltering("name.specified=true", "name.specified=false");
    }

    @Test
    @Transactional
    void getAllSellersByNameContainsSomething() throws Exception {
        // Initialize the database
        insertedSeller = sellerRepository.saveAndFlush(seller);

        // Get all the sellerList where name contains
        defaultSellerFiltering("name.contains=" + DEFAULT_NAME, "name.contains=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllSellersByNameNotContainsSomething() throws Exception {
        // Initialize the database
        insertedSeller = sellerRepository.saveAndFlush(seller);

        // Get all the sellerList where name does not contain
        defaultSellerFiltering("name.doesNotContain=" + UPDATED_NAME, "name.doesNotContain=" + DEFAULT_NAME);
    }

    @Test
    @Transactional
    void getAllSellersByLastNameIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedSeller = sellerRepository.saveAndFlush(seller);

        // Get all the sellerList where lastName equals to
        defaultSellerFiltering("lastName.equals=" + DEFAULT_LAST_NAME, "lastName.equals=" + UPDATED_LAST_NAME);
    }

    @Test
    @Transactional
    void getAllSellersByLastNameIsInShouldWork() throws Exception {
        // Initialize the database
        insertedSeller = sellerRepository.saveAndFlush(seller);

        // Get all the sellerList where lastName in
        defaultSellerFiltering("lastName.in=" + DEFAULT_LAST_NAME + "," + UPDATED_LAST_NAME, "lastName.in=" + UPDATED_LAST_NAME);
    }

    @Test
    @Transactional
    void getAllSellersByLastNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedSeller = sellerRepository.saveAndFlush(seller);

        // Get all the sellerList where lastName is not null
        defaultSellerFiltering("lastName.specified=true", "lastName.specified=false");
    }

    @Test
    @Transactional
    void getAllSellersByLastNameContainsSomething() throws Exception {
        // Initialize the database
        insertedSeller = sellerRepository.saveAndFlush(seller);

        // Get all the sellerList where lastName contains
        defaultSellerFiltering("lastName.contains=" + DEFAULT_LAST_NAME, "lastName.contains=" + UPDATED_LAST_NAME);
    }

    @Test
    @Transactional
    void getAllSellersByLastNameNotContainsSomething() throws Exception {
        // Initialize the database
        insertedSeller = sellerRepository.saveAndFlush(seller);

        // Get all the sellerList where lastName does not contain
        defaultSellerFiltering("lastName.doesNotContain=" + UPDATED_LAST_NAME, "lastName.doesNotContain=" + DEFAULT_LAST_NAME);
    }

    @Test
    @Transactional
    void getAllSellersByEmployeeCodeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedSeller = sellerRepository.saveAndFlush(seller);

        // Get all the sellerList where employeeCode equals to
        defaultSellerFiltering("employeeCode.equals=" + DEFAULT_EMPLOYEE_CODE, "employeeCode.equals=" + UPDATED_EMPLOYEE_CODE);
    }

    @Test
    @Transactional
    void getAllSellersByEmployeeCodeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedSeller = sellerRepository.saveAndFlush(seller);

        // Get all the sellerList where employeeCode in
        defaultSellerFiltering(
            "employeeCode.in=" + DEFAULT_EMPLOYEE_CODE + "," + UPDATED_EMPLOYEE_CODE,
            "employeeCode.in=" + UPDATED_EMPLOYEE_CODE
        );
    }

    @Test
    @Transactional
    void getAllSellersByEmployeeCodeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedSeller = sellerRepository.saveAndFlush(seller);

        // Get all the sellerList where employeeCode is not null
        defaultSellerFiltering("employeeCode.specified=true", "employeeCode.specified=false");
    }

    @Test
    @Transactional
    void getAllSellersByEmployeeCodeContainsSomething() throws Exception {
        // Initialize the database
        insertedSeller = sellerRepository.saveAndFlush(seller);

        // Get all the sellerList where employeeCode contains
        defaultSellerFiltering("employeeCode.contains=" + DEFAULT_EMPLOYEE_CODE, "employeeCode.contains=" + UPDATED_EMPLOYEE_CODE);
    }

    @Test
    @Transactional
    void getAllSellersByEmployeeCodeNotContainsSomething() throws Exception {
        // Initialize the database
        insertedSeller = sellerRepository.saveAndFlush(seller);

        // Get all the sellerList where employeeCode does not contain
        defaultSellerFiltering(
            "employeeCode.doesNotContain=" + UPDATED_EMPLOYEE_CODE,
            "employeeCode.doesNotContain=" + DEFAULT_EMPLOYEE_CODE
        );
    }

    private void defaultSellerFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultSellerShouldBeFound(shouldBeFound);
        defaultSellerShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultSellerShouldBeFound(String filter) throws Exception {
        restSellerMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(seller.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].lastName").value(hasItem(DEFAULT_LAST_NAME)))
            .andExpect(jsonPath("$.[*].employeeCode").value(hasItem(DEFAULT_EMPLOYEE_CODE)));

        // Check, that the count call also returns 1
        restSellerMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultSellerShouldNotBeFound(String filter) throws Exception {
        restSellerMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restSellerMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingSeller() throws Exception {
        // Get the seller
        restSellerMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingSeller() throws Exception {
        // Initialize the database
        insertedSeller = sellerRepository.saveAndFlush(seller);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the seller
        Seller updatedSeller = sellerRepository.findById(seller.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedSeller are not directly saved in db
        em.detach(updatedSeller);
        updatedSeller.name(UPDATED_NAME).lastName(UPDATED_LAST_NAME).employeeCode(UPDATED_EMPLOYEE_CODE);
        SellerDTO sellerDTO = sellerMapper.toDto(updatedSeller);

        restSellerMockMvc
            .perform(
                put(ENTITY_API_URL_ID, sellerDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(sellerDTO))
            )
            .andExpect(status().isOk());

        // Validate the Seller in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedSellerToMatchAllProperties(updatedSeller);
    }

    @Test
    @Transactional
    void putNonExistingSeller() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        seller.setId(longCount.incrementAndGet());

        // Create the Seller
        SellerDTO sellerDTO = sellerMapper.toDto(seller);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSellerMockMvc
            .perform(
                put(ENTITY_API_URL_ID, sellerDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(sellerDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Seller in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchSeller() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        seller.setId(longCount.incrementAndGet());

        // Create the Seller
        SellerDTO sellerDTO = sellerMapper.toDto(seller);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSellerMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(sellerDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Seller in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamSeller() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        seller.setId(longCount.incrementAndGet());

        // Create the Seller
        SellerDTO sellerDTO = sellerMapper.toDto(seller);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSellerMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(sellerDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Seller in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateSellerWithPatch() throws Exception {
        // Initialize the database
        insertedSeller = sellerRepository.saveAndFlush(seller);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the seller using partial update
        Seller partialUpdatedSeller = new Seller();
        partialUpdatedSeller.setId(seller.getId());

        partialUpdatedSeller.lastName(UPDATED_LAST_NAME).employeeCode(UPDATED_EMPLOYEE_CODE);

        restSellerMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSeller.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedSeller))
            )
            .andExpect(status().isOk());

        // Validate the Seller in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertSellerUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedSeller, seller), getPersistedSeller(seller));
    }

    @Test
    @Transactional
    void fullUpdateSellerWithPatch() throws Exception {
        // Initialize the database
        insertedSeller = sellerRepository.saveAndFlush(seller);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the seller using partial update
        Seller partialUpdatedSeller = new Seller();
        partialUpdatedSeller.setId(seller.getId());

        partialUpdatedSeller.name(UPDATED_NAME).lastName(UPDATED_LAST_NAME).employeeCode(UPDATED_EMPLOYEE_CODE);

        restSellerMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSeller.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedSeller))
            )
            .andExpect(status().isOk());

        // Validate the Seller in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertSellerUpdatableFieldsEquals(partialUpdatedSeller, getPersistedSeller(partialUpdatedSeller));
    }

    @Test
    @Transactional
    void patchNonExistingSeller() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        seller.setId(longCount.incrementAndGet());

        // Create the Seller
        SellerDTO sellerDTO = sellerMapper.toDto(seller);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSellerMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, sellerDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(sellerDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Seller in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchSeller() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        seller.setId(longCount.incrementAndGet());

        // Create the Seller
        SellerDTO sellerDTO = sellerMapper.toDto(seller);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSellerMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(sellerDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Seller in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamSeller() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        seller.setId(longCount.incrementAndGet());

        // Create the Seller
        SellerDTO sellerDTO = sellerMapper.toDto(seller);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSellerMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(sellerDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Seller in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteSeller() throws Exception {
        // Initialize the database
        insertedSeller = sellerRepository.saveAndFlush(seller);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the seller
        restSellerMockMvc
            .perform(delete(ENTITY_API_URL_ID, seller.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return sellerRepository.count();
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

    protected Seller getPersistedSeller(Seller seller) {
        return sellerRepository.findById(seller.getId()).orElseThrow();
    }

    protected void assertPersistedSellerToMatchAllProperties(Seller expectedSeller) {
        assertSellerAllPropertiesEquals(expectedSeller, getPersistedSeller(expectedSeller));
    }

    protected void assertPersistedSellerToMatchUpdatableProperties(Seller expectedSeller) {
        assertSellerAllUpdatablePropertiesEquals(expectedSeller, getPersistedSeller(expectedSeller));
    }
}
