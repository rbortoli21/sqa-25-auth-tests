package com.demoapp.demo.controller;
import com.demoapp.demo.dto.EmailDTO;
import com.demoapp.demo.dto.ErrorResponse;
import com.demoapp.demo.dto.UserDTO;
import com.demoapp.demo.model.User;
import com.demoapp.demo.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthControllerTests {

    private UserService userService;
    private AuthController authController;

    @BeforeEach
    void setUp() {
        userService = mock(UserService.class);
        authController = new AuthController(userService);
    }

    @Test
    void testSignupAcceptsClearlyInvalidEmail() {
        String invalidEmail = "@.";
        String password = "Abcdef1@";

        UserDTO userDTO = new UserDTO();
        userDTO.setEmail(invalidEmail);
        userDTO.setPassword(password);

        when(userService.isEmailValid(invalidEmail)).thenReturn(true);
        when(userService.isPasswordValid(password)).thenReturn(true);
        when(userService.findByEmail(invalidEmail)).thenReturn(null);

        User user = new User();
        user.setEmail(invalidEmail);
        user.setPassword(password);

        when(userService.createUser(invalidEmail, password)).thenReturn(user);

        ResponseEntity<?> response = authController.signup(userDTO);

        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody() instanceof User);
        assertEquals(invalidEmail, ((User) response.getBody()).getEmail());
    }

    @Test
    void testSignupInvalidEmail() {
        UserDTO userDTO = new UserDTO();
        userDTO.setEmail("invalid");
        userDTO.setPassword("Abcdef1@");

        when(userService.isEmailValid("invalid")).thenReturn(false);

        ResponseEntity<?> response = authController.signup(userDTO);
        assertEquals(422, response.getStatusCodeValue());
        assertTrue(response.getBody() instanceof ErrorResponse);
    }

    @Test
    void testSignupInvalidPassword() {
        UserDTO userDTO = new UserDTO();
        userDTO.setEmail("test@email.com");
        userDTO.setPassword("invalid");

        when(userService.isEmailValid("test@email.com")).thenReturn(true);
        when(userService.isPasswordValid("invalid")).thenReturn(false);

        ResponseEntity<?> response = authController.signup(userDTO);
        assertEquals(422, response.getStatusCodeValue());
        assertTrue(response.getBody() instanceof ErrorResponse);
    }

    @Test
    void testSignupEmailAlreadyUsed() {
        UserDTO userDTO = new UserDTO();
        userDTO.setEmail("test@email.com");
        userDTO.setPassword("Abcdef1@");

        when(userService.isEmailValid("test@email.com")).thenReturn(true);
        when(userService.isPasswordValid("Abcdef1@")).thenReturn(true);
        when(userService.findByEmail("test@email.com")).thenReturn(new User());

        ResponseEntity<?> response = authController.signup(userDTO);
        assertEquals(409, response.getStatusCodeValue());
        assertTrue(response.getBody() instanceof ErrorResponse);
    }

    @Test
    void testSignupSuccess() {
        UserDTO userDTO = new UserDTO();
        userDTO.setEmail("test@email.com");
        userDTO.setPassword("Abcdef1@");

        when(userService.isEmailValid("test@email.com")).thenReturn(true);
        when(userService.isPasswordValid("Abcdef1@")).thenReturn(true);
        when(userService.findByEmail("test@email.com")).thenReturn(null);

        User user = new User();
        user.setEmail("test@email.com");
        user.setPassword("Abcdef1@");

        when(userService.createUser("test@email.com", "Abcdef1@")).thenReturn(user);

        ResponseEntity<?> response = authController.signup(userDTO);
        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody() instanceof User);
    }

    @Test
    void testSigninInvalidCredentials() {
        UserDTO userDTO = new UserDTO();
        userDTO.setEmail("test@email.com");
        userDTO.setPassword("wrongpass");

        when(userService.isEmailValid("test@email.com")).thenReturn(true);
        when(userService.isPasswordValid("wrongpass")).thenReturn(true);
        when(userService.findByEmail("test@email.com")).thenReturn(null);

        ResponseEntity<?> response = authController.signin(userDTO);
        assertEquals(401, response.getStatusCodeValue());
        assertTrue(response.getBody() instanceof ErrorResponse);
    }

    @Test
    void testSigninSuccess() {
        UserDTO userDTO = new UserDTO();
        userDTO.setEmail("test@email.com");
        userDTO.setPassword("Abcdef1@");

        User user = new User();
        user.setEmail("test@email.com");
        user.setPassword("Abcdef1@");

        when(userService.isEmailValid("test@email.com")).thenReturn(true);
        when(userService.isPasswordValid("Abcdef1@")).thenReturn(true);
        when(userService.findByEmail("test@email.com")).thenReturn(user);

        ResponseEntity<?> response = authController.signin(userDTO);
        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody() instanceof java.util.Map);
    }

    @Test
    void testResetPasswordUserNotFound() {
        EmailDTO emailDTO = new EmailDTO();
        emailDTO.setEmail("notfound@email.com");

        when(userService.isEmailValid("notfound@email.com")).thenReturn(true);
        when(userService.findByEmail("notfound@email.com")).thenReturn(null);

        ResponseEntity<?> response = authController.resetPassword(emailDTO);
        assertEquals(404, response.getStatusCodeValue());
        assertTrue(response.getBody() instanceof ErrorResponse);
    }

    @Test
    void testResetPasswordSuccess() {
        EmailDTO emailDTO = new EmailDTO();
        emailDTO.setEmail("test@email.com");

        User user = new User();
        user.setEmail("test@email.com");

        when(userService.isEmailValid("test@email.com")).thenReturn(true);
        when(userService.findByEmail("test@email.com")).thenReturn(user);

        ResponseEntity<?> response = authController.resetPassword(emailDTO);
        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody() instanceof java.util.Map);
    }
}
