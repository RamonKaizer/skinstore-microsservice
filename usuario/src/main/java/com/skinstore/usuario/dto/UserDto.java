package com.skinstore.usuario.dto;

import com.skinstore.usuario.domain.Role;
import java.time.LocalDateTime;

public record UserDto(
        Long id,
        String email,
        String nome,
        Long roleId,
        String roleName,
        boolean ativo,
        LocalDateTime criadoEm,
        LocalDateTime alteradoEm
) {}
