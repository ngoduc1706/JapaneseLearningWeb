package com.japaneseLearning.dto;

public record ChangePasswordDTO(
    String currentPassword,
    String newPassword,
    String confirmPassword
) {}
