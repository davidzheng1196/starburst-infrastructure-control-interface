package com.jpmc.starburst.sici.api.dto;

import java.time.Instant;

public record JobResponse(
        String jobId,
        String clusterId,
        String operation,
        String status,
        Instant startedAt,
        Instant completedAt
) {
}