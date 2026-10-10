package com.his.medcenter.user.infrastructure.persistence.mapper;

import com.his.medcenter.user.domain.model.EmailAddress;
import com.his.medcenter.user.domain.model.User;
import com.his.medcenter.user.domain.model.UserId;
import com.his.medcenter.user.domain.model.UserRole;
import com.his.medcenter.user.domain.model.UserStatus;
import com.his.medcenter.user.infrastructure.persistence.entity.UserJpaEntity;
import org.springframework.stereotype.Component;

/**
 * Maps between the Domain User Aggregate Root and the JPA Persistence Entity.
 */
@Component
public class UserPersistenceMapper {

    public UserJpaEntity toJpaEntity(User user) {
        if (user == null) {
            return null;
        }
        return new UserJpaEntity(
                user.getId().value(),
                user.getEmail().value(),
                user.getPasswordHash(),
                user.getFirstName(),
                user.getLastName(),
                user.getPhone(),
                user.getRole().name(),
                user.getStatus().name(),
                user.getDepartment(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }

    public User toDomain(UserJpaEntity entity) {
        if (entity == null) {
            return null;
        }
        return User.reconstitute(
                UserId.of(entity.getId()),
                EmailAddress.of(entity.getEmail()),
                entity.getPasswordHash(),
                entity.getFirstName(),
                entity.getLastName(),
                entity.getPhone(),
                UserRole.valueOf(entity.getRole()),
                UserStatus.valueOf(entity.getStatus()),
                entity.getDepartment(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
