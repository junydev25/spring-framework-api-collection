package com.junydev.spring.api.csv;

import com.junydev.spring.api.csv.internal.Gender;
import com.junydev.spring.api.csv.internal.User;
import com.junydev.spring.api.csv.internal.UserCsvDto;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class BatchConfiguration {

    @Bean
    public Job importUserJob(JobRepository jobRepository, Step importUserStep) {
        return new JobBuilder("importUserJob", jobRepository)
                .start(importUserStep)
                .build();
    }

    @Bean
    public Step importUserStep(JobRepository jobRepository,
                               PlatformTransactionManager transactionManager,
                               ItemProcessor<UserCsvDto, User> processor,
                               JpaItemWriter<User> writer) {
        return new StepBuilder("importUserStep", jobRepository)
                .<UserCsvDto, User>chunk(10, transactionManager)
                .reader(reader(null))
                .processor(processor)
                .writer(writer)
                .build();
    }

    @Bean
    @StepScope
    public FlatFileItemReader<UserCsvDto> reader(@Value("#{jobParameters['filePath']}") String filePath) {
        return new FlatFileItemReaderBuilder<UserCsvDto>()
                .name("userCsvReader")
                .resource(new FileSystemResource(filePath))
                .delimited()
                .names("name", "email", "gener")
                .targetType(UserCsvDto.class)
                .linesToSkip(1)
                .build();
    }

    @Bean
    public ItemProcessor<UserCsvDto, User> processor() {
        return dto -> User.builder()
                .name(dto.getName())
                .email(dto.getEmail())
                .gender(Gender.valueOf(dto.getGender().toUpperCase()))
                .build();
    }

    @Bean
    public JpaItemWriter<User> writer(EntityManagerFactory emf) {
        JpaItemWriter<User> writer = new JpaItemWriter<>();
        writer.setEntityManagerFactory(emf);
        return writer;
    }
}
