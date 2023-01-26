package com.takirahal.srfgroup.config.Batch;

import com.takirahal.srfgroup.modules.address.entities.Address;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Configuration
// @EnableBatchProcessing
public class SpringBatchConfig {

//    @Autowired
//    private JobBuilderFactory jobBuilderFactory;

//    @Autowired
//    private StepBuilderFactory stepBuilderFactory;

    @Autowired
    private ItemReader<Address> addressItemReader;

    @Autowired
    private ItemWriter<Address> addressItemWriter;

    @Autowired
    private ItemProcessor<Address, Address> addressItemProcess;

//    @Bean(name = "step0Address")
//    public Step step0Address(JobRepository jobRepository,
//                      PlatformTransactionManager transactionManager) {
//        return new StepBuilder("step0Address", jobRepository)
//                .<Address, Address> chunk(100, transactionManager)
//                .reader(addressItemReader)
//                .processor(addressItemProcess)
//                .writer(addressItemWriter)
//                .build();
//    }

    @Bean(name = "addressBeanJob")
    // @Qualifier("step0Address")
    public Job importAddressJob(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        Step step0Address = new StepBuilder("step0Address", jobRepository)
                .<Address, Address> chunk(100, transactionManager)
                .reader(addressItemReader)
                .processor(addressItemProcess)
                .writer(addressItemWriter)
                .build();
        return new JobBuilder("address-step-load-data", jobRepository)
                .start(step0Address)
                .build();
    }

//    @Bean
//    @Qualifier("addressBeanJob")
//    public Job addressJob(){
//        Step step0 = stepBuilderFactory.get("address-step-load-data")
//                .<Address, Address>chunk(100)
//                .reader(addressItemReader)
//                .processor(addressItemProcess)
//                .writer(addressItemWriter)
//                .build();
//        return jobBuilderFactory.get("address-data-loader-job")
//                .start(step0)
//                .build();
//    }

    @Bean
    public FlatFileItemReader<Address> addressBeanFlatFileItemReader(@Value("${inputFile}") Resource inputFile){
        FlatFileItemReader<Address> fileItemReader = new FlatFileItemReader<>();
        fileItemReader.setName("FFIR1");
        fileItemReader.setLinesToSkip(1);
        fileItemReader.setResource(inputFile);
        fileItemReader.setLineMapper(addressBeanLineMapper());
        return fileItemReader;
    }

    @Bean
    public LineMapper<Address> addressBeanLineMapper() {
        DefaultLineMapper<Address> bankTransactionLineMapper = new DefaultLineMapper<>();
        DelimitedLineTokenizer delimitedLineTokenizer = new DelimitedLineTokenizer();
        delimitedLineTokenizer.setDelimiter(";");
        delimitedLineTokenizer.setStrict(false);
        delimitedLineTokenizer.setNames("id", "city", "lat", "lng", "country", "iso2", "admin_name", "capital", "population", "population_proper");
        bankTransactionLineMapper.setLineTokenizer(delimitedLineTokenizer);
        BeanWrapperFieldSetMapper fieldSetMapper = new BeanWrapperFieldSetMapper();
        fieldSetMapper.setTargetType(Address.class);
        bankTransactionLineMapper.setFieldSetMapper(fieldSetMapper);
        return bankTransactionLineMapper;
    }
}
