package com.technotrade.pts2.pts2testapp.helper.converter;

public class MonitorInputRepresentation<T> {
	private String mInput;
	private T mRepresentedInput;
	private Class<T> mType;
	private String mErrorMessage;

	public MonitorInputRepresentation(String input, Class<T> type) {
		mInput = input;
		mType = type;
	}

	public T getRepresentedInput() {
		return mRepresentedInput;
	}

	public String getErrorMessage() {
		return mErrorMessage;
	}

	public void initializeRepresentedInputFromString(String value) {
		try {
			IConverter<T> converter = ConverterFactory.getConverter(mType);
			this.mRepresentedInput = converter.convert(value);
		} catch (Exception e) {
			mErrorMessage = "wrong input";
		}
	}

	public boolean representValue() {
		if (mInput == null) {
			mErrorMessage = "input is empty";
			return false;
		}

		try {
			initializeRepresentedInputFromString(mInput);
		} catch (Exception e) {
			mErrorMessage = "wrong input";
			return false;
		}

		return true;
	}
}
