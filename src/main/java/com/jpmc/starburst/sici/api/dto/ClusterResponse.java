package com.jpmc.starburst.sici.api.dto;

import java.time.Instant;

public record ClusterResponse(
        String clusterId,
        String teamId,
        String environment,
        String tier,
        String status,
        String endpoint,
        Instant createdAt
) {
}