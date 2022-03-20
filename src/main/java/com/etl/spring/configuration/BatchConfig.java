
package com.etl.spring.configuration;

import java.io.*;
import java.util.Properties;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.annotation.*;
import org.springframework.batch.core.step.builder.SimpleStepBuilder;
import org.springframework.batch.item.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.context.annotation.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import com.etl.spring.domain.InputFile;
import com.etl.spring.step1.ItemProcessorStep1;
import com.etl.spring.step1.ItemReaderStep1;
import com.etl.spring.step1.ItemWriteStep1;


/**
 * 
 * @author Accenture
 *
 */
@EnableBatchProcessing
@Configuration
@Component
public class BatchConfig extends DefaultBatchConfigurer {

	private static final Logger LOGGER = Logger.getLogger(BatchConfig.class);

	@Autowired
	NamedParameterJdbcTemplate namedTemplate;


	@Autowired
	JdbcTemplate template;

;

	@Override
	@Autowired
	public void setDataSource(DataSource dataSource) {
		// If we don't provide a datasource, an in-memory map will be used.
	}

	/**
	 * 
	 * @param step1
	 * @param jobBuilderFactory
	 * @return
	 */
	@Bean(name = "job")
	public Job bdot1250ArchivoRechazosMallaRecuperadosJob(@Qualifier("step1") Step step1,
			@Autowired JobBuilderFactory jobBuilderFactory) {
		LOGGER.info("CMM.1688 - Genera Bines Para CMM -> Versión ");
		return jobBuilderFactory.get("job").start(step1).preventRestart().build();
	}

	/**
	 * Step 1
	 * 
	 * @param read
	 * @param processor
	 * @param writer
	 * @param stepBuilderFactory
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@Bean("step1")
	public Step step1(@Qualifier("itemReaderStep1") ItemStreamReader<InputFile> read,
			@Qualifier("itemProcessorStep1") ItemProcessor<InputFile, InputFile> processor,
			@Qualifier("itemWriterStep1") ItemStreamWriter<InputFile> writer,
			@Autowired StepBuilderFactory stepBuilderFactory) {
		LOGGER.info("Ejecutando step1");
		return ((SimpleStepBuilder<InputFile, InputFile>) stepBuilderFactory.get("step1")
				.<InputFile, InputFile>chunk(10).reader(read).processor(processor).writer(writer)
				).build();
	}

	/**
	 * 
	 * @param dataSource
	 * @param feccawa
	 * @return
	 */
	@Bean(name = "itemReaderStep1")
	@StepScope
	public ItemStreamReader<InputFile> itemReader(final @Qualifier("bdot2") DataSource dataSource
			) {
	
		return new ItemReaderStep1(dataSource);
	}

	/**
	 * 
	 * @param dataSource
	 * @return
	 */
	@Bean("itemProcessorStep1")
	@StepScope
	public ItemProcessor<InputFile, InputFile> productItemProcessor(final @Qualifier("bdot2") DataSource dataSource) {
		return new ItemProcessorStep1(dataSource);
	}

	/**
	 * 
	 * @param dataSource
	 * @return
	 */
	@Bean(name = "itemWriterStep1", destroyMethod = "")
	@StepScope
	public ItemStreamWriter<InputFile> itemFileWriter(final @Qualifier("bdot2") DataSource dataSource) {
		return new ItemWriteStep1(dataSource);
	}



	/**
	 * @return the logger
	 */
	public static Logger getLogger() {
		return LOGGER;
	}
}
