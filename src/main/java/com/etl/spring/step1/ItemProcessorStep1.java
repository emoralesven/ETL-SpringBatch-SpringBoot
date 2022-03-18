package com.etl.spring.step1;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.batch.item.ItemProcessor;

import com.spring.domain.InputFile;



/**
 * 
 * @author sergio.fica.troncoso
 *
 */
public class ItemProcessorStep1 implements ItemProcessor<InputFile, InputFile> {
	private static final Logger LOGGER = Logger.getLogger(ItemProcessorStep1.class);

	DataSource conexion;
	Integer procSec;

	/**
	 * 
	 * @param dataSource
	 * @param procSec
	 */
	public ItemProcessorStep1(DataSource dataSource) {
		LOGGER.info("ItemProcessorCustom  Bean " + this.getClass().getName());

		this.conexion = dataSource;
	}

	@Override
	public InputFile process(InputFile item) throws Exception {
		try {
			return item;
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			System.exit(2);
		}
		return null;
	}
}
