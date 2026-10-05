package com.javatechie.config;

import com.javatechie.entity.Customer;
import com.javatechie.listner.StepSkipListner;
import com.javatechie.partition.ColumnRangePartitioner;
import com.javatechie.repository.CustomerRepo;
import lombok.AllArgsConstructor;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.listener.SkipListener;
import org.springframework.batch.core.partition.PartitionHandler;
import org.springframework.batch.core.partition.Partitioner;
import org.springframework.batch.core.partition.support.TaskExecutorPartitionHandler;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.skip.SkipPolicy;
import org.springframework.batch.infrastructure.item.data.RepositoryItemWriter;
import org.springframework.batch.infrastructure.item.file.FlatFileItemReader;
import org.springframework.batch.infrastructure.item.file.FlatFileParseException;
import org.springframework.batch.infrastructure.item.file.LineMapper;
import org.springframework.batch.infrastructure.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.infrastructure.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.infrastructure.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.infrastructure.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

import java.io.File;

@Configuration
@EnableBatchProcessing
@AllArgsConstructor
public class SpringBatchConfig {

   // private JobBuilder jobBuilder;
  //  private StepBuilder stepBuilder;
    private CustomerRepo customerRepo;

    //FlatFileItemReader read the information from source

    //Adding argument in reader() method for dynamic file path
    @Bean
    @StepScope   //the spring batch create new instance for each step execution (if u not use it scope become singleton)
    public FlatFileItemReader<Customer> reader(@Value("#{jobParameters['fullPathFileName']}" )String pathToFile) {
        /*FlatFileItemReader<Customer> itemReader = new FlatFileItemReader<>();
        itemReader.setResource(new FileSystemResource("src/main/resources/customersrecords.csv"));  //it set the location of resource i.e here csv file
        itemReader.setName("csvReader");
        itemReader.setLinesToSkip(1);// don't read header
        itemReader.setLineMapper(lineMapper());
        return itemReader;*/

        return new FlatFileItemReaderBuilder<Customer>()
                .name("csvReader")
                //.resource(new FileSystemResource("src/main/resources/customersrecords.csv")) //it set the location of resource i.e here csv file
                .resource(new FileSystemResource(new File(pathToFile)))
                .linesToSkip(1)
                .lineMapper(lineMapper())
                .build();
    }

    //Linemaper do 2 things (i)it extract the data from resource (ii) it dump that information into respected class i.e here customer class
    //LineMapper = takes one line from the input file and maps it to one Java object.
    public LineMapper<Customer> lineMapper() {

        DefaultLineMapper<Customer> lineMapper = new DefaultLineMapper<>();

        //Tokenizer
        DelimitedLineTokenizer delimitedLineTokenizer = new DelimitedLineTokenizer();
        delimitedLineTokenizer.setDelimiter(",");
        delimitedLineTokenizer.setStrict(false);
        delimitedLineTokenizer.setNames("id", "firstname", "lastname", "email", "contactno", "gender", "country", "dob");

        //FieldSetMapper
        BeanWrapperFieldSetMapper<Customer> fieldSetMapper = new BeanWrapperFieldSetMapper<>();
        fieldSetMapper.setTargetType(Customer.class);

        //connect them

        lineMapper.setLineTokenizer(delimitedLineTokenizer);
        lineMapper.setFieldSetMapper(fieldSetMapper);

        return lineMapper;
    }

    @Bean
    public CustomerProcessor processor(){
        return new CustomerProcessor();
    }

    @Bean
    public RepositoryItemWriter<Customer> writer(){
        RepositoryItemWriter<Customer> writer=new RepositoryItemWriter<>(customerRepo);
        writer.setMethodName("save");
        return writer;
    }

   /* @Bean
    public ColumnRangePartitioner partitioner(){
        return new ColumnRangePartitioner();
    }*/

   /* @Bean
    public PartitionHandler partitionerHandler(Step slaveStep){
        TaskExecutorPartitionHandler taskExecutorPartitionHandler=new TaskExecutorPartitionHandler();
        taskExecutorPartitionHandler.setGridSize(2);
        taskExecutorPartitionHandler.setTaskExecutor(taskExecutor());
        taskExecutorPartitionHandler.setStep(slaveStep);
        return taskExecutorPartitionHandler;

    }*/

    @Bean
    public Step slaveStep(JobRepository jobRepository, PlatformTransactionManager transactionManager,FlatFileItemReader<Customer> reader){
        return new StepBuilder("csv-step",jobRepository)
                .<Customer,Customer>chunk(10)
                .transactionManager(transactionManager)
                //.reader(reader())
                .reader(reader)
                .processor(processor())
                .writer(writer())
                .faultTolerant()
                /*.skipLimit(100)
                .skip(FlatFileParseException.class)*/
                .listener(skipListener()) // use to notify users in which step getting error by giving log
                .skipPolicy(skipPolicy())
                .taskExecutor(taskExecutor())
                .build();

    }

    @Bean
    public Job myJob(JobRepository jobRepository,Step step){
        return new JobBuilder("my_first_Job",jobRepository)
                .start(step)
                .build();
    }

    @Bean
    public AsyncTaskExecutor taskExecutor(){
        SimpleAsyncTaskExecutor asyncTaskExecutor=new SimpleAsyncTaskExecutor();
        asyncTaskExecutor.setConcurrencyLimit(10);
        return asyncTaskExecutor;
    /*    ThreadPoolTaskExecutor taskExecutor=new ThreadPoolTaskExecutor();
        taskExecutor.setCorePoolSize(4);
        taskExecutor.setMaxPoolSize(4);
        taskExecutor.setQueueCapacity(4);
        return taskExecutor*/
    }
    @Bean
    public SkipPolicy skipPolicy(){
        return new ExceptionSkipPolicy();
    }

    // use to notify users in which step getting error
    @Bean
    public SkipListener skipListener(){
        return new StepSkipListner();
    }

}
