package com.technotrade.pts2.pts2testapp.helper.converter;

import java.math.BigDecimal;

public class BigDecimalConverter implements IConverter<BigDecimal> {
	@Override
	public BigDecimal convert(String value) {
		return new BigDecimal(value);
	}
}