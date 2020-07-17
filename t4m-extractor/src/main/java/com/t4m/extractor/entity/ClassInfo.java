package com.t4m.extractor.entity;

import com.t4m.extractor.metric.SLOCMetric;

import java.io.Serializable;
import java.util.*;

/**
 * Created by Yuxiang Liao at 2020-06-09 22:55.
 */
public class ClassInfo implements Serializable {

	private static final long serialVersionUID = 2417256803742933401L;

	private String shortName; // 嵌套类的类名为 A.B
	private String fullyQualifiedName; // fully-qualified class name
	private String absolutePath;

	private PackageInfo packageInfo;
	private String packageFullyQualifiedName;

	private ClassModifier classModifier; // 如果该类的源文件为package-info.java，那么可能不存在类修饰符，因为该文件可以不存在类，只包含注释。
	private ClassDeclaration classDeclaration;

	// 对于extraClass的innerClass来说，mainOuterClass与outerClass是不一致的。
	private ClassInfo mainPublicClass; //唯一的公共外部类
	private ClassInfo outerClass; //内部类的外部类

	private List<ClassInfo> nestedClassList = new ArrayList<>();
	private List<ClassInfo> extraClassList = new ArrayList<>();

	private List<ClassInfo> extendedClassList = new ArrayList<>();
	private List<ClassInfo> implementedClassList = new ArrayList<>();

	//依赖（引用的类）
	private List<ClassInfo> activeDependencyAkaFanOutList = new ArrayList<>();
	//被依赖（被其他类引用）
	private List<ClassInfo> passiveDependencyAkaFanInList = new ArrayList<>();

	// 方法列表
	private List<MethodInfo> methodInfoList = new ArrayList<>();
	// 类的class-variable，包括静态变量，不包括常量
	private List<FieldInfo> fieldInfoList = new ArrayList<>();

	private int numberOfMethods;
	private int numberOfFields;
	private int numberOfEnumConstants;
	private int numberOfAnnotationMembers;

	private List<String> unresolvedExceptionList = new ArrayList<>();

	//SLOC counts the number of lines in the source file that are not: blank or empty lines, braces, or comments.
	private Map<SLOCType, Integer> slocCounterMap = new HashMap<>();

	//Response for class
	private Map<String, Integer> rfcMethodQualifiedSignatureMap = new HashMap<>(); // 调用的其他类的方法集合
	// 所有可以对一个类的消息做出响应的方法个数: 类中的所有方法集合，包括从父类继承的方法（但不包括重写的方法，因为方法签名应该唯一）
	// 类中所有方法所调用的方法集合（所有方法，但不可重复）
	private int responseForClass;

	//圈复杂度
	private List<Integer> cyclomaticComplexityList = new ArrayList<>();
	private int maxCyclomaticComplexity;
	private float avgCyclomaticComplexity;
	private int weightedMethodsCount; // sum of all methods complexity;

	public ClassInfo(String shortName, String absolutePath) {
		this.shortName = shortName;
		this.absolutePath = absolutePath;
	}

	public ClassInfo(String innerClassShortName, ClassInfo mainPublicClass) {
		this.shortName = innerClassShortName;
		this.absolutePath = mainPublicClass.absolutePath;
		this.fullyQualifiedName = mainPublicClass.getFullyQualifiedName() + "." + innerClassShortName;
		this.packageInfo = mainPublicClass.packageInfo;
		this.packageFullyQualifiedName = mainPublicClass.packageFullyQualifiedName;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		ClassInfo classInfo = (ClassInfo) o;
		return Objects.equals(shortName, classInfo.shortName) && Objects.equals(fullyQualifiedName,
		                                                                        classInfo.fullyQualifiedName) &&
				Objects.equals(absolutePath, classInfo.absolutePath);
	}

	@Override
	public int hashCode() {
		return Objects.hash(shortName, fullyQualifiedName, absolutePath);
	}

	public String getShortName() {
		return shortName;
	}

	public void setShortName(String shortName) {
		this.shortName = shortName;
	}

	public String getFullyQualifiedName() {
		return fullyQualifiedName;
	}

	public void setFullyQualifiedName(String fullyQualifiedName) {
		this.fullyQualifiedName = fullyQualifiedName;
	}

	public PackageInfo getPackageInfo() {
		return packageInfo;
	}

	public void setPackageInfo(PackageInfo packageInfo) {
		this.packageInfo = packageInfo;
	}

	public String getAbsolutePath() {
		return absolutePath;
	}

	public void setAbsolutePath(String absolutePath) {
		this.absolutePath = absolutePath;
	}

	public String getPackageFullyQualifiedName() {
		return packageFullyQualifiedName;
	}

	public void setPackageFullyQualifiedName(String packageFullyQualifiedName) {
		this.packageFullyQualifiedName = packageFullyQualifiedName;
	}

	public ClassModifier getClassModifier() {
		return classModifier;
	}

	public void setClassModifier(ClassModifier classModifier) {
		this.classModifier = classModifier;
	}

	public ClassDeclaration getClassDeclaration() {
		return classDeclaration;
	}

	public void setClassDeclaration(ClassDeclaration classDeclaration) {
		this.classDeclaration = classDeclaration;
	}

	public ClassInfo getMainPublicClass() {
		return mainPublicClass;
	}

	public void setMainPublicClass(ClassInfo mainPublicClass) {
		this.mainPublicClass = mainPublicClass;
	}

	public ClassInfo getOuterClass() {
		return outerClass;
	}

	public void setOuterClass(ClassInfo outerClass) {
		this.outerClass = outerClass;
	}

	public List<ClassInfo> getNestedClassList() {
		return nestedClassList;
	}

	public void setNestedClassList(List<ClassInfo> nestedClassList) {
		this.nestedClassList = nestedClassList;
	}

	public List<ClassInfo> getExtraClassList() {
		return extraClassList;
	}

	public void setExtraClassList(List<ClassInfo> extraClassList) {
		this.extraClassList = extraClassList;
	}

	public List<ClassInfo> getExtendedClassList() {
		return extendedClassList;
	}

	public void setExtendedClassList(List<ClassInfo> extendedClassList) {
		this.extendedClassList = extendedClassList;
	}

	public List<ClassInfo> getImplementedClassList() {
		return implementedClassList;
	}

	public void setImplementedClassList(List<ClassInfo> implementedClassList) {
		this.implementedClassList = implementedClassList;
	}

	public List<ClassInfo> getActiveDependencyAkaFanOutList() {
		return activeDependencyAkaFanOutList;
	}

	public void setActiveDependencyAkaFanOutList(List<ClassInfo> activeDependencyAkaFanOutList) {
		this.activeDependencyAkaFanOutList = activeDependencyAkaFanOutList;
	}

	public List<ClassInfo> getPassiveDependencyAkaFanInList() {
		return passiveDependencyAkaFanInList;
	}

	public void setPassiveDependencyAkaFanInList(List<ClassInfo> passiveDependencyAkaFanInList) {
		this.passiveDependencyAkaFanInList = passiveDependencyAkaFanInList;
	}

	public List<MethodInfo> getMethodInfoList() {
		return methodInfoList;
	}

	public void setMethodInfoList(List<MethodInfo> methodInfoList) {
		this.methodInfoList = methodInfoList;
	}

	public List<FieldInfo> getFieldInfoList() {
		return fieldInfoList;
	}

	public void setFieldInfoList(List<FieldInfo> fieldInfoList) {
		this.fieldInfoList = fieldInfoList;
	}

	public int getNumberOfMethods() {
		return numberOfMethods;
	}

	public void setNumberOfMethods(int numberOfMethods) {
		this.numberOfMethods = numberOfMethods;
	}

	public int getNumberOfFields() {
		return numberOfFields;
	}

	public void setNumberOfFields(int numberOfFields) {
		this.numberOfFields = numberOfFields;
	}

	public int getNumberOfEnumConstants() {
		return numberOfEnumConstants;
	}

	public void setNumberOfEnumConstants(int numberOfEnumConstants) {
		this.numberOfEnumConstants = numberOfEnumConstants;
	}

	public int getNumberOfAnnotationMembers() {
		return numberOfAnnotationMembers;
	}

	public void setNumberOfAnnotationMembers(int numberOfAnnotationMembers) {
		this.numberOfAnnotationMembers = numberOfAnnotationMembers;
	}

	public List<String> getUnresolvedExceptionList() {
		return unresolvedExceptionList;
	}

	public void setUnresolvedExceptionList(List<String> unresolvedExceptionList) {
		this.unresolvedExceptionList = unresolvedExceptionList;
	}

	public Map<SLOCType, Integer> getSlocCounterMap() {
		if (slocCounterMap == null || slocCounterMap.isEmpty()) {
			initSlocCounterMap();
		}
		return slocCounterMap;
	}

	public void setSlocCounterMap(Map<SLOCType, Integer> slocCounterMap) {
		this.slocCounterMap = slocCounterMap;
	}

	public Map<String, Integer> getRfcMethodQualifiedSignatureMap() {
		return rfcMethodQualifiedSignatureMap;
	}

	public void setRfcMethodQualifiedSignatureMap(Map<String, Integer> rfcMethodQualifiedSignatureMap) {
		this.rfcMethodQualifiedSignatureMap = rfcMethodQualifiedSignatureMap;
	}

	public int getResponseForClass() {
		return responseForClass;
	}

	public void setResponseForClass(int responseForClass) {
		this.responseForClass = responseForClass;
	}

	public List<Integer> getCyclomaticComplexityList() {
		return cyclomaticComplexityList;
	}

	public void setCyclomaticComplexityList(List<Integer> cyclomaticComplexityList) {
		this.cyclomaticComplexityList = cyclomaticComplexityList;
	}

	public int getMaxCyclomaticComplexity() {
		return maxCyclomaticComplexity;
	}

	public void setMaxCyclomaticComplexity(int maxCyclomaticComplexity) {
		this.maxCyclomaticComplexity = maxCyclomaticComplexity;
	}

	public float getAvgCyclomaticComplexity() {
		return avgCyclomaticComplexity;
	}

	public void setAvgCyclomaticComplexity(float avgCyclomaticComplexity) {
		this.avgCyclomaticComplexity = avgCyclomaticComplexity;
	}

	public int getWeightedMethodsCount() {
		return weightedMethodsCount;
	}

	public void setWeightedMethodsCount(int weightedMethodsCount) {
		this.weightedMethodsCount = weightedMethodsCount;
	}

	/**
	 * Source File以文件为单位，包括了package, import, nested class，non public class
	 * AST以类为单位，不包括package和 import，对于注释和代码也会进行格式化
	 * （比如注释会与代码行不会混合，注释行会被单独提取成行；一个stmt成一行）
	 */
	public Map<SLOCType, Integer> initSlocCounterMap() {
		this.slocCounterMap.put(SLOCType.LOGIC_CODE_LINES_FROM_SOURCE_FILE, 0); // 不包括空白行，单独大括号和注释行
		this.slocCounterMap.put(SLOCType.COMMENT_LINES_FROM_SOURCE_FILE, 0); // 包括这样的注释和代码混合的行
		this.slocCounterMap.put(SLOCType.PHYSICAL_CODE_LINES_FROM_SOURCE_FILE, 0);  // 包括代码行、大括号，不包括单独的注释行
		this.slocCounterMap.put(SLOCType.LOGIC_CODE_LINES_FROM_AST, 0); // 不包括空白行，单独大括号和注释行
		this.slocCounterMap.put(SLOCType.COMMENT_LINES_FROM_AST, 0); // 不包括"//"注释行，只包括"/**/"的doc注释行
		this.slocCounterMap.put(SLOCType.PHYSICAL_CODE_LINES_FROM_AST, 0);  // 包括代码行、大括号，不包括单独的注释行
		return slocCounterMap;
	}

	/**
	 * 获取自身的SLOC，以数组形式返回。索引与对应的值，查看{@link SLOCMetric#sumSLOC(int[], Map)}
	 */
	public int[] getSumOfSLOC() {
		int[] slocArray = new int[6];
		Arrays.fill(slocArray, 0);
		SLOCMetric.sumSLOC(slocArray, slocCounterMap);
		return slocArray;
	}

	public enum ClassModifier {
		CLASS("class"),
		ENUM("enum"),
		ANNOTATION("annotation"),
		ABSTRACT_CLASS("abstract"),
		INTERFACE("interface");

		String str;

		ClassModifier(String str) {
			this.str = str;
		}

		@Override
		public String toString() {
			return str;
		}
	}

	public enum SLOCType {
		LOGIC_CODE_LINES_FROM_SOURCE_FILE,
		PHYSICAL_CODE_LINES_FROM_SOURCE_FILE,
		COMMENT_LINES_FROM_SOURCE_FILE,
		LOGIC_CODE_LINES_FROM_AST,
		PHYSICAL_CODE_LINES_FROM_AST,
		COMMENT_LINES_FROM_AST
	}

	public enum ClassDeclaration {
		NESTED_CLASS, // 嵌套类：包括static nested class和inner class
		EXTRA_CLASS, // 非public的外部类
		MAIN_PUBLIC_CLASS // 唯一的public外部类，与java文件名一致
	}

	@Override
	public String toString() {
		return "ClassInfo{" + "fullyQualifiedName='" + fullyQualifiedName + '\'' + '}';
	}
}
