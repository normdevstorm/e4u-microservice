package com.renting.item_service.common.enums;

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

    USER(
            Set.of(Permission.USER_READ, Permission.USER_CREATE, Permission.USER_UPDATE)
    ),
    ADMIN(
            Set.of(Permission.USER_READ, Permission.USER_CREATE, Permission.USER_UPDATE, Permission.USER_DELETE, Permission.ADMIN_READ, Permission.ADMIN_CREATE, Permission.ADMIN_UPDATE, Permission.ADMIN_DELETE, Permission.ITEM_READ, Permission.ITEM_CREATE, Permission.ITEM_UPDATE, Permission.ITEM_DELETE)
    ),
    OWNER(Set.of(Permission.ITEM_CREATE, Permission.ITEM_READ, Permission.ITEM_UPDATE, Permission.ITEM_DELETE)),
    RENTER(Set.of(Permission.ITEM_READ));

    @JsonEnumDefaultValue
    public static Role DEFAULT = USER;

    private final Set<Permission> authorities;

    public List<SimpleGrantedAuthority> getAuthorities() {
        ArrayList<SimpleGrantedAuthority> authorities = new ArrayList<SimpleGrantedAuthority>(this.authorities.stream()
                .map(permission -> new SimpleGrantedAuthority(permission.getPermission()))
                .toList());
        authorities.add(new SimpleGrantedAuthority("ROLE_" + this.name()));
        return authorities;
    }
}
