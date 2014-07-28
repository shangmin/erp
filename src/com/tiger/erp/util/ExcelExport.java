package com.tiger.erp.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFClientAnchor;
import org.apache.poi.hssf.usermodel.HSSFComment;
import org.apache.poi.hssf.usermodel.HSSFPatriarch;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import com.tiger.erp.common.Excel;
import com.tiger.erp.common.ExcelVLDException;
import com.tiger.erp.common.ExportException;
import com.tiger.erp.common.ExportMessage;
import com.tiger.erp.common.SystemConstant;

public class ExcelExport {
	
	public static final String GET_METHOD_PREFIX = "get";
	public static final String SET_METHOD_PREFIX = "set";
	public static final String CONVERT_METHOD_PREFIX = "Convert";
	
	@SuppressWarnings("unchecked")
	public static void exportExcel(String title,Class clazz,Collection dataList,OutputStream out) throws ExportException {
		if(dataList == null || dataList.size() == 0) {
			throw new ExportException("数据为空");
		}
		HSSFWorkbook workbook = new HSSFWorkbook();
		Sheet sheet = workbook.createSheet(title);
		Field fields[] = clazz.getDeclaredFields();
		List<String> columnList = new ArrayList<String>();
		Map<String,Method> getMethods = new HashMap<String,Method>();
		Map<String,Method> converMethod = new HashMap<String,Method>();
		for (int i = 0; i < fields.length; i++) {
			Excel excel = fields[i].getAnnotation(Excel.class);
			String fieldName = fields[i].getName();
			if(excel == null) {
				excel = scanMethodAnnotation(clazz,fieldName);
			}
			if(excel != null && excel.isExport()) {
				columnList.add(excel.columnName());
				StringBuffer getMethodName = paseMethodName(GET_METHOD_PREFIX,fieldName);
				putMethod(clazz,excel,getMethods,converMethod,getMethodName,fields[i].getType());
			}
		}
		createHeader(sheet,columnList);
		int index = 0;
		for (Object object : dataList) {
			index ++ ;
			Row row = sheet.createRow(index);
			for (int i = 0 ; i< columnList.size() ; i++ ) {
				sheet.autoSizeColumn(i);
				Cell cell = row.createCell(i);
				setCellValue(cell,converMethod,getMethods,columnList.get(i),object);
			}
		}
		try {
			workbook.write(out);
		} catch (IOException e) {
			throw new ExportException(e);
		}
	}
	
	private static void setCellValue(Cell cell,Map<String, Method> converMethod, Map<String, Method> getMethods, String columnName,Object object ) throws ExportException {
		Object value = null;
		try {
			if(converMethod.containsKey(columnName.substring(6))) {
					value = converMethod.get(columnName).invoke(object, new Class[]{});
			} else {
			    value = getMethods.get(columnName).invoke(object, new Class[]{});
			    if(getMethods.get(columnName).getReturnType().equals(Date.class)) {
			        value = DateUtil.formatDate((Date)value, SystemConstant.DATE_CHS);
			    }
			}
		} catch (IllegalArgumentException e) {
			throw new ExportException(e);
		} catch (IllegalAccessException e) {
			throw new ExportException(e);
		} catch (InvocationTargetException e) {
			throw new ExportException(e);
		}
		cell.setCellValue(value == null ? null : value.toString());
	}

	private static void createHeader(Sheet sheet, List<String> columnList) {
		Row firstRow = sheet.createRow(0);
		for (int i = 0 ; i< columnList.size() ; i++ ) {
			sheet.autoSizeColumn(i);
			Cell cell = firstRow.createCell(i);
			  String columnName= MessageUtils.getProperty(columnList.get(i)) == "" ? columnList.get(i) :  MessageUtils.getProperty(columnList.get(i));
			cell.setCellValue(new HSSFRichTextString(columnName));
		}
	}

	@SuppressWarnings("unchecked")
	private static void putMethod(Class clazz, Excel excel, Map<String, Method> methods,  Map<String, Method> converMethod,StringBuffer methodName, Class  type) throws ExportException {
		try {
			if(StringUtils.startsWith(methodName.toString(), GET_METHOD_PREFIX)) {
				Method method = clazz.getDeclaredMethod(methodName.toString(), new Class[]{});
				methods.put(excel.columnName(), method);
			} else if(StringUtils.startsWith(methodName.toString(), SET_METHOD_PREFIX)){
				Method method = clazz.getDeclaredMethod(methodName.toString(), new Class[]{type});
				methods.put(excel.columnName(), method);
			}
		} catch (SecurityException e) {
			throw new ExportException(e);
		} catch (NoSuchMethodException e) {
			throw new ExportException(e);
		}
		if(excel.isConvert() && !Date.class.equals(type)) {
			methodName.append(CONVERT_METHOD_PREFIX);
			try {
				if(StringUtils.startsWith(methodName.toString(), GET_METHOD_PREFIX)) {
					Method convertMethod = clazz.getDeclaredMethod(methodName.toString(), new Class[]{});
					converMethod.put(excel.columnName(), convertMethod);
				} else if(StringUtils.startsWith(methodName.toString(), SET_METHOD_PREFIX)){
					Method convertMethod = clazz.getDeclaredMethod(methodName.toString(), new Class[]{String.class});
					converMethod.put(excel.columnName(), convertMethod);
				}
			} catch (SecurityException e) {
				throw new ExportException(e);
			} catch (NoSuchMethodException e) {
				throw new ExportException(e);
			}
	    }
	}

	private static StringBuffer paseMethodName(String prefix, String fieldName) {
		StringBuffer methodName = new StringBuffer(prefix);
		methodName.append(StringUtils.capitalize(fieldName));
		return methodName;
	}

	@SuppressWarnings("unchecked")
	private static Excel scanMethodAnnotation(Class clazz, String fieldName) {
		Method[] methods = clazz.getDeclaredMethods();
		for (Method method : methods) {
			method.setAccessible(true);
			String property = StringUtils.uncapitalize(method.getName().replaceFirst(GET_METHOD_PREFIX, StringUtils.EMPTY_STR));
			if(StringUtils.equals(property, fieldName)){
				return method.getAnnotation(Excel.class);
			}
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public static Collection importExcel(File file,Class clazz) throws ExportException {
		Collection dataList = new ArrayList();
		List<String> columnList = new ArrayList<String>();
		Map<String,Method> setMethods = new HashMap<String,Method>();
		Map<String,Method> converMethod = new HashMap<String,Method>();
		Field fields[] = clazz.getDeclaredFields();
		for (int i = 0; i < fields.length; i++) {
			Excel excel = fields[i].getAnnotation(Excel.class);
			String fieldName = fields[i].getName();
			if(excel == null) {
				excel = scanMethodAnnotation(clazz,fieldName);
			}
			if(excel != null && excel.isImport()) {
				StringBuffer setMethodName = paseMethodName(SET_METHOD_PREFIX,fieldName);
				putMethod(clazz,excel,setMethods,converMethod,setMethodName,fields[i].getType());
			}
		}
		try {
			HSSFWorkbook workbook = new HSSFWorkbook(new FileInputStream(file));
			HSSFSheet sheet = workbook.getSheetAt(0);
			Iterator<Row> rowIterator = sheet.rowIterator();
			getColumnName(rowIterator,columnList);
			while(rowIterator.hasNext()) {
				Row row = rowIterator.next();
				Iterator<Cell> cellIterator = row.cellIterator();
				Object object = clazz.newInstance();
				int index = 0;
				sheet.autoSizeColumn(index);
				while(cellIterator.hasNext()) {
					Cell cell = cellIterator.next();
				    setObjectValue(cell, converMethod, setMethods, columnList.get(index),object);
					index ++;
				}
				dataList.add(object);
			}
		} catch (FileNotFoundException e) {
			throw new ExportException(e);
		} catch (IOException e) {
			throw new ExportException(e);
		} catch (InstantiationException e) {
			throw new ExportException(e);
		} catch (IllegalAccessException e) {
			throw new ExportException(e);
		}
		return dataList;
	}
	
	
	public static void handError(File file,ExcelVLDException vld){
		List<String> columnList = new ArrayList<String>();
		try {
			HSSFWorkbook workbook = new HSSFWorkbook(new FileInputStream(file));
			HSSFSheet sheet = workbook.getSheetAt(0);
		    HSSFPatriarch p = sheet.createDrawingPatriarch();
			Iterator<Row> rowIterator = sheet.rowIterator();
			getColumnName(rowIterator,columnList);
			List<ExportMessage> errors = vld.getErrors();
			if(errors != null && errors.size() > 0 ) {
				int rowNum = 1;
				while(rowIterator.hasNext()) {
					Row row = rowIterator.next();
					for (ExportMessage exportMessage : errors) {
						if(rowNum == exportMessage.getLineNum()) {
							Cell cell = row.getCell(columnList.indexOf(exportMessage.getColomuName()));
							cell.setCellStyle(getErrorStyle(workbook));
							if(StringUtils.isNotEmptyString(exportMessage.getMessage())) {
								HSSFComment comment = p.createComment(new HSSFClientAnchor(0,0, 0, 0, (short) 5, 7, (short) 8, 9));
								comment.setString(new HSSFRichTextString(exportMessage.getMessage()));
								cell.setCellComment(comment);
							}
						}
					}
					rowNum ++;
				}
				moveErrorFile(workbook,file);
			}
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		}  catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	
	private static void moveErrorFile(HSSFWorkbook workbook, File file) throws FileNotFoundException, IOException {
			String path = file.getParent();
			String fileName = file.getName();
			workbook.write(new FileOutputStream(new File(path,System.currentTimeMillis()+ fileName)));
	}

	@SuppressWarnings("unchecked")
	private static void  validate(Collection<Unit> dataList) throws ExcelVLDException{
		if(dataList != null && !dataList.isEmpty()) {
			ExcelVLDException vld = new ExcelVLDException();
			int rowNum = 1;
			for (Iterator iterator = dataList.iterator(); iterator.hasNext();) {
				Unit unit = (Unit) iterator.next();
					if(unit.getUnitName().length() < 3) {
						vld.reject(rowNum,getColomuName(Unit.class,"unitName"), "to short");
				    }
				rowNum ++;
			}
			if(vld.hasError()) {
				throw vld;
			}
		}
	}
	
	@SuppressWarnings("unchecked")
    private static String getColomuName(Class clazz, String fieldName) {
    	StringBuffer getMethodName = paseMethodName(GET_METHOD_PREFIX,fieldName);
    	try {
			Field field = clazz.getDeclaredField(fieldName);
			Excel excel = null;
			if(field != null) {
				excel = field.getAnnotation(Excel.class);
			}
			if(excel == null) {
				Method method = clazz.getDeclaredMethod(getMethodName.toString(), new Class[]{});
				if(method != null) {
					excel = method.getAnnotation(Excel.class);
					if(excel != null) {
						return excel.columnName();
					}
				}
			} else {
				return excel.columnName();
			}
		} catch (SecurityException e) {
			throw new RuntimeException(e);
		} catch (NoSuchFieldException e) {
			throw new RuntimeException(e);
		} catch (NoSuchMethodException e) {
			throw new RuntimeException(e);
		}
		return null;
	}

	private static void setObjectValue(Cell cell, Map<String, Method> converMethod, Map<String, Method> methods, String columnName, Object object) throws ExportException {
		try {
			if(converMethod.containsKey(columnName)) {
				Method setMethod = converMethod.get(columnName);
				String value = cell.getStringCellValue();
				setMethod.invoke(object, value);
			} else {  
				Method setMethod = methods.get(columnName);
				Type[] types = setMethod.getGenericParameterTypes();
				if(types[0].equals(Integer.class)) {
					Integer value = Integer.valueOf(cell.getStringCellValue());
					setMethod.invoke(object, value);
				}else if(types[0].equals(String.class)) {
					String value = cell.getStringCellValue();
					setMethod.invoke(object, value);
				}else if(types[0].equals(Date.class)) {
					Date value = cell.getDateCellValue();
					setMethod.invoke(object, value);
				}else if(types[0].equals(Double.class)) {
					Double value = cell.getNumericCellValue();
					setMethod.invoke(object, value);
				}else if(types[0].equals(Short.class)) {
					Short value = Short.valueOf(cell.getStringCellValue());
					setMethod.invoke(object, value);
				}else if(types[0].equals(Float.class)) {
					Float value = Float.valueOf(cell.getStringCellValue());
					setMethod.invoke(object, value);
				}
			}
		} catch (IllegalArgumentException e) {
			throw new ExportException(e);
		} catch (IllegalAccessException e) {
			throw new ExportException(e);
		} catch (InvocationTargetException e) {
			throw new ExportException(e);
		}
	}


	
	private static void getColumnName(Iterator<Row> rowIterator, List<String> columnList) {
		Row columnNames = rowIterator.next();
		Iterator<Cell> columnIterator = columnNames.cellIterator();
		while(columnIterator.hasNext()) {
			Cell cell = columnIterator.next();
			String value = cell.getStringCellValue();
			columnList.add(value);
		}
	}

	
	private static HSSFCellStyle getErrorStyle(HSSFWorkbook workbook) {
		HSSFCellStyle errorStyle = workbook.createCellStyle();
		errorStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
		errorStyle.setFillForegroundColor(HSSFColor.RED.index);
		return errorStyle;
	}

	public static void main(String[] args) throws Exception {
		Unit unit1 = new Unit(1,"zhang",new Date());
		Unit unit2 = new Unit(2,"li",new Date());
		Unit unit3 = new Unit(3,"wang",new Date());
		List list = new ArrayList();
		list.add(unit1);
		list.add(unit2);
		list.add(unit3);
		OutputStream out = new FileOutputStream("d:\\test.xls");
		exportExcel("Unit", Unit.class, list, out);
		
		Collection dataList = importExcel(new File("d:\\test.xls"),Unit.class);
		for (Object object : dataList) {
			System.out.println(object);
		}
		try {
			validate(dataList);
		} catch (ExcelVLDException e) {
			System.out.println(e);
			handError(new File("d:\\test.xls"),e);
		}
	}
	
}
class Unit implements java.io.Serializable {

	@Excel(columnName="ID")
	private Integer unitId;
	@Excel(columnName="Name")
	private String unitName;
	private Date createDate;

	public Unit() {
	}

	public Unit(Integer unitId, String unitName, Date createDate) {
		super();
		this.unitId = unitId;
		this.unitName = unitName;
		this.createDate = createDate;
	}

	public Integer getUnitId() {
		return unitId;
	}

	public void setUnitId(Integer unitId) {
		this.unitId = unitId;
	}

	public String getUnitName() {
		return unitName;
	}

	public void setUnitName(String unitName) {
		this.unitName = unitName;
	}

	@Excel(columnName="Create Date",isConvert=true)
	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}
	public String getCreateDateConvert() {
		return StringUtils.formatDate(createDate,SystemConstant.DATE_CHS);
	}

	public void setCreateDateConvert(String createDate) {
		this.createDate = StringUtils.parseDate(createDate, SystemConstant.DATE_CHS);
	}
}



