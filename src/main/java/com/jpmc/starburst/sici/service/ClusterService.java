package com.jpmc.starburst.sici.service;

import com.jpmc.starburst.sici.model.Cluster;
import com.jpmc.starburst.sici.repository.ClusterRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClusterService {

    private final ClusterRepository clusterRepository;

    public ClusterService(ClusterRepository clusterRepository) {
        this.clusterRepository = clusterRepository;
    }

    public void saveCluster(Cluster cluster) {
        clusterRepository.save(cluster);
    }

    public Cluster getCluster(String clusterId) {
        return clusterRepository.findById(clusterId);
    }

    public List<Cluster> getAllClusters() {
        return clusterRepository.findAll();
    }
}