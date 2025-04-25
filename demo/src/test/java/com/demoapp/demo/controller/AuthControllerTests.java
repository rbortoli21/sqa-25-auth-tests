package com.demoapp.demo.controller;

import com.demoapp.demo.dto.EmailDTO;
import com.demoapp.demo.dto.ErrorResponse;
import com.demoapp.demo.dto.UserDTO;
import com.demoapp.demo.model.User;
import com.demoapp.demo.repository.UserRepository;
import com.demoapp.demo.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class AuthControllerTests {

    private UserRepository userRepository;
    private UserService userService;
    private AuthController authController;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        userService = new UserService(userRepository);
        authController = new AuthController(userService);
    }

    @Test
    void testSignupWithClearlyInvalidEmail() {
        String invalidEmail = "@.";
        String password = "Abcdef1@";

        UserDTO userDTO = new UserDTO();
        userDTO.setEmail(invalidEmail);
        userDTO.setPassword(password);

        ResponseEntity<?> response = authController.signup(userDTO);

        assertEquals(422, response.getStatusCodeValue());
        assertTrue(response.getBody() instanceof ErrorResponse);
    }

    @Test
    void testSigninInvalidCredentials() {
        UserDTO userDTO = new UserDTO();
        userDTO.setEmail("test@email.com");
        userDTO.setPassword("wrongpass");

        when(userRepository.findByEmail("test@email.com")).thenReturn(Optional.empty());

        ResponseEntity<?> response = authController.signin(userDTO);
        assertEquals(401, response.getStatusCodeValue());
        assertTrue(response.getBody() instanceof ErrorResponse);
    }

    @Test
    void testSignupInvalidEmail() {
        UserDTO userDTO = new UserDTO();
        userDTO.setEmail("invalid");
        userDTO.setPassword("Abcdef1@");

        ResponseEntity<?> response = authController.signup(userDTO);
        assertEquals(422, response.getStatusCodeValue());
        assertTrue(response.getBody() instanceof ErrorResponse);
    }

    @Test
    void testSignupInvalidPassword() {
        UserDTO userDTO = new UserDTO();
        userDTO.setEmail("test@email.com");
        userDTO.setPassword("invalid");

        ResponseEntity<?> response = authController.signup(userDTO);
        assertEquals(422, response.getStatusCodeValue());
        assertTrue(response.getBody() instanceof ErrorResponse);
    }

    @Test
    void testSignupEmailAlreadyUsed() {
        UserDTO userDTO = new UserDTO();
        userDTO.setEmail("test@email.com");
        userDTO.setPassword("Abcdef1@");

        User existingUser = new User();
        existingUser.setEmail("test@email.com");
        existingUser.setPassword("Abcdef1@");

        when(userRepository.findByEmail("test@email.com")).thenReturn(Optional.of(existingUser));

        ResponseEntity<?> response = authController.signup(userDTO);
        assertEquals(409, response.getStatusCodeValue());
        assertTrue(response.getBody() instanceof ErrorResponse);
    }

    @Test
    void testSignupSuccess() {
        UserDTO userDTO = new UserDTO();
        userDTO.setEmail("test@email.com");
        userDTO.setPassword("Abcdef1@");

        when(userRepository.findByEmail("test@email.com")).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setId(1L);
            return user;
        });

        ResponseEntity<?> response = authController.signup(userDTO);
        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody() instanceof User);
        assertEquals("test@email.com", ((User) response.getBody()).getEmail());
    }

    @Test
    void testSigninSuccess() {
        UserDTO userDTO = new UserDTO();
        userDTO.setEmail("test@email.com");
        userDTO.setPassword("Abcdef1@");

        User user = new User();
        user.setEmail("test@email.com");
        user.setPassword("Abcdef1@");

        when(userRepository.findByEmail("test@email.com")).thenReturn(Optional.of(user));

        ResponseEntity<?> response = authController.signin(userDTO);
        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody() instanceof java.util.Map);
    }

    @Test
    void testResetPasswordUserNotFound() {
        EmailDTO emailDTO = new EmailDTO();
        emailDTO.setEmail("notfound@email.com");

        when(userRepository.findByEmail("notfound@email.com")).thenReturn(Optional.empty());

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

        when(userRepository.findByEmail("test@email.com")).thenReturn(Optional.of(user));

        ResponseEntity<?> response = authController.resetPassword(emailDTO);
        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody() instanceof java.util.Map);
    }
}
