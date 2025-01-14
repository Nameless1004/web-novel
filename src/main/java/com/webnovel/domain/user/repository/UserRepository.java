package com.webnovel.domain.user.repository;

import com.webnovel.common.exceptions.NotFoundException;
import com.webnovel.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    default User findByUsernameOrElseThrow(String username) {
        return this.findByUsername(username).orElseThrow(() -> new NotFoundException("User not found"));
    }

    default User findByIdOrElseThrow(long id) {
        return findById(id)
                .orElseThrow(() -> new NotFoundException("User not found"));
    }

    boolean existsByNickname(String newNickname);

    boolean existsByEmail(String email);

    boolean existsByUsername(String username);

    Optional<User> findByNickname(String nickname);

}
