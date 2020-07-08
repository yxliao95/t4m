package com.t4m.web.util;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by Yuxiang Liao on 2020-07-01 15:37.
 */
public class SLOCUtil {

	/**
	 * 构造用于dashboard-SLOC页面的表格的一行记录
	 */
	public static Map<String, Object> initSLOCRowRecordForFrontPage(String name, String type, int[] slocArray) {
		Map<String, Object> cols = new LinkedHashMap<>();
		cols.put("name", name);
		cols.put("type", type);
		cols.put("logicCodeLineFromSourceFile", slocArray[0]);
		cols.put("physicalCodeLineFromSourceFile", slocArray[1]);
		cols.put("allCommentLineFromSourceFile", slocArray[2]);
		cols.put("logicCodeLineFromAST", slocArray[3]);
		cols.put("physicalCodeLineFromAST", slocArray[4]);
		cols.put("docCommentLineFromAST", slocArray[5]);
		return cols;
	}
}