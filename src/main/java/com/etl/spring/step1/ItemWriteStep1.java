package com.etl.spring.step1;

import java.math.BigDecimal;
import java.util.List;

import javax.sql.DataSource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStreamWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import com.spring.domain.InputFile;



/**
 * 
 * @author sergio.fica.troncoso
 *
 */
public class ItemWriteStep1 implements ItemStreamWriter<InputFile> {

	private static final Logger LOGGER = LogManager.getLogger(ItemWriteStep1.class);

	private DataSource conexion;
	private JdbcTemplate jTmp;

	private String feccawa;
	private BigDecimal montoTotal;
	private Long cantidadTotal;
	


	/**
	 * 
	 * @param pathOut
	 * @param procSecMax
	 * @param fechaArchivoFinal
	 */
	public ItemWriteStep1(DataSource dataSource) {
		try {
			LOGGER.info("Termino inicio  Bean");
			

			this.jTmp.setDataSource(dataSource);
			this.conexion=dataSource;

			this.montoTotal=BigDecimal.ZERO;
			this.cantidadTotal = 0L;
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			System.exit(2);
		}
	}

	@Override
	public void write(List<? extends InputFile> items) {
		try {
			LOGGER.info("WRITER WRITER");
			
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);

			System.exit(2);
		}
	}

	@Override
	public void open(ExecutionContext executionContext) {
		try {
			LOGGER.info("OPEN WRITER");
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);

			System.exit(2);
		}
	}

	@Override
	public void update(ExecutionContext executionContext) {
		LOGGER.debug("UPDATE WRITER");
	}

	@Override
	public void close() {
		try {
			LOGGER.info("CLOSE WRITER");
			updateInpProcesos();
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);

			System.exit(2);
		}
	}
	
	private void updateInpProcesos() {
		try {

		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);

			System.exit(2);
		}
	}
}
