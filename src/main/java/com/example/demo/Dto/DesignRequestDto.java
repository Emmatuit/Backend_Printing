package com.example.demo.Dto;


public class DesignRequestDto {

    private String fileName;
    private String fileType;
    private String fileUrl;
    private String description;
    private String fileId;  // ImageKit file ID for deletion

    // Constructor, getters and setters


    public DesignRequestDto(String fileName, String fileType, String fileUrl, String description, String fileId) {
		super();
		this.fileName = fileName;
		this.fileType = fileType;
		this.fileUrl = fileUrl;
		this.description = description;
		this.fileId = fileId;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getFileType() {
		return fileType;
	}

	public void setFileType(String fileType) {
		this.fileType = fileType;
	}

	public String getFileUrl() {
		return fileUrl;
	}

	public void setFileUrl(String fileUrl) {
		this.fileUrl = fileUrl;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getFileId() {
		return fileId;
	}

	public void setFileId(String fileId) {
		this.fileId = fileId;
	}


}

