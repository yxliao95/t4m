package com.t4m.extractor;

import com.t4m.extractor.entity.ProjectInfo;
import com.t4m.extractor.scanner.*;

import java.io.File;
import java.util.List;

/**
 * Created by Yuxiang Liao on 2020-06-17 03:31.
 */
public class T4MExtractor {
	private ProjectInfo projectInfo;
	private List<File> rawJavaFileList;

	public T4MExtractor(ProjectInfo projectInfo) {
		this.projectInfo = projectInfo;
	}

	public T4MExtractor(String projectPath) {
		this.projectInfo = new ProjectInfo(projectPath);
	}

	public ProjectInfo getProjectInfo() {
		return projectInfo;
	}

	public List<File> getRawJavaFileList() {
		return rawJavaFileList;
	}

	public void scanDirectory() {
		No1_DirectoryFileScanner directoryFileScanner = new No1_DirectoryFileScanner(projectInfo);
		rawJavaFileList = directoryFileScanner.scan();
	}

	public void scanClass() {
		scanDirectory();
		No2_ClassScanner classScanner = new No2_ClassScanner(projectInfo);
		classScanner.scan(rawJavaFileList);
	}

	public void scanPackage() {
		scanClass();
		No3_PackageScanner packageScanner = new No3_PackageScanner(projectInfo);
		packageScanner.scan();
	}

	public void scanModule() {
		scanPackage();
		No4_ModuleScanner moduleScanner = new No4_ModuleScanner(projectInfo);
		moduleScanner.scan();
	}

	public void scanDependency() {
		scanModule();
		No5_DependencyScanner dependencyScanner = new No5_DependencyScanner(projectInfo);
		dependencyScanner.scan();
	}

	@Deprecated
	public void scanASTParser() {
		scanDependency();
		No6_ASTParserScanner astParserScanner = new No6_ASTParserScanner(projectInfo);
		astParserScanner.scan();
	}

	public void scanJavaParser() {
		scanDependency();
		No6_JavaParserScanner javaParserScanner = new No6_JavaParserScanner(projectInfo);
		javaParserScanner.scan();
	}

	public void scanMetricData(){
		scanJavaParser();
		No7_MetricsScanner metricsScanner = new No7_MetricsScanner(projectInfo);
		metricsScanner.scan();
	}

	public ProjectInfo extract() {
		scanMetricData();
		return projectInfo;
	}
}
