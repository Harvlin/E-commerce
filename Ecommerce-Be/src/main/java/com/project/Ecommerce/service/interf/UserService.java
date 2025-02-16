package com.project.Ecommerce.service.interf;

import com.project.Ecommerce.domain.dto.UserDto;
import com.project.Ecommerce.domain.dto.request.LoginRequest;
import com.project.Ecommerce.domain.dto.response.Response;
import com.project.Ecommerce.domain.entity.UserEntity;

public interface UserService {
    Response registerUser(UserDto userDto);
    Response loginUser(LoginRequest loginRequest);
    Response getAllUsers();
    UserEntity getLoggedInUser();
    Response getUserInfoAndOrderHistory();
}
