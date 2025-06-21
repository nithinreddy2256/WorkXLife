package com.workxlife.authentication_service.dto;

public class AuthResponse {

    private String token;
    private Long userId;
    private Long employeeId;

    public AuthResponse(String token, Long userId, Long employeeId) {
        this.token = token;
        this.userId = userId;
        this.employeeId = employeeId;
    }

    public AuthResponse(String token, Long userId) {
        this.token = token;
        this.userId = userId;
    }

    // Getters & setters...
    public String getToken() { return token; }
    public Long getUserId() { return userId; }
    public Long getEmployeeId() { return employeeId; }

    public void setToken(String token) { this.token = token; }
    public void setUserId(Long userId) { this.userId = userId; }
    public void setEmployeeId(Long employeeId) { this.employeeId = employeeId; }
}
