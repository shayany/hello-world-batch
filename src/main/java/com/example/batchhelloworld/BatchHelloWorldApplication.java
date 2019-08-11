package com.example.batchhelloworld;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableBatchProcessing
public class BatchHelloWorldApplication {

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Bean
    public Step step() {
        return stepBuilderFactory
                .get("step1")
                .tasklet(helloWorldTasklet(null))
                .build();
    }

    @StepScope
    @Bean
    public Tasklet helloWorldTasklet(@Value("#{jobParameters['name']}") String name) {
        return (stepContribution, chunkContext) -> {
            System.out.println(String.format("Hello, %s!",  name));
            return RepeatStatus.FINISHED;
        };
    }

    @Bean
    public Job job(){
        return this.jobBuilderFactory
                .get("job")
                .start(step())
                .build();
    }

    public static void main(String[] args) {
        SpringApplication.run(BatchHelloWorldApplication.class, args);
    }

}
