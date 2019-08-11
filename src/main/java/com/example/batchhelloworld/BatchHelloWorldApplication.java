package com.example.batchhelloworld;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersValidator;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.CompositeJobParametersValidator;
import org.springframework.batch.core.job.DefaultJobParametersValidator;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Arrays;

@SpringBootApplication
@EnableBatchProcessing
public class BatchHelloWorldApplication {

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job job(){
        return this.jobBuilderFactory
                .get("job")
                .validator(compositeValidator())
                .start(step())
                .build();
    }

    @Bean
    public Step step() {
        return stepBuilderFactory
                .get("step1")
                .tasklet(helloWorldTasklet(null, null))
                .build();
    }

    @StepScope
    @Bean
    public Tasklet helloWorldTasklet(@Value("#{jobParameters['name']}") String name,
                                     @Value("#{jobParameters['fileName']}") String fileName) {
        return (stepContribution, chunkContext) -> {
            System.out.println(String.format("Hello, %s!",  name));
            System.out.println(String.format("fileName =  %s!",  fileName));
            return RepeatStatus.FINISHED;
        };
    }

    @Bean
    public JobParametersValidator validator(){
        DefaultJobParametersValidator defaultJobParametersValidator = new DefaultJobParametersValidator();
        defaultJobParametersValidator.setRequiredKeys(new String[]{"fileName"});
        defaultJobParametersValidator.setOptionalKeys(new String[]{"name"});
        return defaultJobParametersValidator;
    }

    @Bean
    public CompositeJobParametersValidator compositeValidator(){
        CompositeJobParametersValidator compositeJobParametersValidator = new CompositeJobParametersValidator();

        compositeJobParametersValidator.setValidators(Arrays.asList(new ParameterValidator(),validator()));

        return compositeJobParametersValidator;
    }

    public static void main(String[] args) {
        SpringApplication.run(BatchHelloWorldApplication.class, args);
    }

}
