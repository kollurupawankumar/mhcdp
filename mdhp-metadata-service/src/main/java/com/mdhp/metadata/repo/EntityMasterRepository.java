package com.mdhp.metadata.repo;

import com.mdhp.metadata.model.EntityMasterEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;



import java.util.Optional;
import java.util.List;

public interface EntityMasterRepository extends JpaRepository<EntityMasterEntity, Long> {

    Optional<EntityMasterEntity> findByEntityId(String entityId);

    boolean existsByEntityId(String entityId);

    List<EntityMasterEntity> findByDomainCode(String domainCode);
}

