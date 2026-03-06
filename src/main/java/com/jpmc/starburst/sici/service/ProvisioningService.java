package com.jpmc.starburst.sici.service;

import com.jpmc.starburst.sici.model.Cluster;
import com.jpmc.starburst.sici.model.ProvisioningJob;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ProvisioningService {

    private final JobService jobService;
    private final ClusterService clusterService;

    public ProvisioningService(JobService jobService, ClusterService clusterService) {
        this.jobService = jobService;
        this.clusterService = clusterService;
    }

    public Result createClusterJob(String teamId, String environment) {
        String clusterId = "clu-" + UUID.randomUUID();
        String jobId = "job-" + UUID.randomUUID();

        Cluster cluster = new Cluster(
                clusterId,
                teamId,
                environment,
                "SMALL"
        );

        ProvisioningJob job = new ProvisioningJob(
                jobId,
                clusterId,
                ProvisioningJob.OperationType.CREATE_CLUSTER
        );

        clusterService.saveCluster(cluster);
        jobService.saveJob(job);

        return new Result(clusterId, jobId, "PROVISIONING");
    }

    public record Result(String clusterId, String jobId, String status) {
    }
}