package com.mdhp.orchestrator.repository;



import com.mdhp.orchestrator.entity.PipelineOutboxEventEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PipelineOutboxEventRepository extends JpaRepository<PipelineOutboxEventEntity, Long> {

    @Query("""
        select e from PipelineOutboxEventEntity e
        where e.status = 'NEW'
        order by e.createdAt asc
    """)
    List<PipelineOutboxEventEntity> findNewEvents(Pageable pageable);
}

