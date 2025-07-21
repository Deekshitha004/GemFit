package com.example.user_service.service;

import com.example.user_service.dto.UserResponse;
import com.example.user_service.dto.request.RegisterRequest;
import com.example.user_service.mapper.UserMapper;
import com.example.user_service.model.User;
import com.example.user_service.repository.UserRepository;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@Slf4j
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserMapper userMapper;
    public UserResponse getUserProfile(String keycloakId) {
        User user = (User) userRepository.findByKeyCloakId(keycloakId)
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND,
                                "User not found for keycloakId=" + keycloakId));
        return userMapper.toDto(user);
    }

    public UserResponse register(RegisterRequest registerRequest) {
        if(userRepository.existsByEmail(registerRequest.getEmail())){
            User existingUser=userRepository.findByEmail(registerRequest.getEmail());
            UserResponse userResponse=userMapper.toDto(existingUser);
            return userResponse;
        }
        User user=new User();
        user.setEmail(registerRequest.getEmail());
        log.info(registerRequest.getKeyCloakId()); // <-- Line 1
        user.setKeyCloakId(registerRequest.getKeyCloakId()); // <-- Line 2
        user.setPassword(registerRequest.getPassword());
        user.setFirstName(registerRequest.getFirstName());
        user.setLastName(registerRequest.getLastName());
        User savedUser=userRepository.save(user);
        UserResponse userResponse=userMapper.toDto(savedUser);
        return userResponse;
    }


    public Boolean existsByUserId(String userId) {
        log.info("Calling User validation APi for UserID:{}",userId);
        return userRepository.existsByKeyCloakId(userId);
    }
}
