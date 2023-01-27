package com.takirahal.srfgroup.modules.user.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.takirahal.srfgroup.IntegrationTest;
import com.takirahal.srfgroup.constants.SrfGroupConstants;
import com.takirahal.srfgroup.exceptions.ResouorceNotFoundException;
import com.takirahal.srfgroup.modules.user.dto.JwtResponseVM;
import com.takirahal.srfgroup.modules.user.dto.LoginDTO;
import com.takirahal.srfgroup.modules.user.dto.RegisterDTO;
import com.takirahal.srfgroup.modules.user.dto.UserDTO;
import com.takirahal.srfgroup.modules.user.entities.User;
import com.takirahal.srfgroup.modules.user.repositories.UserRepository;
import com.takirahal.srfgroup.modules.user.services.UserService;
import com.takirahal.srfgroup.modules.utils.TestUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@IntegrationTest
@AutoConfigureMockMvc
class UserControllerTest {

    private static final String ENTITY_API_URL = "/api/user/";

    @Autowired
    private MockMvc restUserMockMvc;

    @Autowired
    UserService userService;

    @Autowired
    ObjectMapper objectMapper;

    @Value("${spring.datasource.url}")
    String val;


    @Autowired
    UserRepository userRepository;

    @BeforeEach
    public void init(){
        // userRepository.deleteAll();
    }

    @Test
    void testSignin() throws Exception {

        // Given
        RegisterDTO registerDTO = new RegisterDTO();
        registerDTO.setEmail("taki@rahal.com");
        registerDTO.setPassword("rahal");
        registerDTO.setIdOneSignal("123456789");
        User user = userService.registerUser(registerDTO);
        // Activate account
        userService.activateRegistration(user.getActivationKey())
                .orElseThrow(() -> new ResouorceNotFoundException("Invalid key"));

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

        MvcResult result = mvcResult.andReturn();
        String contentAsString = result.getResponse().getContentAsString();
        JwtResponseVM jwtResponseVM = objectMapper.readValue(contentAsString, JwtResponseVM.class);
        assertFalse(jwtResponseVM.getToken().isEmpty());
        assertFalse(jwtResponseVM.getRefreshToken().isEmpty());
    }

    @Test
    void testFailureSignin() throws Exception {

        // Given
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setEmail("wrong@email.com");
        loginDTO.setPassword("wrong-password");
        loginDTO.setRememberMe(true);
        loginDTO.setIdOneSignal("123456789");

        // When
        ResultActions mvcResult = restUserMockMvc
                .perform(post(ENTITY_API_URL+"public/signin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestUtil.convertObjectToJsonBytes(loginDTO))
                        .header(SrfGroupConstants.LANG_KEY, "en")
                        .header(SrfGroupConstants.SOURCE_CONNECTED_DEVICE, "WebBrowser")
                );

        // Then
        mvcResult
                .andExpect(status().isBadRequest());
    }

//    @Test
//    void signinGooglePlus() {
//    }
//
//    @Test
//    void signinFacebook() {
//    }
//
    @Test
    void signup() throws Exception {

        // Given
        RegisterDTO registerDTO = new RegisterDTO();
        registerDTO.setEmail("rahal@taki.com");
        registerDTO.setPassword("rahal");
        registerDTO.setIdOneSignal("123456789");

        // When
        ResultActions mvcResult = restUserMockMvc
                .perform(post(ENTITY_API_URL+"public/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestUtil.convertObjectToJsonBytes(registerDTO))
                        .header(SrfGroupConstants.LANG_KEY, "en")
                        .header(SrfGroupConstants.SOURCE_CONNECTED_DEVICE, "WebBrowser")
                );

        // Then
        mvcResult
                .andExpect(status().isCreated());

        MvcResult result = mvcResult.andReturn();
        String contentAsString = result.getResponse().getContentAsString();
        String body = objectMapper.readValue(contentAsString, String.class);
        assertFalse(body.isEmpty());
        assertEquals(body, "true");

    }
//
//    @Test
//    void signinAdmin() {
//    }
//
//    @Test
//    void activateAccount() {
//    }
//
    @Test
    void getProfile() throws Exception {
        // Given
        RegisterDTO registerDTO = new RegisterDTO();
        registerDTO.setEmail("profile@rahal.com");
        registerDTO.setPassword("rahal");
        registerDTO.setIdOneSignal("123456789");
        User user = userService.registerUser(registerDTO);
        // Activate account
        userService.activateRegistration(user.getActivationKey())
                .orElseThrow(() -> new ResouorceNotFoundException("Invalid key"));

        // When
        ResultActions mvcResult = restUserMockMvc
                .perform(get(ENTITY_API_URL+"public/profile/"+user.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(SrfGroupConstants.LANG_KEY, "en")
                        .header(SrfGroupConstants.SOURCE_CONNECTED_DEVICE, "WebBrowser")
                );

        // Then
        mvcResult
                .andExpect(status().isOk());

        MvcResult result = mvcResult.andReturn();
        String contentAsString = result.getResponse().getContentAsString();
        UserDTO userDTO = objectMapper.readValue(contentAsString, UserDTO.class);
        assertFalse(userDTO.getEmail().isEmpty());
    }

    @Test
    @WithMockUser(username = "user-account@gmail.com", password = "rahal")
    void getAccountUser() throws Exception {

        // Given
        RegisterDTO registerDTO = new RegisterDTO();
        registerDTO.setEmail("user-account@gmail.com");
        registerDTO.setPassword("rahal");
        registerDTO.setIdOneSignal("123456789");
        User user = userService.registerUser(registerDTO);
        // Activate account
        userService.activateRegistration(user.getActivationKey())
                .orElseThrow(() -> new ResouorceNotFoundException("Invalid key"));

        // When
        ResultActions mvcResult = restUserMockMvc
                .perform(get(ENTITY_API_URL+"current-user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(SrfGroupConstants.LANG_KEY, "en")
                        .header(SrfGroupConstants.SOURCE_CONNECTED_DEVICE, "WebBrowser")
                );

        // Then
        mvcResult
                .andExpect(status().isOk());
        MvcResult result = mvcResult.andReturn();
        String contentAsString = result.getResponse().getContentAsString();
        UserDTO userDTO = objectMapper.readValue(contentAsString, UserDTO.class);
        assertFalse(userDTO.getEmail().isEmpty());
    }
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