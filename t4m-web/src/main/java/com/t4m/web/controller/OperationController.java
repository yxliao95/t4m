package com.t4m.web.controller;

import com.t4m.conf.GlobalProperties;
import com.t4m.extractor.T4MExtractor;
import com.t4m.extractor.entity.ProjectInfo;
import com.t4m.extractor.util.TimeUtil;
import com.t4m.serializer.T4MProjectInfoSerializer;
import com.t4m.serializer.T4MSerializer;
import com.t4m.web.dao.ProjectRecordDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Yuxiang Liao on 2020-07-03 02:19.
 */
@RestController
@RequestMapping("/operation")
public class OperationController {

	private static final Logger LOGGER = LoggerFactory.getLogger(OperationController.class);

	@GetMapping("/new")
	public String createNewProject(
			@RequestParam(name = "projectPath") String projectPath,
			@RequestParam(name = "excludedPath") String excludedPath,
			@RequestParam(name = "dependencyPath", defaultValue = "") String dependencyPath) {
		// 更新t4m.properties中的当前项目名称
		String[] paths = projectPath.split(File.separator);
		String projectIdentifier = paths[paths.length - 1] + "#" + System.currentTimeMillis();
		// 更新全局项目指针
		GlobalProperties.updateCurrentProjectPointer(projectIdentifier);
		// 创建一条新的项目纪录
		ProjectInfo projectInfo = new ProjectInfo(new File(projectPath).getAbsolutePath(), excludedPath,
		                                          dependencyPath);
		// 扫描项目
		T4MExtractor t4MExtractor = new T4MExtractor(projectInfo);
		t4MExtractor.extract();
		// 持久化该记录
		T4MSerializer serializer = new T4MProjectInfoSerializer();
		String recordFileName = TimeUtil.formatToLogFileName(projectInfo.getCreateDate());
		serializer.serializeTo(projectInfo, recordFileName);
		// 更新ProjectRecord
		ProjectRecordDao.updateProjectInfoRecord();
		return "success";
	}

	@GetMapping("/scan")
	public String scanProject(
			@RequestParam(name = "excludedPath") String excludedPath,
			@RequestParam(name = "dependencyPath", defaultValue = "") String dependencyPath) {
		List<ProjectInfo> projectInfoList = ProjectRecordDao.getProjectInfoList();

		ProjectInfo oldProjectInfo = projectInfoList.get(ProjectRecordDao.getProjectInfoList().size() - 1);
		// 创建一条新的项目纪录
		ProjectInfo projectInfo = new ProjectInfo(oldProjectInfo.getAbsolutePath(), excludedPath, dependencyPath);
		// 扫描项目
		T4MExtractor t4MExtractor = new T4MExtractor(projectInfo);
		t4MExtractor.extract();
		// 持久化该记录
		T4MSerializer serializer = new T4MProjectInfoSerializer();
		String recordFileName = TimeUtil.formatToLogFileName(projectInfo.getCreateDate());
		serializer.serializeTo(projectInfo, recordFileName);
		// 更新ProjectRecord
		ProjectRecordDao.updateProjectInfoRecord();
		return "success";
	}

	@GetMapping("search")
	public Map<String, Object> searchAllProject() {
		Map<String, Object> data = new HashMap<>();
		data.put("projectList", ProjectRecordDao.getAllProjectRecordsDirName());
		data.put("currentProject", GlobalProperties.CURRENT_PROJECT_IDENTIFIER);
		return data;
	}

	@GetMapping("/switch")
	public String switchProject(
			@RequestParam(name = "projectName") String projectName,
			@RequestParam(name = "projectId") String projectId) {
		// 更新全局项目指针和项目记录
		GlobalProperties.updateCurrentProjectPointer(projectName+"#"+projectId);
		ProjectRecordDao.updateProjectInfoRecord();
		return "success";
	}

}
