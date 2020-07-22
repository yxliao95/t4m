package com.t4m.extractor.metric;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.expr.ConditionalExpr;
import com.github.javaparser.ast.stmt.*;
import com.t4m.extractor.entity.ClassInfo;
import com.t4m.extractor.util.MathUtil;

import java.text.DecimalFormat;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Yuxiang Liao on 2020-07-16 18:39.
 */
public class ComplexityMetric {

	/**
	 * 递归查询子节点，计算圈复杂度。
	 * if, while, for, &&, ||, cases and default of switch, catches of try
	 */
	public static int resolveComplexity(Node n, int cyclomaticComplexityCount) {
		for (Node childNode : n.getChildNodes()) {
			cyclomaticComplexityCount += resolveComplexity(childNode, 0);
			if (childNode instanceof DoStmt) {
				cyclomaticComplexityCount += 1;
			} else if (childNode instanceof WhileStmt) {
				cyclomaticComplexityCount += 1;
			} else if (childNode instanceof ForEachStmt) {
				cyclomaticComplexityCount += 1;
			} else if (childNode instanceof ForStmt) {
				cyclomaticComplexityCount += 1;
			} else if (childNode instanceof IfStmt) {
				cyclomaticComplexityCount += 1;
			} else if (childNode instanceof SwitchEntry) {
				// default case does not have label.
				cyclomaticComplexityCount += ((SwitchEntry) childNode).getLabels().isEmpty() ? 1
				                                                                             : ((SwitchEntry) childNode)
						                             .getLabels().size();
			} else if (childNode instanceof CatchClause) {
				cyclomaticComplexityCount += 1;
			} else if (childNode instanceof BinaryExpr) {
				BinaryExpr.Operator operator = ((BinaryExpr) childNode).getOperator();
				if (operator.equals(BinaryExpr.Operator.AND) || operator.equals(BinaryExpr.Operator.OR)) {
					cyclomaticComplexityCount += 1;
				}
			} else if (childNode instanceof ConditionalExpr) {
				//The ternary conditional expression: b==0?x:y
				cyclomaticComplexityCount += 1;
			}
		}
		return cyclomaticComplexityCount;
	}

	/**
	 * 以类为单位，计算cyclomatic Complexity的相关度量
	 */
	public static void calculateComplexity(ClassInfo classInfo) {
		int sum = 0;
		int max = 0;
		for (int i : classInfo.getCyclomaticComplexityList()) {
			sum += i;
			max = Math.max(i, max);
		}
		classInfo.setWeightedMethodsCount(sum);
		classInfo.setMaxCyclomaticComplexity(max);
		String avg = MathUtil.divide(max, classInfo.getCyclomaticComplexityList().size());
		classInfo.setAvgCyclomaticComplexity(avg);
	}

}