package com.org.infy.adapter.payload;


public class UploadFileResponse {
    private String fileName;
    private String convertedOutputLocation;
    private String fileType;
    private long size;

	public UploadFileResponse(String fileName , String fileType, long size, String convertedOutputLocation) {
        this.fileName = fileName;
        this.convertedOutputLocation = convertedOutputLocation;
        this.fileType = fileType;
        this.size = size;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

	public String getConvertedOutputLocation() {
		return convertedOutputLocation;
	}

	public void setConvertedOutputLocation(String convertedOutputLocation) {
		this.convertedOutputLocation = convertedOutputLocation;
	}

	public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }
}
