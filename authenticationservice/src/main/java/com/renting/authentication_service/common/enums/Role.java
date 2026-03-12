package com.renting.authentication_service.common.enums;

import com.fasterxml.jackson.annotation.JsonEnumDefaultValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Getter
@RequiredArgsConstructor
public enum Role {

        @JsonEnumDefaultValue
        LEARNER(Set.of(
                        Permission.PROFILE_READ,
                        Permission.PROFILE_UPDATE,
                        Permission.CURRICULUM_READ,
                        Permission.LESSON_READ,
                        Permission.VOCAB_READ,
                        Permission.PROGRESS_READ,
                        Permission.PROGRESS_WRITE,
                        Permission.STATS_READ,
                        Permission.PAYMENT_READ,
                        Permission.PAYMENT_CREATE)),

        TEACHER(Set.of(// Inherits all LEARNER permissions
                        Permission.PROFILE_READ,
                        Permission.PROFILE_UPDATE,
                        Permission.CURRICULUM_READ,
                        Permission.CURRICULUM_CREATE,
                        Permission.CURRICULUM_UPDATE,
                        Permission.CURRICULUM_DELETE,
                        Permission.LESSON_READ,
                        Permission.LESSON_CREATE,
                        Permission.LESSON_UPDATE,
                        Permission.LESSON_DELETE,
                        Permission.VOCAB_READ,
                        Permission.VOCAB_CREATE,
                        Permission.VOCAB_UPDATE,
                        Permission.VOCAB_DELETE,
                        Permission.PROGRESS_READ,
                        Permission.PROGRESS_WRITE,
                        Permission.STATS_READ,
                        Permission.STATS_WRITE,
                        Permission.PAYMENT_READ,
                        Permission.PAYMENT_CREATE)),

        ADMIN(Set.of(
                        Permission.PROFILE_READ,
                        Permission.PROFILE_UPDATE,
                        Permission.CURRICULUM_READ,
                        Permission.CURRICULUM_CREATE,
                        Permission.CURRICULUM_UPDATE,
                        Permission.CURRICULUM_DELETE,
                        Permission.LESSON_READ,
                        Permission.LESSON_CREATE,
                        Permission.LESSON_UPDATE,
                        Permission.LESSON_DELETE,
                        Permission.VOCAB_READ,
                        Permission.VOCAB_CREATE,
                        Permission.VOCAB_UPDATE,
                        Permission.VOCAB_DELETE,
                        Permission.PROGRESS_READ,
                        Permission.PROGRESS_WRITE,
                        Permission.STATS_READ,
                        Permission.STATS_WRITE,
                        Permission.PAYMENT_READ,
                        Permission.PAYMENT_CREATE,
                        Permission.USER_READ,
                        Permission.USER_CREATE,
                        Permission.USER_UPDATE,
                        Permission.USER_DELETE,
                        Permission.ADMIN_READ,
                        Permission.ADMIN_CREATE,
                        Permission.ADMIN_UPDATE,
                        Permission.ADMIN_DELETE));

        /** Programmatic default — also the @JsonEnumDefaultValue constant above */
        public static final Role DEFAULT = LEARNER;

        private final Set<Permission> authorities;

        public List<SimpleGrantedAuthority> getAuthorities() {
                ArrayList<SimpleGrantedAuthority> authorities = new ArrayList<>(this.authorities.stream()
                                .map(permission -> new SimpleGrantedAuthority(permission.getPermission()))
                                .toList());
                authorities.add(new SimpleGrantedAuthority("ROLE_" + this.name()));
                return authorities;
        }
}
