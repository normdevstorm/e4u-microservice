package com.renting.authentication_service.repository;
import com.renting.authentication_service.entity.Key;
import com.renting.authentication_service.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Repository
public interface KeyRepository extends JpaRepository<Key, UUID> {
    @Query("select k from Key k where k.user.username = ?1")
    Key findByUser_Username(@Nullable String username);

    @Query("select k from Key k where k.user.userId = ?1")
    Key findByUser_UserId(UUID userId);

    @Transactional
    @Modifying
    @Query("update Key k set k.refreshTokenVersion = ?1 where k.user = ?2")
    void updateRefreshTokenVersionByUser(Integer version, @NonNull User user);

    @Transactional
    @Modifying
    @Query("update Key k set k.refreshTokenVersion = ?1 where k.id = ?2")
    int updateRefreshTokenVersionById(Integer version, UUID id);

    @Transactional
    @Modifying
    @Query("update Key k set k.refreshToken = ?1 where k.user.userId = ?2")
    void updateRefreshTokenByUserId(String refreshToken, UUID userId);

    @Transactional
    @Modifying
    @Query("update Key k set k.accessTokenVersion = ?1 where k.user.userId = ?2")
    void updateAccessTokenVersionByUserId(Integer version, UUID userId);

    @Transactional
    @Modifying
    @Query("update Key k set k.accessTokenVersion = ?1 where k.user.username = ?2")
    void updateAccessTokenVersionByUsername(Integer version, String username);
}
