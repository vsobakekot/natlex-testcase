package com.vsobakekot.natlex.repository;

import com.vsobakekot.natlex.model.Job;
import com.vsobakekot.natlex.model.enums.JobResultStatus;
import com.vsobakekot.natlex.model.enums.JobType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface JobRepository extends JpaRepository<Job,Long> {

    Optional<Job> findByIdAndType(Long jobId, JobType jobType);
    Optional<Job> findByIdAndTypeAndStatus(Long id, JobType type, JobResultStatus status);
}

