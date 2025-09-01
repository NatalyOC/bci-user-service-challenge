package com.bci.service.user.entity;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.UUID;

@Entity
@Table(name = "phones")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Phone {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(columnDefinition = "BINARY(16)", updatable = false, nullable = false)
    private UUID id;



    @NotBlank(message = "El número es obligatorio")
    @Size(min = 7, max = 15, message = "El número debe tener entre 7 y 15 dígitos")
    @Column(nullable = false, length = 15)
    private String number;

    @NotBlank(message = "El código de ciudad es obligatorio")
    @Size(min = 1, max = 4, message = "El código de ciudad debe tener entre 1 y 4 dígitos")
    @Column(nullable = false, length = 4)
    private String citycode;

    @NotBlank(message = "El código de país es obligatorio")
    @Size(min = 1, max = 4, message = "El código de país debe tener entre 1 y 4 dígitos")
    @Column(nullable = false, length = 4)
    private String contrycode;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false, columnDefinition = "BINARY(16)")
    private User user;

}
