package com.takirahal.srfgroup.modules.user.services;

import com.takirahal.srfgroup.modules.permission.dto.UpdateUserAuthorityDTO;
import com.takirahal.srfgroup.modules.permission.enums.EPermission;
import com.takirahal.srfgroup.modules.user.dto.AuthorityDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Set;

public interface AuthorityService {

    /**
     *
     * @param authorityDTO
     * @return
     */
    AuthorityDTO save(AuthorityDTO authorityDTO);

    /**
     *
     * @param authorityDTO
     * @return
     */
    AuthorityDTO update(Integer id, AuthorityDTO authorityDTO);

    /**
     *
     * @param id
     * @return
     */
    AuthorityDTO findById(Integer id);

    /**
     *
     * @param userId
     * @param updateUserAuthorityDTO
     * @return
     */
    Set<AuthorityDTO> updateUserAuthority(Long userId, UpdateUserAuthorityDTO updateUserAuthorityDTO);


    /**
     *
     * @param permission
     */
    void checkForPermissions(EPermission permission);

    /**
     *
     * @param pageable
     * @return
     */
    Page<AuthorityDTO> findByCriteria(Pageable pageable);
}
