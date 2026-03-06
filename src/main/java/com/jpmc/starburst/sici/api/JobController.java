package com.jpmc.starburst.sici.api;

import com.jpmc.starburst.sici.api.dto.JobResponse;
import com.jpmc.starburst.sici.model.ProvisioningJob;
import com.jpmc.starburst.sici.service.JobService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/jobs")
public class JobController {

    private final JobService jobService;

    public JobController(JobService jobService) {
        this.jobService = jobService;
    }

    @GetMapping("/{jobId}")
    public ResponseEntity<JobResponse> getJob(@PathVariable String jobId) {
        ProvisioningJob job = jobService.getJob(jobId);

        if (job == null) {
            return ResponseEntity.notFound().build();
        }

        JobResponse response = new JobResponse(
                job.getJobId(),
                job.getClusterId(),
                job.getOperationType().name(),
                job.getStatus().name(),
                job.getStartedAt(),
                job.getCompletedAt()
        );

        return ResponseEntity.ok(response);
    }
}