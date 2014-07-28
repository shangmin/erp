package com.tiger.erp.common;

import java.io.Serializable;

public class ExportMessage implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private int lineNum;
	
	private String colomuName;
	
	private String message;
	
	public ExportMessage() {
		super();
	}

	public ExportMessage(int lineNum, String colomuName) {
		super();
		this.lineNum = lineNum;
		this.colomuName = colomuName;
	}

	public ExportMessage(int lineNum, String colomuName, String message) {
		super();
		this.lineNum = lineNum;
		this.colomuName = colomuName;
		this.message = message;
	}

	public int getLineNum() {
		return lineNum;
	}

	public void setLineNum(int lineNum) {
		this.lineNum = lineNum;
	}

	public String getColomuName() {
		return colomuName;
	}

	public void setColomuName(String colomuName) {
		this.colomuName = colomuName;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	

}
