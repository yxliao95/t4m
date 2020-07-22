package com.t4m.extractor.util;

import java.text.DecimalFormat;

/**
 * Created by Yuxiang Liao on 2020-07-17 17:23.
 */
public class MathUtil {

	public static String divide(float a, float b) {
		return b == 0 ? "0" : new DecimalFormat("0.00").format(a / b);
	}

	public static String percentage(float a, float b) {
		return b == 0 ? "0" : new DecimalFormat("0.00").format((a / b) * 100);
	}
}