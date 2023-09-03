package com.takirahal.srfgroup.modules.user.services.impl;

import com.takirahal.srfgroup.exceptions.BadRequestAlertException;
import com.takirahal.srfgroup.exceptions.ResouorceNotFoundException;
import com.takirahal.srfgroup.exceptions.UnauthorizedException;
import com.takirahal.srfgroup.modules.faq.dto.filter.FaqFilter;
import com.takirahal.srfgroup.modules.faq.entities.Faq;
import com.takirahal.srfgroup.modules.permission.dto.PermissionDTO;
import com.takirahal.srfgroup.modules.permission.dto.UpdateUserAuthorityDTO;
import com.takirahal.srfgroup.modules.permission.entities.Permission;
import com.takirahal.srfgroup.modules.permission.enums.EPermission;
import com.takirahal.srfgroup.modules.permission.mapper.PermissionMapper;
import com.takirahal.srfgroup.modules.user.dto.AuthorityDTO;
import com.takirahal.srfgroup.modules.user.entities.Authority;
import com.takirahal.srfgroup.modules.user.enums.EAuthority;
import com.takirahal.srfgroup.modules.user.mapper.AuthorityMapper;
import com.takirahal.srfgroup.modules.user.repositories.AuthorityRepository;
import com.takirahal.srfgroup.modules.user.services.AuthorityService;
import com.takirahal.srfgroup.modules.user.services.UserService;
import com.takirahal.srfgroup.utils.RequestUtil;
import com.takirahal.srfgroup.utils.SecurityUtils;
import jakarta.persistence.criteria.Predicate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class AuthorityServiceImpl implements AuthorityService {

    private final Logger log = LoggerFactory.getLogger(AuthorityServiceImpl.class);

    @Autowired
    AuthorityRepository authorityRepository;

    @Autowired
    UserService userService;

    @Autowired
    AuthorityMapper authorityMapper;

    @Autowired
    PermissionMapper permissionMapper;

    @Override
    public AuthorityDTO save(AuthorityDTO authorityDTO) {
        log.debug("Request to save Authority : {}", authorityDTO);
        Optional<Authority> authorityByName = authorityRepository.findByName(authorityDTO.getName());
        if( authorityByName.isPresent() ){
            throw new BadRequestAlertException("Authority already exist");
        }

        Authority authority = authorityRepository.save(authorityMapper.toEntity(authorityDTO));
        return authorityMapper.toDto(authority);
    }

    @Override
    public AuthorityDTO update(Integer id, AuthorityDTO authorityDTO) {
        if (!Objects.equals(id, authorityDTO.getId())) {
            throw new UnauthorizedException("Unauthorized action");
        }

        if (Objects.equals(authorityDTO.getName(), EAuthority.ROLE_MODERATOR.name()) ||
                Objects.equals(authorityDTO.getName(), EAuthority.ROLE_ADMIN.name()) ||
                Objects.equals(authorityDTO.getName(), EAuthority.ROLE_USER.name())) {
            throw new UnauthorizedException("Unauthorized action");
        }

        Authority result = authorityRepository.findById(id)
                .orElseThrow(() -> new ResouorceNotFoundException(RequestUtil.messageTranslate("common.resource_not_found")));

        Set<Permission> permissions = authorityDTO.getPermissions().stream()
                .map(permissionDTO -> permissionMapper.toEntity(permissionDTO)).collect(Collectors.toSet());
        result.setPermissions(permissions);

        result = authorityRepository.save(result);

        return authorityMapper.toDto(result);
    }

    @Override
    public AuthorityDTO findById(Integer id) {
        Authority result = authorityRepository.findById(id)
                .orElseThrow(() -> new ResouorceNotFoundException(RequestUtil.messageTranslate("common.resource_not_found")));
        return authorityMapper.toDto(result);
    }

    @Override
    public Set<AuthorityDTO> updateUserAuthority(Long userId, UpdateUserAuthorityDTO updateUserAuthorityDTO) {

        // Check Permission
        checkForPermissions(EPermission.UPDATE_USER_AUTHORITY);

        if( !userId.equals(updateUserAuthorityDTO.getUserId() )){
            throw new BadRequestAlertException("User invalid");
        }

        Set<Authority> authorities = new HashSet<>();
        updateUserAuthorityDTO.getNameAuthority().forEach(eAuthority -> {
            Authority authorityByName = authorityRepository.findByName(eAuthority)
                    .orElseThrow(() -> new ResouorceNotFoundException("Not found authority by name "+eAuthority));
            authorities.add(authorityByName);
        });

        userService.updateUserAuthority(userId, authorities);
        return authorities.stream().map(authority -> authorityMapper.toDto(authority)).collect(Collectors.toSet());
    }

    @Override
    public void checkForPermissions(EPermission permission){
        SecurityUtils.getAuthorities().forEach(authority -> {

            if( authority.equals(EAuthority.ROLE_MODERATOR.name() ) ||
                authority.equals(EAuthority.ROLE_ADMIN.name()) ){
                return;
            }

            EAuthority eAuthority = EAuthority.valueOf(authority);
            Authority auth = authorityRepository.findByName(eAuthority)
                    .orElseThrow(() -> new ResouorceNotFoundException("Not found authority by name "+authority));

            auth.getPermissions().stream()
                    .filter(perm -> perm.getName().equals(permission))
                    .findFirst()
                    .orElseThrow(() -> new UnauthorizedException("Aunauthorized action"));
        });
    }

    @Override
    public Page<AuthorityDTO> findByCriteria(Pageable pageable) {
        return authorityRepository.findAll(createSpecification(), pageable).map(item -> authorityMapper.toDto(item));
    }

    protected Specification<Authority> createSpecification() {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            query.orderBy(criteriaBuilder.asc(root.get("id")));
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
