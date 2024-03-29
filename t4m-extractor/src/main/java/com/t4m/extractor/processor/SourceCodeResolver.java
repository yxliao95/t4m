package com.t4m.extractor.processor;

import com.github.javaparser.ParseProblemException;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.visitor.VoidVisitor;
import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import com.github.javaparser.symbolsolver.model.resolution.TypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JarTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JavaParserTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;
import com.t4m.extractor.ProcessChain;
import com.t4m.extractor.ProcessNode;
import com.t4m.extractor.entity.ClassInfo;
import com.t4m.extractor.entity.ModuleInfo;
import com.t4m.extractor.entity.ProjectInfo;
import com.t4m.extractor.processor.javaparser.ClassInfoVisitor;
import com.t4m.extractor.processor.javaparser.DeclarationVisitor;
import com.t4m.extractor.processor.javaparser.InMethodDependencyVisitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Yuxiang Liao on 2020-07-11 11:00.
 */
public class SourceCodeResolver implements ProcessNode {
	public static final Logger LOGGER = LoggerFactory.getLogger(SourceCodeResolver.class);

	private ProjectInfo projectInfo;

	@Override
	public void scan(ProjectInfo projectInfo, ProcessChain processChain) {
		this.projectInfo = projectInfo;
		LOGGER.info("Using JavaParser to resolve the static code.");
		initParser();
		LOGGER.info("Creating entities for the missing package-private outer classes and nested classes.");
		scanVisitor(ClassInfoVisitor.class);
		LOGGER.info("Resolving the extra information of classes.");
		LOGGER.info("Constructing entities for methods and fields.");
		scanVisitor(DeclarationVisitor.class);
		LOGGER.info("Resolving dependencies for all entities.");
		scanVisitor(InMethodDependencyVisitor.class);
		processChain.scan(projectInfo);
	}

	private void scanVisitor(Class<? extends VoidVisitor> visitorClass) {
		for (ClassInfo classInfo : projectInfo.getClassList()) {
			try {
				CompilationUnit cu = StaticJavaParser.parse(new File(classInfo.getAbsolutePath()));
				VoidVisitor<?> methodNameVisitor = visitorClass.getConstructor(ClassInfo.class, ProjectInfo.class)
				                                               .newInstance(classInfo, projectInfo);
				methodNameVisitor.visit(cu, null);
			} catch (FileNotFoundException e) {
				LOGGER.error("Cannot find {}. Stop the scanning now. {}", classInfo.getAbsolutePath(), e.toString());
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				LOGGER.error("Cannot initial Visitor [{}]. Stop the scanning now. {}", visitorClass, e.toString());
				e.printStackTrace();
			} catch (ParseProblemException e) {
				LOGGER.error("{} of file [{}]", e, classInfo.getAbsolutePath(), e);
				if (!"module-info".equals(classInfo.getShortName())) {
					LOGGER.error("Unexpected file. Stop the scanning now. Please check the code.");
					e.printStackTrace();
				}
			} catch (Exception e) {
				LOGGER.error("Unexpected error happen for file [{}]. Stop the scanning now. Please check the code.",
				             classInfo.getAbsolutePath(), e);
				e.printStackTrace();
			}
		}
	}

	public void initParser() {
		List<TypeSolver> typeSolverList = new ArrayList<>();
		// ReflectionTypeSolver is used to resolve java core like rt.jar
		typeSolverList.add(new ReflectionTypeSolver());
		for (ModuleInfo moduleInfo : projectInfo.getModuleList()) {
			//JavaParserTypeSolver requires the path of the directory that the root package is.
			if (moduleInfo.getMainScopePath() != null) {
				typeSolverList.add(new JavaParserTypeSolver(new File(moduleInfo.getMainScopePath())));
			}
			if (moduleInfo.getTestScopePath() != null) {
				typeSolverList.add(new JavaParserTypeSolver(new File(moduleInfo.getTestScopePath())));
			}
			if (moduleInfo.getOtherScopePath() != null) {
				typeSolverList.add(new JavaParserTypeSolver(new File(moduleInfo.getOtherScopePath())));
			}
		}
		String dependencyPath = projectInfo.getDependencyPath();
		if (!"".equals(dependencyPath)) {
			String[] jars = dependencyPath.split(File.pathSeparator);
			for (String jarPath : jars) {
				try {
					typeSolverList.add(new JarTypeSolver(jarPath));
				} catch (IOException e) {
					LOGGER.error("Error when adding jar path to initial JavaParser. [{}]", jarPath, e);
				}
			}
		}
		TypeSolver typeSolver = new CombinedTypeSolver(typeSolverList.toArray(new TypeSolver[0]));
		JavaSymbolSolver symbolSolver = new JavaSymbolSolver(typeSolver);
		StaticJavaParser.getConfiguration().setSymbolResolver(symbolSolver);
	}
}
