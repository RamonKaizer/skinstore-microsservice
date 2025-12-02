package com.skinstore.usuario.service;

import com.skinstore.usuario.domain.Role;
import com.skinstore.usuario.domain.UserAccount;
import com.skinstore.usuario.repository.RoleRepository;
import com.skinstore.usuario.repository.UserAccountRepository;
import com.skinstore.usuario.dto.*;
import com.skinstore.usuario.mapper.UserMapper;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserAccountRepository users;
    private final RoleRepository roles;

    @Transactional
    public UserDto create(CreateUserRequest req) {
        String email = req.email().trim().toLowerCase();
        if (users.existsByEmail(email)) throw new IllegalArgumentException("E-mail já utilizado");

        Role role = roles.findById(req.roleId())
                .orElseThrow(() -> new EntityNotFoundException("Role não encontrada"));

        UserAccount e = UserAccount.builder()
                .email(email)
                .nome(req.nome())
                .role(role)
                .ativo(true)
                .build();

        return UserMapper.toDto(users.save(e));
    }

    @Transactional(readOnly = true)
    public UserDto get(Long id) {
        UserAccount e = users.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado"));
        return UserMapper.toDto(e);
    }

    @Transactional(readOnly = true)
    public Page<UserDto> list(Pageable pageable) {
        return users.findAll(pageable).map(UserMapper::toDto);
    }

    @Transactional
    public UserDto update(Long id, UpdateUserRequest req) {
        UserAccount e = users.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado"));

        if (req.email() != null) e.setEmail(req.email().trim().toLowerCase());
        if (req.nome() != null) e.setNome(req.nome());
        if (req.roleId() != null) {
            Role role = roles.findById(req.roleId())
                    .orElseThrow(() -> new EntityNotFoundException("Role não encontrada"));
            e.setRole(role);
        }
        if (req.ativo() != null) e.setAtivo(req.ativo());

        return UserMapper.toDto(users.save(e));
    }

    @Transactional
    public void delete(Long id) {
        UserAccount e = users.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado"));
        users.delete(e);
    }
}
