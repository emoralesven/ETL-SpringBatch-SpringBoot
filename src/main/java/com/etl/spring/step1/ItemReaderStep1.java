package com.etl.spring.step1;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.sql.DataSource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.ItemStreamReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import com.etl.spring.configuration.BatchConfig;
import com.etl.spring.domain.InputFile;




/**
 * 
 * @author sergio.fica.troncoso
 *
 */
public class ItemReaderStep1 implements ItemStreamReader<InputFile> {

	private static final Logger logger = LogManager.getLogger(ItemReaderStep1.class);

	@Autowired
	BatchConfig batchConfig;

	@Autowired
	JdbcTemplate jTmp;


	int i = 0;
	private String feccawa;
	private String procSec;
	private Integer sysdate;
	private String paramNumCiclo;
	private String feccawaYYMMDD;


	/**
	 * 
	 * @param dataSource
	 * @param tmp
	 * @param feccawa
	 */
	public ItemReaderStep1(DataSource dataSource) {

	}

	@Override
	public InputFile read() {
		try {

			
				return null;

		}catch (Exception e) {
			logger.error(e.getMessage(), e);

			return null;
		}
	}

	@Override
	public void open(ExecutionContext executionContext) {
		try {
			logger.info("Proceso READ - inicio open");




		} catch (Exception e) {
			logger.error(e.getMessage(), e);

			System.exit(2);
		}
	}

	@Override
	public void update(ExecutionContext executionContext) throws ItemStreamException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void close() throws ItemStreamException {
		// TODO Auto-generated method stub
		
	}


}
