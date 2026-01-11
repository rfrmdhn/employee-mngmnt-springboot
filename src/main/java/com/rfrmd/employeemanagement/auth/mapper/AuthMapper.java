package com.rfrmd.employeemanagement.auth.mapper;

import com.rfrmd.employeemanagement.auth.dto.RegisterRequest;
import com.rfrmd.employeemanagement.auth.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AuthMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "password", ignore = true) // Handled by password encoder
    User toUser(RegisterRequest request);
}
