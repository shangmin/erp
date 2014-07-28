package com.tiger.erp.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.tiger.erp.common.ExportException;



public class ExportEngine {
	private static final int MAX_ROWS = 65535;
	private static final int MAX_COLS = 256;
	private static final int DEF_COL_WIDTH = 256 * 20;
	private static final int MIN_COL_WIDTH = 256 * 3;
	private static final int MAX_COL_WIDTH = 256 * 50;
	private static final String ETMS_CFG_PATH = System.getProperty("java.class.path");
	private static final Logger logger = Logger.getLogger(ExportEngine.class);
	private static ExportEngine instance;
	private static Map<String, ExportConfig> exportConfigCache;
	private static Object mutex = new Object();
	private HSSFCellStyle defCellStyle;
	private HSSFCellStyle headerStyle;
	private HSSFCellStyle dateStyle;

	private static enum ExportDataType {
		Boolean, Number, Date, String
	}

	private ExportEngine() {
		super();
	}

	public static ExportEngine getInstance() throws ExportException {
		String path = ETMS_CFG_PATH;
		if (StringUtils.isNotEmptyString(path) && !path.endsWith("/") && !path.endsWith("\\")) {
			path = path + System.getProperty("file.separator");
		}
		return getInstance(path + "data-export.xml");
	}

	public static ExportEngine getInstance(String exportConfigFilePath) throws ExportException {
		synchronized(mutex){
			if (instance != null) {
				logger.debug("returning singleton ExportEngine.");
				return instance;
			} else {
				logger.debug("initiating ExportEngine.");
				File exportConfigFile = new File(exportConfigFilePath);
				logger.debug("export config file path: " + exportConfigFilePath);
				if (exportConfigFile.exists() && exportConfigFile.isFile()) {
					Document doc;
					try {
						instance = new ExportEngine();
						doc = instance.read(exportConfigFile);
						instance.cacheExportConfig(doc);
					} catch (MalformedURLException e) {
						instance = null;
						throw new ExportException("export config initiation error.", e);
					} catch (DocumentException e) {
						instance = null;
						throw new ExportException("export config initiation error.", e);
					}
				} else {
					throw new ExportException("export config xml not found.");
				}
				return instance;
			}
		}
	}

	public String getExportFileName(String exportId) throws ExportException {
		logger.debug("get export file name by export ID.");
		ExportConfig exportConfig = exportConfigCache.get(exportId);
		if (null == exportConfig) {
			throw new ExportException("could not find the export config by \"" + exportId + "\".");
		}
		String fileName = exportConfig.getFileName();
		if (StringUtils.isEmptyString(fileName)) {
			throw new ExportException("file name can not be empty. [exportId: " + exportId + "]");
		}
		return fileName;
	}

	public <T> File exportToExcel(String exportId, Collection<T> vos, String filePath) throws ExportException {
		ExportEngine.logger.debug("export data to excel xls file.");
		ExportConfig exportConfig = exportConfigCache.get(exportId);
		if (null == exportConfig) {
			throw new ExportException("could not find the export config by \"" + exportId + "\".");
		}
		if (StringUtils.isNotEmptyString(filePath) && !filePath.endsWith("/") && !filePath.endsWith("\\")) {
			filePath = filePath + System.getProperty("file.separator");
		}
		if (StringUtils.isNotEmptyString(filePath)) {
			File pathDir = new File(filePath);
			if (!pathDir.exists()) {
				boolean rt = pathDir.mkdirs();
				if(rt){
					logger.debug("Created directory"+filePath);
				}else{
					logger.warn("Failed to create directory" + filePath);
				}
			}
		}
		// build Excel work book
		HSSFWorkbook wb = this.buildWookBook(vos, exportConfig.getExportItems());
		// create export file
		String fileUniqueName = filePath + "export_" + exportId + "_" + String.valueOf(System.currentTimeMillis()) + ".xls";
		File exportFile = new File(fileUniqueName);
		while (exportFile.exists()) {
			fileUniqueName = filePath + "export_" + exportId + "_" + String.valueOf(System.currentTimeMillis()) + ".xls";
			exportFile = new File(fileUniqueName);
		}
		ExportEngine.logger.debug("export temp file: " + fileUniqueName);
		FileOutputStream fos;
		try {
			fos = new FileOutputStream(exportFile);
			wb.write(fos);
			fos.flush();
			fos.close();
		} catch (FileNotFoundException e) {
			throw new ExportException("could not create file \"" + fileUniqueName + "\"", e);
		} catch (IOException e) {
			throw new ExportException(e);
		}
		return exportFile;
	}

	private Document read(File file) throws MalformedURLException, DocumentException {
		SAXReader reader = new SAXReader();
		Document document = reader.read(file);
		return document;
	}

	@SuppressWarnings("unchecked")
	private void cacheExportConfig(Document doc) throws ExportException {
        logger.debug("resolving export config xml file.");
		Element root = doc.getRootElement();
		List<Element> exportConfigs = root.elements("export");
		if (null == exportConfigs) {
			return;
		}
		exportConfigCache = new HashMap<String, ExportConfig>();
		for (Element exportConfig : exportConfigs) {
			String exportId = exportConfig.elementTextTrim("export-id");
			if (StringUtils.isEmptyString(exportId)) {
				throw new ExportException("the \"export-id\" value can not be empty.");
			}
			if (exportConfigCache.containsKey(exportId)) {
				throw new ExportException("the \"export-id\" value \"" + exportId + "\" duplicated.");
			}
			String fileName = exportConfig.elementTextTrim("export-file-name");
			if (StringUtils.isEmptyString(fileName)) {
				throw new ExportException("the \"export-file-name\" value can not be empty.");
			}
			Element exportItemsElement = exportConfig.element("export-items");
			if (null == exportItemsElement) {
				return;
			}
			List<Element> exportItemConfigs = exportItemsElement.elements("export-item");
			if (null == exportItemConfigs) {
				return;
			}
			Set<ExportItemConfig> exportItems = new HashSet<ExportItemConfig>(0);
			for (Element exportItemConfig : exportItemConfigs) {
				String seqStr = exportItemConfig.elementTextTrim("seq");
				Integer seq = null;
				try {
					seq = Integer.valueOf(seqStr);
					if (seq.intValue() >= MAX_COLS) {
						throw new ExportException("columns can not more than " + MAX_COLS + ".");
					}
					if (seq.intValue() < 0) {
						throw new ExportException("the \"seq\" value \"" + seqStr + "\" invalid.");
					}
				} catch (NumberFormatException e) {
					throw new ExportException("the \"seq\" value \"" + seqStr + "\" invalid.");
				}
				String colLabel = exportItemConfig.elementText("column-label");
				String fieldName = exportItemConfig.elementText("field-name");
				if (StringUtils.isEmptyString(fieldName)) {
					throw new ExportException("the \"field-name\" value can not be empty.");
				}
				String dataType = exportItemConfig.elementText("data-type");
				if (StringUtils.isEmptyString(dataType)) {
					throw new ExportException("the \"data-type\" value can not be empty.");
				}
				ExportDataType[] typeEnums = ExportDataType.values();
				boolean isDataTypeValid = false;
				for (ExportDataType typeEnum : typeEnums) {
					if (typeEnum.toString().equals(dataType)) {
						isDataTypeValid = true;
						break;
					}
				}
				if (!isDataTypeValid) {
					throw new ExportException("the \"data-type\" value \"" + dataType + "\" invalid.");
				}
				ExportItemConfig exportItem = new ExportItemConfig();
				exportItem.setSeq(seq);
				exportItem.setColLabel(colLabel);
				exportItem.setFieldName(fieldName);
				exportItem.setDataType(dataType);
				if (!exportItems.add(exportItem)) {
					throw new ExportException("the \"seq\" of \"export-item\" duplicated.");
				}
			}
			ExportConfig export = new ExportConfig();
			export.setExportId(exportId);
			export.setFileName(fileName);
			export.setExportItems(exportItems);
			exportConfigCache.put(exportId, export);
		}
	}

	private HSSFWorkbook buildWookBook(Collection<?> vos, Set<ExportItemConfig> exportItems) throws ExportException {
		ExportEngine.logger.debug("build excel work book.");
		HSSFWorkbook wb = new HSSFWorkbook();
		if (null == exportItems) {
			return wb;
		}
		// init cell style
		this.initCellStyle(wb);
		// set cell data
		if (null != vos) {
			// calculate the total sheets count
			Iterator<?> iter = vos.iterator();
			int curRow = 0;
			HSSFSheet sheet = null;
			while (iter.hasNext()) {
				// create sheet and fill sheet data
				if (curRow % MAX_ROWS == 0) {
					sheet = this.createSheetWithHeader(wb, exportItems);
				}
				Object vo = iter.next();
				if (null != vo) {
					HSSFRow row = sheet.createRow(curRow % MAX_ROWS + 1);
					Iterator<ExportItemConfig> itemIter = exportItems.iterator();
					while (itemIter.hasNext()) {
						ExportItemConfig item = itemIter.next();
						if (null != item) {
							Integer seq = item.getSeq();
							String fieldName = item.getFieldName();
							String dataType = item.getDataType();
							HSSFCell cell = row.createCell(seq.intValue());
							this.fillCellValue(cell, vo, dataType, fieldName);
						}
					}
				}
				curRow++;
				// adjust column width when current sheet finished
				if (curRow % MAX_ROWS == 0 || !iter.hasNext()) {
					Iterator<ExportItemConfig> itemIter = exportItems.iterator();
					while (itemIter.hasNext()) {
						ExportItemConfig item = itemIter.next();
						if (null != item) {
							Integer seq = item.getSeq();
							String dataType = item.getDataType();
							if (!ExportDataType.Date.toString().equals(dataType)) {
								sheet.autoSizeColumn(seq.intValue());
								int colWidth = sheet.getColumnWidth(seq.intValue());
								if (colWidth < MIN_COL_WIDTH) {
									sheet.setColumnWidth(seq.intValue(), MIN_COL_WIDTH);
								} else if (colWidth > MAX_COL_WIDTH) {
									sheet.setColumnWidth(seq.intValue(), MAX_COL_WIDTH);
								}
							} else {
								sheet.setColumnWidth(seq.intValue(), DEF_COL_WIDTH);
							}
						}
					}
				}
			}
		}
		return wb;
	}

	public void buildWookBook(HSSFWorkbook workbook,Collection<?> vos, String exportId) throws ExportException {
		ExportConfig exportConfig = exportConfigCache.get(exportId);
		if (null == exportConfig) {
			throw new ExportException("could not find the export config by \"" + exportId + "\".");
		}
		ExportEngine.logger.debug("build excel work book.");
		// init cell style
		this.initCellStyle(workbook);
		// set cell data
		if (null != vos) {
			// calculate the total sheets count
			Iterator<?> iter = vos.iterator();
			int curRow = 0;
			HSSFSheet sheet = null;
			while (iter.hasNext()) {
				// create sheet and fill sheet data
				if (curRow % MAX_ROWS == 0) {
					sheet = this.createSheetWithHeader(workbook, exportConfig.getExportItems());
				}
				Object vo = iter.next();
				if (null != vo) {
					HSSFRow row = sheet.createRow(curRow % MAX_ROWS + 1);
					Iterator<ExportItemConfig> itemIter = exportConfig.getExportItems().iterator();
					while (itemIter.hasNext()) {
						ExportItemConfig item = itemIter.next();
						if (null != item) {
							Integer seq = item.getSeq();
							String fieldName = item.getFieldName();
							String dataType = item.getDataType();
							HSSFCell cell = row.createCell(seq.intValue());
							this.fillCellValue(cell, vo, dataType, fieldName);
						}
					}
				}
				curRow++;
				// adjust column width when current sheet finished
				if (curRow % MAX_ROWS == 0 || !iter.hasNext()) {
					Iterator<ExportItemConfig> itemIter = exportConfig.getExportItems().iterator();
					while (itemIter.hasNext()) {
						ExportItemConfig item = itemIter.next();
						if (null != item) {
							Integer seq = item.getSeq();
							String dataType = item.getDataType();
							if (!ExportDataType.Date.toString().equals(dataType)) {
								sheet.autoSizeColumn(seq.intValue());
								int colWidth = sheet.getColumnWidth(seq.intValue());
								if (colWidth < MIN_COL_WIDTH) {
									sheet.setColumnWidth(seq.intValue(), MIN_COL_WIDTH);
								} else if (colWidth > MAX_COL_WIDTH) {
									sheet.setColumnWidth(seq.intValue(), MAX_COL_WIDTH);
								}
							} else {
								sheet.setColumnWidth(seq.intValue(), DEF_COL_WIDTH);
							}
						}
					}
				}
			}
			//autoSizeColumn(sheet, exportConfig.getExportItems());
		}
	}
	
	private void initCellStyle(HSSFWorkbook wb) {
		this.defCellStyle = wb.createCellStyle();
		this.defCellStyle.setWrapText(true);
		this.defCellStyle.setVerticalAlignment(CellStyle.VERTICAL_TOP);
		this.headerStyle = wb.createCellStyle();
		this.headerStyle.cloneStyleFrom(this.defCellStyle);
		this.headerStyle.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
		HSSFFont headerFont = wb.createFont();
		headerFont.setBoldweight(Font.BOLDWEIGHT_BOLD);
		this.headerStyle.setFont(headerFont);
		this.headerStyle.setAlignment(CellStyle.ALIGN_CENTER);
		this.headerStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
		this.headerStyle.setFillForegroundColor(new HSSFColor.YELLOW().getIndex());
		HSSFDataFormat df = wb.createDataFormat();
		this.dateStyle = wb.createCellStyle();
		this.dateStyle.cloneStyleFrom(this.defCellStyle);
		this.dateStyle.setDataFormat(df.getFormat("yyyy-MM-dd HH:mm:ss"));
	}

	private HSSFSheet createSheetWithHeader(HSSFWorkbook wb, Set<ExportItemConfig> exportItems) {
		// create sheet
		HSSFSheet sheet = wb.createSheet();
		// create header and set content
		HSSFRow header = sheet.createRow(0);
		Iterator<ExportItemConfig> iter = exportItems.iterator();
		while (iter.hasNext()) {
			ExportItemConfig item = iter.next();
			if (null != item) {
				Integer seq = item.getSeq();
				sheet.setDefaultColumnStyle(seq.intValue(), this.defCellStyle);
				String colLabel = item.getColLabel();
				HSSFCell cell = header.createCell(seq.intValue());
				cell.setCellStyle(this.headerStyle);
				cell.setCellValue(colLabel);
			}
		}
		return sheet;
	}

	private void autoSizeColumn(HSSFSheet sheet, Set<ExportItemConfig> exportItems) {
		// create sheet
		Iterator<ExportItemConfig> iter = exportItems.iterator();
		while (iter.hasNext()) {
			ExportItemConfig item = iter.next();
			if (null != item) {
				Integer seq = item.getSeq();
				sheet.autoSizeColumn(seq);
			}
		}
	}
	
	private void fillCellValue(HSSFCell cell, Object obj, String dataType, String fieldName) throws ExportException {
		Object valueObj = null;
		try {
			valueObj = this.resolveFieldValue(obj, fieldName);
		} catch (ExportException e) {
			throw new ExportException("can not get the value of \"" + fieldName + "\" field.", e);
		}
		// resolve field value with the specific data type
		final String typeMismatchErrMsg = "field \"" + fieldName + "\" can not be resolved as \"" + dataType + "\" type.";
		if (ExportDataType.Boolean.toString().equals(dataType)) {
			try {
				cell.setCellType(Cell.CELL_TYPE_BOOLEAN);
				if (null != valueObj) {
					Boolean cellValue = (Boolean) valueObj;
					cell.setCellValue(cellValue.booleanValue());
				}
			} catch (ClassCastException e) {
				throw new ExportException(typeMismatchErrMsg, e);
			}
		} else if (ExportDataType.Number.toString().equals(dataType)) {
			try {
				cell.setCellType(Cell.CELL_TYPE_NUMERIC);
				if (null != valueObj) {
					Number cellValue = (Number) valueObj;
					cell.setCellValue(cellValue.doubleValue());
				}
			} catch (ClassCastException e) {
				throw new ExportException(typeMismatchErrMsg, e);
			}
		} else if (ExportDataType.Date.toString().equals(dataType)) {
			try {
				cell.setCellStyle(this.dateStyle);
				if (null != valueObj) {
					Date cellValue = (Date) valueObj;
					cell.setCellValue(cellValue);
				}
			} catch (ClassCastException e) {
				throw new ExportException(typeMismatchErrMsg, e);
			}
		} else if (ExportDataType.String.toString().equals(dataType)) {
			cell.setCellType(Cell.CELL_TYPE_STRING);
			if (null != valueObj) {
				cell.setCellValue(valueObj.toString());
			}
		} else {
			throw new ExportException("data type \"" + dataType + "\" unsupported.");
		}
	}

	private Object resolveFieldValue(Object obj, String fieldName) throws ExportException {
		 try{   
			return PropertyUtils.getNestedProperty(obj, fieldName);
		} catch (IllegalAccessException e) {
			throw new ExportException(e);
		} catch (InvocationTargetException e) {
			throw new ExportException(e);
		} catch (NoSuchMethodException e) {
			throw new ExportException(e);
		}
	}
}
