package com.example.coinserver.db.repository;

import com.example.coinserver.db.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Integer> {

    Optional<UserEntity> findUserEntityByToken(String token);

    Optional<UserEntity> findUserEntityByLoginAndPassword(String login, String password);

    Optional<UserEntity> findUserEntityByLogin(String login);
}
