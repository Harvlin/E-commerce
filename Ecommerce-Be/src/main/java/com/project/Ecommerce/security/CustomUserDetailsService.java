package com.project.Ecommerce.security;

import com.project.Ecommerce.domain.entity.UserEntity;
import com.project.Ecommerce.exception.NotFoundException;
import com.project.Ecommerce.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        UserEntity userEntity = userRepository.findByEmail(username)
                .orElseThrow(()-> new NotFoundException("User/Email Not found"));

        return AuthUser.builder()
                .user(userEntity)
                .build();
    }
}