package com.tiger.erp.common;

import java.util.ArrayList;
import java.util.List;

import com.tiger.erp.util.StringUtils;




public class ExcelVLDException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private List<ExportMessage> errors = new ArrayList<ExportMessage>(0);

	private List<ExportMessage> warnings = new ArrayList<ExportMessage>(0);
	
	private static final String LINE_SEPARATOR = System.getProperty("line.separator");

	public void reject(int lineNum, String colomuName, String message){
		if(StringUtils.isNotEmptyString(message)){
			errors.add(new ExportMessage(lineNum,colomuName,message));
		}else {
			errors.add(new ExportMessage(lineNum,colomuName));
		}
	}
	
	public void warn(int lineNum, String colomuName, String message){
		if(StringUtils.isNotEmptyString(message)){
			warnings.add(new ExportMessage(lineNum,colomuName,message));
		}else {
			warnings.add(new ExportMessage(lineNum,colomuName));
		}
	}
	public List<ExportMessage> getErrors() {
		return errors;
	}
	
	public List<ExportMessage> getWarnings() {
		return warnings;
	}
	
	public void clear(){
		this.errors.clear();
		this.warnings.clear();
	}
	
	public boolean hasWarning(){
		return this.warnings!=null && this.warnings.size()>0;
	}
	
	public boolean hasError(){
		return this.errors!=null && this.errors.size()>0;
	}
	
	public void addError (ExcelVLDException vld) {
		if (null == vld) return;
		
		errors.addAll( vld.getErrors() );
	}
	
	public void addWarning (ExcelVLDException vld) {
		if (null == vld) return;
		
		warnings.addAll( vld.getWarnings() );
	}
	
	
	public String getFullAndPrettyMessage(){
		StringBuilder sb = new StringBuilder();
		for(ExportMessage msg: errors){
			sb.append("[ERROR] Line [");
			sb.append(StringUtils.trim(String.valueOf(msg.getLineNum()))); 
			sb.append("] "); 
			sb.append(StringUtils.trim(msg.getColomuName())); 
			sb.append(" :"); 
			sb.append(StringUtils.trim(msg.getMessage())); 
			sb.append(LINE_SEPARATOR);
		}
		
		for(ExportMessage msg: warnings){
			sb.append("[WARN] Line [");
			sb.append(StringUtils.trim(String.valueOf(msg.getLineNum()))); 
			sb.append("] "); 
			sb.append(StringUtils.trim(msg.getColomuName())); 
			sb.append(" :"); 
			sb.append(StringUtils.trim(msg.getMessage())); 
			sb.append(LINE_SEPARATOR);
			sb.append(LINE_SEPARATOR);
		}
		return StringUtils.trim(sb.toString());
	}

	@Override
	public String getLocalizedMessage() {
		return this.getFullAndPrettyMessage();
	}

	@Override
	public String getMessage() {
		return this.getFullAndPrettyMessage();
	}

}
