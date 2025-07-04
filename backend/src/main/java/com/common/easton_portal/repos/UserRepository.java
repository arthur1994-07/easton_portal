package com.common.easton_portal.repos;

import com.common.easton_portal.entity.OAuthEntity;
import com.common.easton_portal.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByDomainUserIdAndDomain(String domainUserId, OAuthEntity domain);
    List<UserEntity> findByDomain(OAuthEntity domain);
}
