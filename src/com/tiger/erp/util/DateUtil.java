/********************************************************************************
 * Create by   : 628743
 * Date        : 01-Mar-2006
 *
 * Copyright (c) 2006 by PCCW Limited
 *
 * All rights reserved. All information contained in this software is confidential and proprietary to
 * PCCW Limited. No part of this software may be reproduced or transmitted in any form or any means,
 * electronic, mechanical, photocopying, recording or otherwise stored in any retrieval system of any
 * nature without the prior written permission of PCCW Limited.
 *
 * This material is a trade secret and its confidentiality is strictly maintained. Use of any copyright
 * notice does not imply unrestricted public access to this material.
 ********************************************************************************
 */

package com.tiger.erp.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import org.apache.log4j.Logger;

import com.tiger.erp.common.SystemConstant;


public final class DateUtil {

	private static final Logger log = Logger.getLogger(DateUtil.class);
	
	public static Date addDate(Date d, int dayOffset) {
		GregorianCalendar gc = new GregorianCalendar();
		gc.setTime(d);
		gc.add(Calendar.DATE, dayOffset);
		return gc.getTime();
	}

	public static Date addDate(String strDate, String strfmt, int numDay) {
		Date bdate = null;
		try {
			SimpleDateFormat formatter = new SimpleDateFormat(strfmt);
			Date sd = formatter.parse(strDate);
			Calendar cal = Calendar.getInstance();
			cal.setTime(sd);
			cal.add(Calendar.DATE, numDay);
			bdate = cal.getTime();
		} catch (ParseException e) {
			log.error("Parse Date Execption", e);
		}
		return bdate;
	}

	public static String formatDate(Date date) {
		String tempStrDate = "";
		if (null == date) {
			return tempStrDate;
		}
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
				"dd/MM/yyyy HH:mm:ss");
		tempStrDate = simpleDateFormat.format(date);
		return tempStrDate;
	}

	public static String formatDate(Date date, String format) {
		String tempStrDate = null;
		if (null == date) {
			return tempStrDate;
		}
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
		tempStrDate = simpleDateFormat.format(date);
		return tempStrDate;
	}

	/**
	 * 
	 * Convert LDAP date format (yyyyMMdd-HHmmss) to java.util.Date
	 */
	public static Date formatLDAPDate(String stime) {
		Date currentTime = null;
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd-HHmmss");
			currentTime = sdf.parse(stime);
		} catch (Exception e) {
			log.error("Format Date Execption", e);
		}
		return currentTime;
	}

	public static int getDateDiff(Date firstDate, Date secondDate) {
		long differenceInMillis = firstDate.getTime() - secondDate.getTime();
		double differenceInDays = (differenceInMillis / 86400000.0);
		int intDay = (int) Math.round(differenceInDays);
		return intDay;
	}

	public static int getDateDiff(java.util.Calendar d1, java.util.Calendar d2) {
		if (d1.after(d2)) { // swap dates so that d1 is start and d2 is end
			java.util.Calendar swap = d1;
			d1 = d2;
			d2 = swap;
		}
		int days = d2.get(java.util.Calendar.DAY_OF_YEAR)
				- d1.get(java.util.Calendar.DAY_OF_YEAR);

		int y2 = d2.get(java.util.Calendar.YEAR);
		if (d1.get(java.util.Calendar.YEAR) != y2) {
			d1 = (java.util.Calendar) d1.clone();
			do {
				days += d1.getActualMaximum(java.util.Calendar.DAY_OF_YEAR);
				d1.add(java.util.Calendar.YEAR, 1);
			} while (d1.get(java.util.Calendar.YEAR) != y2);
		}
		return days;
	}

	/**
	 * Translates a java.util.Date to a java.sql.Date.
	 * 
	 * @param date
	 *            The java.util.Date to be translated.
	 * @return A java.sql.Date representing the given date.
	 */
	public static java.sql.Date getSQLDate(java.util.Date date) {
		if (date == null)
			return null;
		else {
			return new java.sql.Date(date.getTime());
		}
	}

	public static Date getSystemDate() {
		java.util.Date bdate;

		Calendar today = new GregorianCalendar();
		bdate = today.getTime();
		return bdate;
	}

	/**
	 * Translates a java.util.Date to a java.sql.Timestamp.
	 * 
	 * @param date
	 *            The java.util.Date to be translated.
	 * @return A java.sql.Timestamp representing the given date.
	 */
	public static java.sql.Timestamp getTimestamp(java.util.Date date) {
		if (date == null)
			return null;
		else {
			return new java.sql.Timestamp(date.getTime());
		}
	}
	
	public static Date parseDate(String strdate){
        Date bdate = null;
        try {
            if (StringUtils.isEmptyString(strdate)) {
                return bdate;
            }
            SimpleDateFormat dFormat = new SimpleDateFormat(SystemConstant.DATE_CHS);
            dFormat.setLenient(false);
            bdate = new Date(dFormat.parse(strdate).getTime());
        } catch (ParseException e) {
            log.error("Parse Date Execption", e);
        }
        return bdate;
    }

	public static Date parseDate(String strdate, String format) {
		Date bdate = null;
		try {
			if (StringUtils.isEmptyString(strdate)) {
				return bdate;
			}
			SimpleDateFormat dFormat = new SimpleDateFormat(format);
			dFormat.setLenient(false);
			bdate = new Date(dFormat.parse(strdate).getTime());
		} catch (ParseException e) {
			log.error("Parse Date Execption", e);
		}
		return bdate;
	}

	public static Date toDate(String sDD, String sMMM, String sYYYY) {
		return toDate(sDD, sMMM, sYYYY, "0000");
	}

	public static Date toDate(String sDD, String sMMM, String sYYYY,
			String sHHmm) {
		return toDate(sDD, sMMM, sYYYY, sHHmm, 0, 0);
	}

	public static Date toDate(String sDD, String sMM, String sYYYY,
			String sHHmm, int iFromTZOffset, int iToTZOffset) {
		if (StringUtils.isEmptyString(sDD) && StringUtils.isEmptyString(sMM)
				&& StringUtils.isEmptyString(sYYYY)) {
			return null;
		}

		GregorianCalendar gc = new GregorianCalendar();
		try {
			StringBuffer formatBuf = new StringBuffer();
			if (sDD != null) {
				int count = sDD.length();
				for (int i = 0; i < count; i++) {
					formatBuf.append("d");
				}
			}

			if (sMM != null) {
				int count = sMM.length();
				if (count == 0) {
				} else if (sMM.matches("[0-9]{1,2}")) {
					for (int i = 0; i < count; i++) {
						formatBuf.append("M");
					}
				} else if (sMM.matches("[0-9]{3,}")) {
					return null;
				} else if (sMM.matches("[a-zA-Z]*") && count < 3) {
					return null;
				} else if (sMM.matches("[a-zA-Z]{1,3}")) {
					formatBuf.append("MMM");
				} else if (sMM.matches("[a-zA-Z]{3,}")) {
					formatBuf.append("MMMM");
				} else {
					return null;
				}
			}

			if (sYYYY != null) {
				int count = sYYYY.length();
				for (int i = 0; i < count && i < 5; i++) {
					formatBuf.append("y");
				}
			}

			if (sHHmm != null) {
				formatBuf.append("HHmm");
			}
			gc.setTime(new SimpleDateFormat(formatBuf.toString(), Locale.US)
					.parse(sDD + sMM + sYYYY + sHHmm));

			gc.add(Calendar.HOUR, -iFromTZOffset + iToTZOffset);
			return gc.getTime();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	private DateUtil() {
	}

	public int getDateDiff(String startDate, String endDate) {
		DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT, Locale.US);
		Date sDate = new Date();
		Date eDate = new Date();

		try {
			sDate = df.parse(startDate);
			eDate = df.parse(endDate);
		} catch (Exception e) {
			e.printStackTrace();
		}
		Integer diff = new Integer(
				(int) ((eDate.getTime() - sDate.getTime()) / 86400000) + 1);

		return (diff.intValue());
	}

	
	public static String getCurrentDateStr() {
		return formatDate(getSystemDate(), "yyyy-MM-dd");
	}
   
	public static String getCurrentDateStr(String format) {
		return formatDate(getSystemDate(),format);
	}
}
