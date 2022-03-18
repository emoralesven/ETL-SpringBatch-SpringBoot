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
import org.springframework.batch.item.ItemStreamReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import com.etl.spring.configuration.BatchConfig;
import com.spring.domain.InputFile;



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

			if (i == this.listaLecturaBinSalida.size()) {
				return null;
			}

			BinIxiDto itemDto = this.listaLecturaBinSalida.get(i);
			i++;
			return itemDto;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			excepciones.listener(ManejoExcepciones.failed, ManejoExcepciones.COD_ERROR_LECTURA_QUERY,
					Integer.valueOf(procSec), conexion, true, this.logDto.getLninSec(), this.logDto.getProcSec());
			System.exit(2);
			return null;
		}
	}

	@Override
	public void open(ExecutionContext executionContext) {
		try {
			logger.info("Proceso READ - inicio open");
			procSec = executionContext.get("SECUENCIA_PROC").toString();
			batchConfig.setProcSecGlobal(Integer.valueOf(procSec));
			batchConfig.setFechaContableGlobal(trazaDao.calculoDiaHabil(this.feccawa, conexion));

			String numeroCiclo = trazaDao.traerNumeroCiclo(this.conexion, this.jTmp, this.feccawa,
					Integer.valueOf(procSec), this.paramNumCiclo);

			if (numeroCiclo == null) {
				logger.error("NUMERO DE CICLO ES NULL");
				excepciones.listener(ManejoExcepciones.failed, ManejoExcepciones.COD_ERROR_GENERICO,
						Integer.valueOf(procSec), conexion, true, this.logDto.getLninSec(), this.logDto.getProcSec());
				System.exit(2);
			}

			batchConfig.setNumeroCicloGlobal(numeroCiclo);

			logger.info("============================================================");
			logger.info("PROC_SEC: " + this.batchConfig.getProcSecGlobal());
			logger.info("FECHA CAWA: " + this.feccawa);
			logger.info("FECHA CONTABLE: " + this.batchConfig.getFechaContableGlobal());
			logger.info("NUMERO CICLO: " + this.batchConfig.getNumeroCicloGlobal());
			logger.info("SYSDATE: " + this.sysdate);
			logger.info("============================================================");

			this.feccawaYYMMDD = this.feccawa.substring(6, 8) + this.feccawa.substring(2, 4)
					+ this.feccawa.substring(0, 2);

			logger.info("Inicio trazabilidad INP_PROCESOS");
			trazaDao.insertInpProceso(seteoInpProcesos(), this.conexion);
			logger.info("Fin trazabilidad INP_PROCESOS");

			//PASO 1: Verificar existencia de informacion en la tabla IXI990A.
			Boolean existeInfoTablaIxi990a = this.iService.existeInfoTablaIxi990a(this.conexion,this.feccawa, this.batchConfig.getProcSecGlobal(),
					this.batchConfig.getFechaContableGlobal(),this.batchConfig.getNumeroCicloGlobal(),this.sysdate);

			//PASO 2: Verificar primera ejecucion o proceso de generacion de archivo ixi990a.
			if (!existeInfoTablaIxi990a) {
				//PADO 2.1 Lectura de archivos ixi990a de Credito y Debito
				this.listaLecturaBinEntradaArchivo = procesoLecturaArchivosIxi();
				//PASO 2.2 Llenar tabla IXI990A con la informacion de los archivos.
				this.procesoLlenadoTablaIxi990a(this.listaLecturaBinEntradaArchivo);			
			} else {
				//PASO 2.3 Proceso de generacion del archivo ixi990a
				procesoGeneracionArchivoIxi990A();
			}

		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			excepciones.listener(ManejoExcepciones.failed, ManejoExcepciones.COD_ERROR_PROCESAMIENTO_INFORMACION,
					Integer.valueOf(procSec), conexion, true, this.logDto.getLninSec(), this.logDto.getProcSec());
			System.exit(2);
		}
	}

	private List<IxiEntradaDto> procesoLecturaArchivosIxi() throws IOException {
		String ruta_credito="";
		String ruta_debito="";
       
        File file = new File(ruta_credito);
        
        List<IxiEntradaDto> records = new ArrayList<IxiEntradaDto>();
        IxiEntradaDto record= new IxiEntradaDto() ;
        BufferedReader br = new BufferedReader(new FileReader(file));
        String st;
        while ((st = br.readLine()) != null) {           
            if(st.contains("POS")||st.contains("PAPER") ||st.contains("C-ALL")) {
            	record = new IxiEntradaDto();
            	record.setType(st.substring(0,7));
            	record.setPanPrefix(st.substring(8,27));
            	record.setCardAfilliation(st.substring(28,29));
            	record.setCardBrand(st.substring(29,30));
            	record.setDestination(st.substring(30,31));
            	record.setUserRouting(st.substring(31,32));
            	record.setCheckDigit(st.substring(32,33));
            	record.setRegInd(st.substring(34,35));
            	record.setPriority(st.substring(35,36));
            	record.setPanLenght(st.substring(37,38));
            	record.setInstitutionId(st.substring(39,50));
            	record.setProductCode(st.substring(51,54));
            	record.setExtProId(st.substring(54,57));
            	record.setProgId(st.substring(57,60));
            	record.setIntCcy(st.substring(60,63));
            	record.setRegion(st.substring(64,65));
            	record.setCountry(st.substring(66,69));
            	record.setMemberId(st.substring(70,81));
            	record.setTipoPago("C");
            	records.add(record);
            }
        }
               file = new File(ruta_debito);
               br = new BufferedReader(new FileReader(file));
        while ((st = br.readLine()) != null) {           
            if(st.contains("POS")||st.contains("PAPER") ||st.contains("C-ALL")) {
            	record = new IxiEntradaDto();
            	record.setType(st.substring(0,7));
            	record.setPanPrefix(st.substring(8,27));
            	record.setCardAfilliation(st.substring(28,29));
            	record.setCardBrand(st.substring(29,30));
            	record.setDestination(st.substring(30,31));
            	record.setUserRouting(st.substring(31,32));
            	record.setCheckDigit(st.substring(32,33));
            	record.setRegInd(st.substring(34,35));
            	record.setPriority(st.substring(35,36));
            	record.setPanLenght(st.substring(37,38));
            	record.setInstitutionId(st.substring(39,50));
            	record.setProductCode(st.substring(51,54));
            	record.setExtProId(st.substring(54,57));
            	record.setProgId(st.substring(57,60));
            	record.setIntCcy(st.substring(60,63));
            	record.setRegion(st.substring(64,65));
            	record.setCountry(st.substring(66,69));
            	record.setMemberId(st.substring(70,81));
            	record.setTipoPago("D");
            	records.add(record);
            }
        }
        
		return records;
        
	}
	
	private void procesoLlenadoTablaIxi990a(List<IxiEntradaDto> listaLecturaBinEntradaArchivo) {
		Boolean procesoExistoso = this.iService.procesoLlenadoTablaIxi990a(this.conexion,listaLecturaBinEntradaArchivo);
		if (procesoExistoso) {
			logger.info("Proceso de llenado de la tabla IXI990A se realiza con éxito");
		}
	}

	private void procesoGeneracionArchivoIxi990A() {
		//PASO 2.3.1 Extraccion de los bines con accion desde la INP_BINES
		this.listaLecturaInpBines = this.iService.extracionInpBines(this.conexion);
		
		//PASO 2.3.2 Respaldo de la informacion de los bines extraidos desde la tabla INP_BINES:
		Boolean procesoExistoso = this.iService.procesoDeRespaldoInformacion(this.conexion,this.listaLecturaInpBines);
		if (procesoExistoso) {
			logger.info("Proceso de respaldo se realiza con éxito");
		}
		
		this.listaLecturaBinSalida = this.iService.lecturaTablaIxi990A(this.conexion);
		if (this.listaLecturaBinSalida.size() > 0) {
			logger.info("Cantidad de registros extraidos: " + this.listaLecturaBinSalida);
		}
	}

	

	@Override
	public void update(ExecutionContext executionContext) {
		logger.debug("UPDATE READER");
	}

	@Override
	public void close() {
		logger.info("Termino ejecución  Bean " + this.getClass().getName());
	}

	/**
	 * 
	 * @return
	 */
	public InpProcesoDto seteoInpProcesos() {
		InpProcesoDto dto = new InpProcesoDto();
		try {
			Calendar calendar = Calendar.getInstance();
			java.sql.Date ourJavaDateObject = new java.sql.Date(calendar.getTime().getTime());
			dto.setProcSec(Integer.valueOf(procSec));
			dto.setProcTipo("IN");
			dto.setFechIni(ourJavaDateObject);
			dto.setFechFin(null);
			dto.setStatusExecution(2);
			dto.setUsua("ETLBDOT");
			dto.setArch("XXX" + this.feccawaYYMMDD);
			dto.setBalan(null);
			dto.setHdtotMnto(0);
			dto.setHdtotReg(0);
			dto.setSecTxini(0);
			dto.setSecTxfin(0);
			dto.setEstdOutput("PARCI");
			dto.setOrigInfo("XXX");
			dto.setErroCod("1135");
			dto.setAudsid(0);
			dto.setClasCod(1);
			dto.setProcTipoProc("P");
			dto.setProcfech(ourJavaDateObject);
			dto.setEstdOutDebito("PARCI");
			dto.setSeqCierre(0);
			dto.setTipoCarga("CREDITO");
			dto.setSecGenNac(-2);
			dto.setSecGenInt(-2);
			dto.setSecGenGaf(-2);
			dto.setSecExtract(-2);
			dto.setSecPareo(-1);
			dto.setSecGenCn(-2);
			dto.setSecGenCh(-2);
			SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyy");
			Date date = sdf.parse(batchConfig.getFechaContableGlobal());
			Calendar cal = Calendar.getInstance();
			cal.setTime(date);
			java.sql.Date fechaContableBD = new java.sql.Date(cal.getTime().getTime());
			dto.setFechContab(fechaContableBD);

		} catch (ParseException e) {
			logger.error(e.getMessage(), e);
			excepciones.listener(ManejoExcepciones.failed, ManejoExcepciones.COD_ERROR_LEER_ARCHIVO,
					Integer.valueOf(procSec), conexion, true, this.logDto.getLninSec(), this.logDto.getProcSec());
			System.exit(2);
		}
		return dto;
	}
}
