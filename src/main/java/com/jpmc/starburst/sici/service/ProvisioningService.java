package com.jpmc.starburst.sici.service;

import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ProvisioningService {

    public Result createClusterJob(String teamId, String environment) {
        // stubbed for now — later we persist Cluster + ProvisioningJob
        String clusterId = "clu-" + UUID.randomUUID();
        String jobId = "job-" + UUID.randomUUID();
        return new Result(clusterId, jobId, "PROVISIONING");
    }

    public record Result(String clusterId, String jobId, String status) {}
}