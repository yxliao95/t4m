package com.t4m.web.util.dataset;

import com.t4m.extractor.entity.ModuleInfo;
import com.t4m.extractor.util.MathUtil;
import com.t4m.extractor.util.TimeUtil;
import org.springframework.ui.Model;

import java.util.Arrays;
import java.util.Date;
import java.util.Map;

/**
 * Created by Yuxiang Liao on 2020-08-09 01:18.
 */
public class SLOCDatasetUtil {
	private SLOCDatasetUtil() {
	}

	public static String[] formatRowForTableChart(Date createDate) {
		String[] tempRow = new String[9];
		tempRow[0] = TimeUtil.formatToStandardDatetime(createDate);
		Arrays.fill(tempRow, 1, 9, null);
		return tempRow;
	}

	public static String[] formatRowForTableChart(Date createDate, int[] slocArray) {
		String[] tempRow = new String[9];
		tempRow[0] = TimeUtil.formatToStandardDatetime(createDate);
		Arrays.fill(tempRow, 1, 9, null);
		insertCommonRowsForTableChart(tempRow,slocArray);
		return tempRow;
	}
	public static void insertCommonRowsForTableChart(String[] tempRow, int[] slocArray) {
		tempRow[1] = String.valueOf(slocArray[0]);
		tempRow[2] = String.valueOf(slocArray[1]);
		tempRow[3] = String.valueOf(slocArray[2]);
		tempRow[4] = MathUtil.percentage(slocArray[2], slocArray[1]);
		tempRow[5] = String.valueOf(slocArray[3]);
		tempRow[6] = String.valueOf(slocArray[4]);
		tempRow[7] = String.valueOf(slocArray[5]);
		tempRow[8] = MathUtil.percentage(slocArray[5], slocArray[4]);
	}

	public static void insertCommonRowsForTable(Map<String, Object> row, int[] slocArray) {
		row.put("logicCodeLinesSF", slocArray[0]);
		row.put("physicalCodeLinesSF", slocArray[1]);
		row.put("CommentLinesSF", slocArray[2]);
		row.put("percentageOfCommentSF", MathUtil.percentage(slocArray[2], slocArray[1] + slocArray[2]));
		row.put("logicCodeLinesJP", slocArray[3]);
		row.put("physicalCodeLinesJP", slocArray[4]);
		row.put("CommentLinesJP", slocArray[5]);
		row.put("percentageOfCommentJP", MathUtil.percentage(slocArray[5], slocArray[4] + slocArray[5]));
	}


}
