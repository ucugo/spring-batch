package playground.myspringbatch.config;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.AWSCredentialsProviderChain;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.mapping.PassThroughLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.support.ListItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.core.io.ClassPathResource;
import playground.myspringbatch.model.OutputItem;
import playground.myspringbatch.processor.RecservAuditItemProcessor;

import java.net.MalformedURLException;

/**
 * Created by Ugo on 12/12/2015.
 */
@Configuration
@EnableBatchProcessing
@ComponentScan(basePackages = {"playground.myspringbatch"})
public class ProcessConfiguration {

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;


    @Bean
    public ItemReader<String> reader(AmazonS3 amazonS3) throws MalformedURLException {


        FlatFileItemReader<String> reader = new FlatFileItemReader<>();
        reader.setResource(new ClassPathResource("input.txt"));

        reader.setLineMapper(new PassThroughLineMapper());
        return reader;
    }

    @Bean
    public ItemWriter<OutputItem> writer() {
        return new ListItemWriter<>();
    }

    @Bean
    public Job recservAuditConversion(Step s1) throws MalformedURLException {
        return jobBuilderFactory.get("recservAuditConversion").incrementer(new RunIdIncrementer()).flow(s1).end().build();
    }

    @Bean
    public Step convertPrice(@Qualifier("reader")ItemReader<String> reader,
                             @Qualifier("writer")ItemWriter<OutputItem> writer,
                             @Qualifier("recservAuditItemProcessor")RecservAuditItemProcessor processor) throws MalformedURLException {
        return stepBuilderFactory.get("convertPrice")
                .<String, OutputItem> chunk(1000)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .build();
    }

    @Bean(name="amazonS3")
    public AmazonS3 amazonS3(ClientConfiguration clientConfiguration, AWSCredentialsProviderChain awsCredentialsProviderChain) {
        return new AmazonS3Client(awsCredentialsProviderChain, clientConfiguration);
    }

    @Bean
    public AWSCredentialsProviderChain awsCredentialsProviderChain() {
        return new AWSCredentialsProviderChain(new DefaultAWSCredentialsProviderChain());
    }

    @Bean
    @Scope(value = "prototype")
    public ClientConfiguration clientConfiguration() {

        // Configure the AWS client to have a connection pool with
        // the same number of connections as the consumer has threads.
        // If the consumer count is small, use a minimum of 50 connections.
        int numOfAWSConnections = Math.max(50, 10);

        ClientConfiguration clientConfiguration = new ClientConfiguration()
                .withMaxConnections(numOfAWSConnections)
                .withGzip(false)
                .withSocketTimeout(50000)
                .withConnectionTimeout(50000)
                .withMaxErrorRetry(20)
                .withConnectionTTL(300000)
                .withTcpKeepAlive(true);

        return clientConfiguration;
    }



}
