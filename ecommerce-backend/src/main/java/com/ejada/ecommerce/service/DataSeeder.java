package com.ejada.ecommerce.service;

import com.ejada.ecommerce.entity.Role;
import com.ejada.ecommerce.entity.User;
import com.ejada.ecommerce.entity.Product;
import com.ejada.ecommerce.repository.RoleRepository;
import com.ejada.ecommerce.repository.UserRepository;
import com.ejada.ecommerce.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import org.springframework.core.io.ClassPathResource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.math.BigDecimal;
import com.ejada.ecommerce.dto.auth.UserSeedData;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final PasswordEncoder passwordEncoder;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void run(String... args) throws Exception {
        seedRoles();
        seedUsers();
        seedProducts();
    }

    private void seedRoles() {
        for (com.ejada.ecommerce.entity.RoleName roleName : com.ejada.ecommerce.entity.RoleName.values()) {
            if (roleRepository.findByName(roleName).isEmpty()) {
                roleRepository.save(Role.builder().name(roleName).build());
            }
        }
    }

    private void seedUsers() {
        // Seed Users from JSON
        try {
            InputStream inputStream = new ClassPathResource("data/users.json").getInputStream();
            List<UserSeedData> seedUsers = objectMapper.readValue(inputStream, new TypeReference<List<UserSeedData>>() {});
            
            for (UserSeedData seedData : seedUsers) {
                if (!userRepository.existsByUsername(seedData.getUsername())) {
                    Set<Role> userRoles = seedData.getRoles().stream()
                            .map(roleNameStr -> roleRepository.findByName(com.ejada.ecommerce.entity.RoleName.valueOf(roleNameStr)).orElseThrow(() -> new RuntimeException("Role not found: " + roleNameStr)))
                            .collect(Collectors.toSet());
                            
                    User user = User.builder()
                            .username(seedData.getUsername())
                            .email(seedData.getEmail())
                            .password(passwordEncoder.encode(seedData.getPassword()))
                            .firstName(seedData.getFirstName())
                            .lastName(seedData.getLastName())
                            .roles(userRoles)
                            .build();
                            
                    userRepository.save(user);
                }
            }
        } catch (Exception e) {
            System.err.println("Failed to seed users from JSON: " + e.getMessage());
        }
    }

    private void seedProducts() {
        if (productRepository.count() == 0) {
            try {
                InputStream inputStream = new ClassPathResource("data/products.json").getInputStream();
                List<Product> defaultProducts = objectMapper.readValue(inputStream, new TypeReference<List<Product>>() {});
                productRepository.saveAll(defaultProducts);
            } catch (Exception e) {
                System.err.println("Failed to seed products from JSON: " + e.getMessage());
            }
        }
    }
}
