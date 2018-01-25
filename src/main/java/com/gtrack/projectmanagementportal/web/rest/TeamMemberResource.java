package com.gtrack.projectmanagementportal.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.gtrack.projectmanagementportal.security.SecurityUtils;
import com.gtrack.projectmanagementportal.service.TeamMemberService;
import com.gtrack.projectmanagementportal.web.rest.errors.BadRequestAlertException;
import com.gtrack.projectmanagementportal.web.rest.util.HeaderUtil;
import com.gtrack.projectmanagementportal.web.rest.util.PaginationUtil;
import com.gtrack.projectmanagementportal.service.dto.TeamMemberDTO;
import io.github.jhipster.web.util.ResponseUtil;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;

import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing TeamMember.
 */
@RestController
@RequestMapping("/api")
public class TeamMemberResource {

    private final Logger log = LoggerFactory.getLogger(TeamMemberResource.class);

    private static final String ENTITY_NAME = "teamMember";

    private final TeamMemberService teamMemberService;

    public TeamMemberResource(TeamMemberService teamMemberService) {
        this.teamMemberService = teamMemberService;
    }

    /**
     * POST  /team-members : Create a new teamMember.
     *
     * @param teamMemberDTO the teamMemberDTO to create
     * @return the ResponseEntity with status 201 (Created) and with body the new teamMemberDTO, or with status 400 (Bad Request) if the teamMember has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/team-members")
    @Timed
    public ResponseEntity<TeamMemberDTO> createTeamMember(@Valid @RequestBody TeamMemberDTO teamMemberDTO) throws URISyntaxException {
        log.debug("REST request to save TeamMember : {}", teamMemberDTO);
        if (teamMemberDTO.getId() != null) {
            throw new BadRequestAlertException("A new teamMember cannot already have an ID", ENTITY_NAME, "idexists");
        }
        TeamMemberDTO result = teamMemberService.save(teamMemberDTO);
        return ResponseEntity.created(new URI("/api/team-members/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /team-members : Updates an existing teamMember.
     *
     * @param teamMemberDTO the teamMemberDTO to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated teamMemberDTO,
     * or with status 400 (Bad Request) if the teamMemberDTO is not valid,
     * or with status 500 (Internal Server Error) if the teamMemberDTO couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/team-members")
    @Timed
    public ResponseEntity<TeamMemberDTO> updateTeamMember(@Valid @RequestBody TeamMemberDTO teamMemberDTO) throws URISyntaxException {
        log.debug("REST request to update TeamMember : {}", teamMemberDTO);
        if (teamMemberDTO.getId() == null) {
            return createTeamMember(teamMemberDTO);
        }
        TeamMemberDTO result = teamMemberService.save(teamMemberDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, teamMemberDTO.getId().toString()))
            .body(result);
    }

    /**
     * GET  /team-members : get all the teamMembers.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of teamMembers in body
     */
    @GetMapping("/team-members1")
    @Timed
    public ResponseEntity<List<TeamMemberDTO>> getAllTeamMembers(Pageable pageable) {
        log.debug("REST request to get a page of TeamMembers");
        Page<TeamMemberDTO> page = teamMemberService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/team-members1");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /team-members : get all the teamMembers.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of teamMembers in body
     */
    @GetMapping("/team-members")
    @Timed
    public ResponseEntity<List<TeamMemberDTO>> getTeamMembersByTeamName(@RequestParam(value="query", required=false) String query, Pageable pageable) {
        log.debug("REST request to get a page of TeamMembers; query: " + query);
        Page<TeamMemberDTO> page = null;
        HttpHeaders headers = null;
        JSONObject json = null;
        String teamId = null;
        String teamName = null;
        
        if (query != null) {
            try {
    			json = new JSONObject(query);
            	teamId = json.getString("teamId");
            	teamName = json.getString("teamName");
    		} catch (JSONException e) {
    			// do nothing.
    		}
        }
        
        if(teamId != null) {
        	page = teamMemberService.findByTeamId(Long.valueOf(teamId), pageable);
            headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/team-members?teamId=" + teamId + "&teamName=" + teamName);
        } else {
        	page = teamMemberService.findByUserLogin(SecurityUtils.getCurrentUserLogin().get(), pageable);
            headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/team-members");
        }
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /team-members/:id : get the "id" teamMember.
     *
     * @param id the id of the teamMemberDTO to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the teamMemberDTO, or with status 404 (Not Found)
     */
    @GetMapping("/team-members/{id}")
    @Timed
    public ResponseEntity<TeamMemberDTO> getTeamMember(@PathVariable Long id) {
        log.debug("REST request to get TeamMember : {}", id);
        TeamMemberDTO teamMemberDTO = teamMemberService.findOne(id);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(teamMemberDTO));
    }

    /**
     * DELETE  /team-members/:id : delete the "id" teamMember.
     *
     * @param id the id of the teamMemberDTO to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/team-members/{id}")
    @Timed
    public ResponseEntity<Void> deleteTeamMember(@PathVariable Long id) {
        log.debug("REST request to delete TeamMember : {}", id);
        teamMemberService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }
}
