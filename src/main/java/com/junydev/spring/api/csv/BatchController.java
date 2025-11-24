package com.junydev.spring.api.csv;

import com.junydev.spring.api.common.ApiResponse;
import org.springframework.batch.core.BatchStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class BatchController {

    private final BatchService batchService;

    public BatchController(BatchService batchService) {
        this.batchService = batchService;
    }

    @PostMapping("/api/batch/csv")
    public ApiResponse importUsers(@RequestParam("file") MultipartFile file) {
        BatchStatus batchStatus = this.batchService.runBatch(file);

        String status = null;
        if (batchStatus.equals(BatchStatus.FAILED)) {
            status = "fail";
        } else if (batchStatus.equals(BatchStatus.COMPLETED)) {
            status = "success";
        }

        return ApiResponse.builder()
                .status(status)
                .build();
    }
}
