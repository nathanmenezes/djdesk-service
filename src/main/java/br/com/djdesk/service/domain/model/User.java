package br.com.djdesk.service.domain.model;

import br.com.djdesk.service.shared.entity.AbstractAuditEntity;
import br.com.djdesk.service.domain.enums.UserStatus;
import br.com.djdesk.service.domain.enums.UserType;
import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class User extends AbstractAuditEntity {

    @Column(name = "full_name", nullable = false, length = 150)
    private String fullName;

    @Column(name = "artistic_name", nullable = false, length = 100)
    private String artisticName;

    @Column(name = "email", nullable = false, unique = true, length = 255)
    private String email;

    @Column(name = "password", nullable = false, length = 255)
    private String password;

    @Column(name = "phone", length = 20)
    private String phone;

    @Column(name = "profile_photo_url", length = 500)
    private String profilePhotoUrl;

    @Column(name = "bio", columnDefinition = "TEXT")
    private String bio;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private UserStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "user_type", nullable = false, length = 20)
    private UserType userType;

    protected User() {
        super();
    }

    public User(
            String fullName,
            String artisticName,
            String email,
            String password,
            String phone,
            String profilePhotoUrl,
            String bio,
            UserType userType
    ) {
        super();
        this.fullName = fullName;
        this.artisticName = artisticName;
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.profilePhotoUrl = profilePhotoUrl;
        this.bio = bio;
        this.status = UserStatus.ACTIVE;
        this.userType = userType;
    }

    public String getFullName() {
        return fullName;
    }

    public String getArtisticName() {
        return artisticName;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getPhone() {
        return phone;
    }

    public String getProfilePhotoUrl() {
        return profilePhotoUrl;
    }

    public String getBio() {
        return bio;
    }

    public UserStatus getStatus() {
        return status;
    }

    public UserType getUserType() {
        return userType;
    }

    public void deactivate() {
        this.status = UserStatus.INACTIVE;
        super.deactivate();
    }

    public void suspend() {
        this.status = UserStatus.SUSPENDED;
    }
}
