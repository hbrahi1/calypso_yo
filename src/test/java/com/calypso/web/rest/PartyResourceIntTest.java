package com.calypso.web.rest;

import com.calypso.CalypsoApp;
import com.calypso.domain.Party;
import com.calypso.repository.PartyRepository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.hamcrest.Matchers.hasItem;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.calypso.domain.enumeration.PartyStatus;

/**
 * Test class for the PartyResource REST controller.
 *
 * @see PartyResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = CalypsoApp.class)
@WebAppConfiguration
@IntegrationTest
public class PartyResourceIntTest {


    private static final Long DEFAULT_PARTY_ID = 1L;
    private static final Long UPDATED_PARTY_ID = 2L;
    private static final String DEFAULT_PARTY_NAME = "AAAAA";
    private static final String UPDATED_PARTY_NAME = "BBBBB";

    private static final PartyStatus DEFAULT_PARTY_STATUS = PartyStatus.ACTIVE;
    private static final PartyStatus UPDATED_PARTY_STATUS = PartyStatus.INACTIVE;

    @Inject
    private PartyRepository partyRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restPartyMockMvc;

    private Party party;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        PartyResource partyResource = new PartyResource();
        ReflectionTestUtils.setField(partyResource, "partyRepository", partyRepository);
        this.restPartyMockMvc = MockMvcBuilders.standaloneSetup(partyResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        party = new Party();
        party.setPartyId(DEFAULT_PARTY_ID);
        party.setPartyName(DEFAULT_PARTY_NAME);
        party.setPartyStatus(DEFAULT_PARTY_STATUS);
    }

    @Test
    @Transactional
    public void createParty() throws Exception {
        int databaseSizeBeforeCreate = partyRepository.findAll().size();

        // Create the Party

        restPartyMockMvc.perform(post("/api/parties")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(party)))
                .andExpect(status().isCreated());

        // Validate the Party in the database
        List<Party> parties = partyRepository.findAll();
        assertThat(parties).hasSize(databaseSizeBeforeCreate + 1);
        Party testParty = parties.get(parties.size() - 1);
        assertThat(testParty.getPartyId()).isEqualTo(DEFAULT_PARTY_ID);
        assertThat(testParty.getPartyName()).isEqualTo(DEFAULT_PARTY_NAME);
        assertThat(testParty.getPartyStatus()).isEqualTo(DEFAULT_PARTY_STATUS);
    }

    @Test
    @Transactional
    public void checkPartyNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = partyRepository.findAll().size();
        // set the field null
        party.setPartyName(null);

        // Create the Party, which fails.

        restPartyMockMvc.perform(post("/api/parties")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(party)))
                .andExpect(status().isBadRequest());

        List<Party> parties = partyRepository.findAll();
        assertThat(parties).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllParties() throws Exception {
        // Initialize the database
        partyRepository.saveAndFlush(party);

        // Get all the parties
        restPartyMockMvc.perform(get("/api/parties?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(party.getId().intValue())))
                .andExpect(jsonPath("$.[*].partyId").value(hasItem(DEFAULT_PARTY_ID.intValue())))
                .andExpect(jsonPath("$.[*].partyName").value(hasItem(DEFAULT_PARTY_NAME.toString())))
                .andExpect(jsonPath("$.[*].partyStatus").value(hasItem(DEFAULT_PARTY_STATUS.toString())));
    }

    @Test
    @Transactional
    public void getParty() throws Exception {
        // Initialize the database
        partyRepository.saveAndFlush(party);

        // Get the party
        restPartyMockMvc.perform(get("/api/parties/{id}", party.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(party.getId().intValue()))
            .andExpect(jsonPath("$.partyId").value(DEFAULT_PARTY_ID.intValue()))
            .andExpect(jsonPath("$.partyName").value(DEFAULT_PARTY_NAME.toString()))
            .andExpect(jsonPath("$.partyStatus").value(DEFAULT_PARTY_STATUS.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingParty() throws Exception {
        // Get the party
        restPartyMockMvc.perform(get("/api/parties/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateParty() throws Exception {
        // Initialize the database
        partyRepository.saveAndFlush(party);
        int databaseSizeBeforeUpdate = partyRepository.findAll().size();

        // Update the party
        Party updatedParty = new Party();
        updatedParty.setId(party.getId());
        updatedParty.setPartyId(UPDATED_PARTY_ID);
        updatedParty.setPartyName(UPDATED_PARTY_NAME);
        updatedParty.setPartyStatus(UPDATED_PARTY_STATUS);

        restPartyMockMvc.perform(put("/api/parties")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedParty)))
                .andExpect(status().isOk());

        // Validate the Party in the database
        List<Party> parties = partyRepository.findAll();
        assertThat(parties).hasSize(databaseSizeBeforeUpdate);
        Party testParty = parties.get(parties.size() - 1);
        assertThat(testParty.getPartyId()).isEqualTo(UPDATED_PARTY_ID);
        assertThat(testParty.getPartyName()).isEqualTo(UPDATED_PARTY_NAME);
        assertThat(testParty.getPartyStatus()).isEqualTo(UPDATED_PARTY_STATUS);
    }

    @Test
    @Transactional
    public void deleteParty() throws Exception {
        // Initialize the database
        partyRepository.saveAndFlush(party);
        int databaseSizeBeforeDelete = partyRepository.findAll().size();

        // Get the party
        restPartyMockMvc.perform(delete("/api/parties/{id}", party.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<Party> parties = partyRepository.findAll();
        assertThat(parties).hasSize(databaseSizeBeforeDelete - 1);
    }
}
