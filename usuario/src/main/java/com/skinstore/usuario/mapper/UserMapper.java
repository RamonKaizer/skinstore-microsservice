package com.skinstore.usuario.mapper;

import com.skinstore.usuario.domain.UserAccount;
import com.skinstore.usuario.dto.UserDto;

public final class UserMapper {
    private UserMapper() {}
    public static UserDto toDto(UserAccount e) {
        return new UserDto(
                e.getId(),
                e.getEmail(),
                e.getNome(),
                e.getRole().getId(),
                e.getRole().getName(),
                e.isAtivo(),
                e.getCriadoEm(),
                e.getAlteradoEm()
        );
    }
}
