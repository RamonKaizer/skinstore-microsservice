package com.skinstore.usuario.dto;

import com.skinstore.usuario.domain.Role;
import jakarta.validation.constraints.*;

public record CreateUserRequest(
        @NotBlank @Email String email,
        @Size(min = 3, max = 250) String nome,
        @NotNull Long roleId
) {}
