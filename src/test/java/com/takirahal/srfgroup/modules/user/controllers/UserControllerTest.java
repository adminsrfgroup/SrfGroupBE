package com.takirahal.srfgroup.modules.user.controllers;

import com.takirahal.srfgroup.IntegrationTest;
import com.takirahal.srfgroup.constants.SrfGroupConstants;
import com.takirahal.srfgroup.modules.user.dto.LoginDTO;
import com.takirahal.srfgroup.modules.user.dto.RegisterDTO;
import com.takirahal.srfgroup.modules.user.entities.User;
import com.takirahal.srfgroup.modules.user.repositories.UserRepository;
import com.takirahal.srfgroup.modules.user.services.UserService;
import com.takirahal.srfgroup.modules.utils.TestUtil;
import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@IntegrationTest
@AutoConfigureMockMvc
class UserControllerTest {

    private static final String ENTITY_API_URL = "/api/user/";

    @Autowired
    private MockMvc restUserMockMvc;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    UserService userService;

    @Before(value = "init")
    public void init(){

    }

    @Test
    void testSignin() throws Exception {

        // Given
        RegisterDTO registerDTO = new RegisterDTO();
        registerDTO.setEmail("taki@rahal.com");
        registerDTO.setPassword(passwordEncoder.encode("rahal"));
        registerDTO.setIdOneSignal("123456789");
        userService.registerUser(registerDTO);

        // When
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setEmail("taki@rahal.com");
        loginDTO.setPassword("rahal");
        loginDTO.setRememberMe(true);
        loginDTO.setIdOneSignal("123456789");
        ResultActions mvcResult = restUserMockMvc
                .perform(post(ENTITY_API_URL+"public/signin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestUtil.convertObjectToJsonBytes(loginDTO))
                        .header(SrfGroupConstants.LANG_KEY, "en")
                        .header(SrfGroupConstants.SOURCE_CONNECTED_DEVICE, "WebBrowser")
                );

        // Then
        mvcResult
                .andExpect(status().isOk());
    }

//    @Test
//    void signinGooglePlus() {
//    }
//
//    @Test
//    void signinFacebook() {
//    }
//
//    @Test
//    void signup() {
//    }
//
//    @Test
//    void signinAdmin() {
//    }
//
//    @Test
//    void activateAccount() {
//    }
//
//    @Test
//    void getProfile() {
//    }
//
//    @Test
//    void getAccountUser() {
//    }
//
//    @Test
//    void updateCurrentUser() {
//    }
//
//    @Test
//    void updatePasswordCurrentUser() {
//    }
//
//    @Test
//    void getNumberNotSeeMessageForUserId() {
//    }
//
//    @Test
//    void requestPasswordReset() {
//    }
//
//    @Test
//    void finishPasswordReset() {
//    }
//
//    @Test
//    void getAllAboutuses() {
//    }
//
//    @Test
//    void updateAvatar() {
//    }
//
//    @Test
//    void getFile() {
//    }
}