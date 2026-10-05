package com.javatechie.controller;

import lombok.AllArgsConstructor;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.parameters.InvalidJobParametersException;
import org.springframework.batch.core.job.parameters.JobParameter;
import org.springframework.batch.core.job.parameters.JobParameters;
import org.springframework.batch.core.job.parameters.JobParametersBuilder;
import org.springframework.batch.core.launch.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.launch.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.batch.core.launch.JobRestartException;
import org.springframework.boot.batch.autoconfigure.JobLauncherApplicationRunner;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/jobs")
@AllArgsConstructor
public class JobController {

    private Job job;
    private JobOperator jobOperator;

    @PostMapping("/import")
    public void importCsvToDB(@RequestParam("file")MultipartFile multipartFile) {
        try {
            Path uploadPath =Paths.get("D:/Study/SpringCore/SpringBatch/springBatch/uploads");
            Files.createDirectories(uploadPath);
            Path filePath=uploadPath.resolve(multipartFile.getOriginalFilename());
            multipartFile.transferTo(filePath);

        JobParameters jobParameters=new JobParametersBuilder()
                .addString("fullPathFileName",filePath.toString())
                .addLong("startAt",System.currentTimeMillis())
                .toJobParameters();


            jobOperator.start(job,jobParameters);
        } catch (JobInstanceAlreadyCompleteException | JobExecutionAlreadyRunningException |
                 InvalidJobParametersException | JobRestartException | IOException e) {
            throw new RuntimeException(e);
        }


    }

}
