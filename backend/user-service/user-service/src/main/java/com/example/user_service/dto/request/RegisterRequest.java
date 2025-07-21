package com.example.user_service.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {

    @JsonProperty("keyCloakId") // 🔥 strongly recommended
    private String keyCloakId;
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid EMail Format")
    private String email;
    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "password must have atleast 6 characters")
    private String password;
    @NotBlank(message = "First Name is required")
    private String firstName;
    @NotBlank(message = "LastName is required")
    private String lastName;
}
