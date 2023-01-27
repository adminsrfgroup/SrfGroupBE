package com.takirahal.srfgroup.modules.user.dto;

import lombok.*;

import java.io.Serializable;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class JwtResponseVM implements Serializable {
    private String token;
    private String refreshToken;
}
