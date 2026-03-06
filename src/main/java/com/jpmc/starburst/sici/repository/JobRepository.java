package com.jpmc.starburst.sici.repository;

import com.jpmc.starburst.sici.model.ProvisioningJob;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class JobRepository {

    private final Map<String, ProvisioningJob> jobs = new ConcurrentHashMap<>();

    public void save(ProvisioningJob job) {
        jobs.put(job.getJobId(), job);
    }

    public ProvisioningJob findById(String jobId) {
        return jobs.get(jobId);
    }
}