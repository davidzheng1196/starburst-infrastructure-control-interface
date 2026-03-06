package com.jpmc.starburst.sici.model;

import java.time.Instant;

public class Cluster {

    private final String clusterId;
    private final String teamId;
    private final String environment;
    private final String tier;
    private ClusterStatus status;
    private String endpoint;
    private final Instant createdAt;

    public Cluster(String clusterId, String teamId, String environment, String tier) {
        this.clusterId = clusterId;
        this.teamId = teamId;
        this.environment = environment;
        this.tier = tier;
        this.status = ClusterStatus.PROVISIONING;
        this.createdAt = Instant.now();
    }

    public String getClusterId() {
        return clusterId;
    }

    public String getTeamId() {
        return teamId;
    }

    public String getEnvironment() {
        return environment;
    }

    public String getTier() {
        return tier;
    }

    public ClusterStatus getStatus() {
        return status;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void markActive(String endpoint) {
        this.status = ClusterStatus.ACTIVE;
        this.endpoint = endpoint;
    }

    public void markFailed() {
        this.status = ClusterStatus.FAILED;
    }

    public void markDeleting() {
        this.status = ClusterStatus.DELETING;
    }

    public enum ClusterStatus {
        PROVISIONING,
        ACTIVE,
        FAILED,
        DELETING
    }
}