package com.example.networktechnologiesproject1.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
@Schema(description = "Entity representing a user of the library system")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Schema(description = "Unique identifier for each user", example = "101", required = true)
    private Integer userId;

    @Schema(description = "The user's chosen username", example = "john_doe")
    private String username;

    // Note: Passwords are generally not included in API documentation for security reasons
    private String password;

    @Schema(description = "Specifies the user's role in the system", example = "ROLE_STAFF")
    private String role;

    @Schema(description = "The user's email address", example = "john.doe@example.com")
    private String email;

    @Schema(description = "The full name of the user", example = "John Doe")
    private String name;

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    @JsonProperty("username") // Ensure this matches the JSON property for serialization/deserialization
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @JsonProperty("password") // Ensure this matches the JSON property for serialization/deserialization
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @JsonProperty("role") // Ensure this matches the JSON property for serialization/deserialization
    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    @JsonProperty("email") // Ensure this matches the JSON property for serialization/deserialization
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @JsonProperty("name") // Ensure this matches the JSON property for serialization/deserialization
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", username='" + username + '\'' +
                ", password='[PROTECTED]'" +
                ", role='" + role + '\'' +
                ", email='" + email + '\'' +
                ", name='" + name + '\'' +
                '}';
    }

public Integer getId() {
    return userId;
}
}
