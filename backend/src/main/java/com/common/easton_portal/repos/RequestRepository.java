package com.common.easton_portal.repos;

import com.common.easton_portal.entity.RequestEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface RequestRepository extends JpaRepository<RequestEntity, Long> {
//    Optional<RequestEntity> findAllByQuotation();
    RequestEntity findByCustomerId(long id);
    RequestEntity findByCollectionNameAndCustomerId(String collectionName, long id);
// @Query(value = """
//        SELECT r.id,
//               r.customer_id,
//               r.customer_name,
//               r.customer_email,
//               r.remarks,
//               r.created_date,
//               r.collection_name,
//               r.quotation_id,
//               q.id as quotation_id_actual,
//               q.quote_num,
//               q.assignee,
//               q.document
//        FROM request_entity r
//        LEFT JOIN quotation_entity q ON r.quotation_id = q.id
//        WHERE r.quotation_id IS NOT NULL AND r.quotation_id > 0
//        """, nativeQuery = true)
//    List<RequestEntity> findAllWithQuotationId();
}
