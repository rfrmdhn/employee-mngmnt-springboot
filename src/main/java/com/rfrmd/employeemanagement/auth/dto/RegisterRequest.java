package com.rfrmd.employeemanagement.auth.dto;

import com.rfrmd.employeemanagement.auth.entity.Role;

public record RegisterRequest(String name, String email, String password, Role role) {
}
