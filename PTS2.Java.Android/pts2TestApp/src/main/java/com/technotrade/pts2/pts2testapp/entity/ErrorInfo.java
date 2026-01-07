package com.technotrade.pts2.pts2testapp.entity;

import java.util.List;

public class ErrorInfo {
    private String mRequestName;
    private int mErrorCode;
    private String mErrorMessage;    
	private List<ErrorInfo> mInnerErrorsInfo;

    public ErrorInfo(String requestName, int errorCode, String errorMessage) {
        mRequestName = requestName;
        mErrorCode = errorCode;
        mErrorMessage = errorMessage;
        mInnerErrorsInfo = null;
    }
	
	public ErrorInfo(String requestName, int errorCode, String errorMessage, List<ErrorInfo> innerErrorsInfo) {
        mRequestName = requestName;
        mErrorCode = errorCode;
        mErrorMessage = errorMessage;
        mInnerErrorsInfo = innerErrorsInfo;
    }

    public String getRequestName() {
        return mRequestName;
    }

    public void setRequestName(String requestName) {
        mRequestName = requestName;
    }
	
    public int getErrorCode() {
        return mErrorCode;
    }

    public void setData(int errorCode) {
        mErrorCode = errorCode;
    }

    public String getErrorMessage() {
        return mErrorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        mErrorMessage = errorMessage;
    }
    public List<ErrorInfo> getInnerErrorsInfo() {
        return mInnerErrorsInfo;
    }

    public void setInnerErrorsInfo(List<ErrorInfo> innerErrorsInfo) {
        mInnerErrorsInfo = innerErrorsInfo;
    }
}
