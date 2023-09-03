package com.takirahal.srfgroup.modules.user.controllers;

import com.takirahal.srfgroup.modules.permission.dto.UpdateUserAuthorityDTO;
import com.takirahal.srfgroup.modules.user.dto.AuthorityDTO;
import com.takirahal.srfgroup.modules.user.repositories.AuthorityRepository;
import com.takirahal.srfgroup.modules.user.services.AuthorityService;
import com.takirahal.srfgroup.utils.HeaderUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/api/authority/")
public class AuthorityController {

    private final Logger log = LoggerFactory.getLogger(AuthorityController.class);

    @Autowired
    AuthorityService authorityService;

    @Autowired
    AuthorityRepository authorityRepository;

    @GetMapping("admin")
    public ResponseEntity<Page<AuthorityDTO>> getAllAuthorities(Pageable pageable) {
        log.info("REST request to get Permission by criteria: {}", pageable);
        Page<AuthorityDTO> page = authorityService.findByCriteria(pageable);
        return new ResponseEntity<>(page, HttpStatus.OK);
    }

    @PostMapping("admin")
    public ResponseEntity<AuthorityDTO> createAuthority(@RequestBody AuthorityDTO authorityDTO) {
        log.info("REST request to save new Authority: {}", authorityDTO);
        AuthorityDTO result = authorityService.save(authorityDTO);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("admin/{id}")
    public ResponseEntity<AuthorityDTO> getDetailsAuthority(@PathVariable Integer id) {
        log.info("REST request to get Authority: {}", id);
        AuthorityDTO result = authorityService.findById(id);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PutMapping("admin/{id}")
    public ResponseEntity<AuthorityDTO> updateAuthority(@PathVariable Integer id, @RequestBody AuthorityDTO authority) {
        log.info("REST request to update new Authority: {}", authority);
        AuthorityDTO result = authorityService.update(id, authority);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }


    @PutMapping("user-authority/{id}")
    public ResponseEntity<Set<AuthorityDTO>> updateUserAuthority(@PathVariable Long id, @RequestBody UpdateUserAuthorityDTO updateUserAuthorityDTO) {
        log.info("REST request to update User Authority : {}", id);
        Set<AuthorityDTO> result = authorityService.updateUserAuthority(id, updateUserAuthorityDTO);
        return new ResponseEntity<>(result, HeaderUtil.createAlert("Update authority OK", ""), HttpStatus.OK);
    }
}
