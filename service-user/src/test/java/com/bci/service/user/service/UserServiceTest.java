package com.bci.service.user.service;

import com.bci.service.user.dto.PhoneDTO;
import com.bci.service.user.dto.UserCreateDTO;
import com.bci.service.user.dto.UserListDTO;
import com.bci.service.user.dto.UserResponseDTO;
import com.bci.service.user.entity.Phone;
import com.bci.service.user.entity.User;
import com.bci.service.user.exception.UserAlreadyExistsException;
import com.bci.service.user.repository.UserRepository;
import com.bci.service.user.util.ValidationUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtService jwtService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private ValidationUtil validationUtil;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createUser_success() {
        // Arrange
        UserCreateDTO userCreateDTO = new UserCreateDTO();
        userCreateDTO.setName("Test User");
        userCreateDTO.setEmail("test@example.com");
        userCreateDTO.setPassword("Password123!");
        userCreateDTO.setPhones(List.of(new PhoneDTO("123456", "1", "57")));

        when(validationUtil.isValidEmail(userCreateDTO.getEmail())).thenReturn(true);
        when(validationUtil.isValidPassword(userCreateDTO.getPassword())).thenReturn(true);
        when(userRepository.existsByEmail(userCreateDTO.getEmail())).thenReturn(false);

        User savedUser = new User();
        savedUser.setId(UUID.randomUUID()); // ✅ UUID en lugar de Long
        savedUser.setName(userCreateDTO.getName());
        savedUser.setEmail(userCreateDTO.getEmail());
        savedUser.setPassword(userCreateDTO.getPassword());
        savedUser.setIsActive(true);

        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        when(jwtService.generateToken(anyString(), any(UUID.class), anyMap())).thenReturn("fake-jwt-token");

        // Act
        UserResponseDTO response = userService.createUser(userCreateDTO);

        // Assert
        assertNotNull(response);
        assertEquals("test@example.com", response.getEmail());
        assertEquals("Test User", response.getName());
        assertEquals("fake-jwt-token", response.getToken());

        verify(userRepository, times(1)).save(any(User.class));
        verify(jwtService, times(1))
                .generateToken(eq("test@example.com"), any(UUID.class), anyMap());
    }

    @Test
    void createUser_invalidEmail_throwsException() {
        UserCreateDTO dto = new UserCreateDTO();
        dto.setEmail("invalidEmail");
        dto.setPassword("Password123!");

        when(validationUtil.isValidEmail(dto.getEmail())).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> userService.createUser(dto));

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void createUser_invalidPassword_throwsException() {
        UserCreateDTO dto = new UserCreateDTO();
        dto.setEmail("test@example.com");
        dto.setPassword("badpass");

        when(validationUtil.isValidEmail(dto.getEmail())).thenReturn(true);
        when(validationUtil.isValidPassword(dto.getPassword())).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> userService.createUser(dto));

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void createUser_emailAlreadyExists_throwsException() {
        UserCreateDTO dto = new UserCreateDTO();
        dto.setEmail("duplicate@example.com");
        dto.setPassword("Password123!");

        when(validationUtil.isValidEmail(dto.getEmail())).thenReturn(true);
        when(validationUtil.isValidPassword(dto.getPassword())).thenReturn(true);
        when(userRepository.existsByEmail(dto.getEmail())).thenReturn(true);

        assertThrows(UserAlreadyExistsException.class, () -> userService.createUser(dto));

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void getAllUser_returnsListOfUsers() {
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setName("Test User");
        user.setEmail("test@example.com");
        user.setPassword("Password123!");
        user.setIsActive(true);

        Phone phone = new Phone();
        phone.setNumber("123456");
        phone.setCitycode("1");
        phone.setContrycode("57");
        phone.setUser(user);
        user.getPhones().add(phone);

        when(userRepository.findAll()).thenReturn(List.of(user));

        List<UserListDTO> users = userService.getAllUser();

        assertNotNull(users);
        assertEquals(1, users.size());
        assertEquals("test@example.com", users.get(0).getEmail());
        assertEquals(1, users.get(0).getPhones().size());

        verify(userRepository, times(1)).findAll();
    }
}
