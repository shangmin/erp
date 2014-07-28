package com.tiger.erp.common;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Retention(RetentionPolicy.RUNTIME)
@Target( {FIELD,METHOD})
public @interface Excel {
	
	public String columnName();
	
	public boolean isConvert() default false;
	
	public boolean isImport() default true;
	
	public boolean isExport() default true;
}
