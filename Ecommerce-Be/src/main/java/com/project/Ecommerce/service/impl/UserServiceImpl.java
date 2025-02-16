package com.project.Ecommerce.service.impl;

import com.project.Ecommerce.domain.dto.UserDto;
import com.project.Ecommerce.domain.dto.request.LoginRequest;
import com.project.Ecommerce.domain.dto.response.Response;
import com.project.Ecommerce.domain.entity.UserEntity;
import com.project.Ecommerce.domain.enums.UserRole;
import com.project.Ecommerce.exception.InvalidCredentialsException;
import com.project.Ecommerce.exception.NotFoundException;
import com.project.Ecommerce.mapper.EntityDtoMapper;
import com.project.Ecommerce.repository.UserRepository;
import com.project.Ecommerce.security.JwtUtils;
import com.project.Ecommerce.service.interf.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final EntityDtoMapper entityDtoMapper;

    @Override
    public Response registerUser(UserDto userDto) {
        UserRole role = UserRole.USER;
        if (userDto.getRole() != null && userDto.getRole().equals("admin")) {
            role = UserRole.ADMIN;
        }

        UserEntity userEntity = UserEntity.builder()
                .name(userDto.getName())
                .email(userDto.getEmail())
                .password(passwordEncoder.encode(userDto.getPassword()))
                .phoneNumber(userDto.getPhoneNumber())
                .userRole(role)
                .build();

        UserEntity savedUserEntity = userRepository.save(userEntity);
        UserDto mappedUserToDtoBasic = entityDtoMapper.mapUserToDtoBasic(savedUserEntity);
        return Response.builder()
                .status(201)
                .message("User successfully added")
                .user(mappedUserToDtoBasic)
                .build();
    }

    @Override
    public Response loginUser(LoginRequest loginRequest) {
        UserEntity userEntity = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(
                        () -> new NotFoundException("Email not found")
                );
        if (!passwordEncoder.matches(loginRequest.getPassword(), userEntity.getPassword())) {
            throw new InvalidCredentialsException("Password incorrect");
        }

        String token = jwtUtils.generateToken(userEntity);
        return Response.builder()
                .status(200)
                .message("User logged in")
                .token(token)
                .expirationTime("6 month")
                .role(userEntity.getUserRole().name())
                .build();
    }

    @Override
    public Response getAllUsers() {
        List<UserEntity> users = userRepository.findAll();
        List<UserDto> userDtos = users.stream()
                .map(entityDtoMapper::mapUserToDtoBasic)
                .collect(Collectors.toList());

        return Response.builder()
                .status(200)
                .message("Successfully retrieved all users")
                .userList(userDtos)
                .build();
    }

    @Override
    public UserEntity getLoggedInUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        log.info("User email is: {}", email);
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("Email not found"));
    }

    @Override
    public Response getUserInfoAndOrderHistory() {
        UserEntity userEntity = getLoggedInUser();
        UserDto userDto = entityDtoMapper.mapUserToDtoBasic(userEntity);

        return Response.builder()
                .status(200)
                .user(userDto)
                .build();
    }
}
