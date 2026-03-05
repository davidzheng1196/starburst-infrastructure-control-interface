package com.jpmc.starburst.sici.api.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateClusterRequest(
        @NotBlank String teamId,
        @NotBlank String environment
) {}
