package com.example.demo.Dto;

public class DesignRequestDto {

	private String fileName;
	private String fileType;
	private String fileUrl;
	private String description;
	private String fileId; // ImageKit file ID for deletion

	// Constructor, getters and setters

	public DesignRequestDto(String fileName, String fileType, String fileUrl, String description, String fileId) {
		super();
		this.fileName = fileName;
		this.fileType = fileType;
		this.fileUrl = fileUrl;
		this.description = description;
		this.fileId = fileId;
	}

	public String getDescription() {
		return description;
	}

	public String getFileId() {
		return fileId;
	}

	public String getFileName() {
		return fileName;
	}

	public String getFileType() {
		return fileType;
	}

	public String getFileUrl() {
		return fileUrl;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setFileId(String fileId) {
		this.fileId = fileId;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public void setFileType(String fileType) {
		this.fileType = fileType;
	}

	public void setFileUrl(String fileUrl) {
		this.fileUrl = fileUrl;
	}

}
