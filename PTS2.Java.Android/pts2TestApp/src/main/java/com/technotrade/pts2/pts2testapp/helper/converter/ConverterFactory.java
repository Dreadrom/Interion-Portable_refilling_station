package com.technotrade.pts2.pts2testapp.helper.converter;

import java.math.BigDecimal;

public class ConverterFactory {
	@SuppressWarnings("unchecked")
	public static <T> IConverter<T> getConverter(Class<T> type) {
		if (type.equals(Integer.class)) {
			return (IConverter<T>) new IntegerConverter();
		} else if (type.equals(BigDecimal.class)) {
			return (IConverter<T>) new BigDecimalConverter();
		}
		
		throw new IllegalArgumentException("No converter found for type: " + type);
	}
}