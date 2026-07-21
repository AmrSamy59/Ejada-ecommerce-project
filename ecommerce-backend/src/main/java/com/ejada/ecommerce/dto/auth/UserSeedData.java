package com.ejada.ecommerce.dto.auth;

import lombok.Data;
import java.util.List;

@Data
public class UserSeedData {
    private String username;
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private List<String> roles;
}
