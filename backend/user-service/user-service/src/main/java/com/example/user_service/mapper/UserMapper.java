package com.example.user_service.mapper;

import com.example.user_service.dto.UserResponse;
import com.example.user_service.model.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserResponse toDto(User user){

        UserResponse userResponse=new UserResponse();
        userResponse.setId(user.getId());
        userResponse.setKeyCloakId(user.getKeyCloakId());
        userResponse.setEmail(user.getEmail());
        userResponse.setPassword(user.getPassword());
        userResponse.setFirstName(user.getFirstName());
        userResponse.setLastName(user.getLastName());
        userResponse.setCreatedAt(user.getCreatedAt());
        userResponse.setUpdatedAt(user.getUpdatedAt());
        userResponse.setRole(user.getRole());
        return userResponse;
    }
}
