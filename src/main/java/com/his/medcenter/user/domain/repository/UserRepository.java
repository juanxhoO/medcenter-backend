package com.his.medcenter.user.domain.repository;

import com.his.medcenter.user.domain.model.EmailAddress;
import com.his.medcenter.user.domain.model.User;
import com.his.medcenter.user.domain.model.UserId;
import com.his.medcenter.user.domain.model.UserRole;

import java.util.List;
import java.util.Optional;

/**
 * Domain Repository interface (Outbound Port).
 * Defines persistence contracts without coupling to database frameworks.
 */
public interface UserRepository {

    User save(User user);

    Optional<User> findById(UserId id);

    Optional<User> findByEmail(EmailAddress email);

    boolean existsByEmail(EmailAddress email);

    List<User> findAll();

    List<User> findByRole(UserRole role);

    void deleteById(UserId id);
}
