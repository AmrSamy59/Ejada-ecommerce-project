package com.ejada.ecommerce.mapper;

import com.ejada.ecommerce.dto.AuthDto;
import com.ejada.ecommerce.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "roles", ignore = true)
    User toEntity(AuthDto.RegisterRequest request);
}
