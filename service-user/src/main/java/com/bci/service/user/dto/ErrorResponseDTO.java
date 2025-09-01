package com.bci.service.user.dto;

import lombok.Getter;

@Getter
public class ErrorResponseDTO {
    private String message;

    public ErrorResponseDTO(String message) {
        this.message = message;
    }

}

