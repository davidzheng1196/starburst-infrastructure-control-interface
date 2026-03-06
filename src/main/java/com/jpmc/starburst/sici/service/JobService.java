package com.jpmc.starburst.sici.service;

import com.jpmc.starburst.sici.model.ProvisioningJob;
import com.jpmc.starburst.sici.repository.JobRepository;
import org.springframework.stereotype.Service;

@Service
public class JobService {

    private final JobRepository jobRepository;

    public JobService(JobRepository jobRepository) {
        this.jobRepository = jobRepository;
    }

    public void saveJob(ProvisioningJob job) {
        jobRepository.save(job);
    }

    public ProvisioningJob getJob(String jobId) {
        return jobRepository.findById(jobId);
    }
}