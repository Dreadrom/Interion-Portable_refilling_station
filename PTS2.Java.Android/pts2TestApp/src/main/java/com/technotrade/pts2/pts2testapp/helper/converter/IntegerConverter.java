package com.technotrade.pts2.pts2testapp.helper.converter;

public class IntegerConverter implements IConverter<Integer> {
	@Override
	public Integer convert(String value) {
		return Integer.valueOf(value);
	}
}