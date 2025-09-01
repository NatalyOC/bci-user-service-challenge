package com.bci.service.user.dto;



import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PhoneDTO {
    @NotBlank(message = "El número es obligatorio")
    @Size(min = 7, max = 15, message = "El número debe tener entre 7 y 15 dígitos")
    private String number;

    @NotBlank(message = "El código de ciudad es obligatorio")
    @Size(min = 1, max = 4, message = "El código de ciudad debe tener entre 1 y 4 dígitos")
    private String citycode;

    @NotBlank(message = "El código de país es obligatorio")
    @Size(min = 1, max = 4, message = "El código de país debe tener entre 1 y 4 dígitos")
    private String contrycode;
}
