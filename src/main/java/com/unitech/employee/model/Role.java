// File: src/main/java/com/unitech/employee/model/Role.java
package com.unitech.employee.model;

/**
 * Role enum stored as STRING in DB.
 * Application maps to authorities by prefixing "ROLE_" at runtime.
 */
public enum Role {
    ADMIN,
    HR,
    EMPLOYEE
}
