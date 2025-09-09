package com.common.easton_portal.repos;

import com.common.easton_portal.entity.QuotationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuotationRepository extends JpaRepository<QuotationEntity, Long> {
}
