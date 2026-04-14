package com.neighbor.care.user.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_seq_gen")
    @SequenceGenerator(
            name = "user_seq_gen",
            sequenceName = "user_id_seq",
            initialValue = 100000,
            allocationSize = 1
    )
    private Long id;

    @Column(unique = true, name = "social_id")
    private String socialId;
    @Column(name = "provider")
    private String provider;
    @Column(name = "email")
    private String email;
    private String profileImage;


    @Column(name = "name")
    private String name;
    @Column(name = "phone_number")
    private String phoneNumber;
    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Column(name ="role")
    @Enumerated(EnumType.STRING)
    private Role role;
    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private Status status;

    @Column(name = "created_at")
    private LocalDate createdAt;
    @Column(name = "updated_at")
    private LocalDate updatedAt;

    @Builder
    public User(String socialId, String provider, String name, String phoneNumber, LocalDate birthDate, Role role, Status status) {
        this.socialId = socialId;
        this.provider =provider;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.birthDate = birthDate;
        this.role = role;
        this.status = status;
    }

    public void updateProfile(String email, String name, String profileImage){
        this.email = email;
        this.name = name;
        this.profileImage = profileImage;
    }

    @PrePersist
    public void setCreatedAt(){
        this.createdAt = LocalDateTime.now().toLocalDate();
        this.updatedAt = LocalDateTime.now().toLocalDate();
    }

    @PreUpdate
    public void setUpdatedAt(){
        this.updatedAt = LocalDateTime.now().toLocalDate();
    }
    public enum  Status{
        ACTIVE,
        INACTIVE,
        SUSPENDED,
        DELETED
    }

    public enum Role{
        GUARDIAN,
        CAREGIVER,
        ADMIN
    }
}
