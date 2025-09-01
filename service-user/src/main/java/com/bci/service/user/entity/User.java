package com.bci.service.user.entity;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @Column(columnDefinition = "BINARY(16)")

    private UUID id;

    @NotBlank(message = "El nombre es obligatorio")
    @Size( max = 250, message = "El nombre debe tener max 250 caracteres")
    @Column(nullable = false, length = 50)
    private String name;

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El email debe tener un formato válido")
    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @NotBlank(message = "El password es obligatorio")
    @Size(min = 8, max = 100, message = "El password debe tener entre 8 y 100 caracteres")
    @Column(nullable = false, length = 100)
    private String password;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Phone> phones = new ArrayList<>();

    @Column(nullable = false)
    private Boolean isActive = true;

    @Column(length = 1000)
    private String token;

    @Column(nullable = false, updatable = false)
    private LocalDateTime created;

    @Column(nullable = false)
    private LocalDateTime modified;

    @Column(nullable = false)
    private LocalDateTime last_login;


}
