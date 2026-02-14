package com.shbvn.jms.repository;

import com.shbvn.jms.model.CustomFieldValue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomFieldValueRepository extends JpaRepository<CustomFieldValue, String> {
    List<CustomFieldValue> findByTaskId(String taskId);
    Optional<CustomFieldValue> findByTaskIdAndFieldId(String taskId, String fieldId);
    void deleteByFieldId(String fieldId);
}
