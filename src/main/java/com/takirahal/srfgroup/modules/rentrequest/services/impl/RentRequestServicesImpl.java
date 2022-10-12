package com.takirahal.srfgroup.modules.rentrequest.services.impl;

import com.takirahal.srfgroup.exceptions.BadRequestAlertException;
import com.takirahal.srfgroup.modules.offer.entities.RentOffer;
import com.takirahal.srfgroup.modules.offer.mapper.RentOfferMapper;
import com.takirahal.srfgroup.modules.rentrequest.dto.RentRequestDTO;
import com.takirahal.srfgroup.modules.rentrequest.dto.filter.RentRequestFilter;
import com.takirahal.srfgroup.modules.rentrequest.entities.RentRequest;
import com.takirahal.srfgroup.modules.rentrequest.enums.StatusRentRequest;
import com.takirahal.srfgroup.modules.rentrequest.mapper.RentRequestMapper;
import com.takirahal.srfgroup.modules.rentrequest.repositories.RentRequestRepository;
import com.takirahal.srfgroup.modules.rentrequest.services.RentRequestService;
import com.takirahal.srfgroup.modules.user.dto.UserDTO;
import com.takirahal.srfgroup.modules.user.dto.filter.UserFilter;
import com.takirahal.srfgroup.modules.user.entities.User;
import com.takirahal.srfgroup.utils.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.Predicate;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class RentRequestServicesImpl implements RentRequestService {

    private final Logger log = LoggerFactory.getLogger(RentRequestServicesImpl.class);

    @Autowired
    RentRequestRepository rentRequestRepository;

    @Autowired
    RentRequestMapper rentRequestMapper;

    @Autowired
    RentOfferMapper rentOfferMapper;

    @Override
    public RentRequestDTO save(RentRequestDTO rentRequestDTO) {
        log.debug("Request to save RentRequest : {}", rentRequestDTO);

        Long curentUserId = SecurityUtils.getIdByCurrentUser();

        if( rentRequestDTO.getReceiverUser().getId().equals(curentUserId)){
            throw new BadRequestAlertException("Not allowed");
        }

        UserDTO userDTO = new UserDTO();
        User user = new User();
        user.setId(curentUserId);
        userDTO.setId(curentUserId);

        RentOffer rentOffer = rentOfferMapper.toEntity(rentRequestDTO.getRentOffer());
        Optional<RentRequest> rentRequest = rentRequestRepository.findByRentOfferAndSenderUser(rentOffer, user);
        if( !rentRequest.isPresent() ){
            rentRequestDTO.setSenderUser(userDTO);
            rentRequestDTO.setSendDate(Instant.now());
            rentRequestDTO.setStatus(StatusRentRequest.STANDBY.toString());
            RentRequest rentRequestNew = rentRequestMapper.toEntity(rentRequestDTO);
            rentRequestNew = rentRequestRepository.save(rentRequestNew);
            return rentRequestMapper.toDto(rentRequestNew);
        }

        return rentRequestMapper.toDto(rentRequest.get());
    }

    @Override
    public Page<RentRequestDTO> getCartsByCurrentUserSent(RentRequestFilter rentRequestFilter, Pageable page) {
        log.debug("Request to get all RentRequest for current user : {}", rentRequestFilter);
        Long currentUserId = SecurityUtils.getIdByCurrentUser();
        UserFilter userFilter = new UserFilter();
        userFilter.setId(currentUserId);
        rentRequestFilter.setSenderUser(userFilter);
        return findByCriteria(rentRequestFilter, page);
    }

    @Override
    public Page<RentRequestDTO> getCartsByCurrentUserReceived(RentRequestFilter rentRequestFilter, Pageable page) {
        log.debug("Request to get all RentRequest for current user : {}", rentRequestFilter);
        Long currentUserId = SecurityUtils.getIdByCurrentUser();
        UserFilter userFilter = new UserFilter();
        userFilter.setId(currentUserId);
        rentRequestFilter.setReceiverUser(userFilter);
        return findByCriteria(rentRequestFilter, page);
    }

    private Page<RentRequestDTO> findByCriteria(RentRequestFilter rentRequestFilter, Pageable page) {
        return rentRequestRepository.findAll(createSpecification(rentRequestFilter), page).map(rentRequestMapper::toDto);
    }

    private Specification<RentRequest> createSpecification(RentRequestFilter rentRequestFilter) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if( rentRequestFilter.getStatus() != null){
                predicates.add(criteriaBuilder.equal(root.get("status"), rentRequestFilter.getStatus()));
            }

            if ( rentRequestFilter.getSenderUser() != null && rentRequestFilter.getSenderUser().getId() != null ) {
                predicates.add(criteriaBuilder.equal(root.get("senderUser").get("id"), rentRequestFilter.getSenderUser().getId()));
            }

            if ( rentRequestFilter.getReceiverUser() != null && rentRequestFilter.getReceiverUser().getId() != null ) {
                predicates.add(criteriaBuilder.equal(root.get("receiverUser").get("id"), rentRequestFilter.getReceiverUser().getId()));
            }

            query.orderBy(criteriaBuilder.desc(root.get("id")));
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
