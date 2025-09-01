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

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Servicio para la gestión de usuarios en el sistema.
 * <p>
 * Permite registrar nuevos usuarios, validar datos de entrada, y consultar la lista de usuarios existentes.
 * También se encarga de generar el token JWT para autenticación y encriptar las contraseñas.
 * </p>
 *
 * @author Nataly Otero Celis
 * @since 2025-08-27
 */
@Service
@Transactional
public class UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private ValidationUtil validationUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;
public UserResponseDTO createUser(UserCreateDTO userCreateDTO) {
    logger.info("Creating user with email: {}", userCreateDTO.getEmail());

    if (!validationUtil.isValidEmail(userCreateDTO.getEmail())) {
        throw new IllegalArgumentException("Formato de correo inválido");
    }

    if (!validationUtil.isValidPassword(userCreateDTO.getPassword())) {
        throw new IllegalArgumentException("Formato de contraseña inválido");
    }

    if (userRepository.existsByEmail(userCreateDTO.getEmail())) {
        logger.warn("Email already exists: {}", userCreateDTO.getEmail());
        throw new UserAlreadyExistsException("El email " + userCreateDTO.getEmail() + " ya está registrado");
    }

    User user = new User();
    user.setId(UUID.randomUUID());
    user.setName(userCreateDTO.getName());
    user.setEmail(userCreateDTO.getEmail());
String hashedPassword = passwordEncoder.encode(userCreateDTO.getPassword());
    user.setPassword(hashedPassword);
    user.setCreated(LocalDateTime.now());
    user.setModified(LocalDateTime.now());
    user.setLast_login(LocalDateTime.now());
    user.setIsActive(true);

    userCreateDTO.getPhones().forEach(phoneDTO -> {
        Phone phone = new Phone();
        phone.setNumber(phoneDTO.getNumber());
        phone.setCitycode(phoneDTO.getCitycode());
        phone.setContrycode(phoneDTO.getContrycode());
        phone.setUser(user);
        user.getPhones().add(phone);
    });


    Map<String, Object> additionalClaims = Map.of(
            "name", user.getName(),
            "role", "USER",
            "isActive", user.getIsActive()
    );

    String accessToken = jwtService.generateToken(
            user.getEmail(),
            user.getId(),
            additionalClaims
    );
    user.setToken(accessToken);

    User savedUser = userRepository.save(user);

    logger.info("User created successfully with ID: {}", savedUser.getId());

    return convertToResponseDTO(savedUser);
}


    public List<UserListDTO> getAllUser() {
        return userRepository.findAll()
                .stream()
                .map(user -> convertToDTO(user, UserListDTO.class))
                .collect(Collectors.toList());
    }

    private UserResponseDTO convertToResponseDTO(User user) {
        ModelMapper modelMapper = new ModelMapper();
        UserResponseDTO userResponseDTO = modelMapper.map(user, UserResponseDTO.class);
        Type listType = new TypeToken<List<PhoneDTO>>(){}.getType();
        List<PhoneDTO> phoneDTOs = modelMapper.map(user.getPhones(), listType);

        userResponseDTO.setPhones(phoneDTOs);

        return userResponseDTO;
    }
    private <T> T convertToDTO(User user, Class<T> dtoClass) {
        ModelMapper modelMapper = new ModelMapper();

        T dto = modelMapper.map(user, dtoClass);

        try {
            java.lang.reflect.Field phonesField = dtoClass.getDeclaredField("phones");
            phonesField.setAccessible(true);

            Type listType = new TypeToken<List<PhoneDTO>>() {}.getType();
            List<PhoneDTO> phoneDTOs = modelMapper.map(user.getPhones(), listType);

            phonesField.set(dto, phoneDTOs);
        } catch (NoSuchFieldException e) {

        } catch (IllegalAccessException e) {
            throw new RuntimeException("Error al mapear phones", e);
        }

        return dto;
    }


}
