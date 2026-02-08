package com.renting.authentication_service.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.renting.authentication_service.common.enums.Role;
import com.renting.authentication_service.config.validate.CustomPhoneNumValidation;
import io.micrometer.common.lang.Nullable;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.UUID;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@Table(name = "users")
public class User extends BaseEntity implements UserDetails  {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @JsonIgnore(value = true)
    private UUID userId;
    @NotBlank(message = "Username is mandatory")
    @Column(unique = true)
    private String username;
    @JsonIgnore()
    private String password;
    @Nullable
    private String email;
    //    @Column(columnDefinition = "default 'admin'")
    @Enumerated(EnumType.STRING)
    private Role role;
    @NonNull
//    @Size(max = 50, message = "Name should not exceed 50 character length")
    private String firstName;
    @NonNull
//    @Size(max = 50, message = "Name should not exceed 50 character length")
    private String lastName;
    @NonNull
    @CustomPhoneNumValidation
    private String phoneNumber;
    @OneToOne(mappedBy = "user", orphanRemoval = true, fetch = FetchType.LAZY)
    private Key key;

    @Builder
    public User(final String username, final String password, final Role role, final @NonNull String firstName, final @NonNull String lastName, final @NonNull String phoneNumber) {
        this.username = username;
        this.password = password;
        this.role = role;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.email = email;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return role.getAuthorities();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
