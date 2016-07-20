package com.calypso.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.calypso.domain.Party;
import com.calypso.repository.PartyRepository;
import com.calypso.web.rest.util.HeaderUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing Party.
 */
@RestController
@RequestMapping("/api")
public class PartyResource {

    private final Logger log = LoggerFactory.getLogger(PartyResource.class);
        
    @Inject
    private PartyRepository partyRepository;
    
    /**
     * POST  /parties : Create a new party.
     *
     * @param party the party to create
     * @return the ResponseEntity with status 201 (Created) and with body the new party, or with status 400 (Bad Request) if the party has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/parties",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Party> createParty(@Valid @RequestBody Party party) throws URISyntaxException {
        log.debug("REST request to save Party : {}", party);
        if (party.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("party", "idexists", "A new party cannot already have an ID")).body(null);
        }
        Party result = partyRepository.save(party);
        return ResponseEntity.created(new URI("/api/parties/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("party", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /parties : Updates an existing party.
     *
     * @param party the party to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated party,
     * or with status 400 (Bad Request) if the party is not valid,
     * or with status 500 (Internal Server Error) if the party couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/parties",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Party> updateParty(@Valid @RequestBody Party party) throws URISyntaxException {
        log.debug("REST request to update Party : {}", party);
        if (party.getId() == null) {
            return createParty(party);
        }
        Party result = partyRepository.save(party);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("party", party.getId().toString()))
            .body(result);
    }

    /**
     * GET  /parties : get all the parties.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of parties in body
     */
    @RequestMapping(value = "/parties",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<Party> getAllParties() {
        log.debug("REST request to get all Parties");
        List<Party> parties = partyRepository.findAll();
        return parties;
    }

    /**
     * GET  /parties/:id : get the "id" party.
     *
     * @param id the id of the party to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the party, or with status 404 (Not Found)
     */
    @RequestMapping(value = "/parties/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Party> getParty(@PathVariable Long id) {
        log.debug("REST request to get Party : {}", id);
        Party party = partyRepository.findOne(id);
        return Optional.ofNullable(party)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /parties/:id : delete the "id" party.
     *
     * @param id the id of the party to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @RequestMapping(value = "/parties/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteParty(@PathVariable Long id) {
        log.debug("REST request to delete Party : {}", id);
        partyRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("party", id.toString())).build();
    }

}
