package com.t4m.extractor.scanner;

import com.t4m.extractor.entity.ClassInfo;
import com.t4m.extractor.entity.PackageInfo;
import com.t4m.extractor.entity.ProjectInfo;
import com.t4m.extractor.metric.SLOCMetric;
import com.t4m.extractor.util.EntityUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 * Created by Yuxiang Liao on 2020-06-16 13:42.
 */
public class ClassScanner implements T4MScanner {

	public static final Logger LOGGER = LoggerFactory.getLogger(ClassScanner.class);


	@Override
	public void scan(ProjectInfo projectInfo, ScannerChain scannerChain) {
		LOGGER.info(
				"Extracting the basic information from .java files. Extracting the basic information of class level.");
		scannerChain.getRawJavaFileList().forEach(javaFile -> {
			try {
				String line;
				String pkgFullyQualifiedName = PackageInfo.EMPTY_IDENTIFIER;
				BufferedReader reader = new BufferedReader(new FileReader(javaFile));
				String classShortName = javaFile.getName().split("\\.")[0];
				ClassInfo classInfo = EntityUtil.safeAddEntityToList(
						new ClassInfo(classShortName, javaFile.getAbsolutePath().strip()), projectInfo.getClassList());
				// SLOC from source file
				SLOCMetric.SLOCCounter slocCounter = new SLOCMetric.SLOCCounter();
				while ((line = reader.readLine()) != null) {
					String currentLine = line.strip();
					if (currentLine.startsWith("package ")) {
						pkgFullyQualifiedName = line.replaceFirst("package", "").replace(";", "").strip();
					}
					slocCounter.countSLOCByLine(currentLine);
				}
				slocCounter.setSourceFileSLOCToCounterMap(classInfo.getSlocCounterMap());
				classInfo.setMainPublicClass(classInfo);
				classInfo.setClassDeclaration(ClassInfo.ClassDeclaration.PUBLIC_OUTER_CLASS);
				classInfo.setFullyQualifiedName(pkgFullyQualifiedName + "." + classShortName);
				classInfo.setPackageFullyQualifiedName(pkgFullyQualifiedName);
				if ("package-info".equals(classShortName)) {
					classInfo.setClassModifier(ClassInfo.ClassModifier.NONE);
				}
			} catch (FileNotFoundException e) {
				LOGGER.debug("No such file to be converted to ClassInfo object.%n[{}]", e.toString(), e);
			} catch (IOException e) {
				LOGGER.debug("Error happened when finding package path. [{}]", e.toString(), e);
			}
		});
		scannerChain.scan(projectInfo);
	}

}