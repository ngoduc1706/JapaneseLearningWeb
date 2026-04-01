package com.japaneseLearning.dto;

public record UserProfileDTO(
    String id,
    String username,
    String email,
    String firstName,
    String lastName,
    String phoneNumber,
    String avatarUrl,
    String provider,
    Boolean emailConfirmed,
    Boolean isAdmin
) {}
