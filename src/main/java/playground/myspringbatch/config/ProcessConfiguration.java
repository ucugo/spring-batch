package playground.myspringbatch.config;

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
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.support.ListItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.UrlResource;
import playground.myspringbatch.model.InputItem;
import playground.myspringbatch.model.OutputItem;
import playground.myspringbatch.processor.RecservAuditItemProcessor;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

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
    public DefaultLineMapper<InputItem> defaultLineMapper() {

        DefaultLineMapper<InputItem> defaultLineMapper = new DefaultLineMapper<>();
        final DelimitedLineTokenizer lineTokenizer =
                new DelimitedLineTokenizer();
        lineTokenizer.setNames(new String[]{"name", "description", "date", "balance", "price", "interest"});

        defaultLineMapper.setLineTokenizer(lineTokenizer);

        final BeanWrapperFieldSetMapper<InputItem> beanWrapperFieldSetMapper =
                new BeanWrapperFieldSetMapper<>();
        beanWrapperFieldSetMapper.setTargetType(InputItem.class);
        defaultLineMapper.setFieldSetMapper(beanWrapperFieldSetMapper);

        return defaultLineMapper;
    }


    @Bean
    public ItemReader<InputItem> reader(DefaultLineMapper<InputItem> defaultLineMapper) throws MalformedURLException {
        FlatFileItemReader<InputItem> reader = new FlatFileItemReader<>();
        reader.setResource(new ClassPathResource("quotes.csv"));

        reader.setLineMapper(defaultLineMapper);
        return reader;
    }

    @Bean
    public ItemWriter<OutputItem> writer() {
        return new ListItemWriter<>();
    }

    @Bean
    public Job TickerPriceConversion(Step s1) throws MalformedURLException {
        return jobBuilderFactory.get("TickerPriceConversion").incrementer(new RunIdIncrementer()).flow(s1).end().build();
    }

    @Bean
    public Step convertPrice(@Qualifier("reader")ItemReader<InputItem> reader,
                             @Qualifier("writer")ItemWriter<OutputItem> writer,
                             @Qualifier("recservAuditItemProcessor")RecservAuditItemProcessor processor) throws MalformedURLException {
        return stepBuilderFactory.get("convertPrice")
                .<InputItem, OutputItem> chunk(1000)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .build();
    }

}
