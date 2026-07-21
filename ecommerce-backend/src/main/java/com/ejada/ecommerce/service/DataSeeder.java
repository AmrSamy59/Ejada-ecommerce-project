package com.ejada.ecommerce.service;

import com.ejada.ecommerce.entity.Role;
import com.ejada.ecommerce.entity.User;
import com.ejada.ecommerce.repository.RoleRepository;
import com.ejada.ecommerce.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.security.superadmin.username:superuser}")
    private String superAdminUsername;

    @Value("${app.security.superadmin.password:superpassword}")
    private String superAdminPassword;

    @Value("${app.security.superadmin.email:super@ejada.com}")
    private String superAdminEmail;

    @Override
    public void run(String... args) throws Exception {
        seedRoles();
        seedSuperAdmin();
    }

    private void seedRoles() {
        List<String> rolesToSeed = List.of("USER", "ADMIN", "SUPER_ADMIN");
        for (String roleName : rolesToSeed) {
            if (roleRepository.findByName(roleName).isEmpty()) {
                roleRepository.save(Role.builder().name(roleName).build());
            }
        }
    }

    private void seedSuperAdmin() {
        if (!userRepository.existsByUsername(superAdminUsername)) {
            Role superAdminRole = roleRepository.findByName("SUPER_ADMIN").orElseThrow();
            
            User superAdmin = User.builder()
                    .username(superAdminUsername)
                    .email(superAdminEmail)
                    .password(passwordEncoder.encode(superAdminPassword))
                    .firstName("Super")
                    .lastName("Admin")
                    .roles(Set.of(superAdminRole))
                    .build();
                    
            userRepository.save(superAdmin);
        }
    }
}
