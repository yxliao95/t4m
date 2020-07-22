package com.t4m.extractor.scanner.astparser;

import com.t4m.extractor.entity.ClassInfo;
import com.t4m.extractor.entity.ProjectInfo;
import com.t4m.extractor.util.ASTParserUtil;
import com.t4m.extractor.util.EntityUtil;
import org.eclipse.jdt.core.dom.*;

import java.util.ArrayList;
import java.util.List;

/**
 * 用于构造非公共类和嵌套类
 * Created by Yuxiang Liao on 2020-06-21 13:02.
 */
@Deprecated
public class No1_ClassInfoVisitor extends ASTVisitor {

	private ClassInfo outerClassInfo;
	private ProjectInfo projectInfo;
	private List<ClassInfo> allShownClassInfoList = new ArrayList<>();

	public No1_ClassInfoVisitor(ClassInfo outerClassInfo, ProjectInfo projectInfo) {
		this.outerClassInfo = outerClassInfo;
		this.projectInfo = projectInfo;
		allShownClassInfoList.add(outerClassInfo);
	}

	private void createClassInfo(AbstractTypeDeclaration node) {
		if (ASTParserUtil.isInnerClass(node)) {
			// 需要先确定上一层的类是哪个
			AbstractTypeDeclaration parentClassNode = ASTParserUtil.getParentAbstractTypeDeclaration(node);
			ClassInfo parentClassInfo = EntityUtil.getClassByShortName(allShownClassInfoList,
			                                                           parentClassNode.getName().getIdentifier());
			// 创建新的ClassInfo作为内部类，并与外部类关联，并添加到projectInfo中
			String innerClassName = node.getName().toString(); // InnerClass
			ClassInfo innerClassInfo = EntityUtil.safeAddEntityToList(new ClassInfo(innerClassName, parentClassInfo),
			                                                          parentClassInfo.getNestedClassList());
			innerClassInfo.setClassDeclaration(ClassInfo.ClassDeclaration.NESTED_CLASS);
			innerClassInfo.setOuterClass(parentClassInfo);
			innerClassInfo.setMainPublicClass(outerClassInfo);
			EntityUtil.safeAddEntityToList(innerClassInfo, projectInfo.getNestedClassList());
			EntityUtil.safeAddEntityToList(innerClassInfo, innerClassInfo.getPackageInfo().getNestedClassList());
			allShownClassInfoList.add(innerClassInfo);
		} else {
			//由于一个类文件可以创建多个类，因此还需要对这些其他类进行创建。
			String shortName = node.getName().getIdentifier();
			if (!shortName.equals(outerClassInfo.getShortName())) {
				ClassInfo extraClassInfo = new ClassInfo(shortName, outerClassInfo.getAbsolutePath());
				extraClassInfo.setFullyQualifiedName(outerClassInfo.getPackageFullyQualifiedName() + "." + shortName);
				extraClassInfo.setPackageInfo(outerClassInfo.getPackageInfo());
				extraClassInfo.setPackageFullyQualifiedName(outerClassInfo.getPackageFullyQualifiedName());
				extraClassInfo.setClassDeclaration(ClassInfo.ClassDeclaration.NON_PUBLIC_OUTER_CLASS);
				extraClassInfo.setMainPublicClass(outerClassInfo);
				EntityUtil.safeAddEntityToList(extraClassInfo, outerClassInfo.getExtraClassList());
				EntityUtil.safeAddEntityToList(extraClassInfo, projectInfo.getExtraClassList());
				EntityUtil.safeAddEntityToList(extraClassInfo, extraClassInfo.getPackageInfo().getExtraClassList());
				allShownClassInfoList.add(extraClassInfo);
			}
		}
	}

	/*------------------------------------------------------------------------------------------*/

	@Override
	public boolean visit(TypeDeclaration node) {
		createClassInfo(node);
		return true;
	}

	@Override
	public boolean visit(EnumDeclaration node) {
		createClassInfo(node);
		return true;
	}

	@Override
	public boolean visit(AnnotationTypeDeclaration node) {
		createClassInfo(node);
		return true;
	}
}
