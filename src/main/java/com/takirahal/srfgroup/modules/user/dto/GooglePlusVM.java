package com.takirahal.srfgroup.modules.user.dto;

import com.takirahal.srfgroup.enums.SourceConnectedDevice;
import com.takirahal.srfgroup.modules.user.models.ProfileObj;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GooglePlusVM {
    private String Ba;
    private String accessToken;
    private String googleId;
    private ProfileObj profileObj;
    private String tokenId;
    private SourceConnectedDevice sourceConnectedDevice;
    private String idOneSignal;
}
