package com.skinstore.usuario.dto;

import com.skinstore.usuario.domain.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public record UpdateUserRequest(
        @Email String email,
        @Size(min = 3, max = 250) String nome,
        Long roleId,
        Boolean ativo
) {}