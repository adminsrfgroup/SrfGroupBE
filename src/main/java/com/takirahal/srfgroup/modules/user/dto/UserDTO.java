package com.takirahal.srfgroup.modules.user.dto;

import com.takirahal.srfgroup.enums.SourceConnectedDevice;
import com.takirahal.srfgroup.modules.address.dto.AddressDTO;
import com.takirahal.srfgroup.modules.user.entities.Authority;
import lombok.*;

import java.io.Serializable;
import java.time.Instant;
import java.util.Set;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO  implements Serializable {

    private Long id;

    private String username;

    private String firstName;

    private String lastName;

    private String email;

    private Instant registerDate;

    private String langKey;

    private String blocked;

    private String linkProfileFacebook;

    private String imageUrl;

    private String phone;

    private SourceConnectedDevice sourceConnectedDevice;

    private Set<AuthorityDTO> authorities;

    private AddressDTO address;

    private boolean activatedAccount;
}
