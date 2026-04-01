package com.japaneseLearning.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * ApplicationUser entity extending Spring Security's user model
 */
@Entity
@Table(name = "AspNetUsers")
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationUser {
    @Id
    @Column(name = "Id")
    private String id;

    @Column(name = "UserName", nullable = false, unique = true)
    private String username;

    @Column(name = "Email", nullable = false)
    private String email;

    @Column(name = "NormalizedEmail")
    private String normalizedEmail;

    @Column(name = "PasswordHash")
    private String passwordHash;

    @Column(name = "EmailConfirmed")
    private Boolean emailConfirmed = false;

    @Column(name = "LockoutEnabled")
    private Boolean lockoutEnabled = true;

    @Column(name = "AccessFailedCount")
    private Integer accessFailedCount = 0;

    @Column(name = "FirstName")
    private String firstName;

    @Column(name = "LastName")
    private String lastName;

    @Column(name = "PhoneNumber")
    private String phoneNumber;

    @Column(name = "IsAdmin", nullable = false)
    private Boolean isAdmin = false;

    // JLRN-16: OAuth2 provider info
    @Column(name = "Provider")
    private String provider; // "LOCAL" or "GOOGLE"

    @Column(name = "ProviderId")
    private String providerId; // Unique ID from provider

    // JL-28: Avatar URL for user profile picture
    @Column(name = "AvatarUrl", length = 500)
    private String avatarUrl;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNormalizedEmail() {
        return normalizedEmail;
    }

    public void setNormalizedEmail(String normalizedEmail) {
        this.normalizedEmail = normalizedEmail;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public Boolean getEmailConfirmed() {
        return emailConfirmed;
    }

    public void setEmailConfirmed(Boolean emailConfirmed) {
        this.emailConfirmed = emailConfirmed;
    }

    public Boolean getLockoutEnabled() {
        return lockoutEnabled;
    }

    public void setLockoutEnabled(Boolean lockoutEnabled) {
        this.lockoutEnabled = lockoutEnabled;
    }

    public Integer getAccessFailedCount() {
        return accessFailedCount;
    }

    public void setAccessFailedCount(Integer accessFailedCount) {
        this.accessFailedCount = accessFailedCount;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Boolean getIsAdmin() {
        return isAdmin != null && isAdmin;
    }

    public void setIsAdmin(Boolean isAdmin) {
        this.isAdmin = isAdmin != null && isAdmin;
    }

    // JLRN-16: Getters and Setters for OAuth2
    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public String getProviderId() {
        return providerId;
    }

    public void setProviderId(String providerId) {
        this.providerId = providerId;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }
}
