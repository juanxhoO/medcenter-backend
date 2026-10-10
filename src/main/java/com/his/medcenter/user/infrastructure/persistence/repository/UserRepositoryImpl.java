package com.his.medcenter.user.infrastructure.persistence.repository;

import com.his.medcenter.user.domain.model.EmailAddress;
import com.his.medcenter.user.domain.model.User;
import com.his.medcenter.user.domain.model.UserId;
import com.his.medcenter.user.domain.model.UserRole;
import com.his.medcenter.user.domain.repository.UserRepository;
import com.his.medcenter.user.infrastructure.persistence.entity.UserJpaEntity;
import com.his.medcenter.user.infrastructure.persistence.mapper.UserPersistenceMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Adapter implementing the domain's UserRepository port using Spring Data JPA.
 */
@Repository
public class UserRepositoryImpl implements UserRepository {

    private final SpringDataJpaUserRepository jpaRepository;
    private final UserPersistenceMapper mapper;

    public UserRepositoryImpl(SpringDataJpaUserRepository jpaRepository, UserPersistenceMapper mapper) {
        this.jpaRepository = Objects.requireNonNull(jpaRepository, "SpringDataJpaUserRepository cannot be null");
        this.mapper = Objects.requireNonNull(mapper, "UserPersistenceMapper cannot be null");
    }

    @Override
    public User save(User user) {
        UserJpaEntity entity = mapper.toJpaEntity(user);
        UserJpaEntity saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<User> findById(UserId id) {
        return jpaRepository.findById(id.value())
                .map(mapper::toDomain);
    }

    @Override
    public Optional<User> findByEmail(EmailAddress email) {
        return jpaRepository.findByEmail(email.value())
                .map(mapper::toDomain);
    }

    @Override
    public boolean existsByEmail(EmailAddress email) {
        return jpaRepository.existsByEmail(email.value());
    }

    @Override
    public List<User> findAll() {
        return jpaRepository.findAll().stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<User> findByRole(UserRole role) {
        return jpaRepository.findByRole(role.name()).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public void deleteById(UserId id) {
        jpaRepository.deleteById(id.value());
    }
}
