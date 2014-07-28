package com.tiger.erp.util;

import java.util.Set;

public class ExportConfig {
	private String exportId;
	private String fileName;
	private Set<ExportItemConfig> exportItems;

	public String getExportId() {
		return this.exportId;
	}

	public void setExportId(String exportId) {
		this.exportId = exportId;
	}

	public String getFileName() {
		return this.fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public Set<ExportItemConfig> getExportItems() {
		return this.exportItems;
	}

	public void setExportItems(Set<ExportItemConfig> exportItems) {
		this.exportItems = exportItems;
	}
}
