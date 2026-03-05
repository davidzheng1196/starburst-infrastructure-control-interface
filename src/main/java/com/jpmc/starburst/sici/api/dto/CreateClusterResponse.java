package com.jpmc.starburst.sici.api.dto;

public record CreateClusterResponse(
        String clusterId,
        String jobId,
        String status
) {}