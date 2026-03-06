package com.jpmc.starburst.sici.model;

import java.time.Instant;

public class ProvisioningJob {

    private final String jobId;
    private final String clusterId;
    private final OperationType operationType;
    private JobStatus status;
    private final Instant startedAt;
    private Instant completedAt;
    private String errorDetails;

    public ProvisioningJob(String jobId, String clusterId, OperationType operationType) {
        this.jobId = jobId;
        this.clusterId = clusterId;
        this.operationType = operationType;
        this.status = JobStatus.PENDING;
        this.startedAt = Instant.now();
    }

    public String getJobId() {
        return jobId;
    }

    public String getClusterId() {
        return clusterId;
    }

    public OperationType getOperationType() {
        return operationType;
    }

    public JobStatus getStatus() {
        return status;
    }

    public Instant getStartedAt() {
        return startedAt;
    }

    public Instant getCompletedAt() {
        return completedAt;
    }

    public String getErrorDetails() {
        return errorDetails;
    }

    public void markRunning() {
        this.status = JobStatus.RUNNING;
    }

    public void markSuccess() {
        this.status = JobStatus.SUCCESS;
        this.completedAt = Instant.now();
    }

    public void markFailed(String errorDetails) {
        this.status = JobStatus.FAILED;
        this.errorDetails = errorDetails;
        this.completedAt = Instant.now();
    }

    public enum OperationType {
        CREATE_CLUSTER,
        DELETE_CLUSTER,
        UPDATE_CLUSTER,
        ADD_DATASOURCE
    }

    public enum JobStatus {
        PENDING,
        RUNNING,
        SUCCESS,
        FAILED
    }
}