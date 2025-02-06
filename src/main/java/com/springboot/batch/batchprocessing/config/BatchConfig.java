package com.springboot.batch.batchprocessing.config;

import com.springboot.batch.batchprocessing.entity.Customer;
import com.springboot.batch.batchprocessing.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@EnableBatchProcessing
@RequiredArgsConstructor
public class BatchConfig {

    private final CustomerRepository customerRepository;
    private final PlatformTransactionManager transactionManager;

    //create Reader
    @Bean
    public FlatFileItemReader<Customer> customerReader() {
        FlatFileItemReader<Customer> itemReader = new FlatFileItemReader<>();

        itemReader.setResource(new FileSystemResource("src/main/resources/customer_data.csv"));
        itemReader.setName("customer_data");
        itemReader.setLinesToSkip(1);
        itemReader.setLineMapper(lineMapper());


        return itemReader;
    }

    private LineMapper<Customer> lineMapper() {
        DefaultLineMapper<Customer> lineMapper = new DefaultLineMapper<>();

        DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();
        lineTokenizer.setDelimiter(",");
        lineTokenizer.setStrict(false);
        lineTokenizer.setNames("customerId", "customerName", "gender");

        BeanWrapperFieldSetMapper<Customer> fieldSetMapper = new BeanWrapperFieldSetMapper<>();
        fieldSetMapper.setTargetType(Customer.class);

        lineMapper.setLineTokenizer(lineTokenizer);
        lineMapper.setFieldSetMapper(fieldSetMapper);

        return lineMapper;
    }

    //create Processor
    @Bean
    public CustomerProcessor customerProcessor() {
        return new CustomerProcessor();
    }

    //create Writer
    public RepositoryItemWriter<Customer> customerWriter() {

        RepositoryItemWriter<Customer> repositoryWriter = new RepositoryItemWriter<>();
        repositoryWriter.setRepository(customerRepository);
        repositoryWriter.setMethodName("save");

        return repositoryWriter;
    }

    //create Step
    @Bean
    public Step customerStep(JobRepository jobRepository) {
        return new StepBuilder("customerStep", jobRepository)
                .<Customer, Customer>chunk(10, transactionManager)
                .reader(customerReader())
                .processor(customerProcessor())
                .writer(customerWriter())
                .build();
    }

    //create Job
    @Bean
    public Job job(JobRepository jobRepository) {
        return new JobBuilder("customerJob",jobRepository)
                .flow(customerStep(jobRepository))
                .end().build();
    }
}
