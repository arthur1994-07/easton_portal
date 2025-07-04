package com.common.easton_portal.repos;

import com.common.easton_portal.entity.OAuthEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OAuthRepository extends JpaRepository<OAuthEntity, Long> {
}
