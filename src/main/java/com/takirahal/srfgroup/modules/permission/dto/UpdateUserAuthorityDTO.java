package com.takirahal.srfgroup.modules.permission.dto;

import com.takirahal.srfgroup.modules.user.enums.EAuthority;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class UpdateUserAuthorityDTO {
    private Long userId;
    private List<EAuthority> nameAuthority;
}
