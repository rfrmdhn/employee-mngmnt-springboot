package com.rfrmd.employeemanagement.employee.repository.spec;

import com.rfrmd.employeemanagement.employee.entity.Employee;
import org.springframework.data.jpa.domain.Specification;

public class EmployeeSpecifications {

    public static Specification<Employee> withKeyword(String keyword) {
        return (root, query, criteriaBuilder) -> {
            if (keyword == null || keyword.isBlank()) {
                return null;
            }
            String likePattern = "%" + keyword.toLowerCase() + "%";
            return criteriaBuilder.or(
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), likePattern),
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("department")), likePattern),
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("position")), likePattern));
        };
    }
}
