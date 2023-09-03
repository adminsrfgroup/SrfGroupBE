package com.takirahal.srfgroup.modules.user.controllers;

import com.takirahal.srfgroup.exceptions.BadRequestAlertException;
import com.takirahal.srfgroup.exceptions.ResouorceNotFoundException;
import com.takirahal.srfgroup.modules.user.entities.Organigramme;
import com.takirahal.srfgroup.modules.user.repositories.OrganigrammeRepository;
import com.takirahal.srfgroup.utils.HeaderUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;
import java.util.Optional;

@RestController
@RequestMapping("/api/administration/")
public class AdminController {

    private final Logger log = LoggerFactory.getLogger(AdminController.class);

    @Autowired
    OrganigrammeRepository organigrammeRepository;


    /**
     *
     * @return
     */
    @GetMapping("organigramme")
    public ResponseEntity<Organigramme> getOrganigramme() {
        log.info("REST request to get organigramme : {}");
        Optional<Organigramme> organigramme = organigrammeRepository.findTopByOrderByIdDesc();
        return new ResponseEntity<>(organigramme.get(), HttpStatus.OK);
    }


    /**
     *
     * @param organigramme
     * @return
     */
    @PostMapping("organigramme")
    public ResponseEntity<Organigramme> createOrganigramme(@RequestBody Organigramme organigramme){
        log.info("REST request to save Organigramme : {}", organigramme);
        Organigramme result = organigrammeRepository.save(organigramme);
        return new ResponseEntity<>(result, HeaderUtil.createAlert("Organigramme created successfully", ""), HttpStatus.CREATED);
    }

    @PutMapping("organigramme/{id}")
    public ResponseEntity<Organigramme> updateOrganigramme(@PathVariable Long id,
                                                           @RequestBody Organigramme organigramme){
        log.info("REST request to save Organigramme : {}", organigramme);
        if (!Objects.equals(id, organigramme.getId())) {
            throw new BadRequestAlertException("Invalid ID idinvalid");
        }
        Organigramme organigrammeUpdate = organigrammeRepository.findById(id)
                .orElseThrow(() -> new ResouorceNotFoundException("Entity not found with id"));
        organigrammeUpdate.setContent(organigramme.getContent());
        Organigramme result = organigrammeRepository.save(organigrammeUpdate);
        return new ResponseEntity<>(result, HeaderUtil.createAlert("Organigramme created successfully", ""), HttpStatus.CREATED);
    }
}
