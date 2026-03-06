package com.jpmc.starburst.sici.service;

import com.jpmc.starburst.sici.model.ProvisioningJob;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ProvisioningService {

    private final JobService jobService;

    public ProvisioningService(JobService jobService) {
        this.jobService = jobService;
    }

    public Result createClusterJob(String teamId, String environment) {
        String clusterId = "clu-" + UUID.randomUUID();
        String jobId = "job-" + UUID.randomUUID();

        ProvisioningJob job = new ProvisioningJob(
                jobId,
                clusterId,
                ProvisioningJob.OperationType.CREATE_CLUSTER
        );

        jobService.saveJob(job);

        return new Result(clusterId, jobId, "PROVISIONING");
    }

    public record Result(String clusterId, String jobId, String status) {
    }
}