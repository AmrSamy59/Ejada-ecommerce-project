package com.ejada.ecommerce.mapper;

import com.ejada.ecommerce.dto.auth.RegisterRequest;
import com.ejada.ecommerce.dto.user.UserResponse;
import com.ejada.ecommerce.entity.Role;
import com.ejada.ecommerce.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "isActive", ignore = true)
    User toEntity(RegisterRequest request);

    @Mapping(target = "roles", source = "roles", qualifiedByName = "mapRoles")
    UserResponse toDto(User user);

    @Named("mapRoles")
    default Set<String> mapRoles(Set<Role> roles) {
        if (roles == null) return null;
        return roles.stream().map(Role::getName).collect(Collectors.toSet());
    }
}
