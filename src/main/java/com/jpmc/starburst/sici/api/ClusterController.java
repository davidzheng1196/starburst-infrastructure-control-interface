package com.jpmc.starburst.sici.api;

import com.jpmc.starburst.sici.api.dto.CreateClusterRequest;
import com.jpmc.starburst.sici.api.dto.CreateClusterResponse;
import com.jpmc.starburst.sici.service.ProvisioningService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/clusters")
public class ClusterController {

    private final ProvisioningService provisioningService;

    public ClusterController(ProvisioningService provisioningService) {
        this.provisioningService = provisioningService;
    }

    @PostMapping
    public ResponseEntity<CreateClusterResponse> createCluster(
            @Valid @RequestBody CreateClusterRequest req) {
        ProvisioningService.Result result =
                provisioningService.createClusterJob(req.teamId(), req.environment());

        return ResponseEntity.accepted().body(
                new CreateClusterResponse(result.clusterId(), result.jobId(), result.status())
        );
    }
}