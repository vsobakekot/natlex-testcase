package com.vsobakekot.natlex.repository;

import com.vsobakekot.natlex.model.Job;
import com.vsobakekot.natlex.model.enums.JobResultStatus;
import com.vsobakekot.natlex.model.enums.JobType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JobRepository extends JpaRepository<Job,Long> {

    boolean existsByIdAndType(Long jobId, JobType jobType);
    boolean existsByIdAndStatus(Long id, JobResultStatus status);
}

