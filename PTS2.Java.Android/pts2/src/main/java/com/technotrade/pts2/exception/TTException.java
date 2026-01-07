package com.technotrade.pts2.exception;

import com.technotrade.pts2.enumeration.Result;

/// <summary>
/// TTException inherited from Exception.
/// The class is the base class for internal exception types
/// </summary>
public class TTException extends Exception {
	private String mMessage;
	private Result mInnerResult = Result.UNKNOWN_ERROR;

	/// <summary>
	/// TTException constructor
	/// </summary>
	/// <param name="message">Message</param>
	public TTException(String message) {
		super(message);

		mMessage = message;
	}
	/// <summary>
	/// TTException constructor
	/// </summary>
	/// <param name="innerResult">Inner Result value</param>
	public TTException(Result innerResult) {
		super(innerResult.getDescription());

		mInnerResult = innerResult;
		mMessage = innerResult.getDescription();
	}
	/// <summary>
	/// TTException constructor
	/// </summary>
	/// <param name="message">Message</param>
	/// <param name="innerResult">Inner Result value</param>
	public TTException(String message, Result innerResult)
	{
		super(message);

		mInnerResult = innerResult;
		mMessage = message;
	}
	/// <summary>
	/// Message getter
	/// </summary>
	@Override
	public String getMessage() {
		return mMessage;
	}
	public String what() {
		return mMessage;
	}
	/// <summary>
	/// Inner result getter
	/// </summary>
	public Result getInnerResult() {
		return mInnerResult;
	}
};