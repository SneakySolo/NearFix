package com.SneakySolo.nearfix.dto;

import com.SneakySolo.nearfix.domain.user.Role;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor @AllArgsConstructor
@Getter @Setter
public class RegisterDTO {

    @NotBlank(message = "this field cannot be empty")
    String fullname;

    @NotBlank (message = "this field cannot be empty")
    String email;

    @NotBlank (message = "this field cannot be empty")
    String password;

    @NotBlank (message = "this field cannot be empty")
    String phone;

    @NotNull
    Role role;
}
