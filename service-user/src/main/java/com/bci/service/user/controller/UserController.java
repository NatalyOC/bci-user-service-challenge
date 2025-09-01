package com.bci.service.user.controller;

import com.bci.service.user.dto.ErrorResponseDTO;
import com.bci.service.user.dto.UserCreateDTO;
import com.bci.service.user.dto.UserListDTO;
import com.bci.service.user.dto.UserResponseDTO;
import com.bci.service.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * Controlador REST para gestionar usuarios.
 * <p>
 * Provee endpoints para crear y obtener usuarios. Requiere autenticación JWT para acceder a ciertos endpoints.
 *
 * @author Nataly Otero Celis
 * @version 1.0
 * @since 2025-08-27
 */

@RestController
@RequestMapping("/api/users/v1")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     *
     * @param userCreateDTO objeto con los datos del usuario
     * @return usuario creado
     */
    @PostMapping
    @Operation(summary = "Crear un usuario", description = "Registra un nuevo usuario en el sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Usuario creado exitosamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Error en la solicitud",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    public ResponseEntity<?> createUser(@Valid @RequestBody UserCreateDTO userCreateDTO) {


            UserResponseDTO response = userService.createUser(userCreateDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        }

    /**
     *
     * @return Lista de usuarios creados
     */
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de usuarios",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserListDTO.class))),
            @ApiResponse(responseCode = "400", description = "Error en la solicitud",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    public ResponseEntity<?> getAllUsers(){

            List<UserListDTO> users= userService.getAllUser();
            return ResponseEntity.status(HttpStatus.OK).body(users);

    }
}
