package com.jpmc.starburst.sici.repository;

import com.jpmc.starburst.sici.model.Cluster;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class ClusterRepository {

    private final Map<String, Cluster> clusters = new ConcurrentHashMap<>();

    public void save(Cluster cluster) {
        clusters.put(cluster.getClusterId(), cluster);
    }

    public Cluster findById(String clusterId) {
        return clusters.get(clusterId);
    }

    public List<Cluster> findAll() {
        return new ArrayList<>(clusters.values());
    }
}