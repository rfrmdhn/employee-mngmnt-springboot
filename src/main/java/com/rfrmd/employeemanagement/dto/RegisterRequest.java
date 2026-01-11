package com.rfrmd.employeemanagement.dto;

import com.rfrmd.employeemanagement.model.Role;

public record RegisterRequest(String name, String email, String password, Role role) {
}
