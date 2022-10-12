package com.takirahal.srfgroup.modules.rentrequest.controllers;

import com.takirahal.srfgroup.modules.cart.controllers.CartController;
import com.takirahal.srfgroup.modules.cart.dto.CartDTO;
import com.takirahal.srfgroup.modules.cart.dto.filter.CartFilter;
import com.takirahal.srfgroup.modules.rentrequest.dto.RentRequestDTO;
import com.takirahal.srfgroup.modules.rentrequest.dto.filter.RentRequestFilter;
import com.takirahal.srfgroup.modules.rentrequest.entities.RentRequest;
import com.takirahal.srfgroup.modules.rentrequest.services.RentRequestService;
import com.takirahal.srfgroup.utils.HeaderUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/rentrequest/")
public class RentRequestController {

    private final Logger log = LoggerFactory.getLogger(RentRequestController.class);

    @Autowired
    RentRequestService rentRequestService;

    @PostMapping("create")
    public ResponseEntity<RentRequestDTO> createCart(@RequestBody RentRequestDTO rentRequestDTO) {
        log.info("REST request to save RentRequest : {}", rentRequestDTO);
        RentRequestDTO result = rentRequestService.save(rentRequestDTO);
        return new ResponseEntity<>(result, HttpStatus.CREATED);
    }


    @GetMapping("current-user/sent")
    public ResponseEntity<Page<RentRequestDTO>> getCartsByCurrentUserSent(RentRequestFilter rentRequestFilter, Pageable pageable) {
        log.info("REST request to get RentRequest by criteria: {}", rentRequestFilter);
        Page<RentRequestDTO> page = rentRequestService.getCartsByCurrentUserSent(rentRequestFilter, pageable);
        return new ResponseEntity<>(page, HttpStatus.OK);
    }


    @GetMapping("current-user/received")
    public ResponseEntity<Page<RentRequestDTO>> getCartsByCurrentUserReceived(RentRequestFilter rentRequestFilter, Pageable pageable) {
        log.info("REST request to get RentRequest by criteria: {}", rentRequestFilter);
        Page<RentRequestDTO> page = rentRequestService.getCartsByCurrentUserReceived(rentRequestFilter, pageable);
        return new ResponseEntity<>(page, HttpStatus.OK);
    }
}
