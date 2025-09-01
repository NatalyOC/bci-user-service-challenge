package com.bci.service.user.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserCreateDTO {
    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 2, max = 50, message = "El nombre debe tener entre 2 y 50 caracteres")
    private String name;

    @NotBlank(message = "El passwordes obligatorio")
    @Size(min = 2, max = 50, message = "El password debe tener entre 2 y 50 caracteres")
    private String password;

    @NotBlank(message = "El email es obligatorio")
    private String email;

    @NotEmpty(message = "Debe proporcionar al menos un teléfono")
    @Valid
    private List<PhoneDTO> phones=new ArrayList<>();
}
