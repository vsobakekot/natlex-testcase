package com.vsobakekot.natlex.repository;

import com.vsobakekot.natlex.model.Job;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JobRepository extends CrudRepository<Job,Long> {
}
