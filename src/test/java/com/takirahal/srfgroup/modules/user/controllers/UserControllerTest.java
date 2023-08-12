package com.takirahal.srfgroup.modules.user.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.takirahal.srfgroup.IntegrationTest;
import com.takirahal.srfgroup.constants.SrfGroupConstants;
import com.takirahal.srfgroup.enums.SourceConnectedDevice;
import com.takirahal.srfgroup.modules.user.dto.*;
import com.takirahal.srfgroup.modules.user.exceptioins.InvalidPasswordException;
import com.takirahal.srfgroup.modules.user.repositories.UserRepository;
import com.takirahal.srfgroup.modules.user.services.UserService;
import com.takirahal.srfgroup.modules.utils.TestUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.mockito.Mockito.*;

@IntegrationTest
@AutoConfigureMockMvc
class UserControllerTest {

    private static final String ENTITY_API_URL = "/api/user/";

    @Autowired
    private MockMvc restUserMockMvc;

    @MockBean
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
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setEmail("taki@rahal.com");
        loginDTO.setPassword("rahal");
        loginDTO.setRememberMe(true);
        loginDTO.setIdOneSignal("123456789");
        JwtResponseVM jwtResponseVMInput = JwtResponseVM
                .builder()
                .token("azerty")
                .refreshToken("azerty")
                .build();
        when(userService.signinClient(loginDTO)).thenReturn(jwtResponseVMInput);

        // When
        ResultActions mvcResult = restUserMockMvc
                .perform(post(ENTITY_API_URL+"public/signin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestUtil.convertObjectToJsonBytes(loginDTO))
                        .header(SrfGroupConstants.LANG_KEY, "en")
                        .header(SrfGroupConstants.SOURCE_CONNECTED_DEVICE, SourceConnectedDevice.WEB_BROWSER.toString())
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
        when(userService.signinClient(loginDTO)).thenThrow(InvalidPasswordException.class);

        // When
        ResultActions mvcResult = restUserMockMvc
                .perform(post(ENTITY_API_URL+"public/signin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestUtil.convertObjectToJsonBytes(loginDTO))
                        .header(SrfGroupConstants.LANG_KEY, "en")
                        .header(SrfGroupConstants.SOURCE_CONNECTED_DEVICE, SourceConnectedDevice.WEB_BROWSER.toString())
                );

        // Then
        mvcResult
                .andExpect(status().isBadRequest());
    }

    @Test
    void signinGooglePlus() throws Exception {

        // Given
        GooglePlusVM googlePlusVM = GooglePlusVM.builder().build();
        JwtResponseVM jwtResponseVMInput = JwtResponseVM
                .builder()
                .token("azerty")
                .refreshToken("azerty")
                .build();
        when(userService.signinGooglePlus(any())).thenReturn(jwtResponseVMInput);

        // When
        ResultActions mvcResult = restUserMockMvc
                .perform(post(ENTITY_API_URL+"public/signin-google-plus")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestUtil.convertObjectToJsonBytes(googlePlusVM))
                        .header(SrfGroupConstants.LANG_KEY, "en")
                        .header(SrfGroupConstants.SOURCE_CONNECTED_DEVICE, SourceConnectedDevice.WEB_BROWSER.toString())
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
    void signinFacebook() throws Exception {
        // Given
        FacebookVM facebookVM = FacebookVM
                .builder()
                .userID("1")
                .accessToken("azerty")
                .expiresIn("Exipre now")
                .name("test")
                .build();
        JwtResponseVM jwtResponseVM = JwtResponseVM.builder().token("azerty").refreshToken("azerty").build();
        when(userService.signinFacebook(any())).thenReturn(jwtResponseVM);

        // When
        ResultActions mvcResult = restUserMockMvc
                .perform(post(ENTITY_API_URL+"public/signin-facebook")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestUtil.convertObjectToJsonBytes(facebookVM))
                        .header(SrfGroupConstants.LANG_KEY, "en")
                        .header(SrfGroupConstants.SOURCE_CONNECTED_DEVICE, SourceConnectedDevice.WEB_BROWSER.toString())
                );

        // Then
        mvcResult
                .andExpect(status().isOk());
        MvcResult result = mvcResult.andReturn();
        String contentAsString = result.getResponse().getContentAsString();
        JwtResponseVM jwtResponseVMResult = objectMapper.readValue(contentAsString, JwtResponseVM.class);
        assertFalse(jwtResponseVMResult.getToken().isEmpty());
        assertFalse(jwtResponseVMResult.getRefreshToken().isEmpty());
    }

    @Test
    void signinGooglePlusOneTap() throws Exception {

        // Given
        GooglePlusVM googlePlusVM = GooglePlusVM.builder().build();
        JwtResponseVM jwtResponseVMInput = JwtResponseVM
                .builder()
                .token("azerty")
                .refreshToken("azerty")
                .build();
        when(userService.signinGooglePlusOneTap(any())).thenReturn(jwtResponseVMInput);

        // When
        ResultActions mvcResult = restUserMockMvc
                .perform(post(ENTITY_API_URL+"public/signin-google-plus-one-tap")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestUtil.convertObjectToJsonBytes(googlePlusVM))
                        .header(SrfGroupConstants.LANG_KEY, "en")
                        .header(SrfGroupConstants.SOURCE_CONNECTED_DEVICE, SourceConnectedDevice.WEB_BROWSER.toString())
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
        UserDTO userDTOinput = UserDTO.builder()
                .id(1L)
                .username("taki@rahal.com")
                .email("taki@rahal.com")
                .firstName("Taki")
                .build();
        when(userService.findById(userDTOinput.getId())).thenReturn(userDTOinput);

        // When
        ResultActions mvcResult = restUserMockMvc
                .perform(get(ENTITY_API_URL+"public/profile/"+userDTOinput.getId())
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
        UserDTO userDTOinput = UserDTO.builder()
                .id(1L)
                .username("taki@rahal.com")
                .email("taki@rahal.com")
                .firstName("Taki")
                .build();
        when(userService.getCurrentUser()).thenReturn(userDTOinput);

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

    @Test
    @WithMockUser(username = "taki@rahal.com", password = "rahal")
    void updateCurrentUser() throws Exception {
        // Given
        UserDTO userDTOinput = UserDTO.builder()
                .id(1L)
                .username("taki@rahal.com")
                .email("taki@rahal.com")
                .firstName("Taki")
                .build();
        when(userService.updateCurrentUser(any())).thenReturn(userDTOinput);

        // When
        ResultActions mvcResult = restUserMockMvc
                .perform(put(ENTITY_API_URL+"update-current-user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestUtil.convertObjectToJsonBytes(userDTOinput))
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