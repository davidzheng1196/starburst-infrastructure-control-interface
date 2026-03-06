package com.jpmc.starburst.sici.api;

import com.jpmc.starburst.sici.api.dto.ClusterResponse;
import com.jpmc.starburst.sici.api.dto.CreateClusterRequest;
import com.jpmc.starburst.sici.api.dto.CreateClusterResponse;
import com.jpmc.starburst.sici.model.Cluster;
import com.jpmc.starburst.sici.service.ClusterService;
import com.jpmc.starburst.sici.service.ProvisioningService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/clusters")
public class ClusterController {

    private final ProvisioningService provisioningService;
    private final ClusterService clusterService;

    public ClusterController(ProvisioningService provisioningService,
                             ClusterService clusterService) {
        this.provisioningService = provisioningService;
        this.clusterService = clusterService;
    }

    @PostMapping
    public ResponseEntity<CreateClusterResponse> createCluster(
            @Valid @RequestBody CreateClusterRequest req) {

        ProvisioningService.Result result =
                provisioningService.createClusterJob(req.teamId(), req.environment());

        CreateClusterResponse response = new CreateClusterResponse(
                result.clusterId(),
                result.jobId(),
                result.status()
        );

        return ResponseEntity.accepted().body(response);
    }

    @GetMapping("/{clusterId}")
    public ResponseEntity<ClusterResponse> getCluster(@PathVariable String clusterId) {
        Cluster cluster = clusterService.getCluster(clusterId);

        if (cluster == null) {
            return ResponseEntity.notFound().build();
        }

        ClusterResponse response = toResponse(cluster);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<ClusterResponse>> getAllClusters() {
        List<ClusterResponse> response = clusterService.getAllClusters()
                .stream()
                .map(this::toResponse)
                .toList();

        return ResponseEntity.ok(response);
    }

    private ClusterResponse toResponse(Cluster cluster) {
        return new ClusterResponse(
                cluster.getClusterId(),
                cluster.getTeamId(),
                cluster.getEnvironment(),
                cluster.getTier(),
                cluster.getStatus().name(),
                cluster.getEndpoint(),
                cluster.getCreatedAt()
        );
    }
}