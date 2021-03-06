package com.vsobakekot.natlex.controller;

import com.vsobakekot.natlex.model.Job;
import com.vsobakekot.natlex.model.enums.JobType;
import com.vsobakekot.natlex.service.JobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping(path = "/api/jobs", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class JobRestController {

    private final JobService jobService;

    @Autowired
    public JobRestController(JobService jobService) {
        this.jobService = jobService;
    }

    @GetMapping
    public ResponseEntity<?> getJobList() {
        return new ResponseEntity<>(jobService.getAllJobs(), HttpStatus.OK);
    }

    @PostMapping(value = "/import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> importXLSFile(@RequestParam("file") MultipartFile file) {
        if (file == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        Job newImportJob = jobService.startNewJob(JobType.IMPORT);
        jobService.importXLS(file, newImportJob);
        return new ResponseEntity<>(newImportJob.getId(), HttpStatus.OK);
    }

    @GetMapping("/import/{jobId}")
    public ResponseEntity<?> getImportStatus(@PathVariable Long jobId) {
        if (jobId == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(jobService.getJobStatus(jobId, JobType.IMPORT), HttpStatus.OK);
    }

    @GetMapping("/export")
    public ResponseEntity<?> exportSections() {
        Job newExportJob = jobService.startNewJob(JobType.EXPORT);
        jobService.exportXLS(newExportJob);
        return new ResponseEntity<>(newExportJob.getId(), HttpStatus.OK);
    }

    @GetMapping("/export/{jobId}")
    public ResponseEntity<?> getExportStatus(@PathVariable Long jobId) {
        if (jobId == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(jobService.getJobStatus(jobId, JobType.EXPORT), HttpStatus.OK);
    }

    @GetMapping(value = "/export/{jobId}/file", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<?> getXLSFileByJobId(@PathVariable Long jobId) {
        if (jobId == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        Resource resource = jobService.downloadXLS(jobId);
        return ResponseEntity.ok()
                             .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                             .body(resource);
        }
}
