package com.example.coinserver.common.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {
    @NotBlank
    @Pattern(regexp = "^[a-zA-Z0-9]{1,32}$")
    private String login;
    @NotBlank
    @Pattern(regexp = "^[a-zA-Z0-9]{1,32}$")
    private String password;
}
