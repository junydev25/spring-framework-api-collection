package com.junydev.spring.api.csv;

import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class BatchService {

    private final JobLauncher jobLauncher;
    private final Job job;

    public BatchService(JobLauncher jobLauncher, Job job) {
        this.jobLauncher = jobLauncher;
        this.job = job;
    }

    public BatchStatus runBatch(MultipartFile file) {
        String filePath = saveFile(file);

        JobParameters jobParameters = new JobParametersBuilder()
                .addString("filePath", filePath)
                .addLong("timestamp", System.currentTimeMillis())
                .toJobParameters();

        JobExecution jobExecution;
        try {
            jobExecution = this.jobLauncher.run(this.job, jobParameters);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return jobExecution.getStatus();
    }

    private String saveFile(MultipartFile file) {
        Path uploadPath = Paths.get(System.getProperty("java.io.tmpdir"));
        try {
            Path filePath = uploadPath.resolve(file.getOriginalFilename());
            if (!filePath.normalize().startsWith(uploadPath)) {
                throw new SecurityException("Invalid file path");
            }
            file.transferTo(filePath.toFile());
            return filePath.toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
