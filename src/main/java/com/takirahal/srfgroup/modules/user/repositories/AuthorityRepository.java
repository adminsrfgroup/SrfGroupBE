package com.takirahal.srfgroup.modules.user.repositories;

import com.takirahal.srfgroup.modules.user.entities.Authority;
import com.takirahal.srfgroup.modules.user.enums.EAuthority;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AuthorityRepository extends JpaRepository<Authority, Integer>, JpaSpecificationExecutor<Authority> {
    Optional<Authority> findByName(EAuthority name);
}
