package com.springboot.batch.batchprocessing.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class CustomerController {

    private final JobLauncher jobLauncher;
    private final Job job;

    @GetMapping("/customers")
    public void loadCSVToDB() throws Exception {
        JobParameters jobParameters = new JobParametersBuilder().addLong("Start-At", System.currentTimeMillis()).toJobParameters();
        jobLauncher.run(job, jobParameters);
    }

}
