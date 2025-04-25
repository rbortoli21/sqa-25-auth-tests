package com.demoapp.demo.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.demoapp.demo.model.User;
import com.demoapp.demo.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

@SpringBootTest
public class UserServiceTests {

  private UserRepository userRepository;
  private UserService userService;

  @BeforeEach
  void setUp() {
    userRepository = mock(UserRepository.class);
    userService = new UserService(userRepository);
  }

  @Test
  @DisplayName("Test if password is valid according to regex")
  void testIsPasswordValid() {
    String password = "Password123!";
    UserService userService = new UserService(null);
    boolean isValid = userService.isPasswordValid(password);
    assertTrue(isValid, "Password should be valid according to the regex.");
  }

  @Test
  @DisplayName("Test if password is invalid according to regex")
  void testIsPasswordInvalid() {
    String password = "password123";
    UserService userService = new UserService(null);
    boolean isValid = userService.isPasswordValid(password);
    assertTrue(!isValid, "Password should be invalid according to the regex.");
  }

  @Test
  void testIsEmailValid() {
    assertTrue(userService.isEmailValid("test@email.com"));
    assertFalse(userService.isEmailValid("@."));
    assertFalse(userService.isEmailValid("invalidemail"));
    assertFalse(userService.isEmailValid(null));
  }

  @Test
  void testShouldNotCreateUserWithEmptyPassword() {
    String email = "test@email.com";
    String password = "";
    User user = new User();
    user.setEmail(email);
    user.setPassword(password);

    when(userRepository.save(any(User.class))).thenReturn(user);

    assertThrows(Exception.class, () -> userService.createUser(email, password));
  }

  @Test
  void testShouldCreateUserWithEncryptedPassword() {
    String email = "test@email.com";
    String password = "passwordBeforeEncrypt";
    User user = new User();
    user.setEmail(email);
    user.setPassword(password);

    when(userRepository.save(any(User.class))).thenReturn(user);

    User created = userService.createUser(email, password);

    assertNotEquals(password, created.getPassword());
  }

  @Test
  void testCreateUser() {
    String email = "test@email.com";
    String password = "SenhaForte123";
    User user = new User();
    user.setEmail(email);
    user.setPassword(password);

    when(userRepository.save(any(User.class))).thenReturn(user);

    User created = userService.createUser(email, password);

    assertEquals(email, created.getEmail());
    assertEquals(password, created.getPassword());
    verify(userRepository, times(1)).save(any(User.class));
  }

  @Test
  void testFindByEmail() {
    String email = "test@email.com";
    User user = new User();
    user.setEmail(email);

    when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

    User found = userService.findByEmail(email);

    assertNotNull(found);
    assertEquals(email, found.getEmail());
  }

  @Test
  void testFindByEmailNotFound() {
    when(userRepository.findByEmail("notfound@email.com")).thenReturn(Optional.empty());
    User found = userService.findByEmail("notfound@email.com");
    assertNull(found);
  }
}
