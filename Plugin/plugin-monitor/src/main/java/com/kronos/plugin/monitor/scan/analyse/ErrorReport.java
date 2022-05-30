package com.kronos.plugin.monitor.scan.analyse;

import com.kronos.plugin.monitor.repo.LogFile;
import com.kronos.plugin.monitor.scan.analyse.bean.EIssue;
import com.kronos.plugin.monitor.scan.analyse.bean.EProject;
import com.kronos.plugin.monitor.scan.analyse.bean.ETask;
import com.kronos.plugin.monitor.scan.analyse.console.ResultConsole;
import com.kronos.plugin.monitor.utils.FileUtils;
import com.kronos.plugin.monitor.utils.Logger;

import org.gradle.api.Project;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ErrorReport {
    public List<EIssue> ciIsues = new ArrayList<>();
    public List<ETask> cTasks = new ArrayList<>();

    public Map<String, EProject> projectIssues = new HashMap<>();
    private LogFile log;

    public ErrorReport(LogFile log) {
        this.log = log;
        EProject EProject = new EProject(FileUtils.rootFile);
        EProject.setName("root");
        EProject.setDir(FileUtils.rootFile);
        projectIssues.put("root", EProject);
    }

    public void report(EIssue issue) {
        ciIsues.add(issue);
    }

    public boolean isIll() {
        return !ciIsues.isEmpty() || !cTasks.isEmpty();
    }

    public void reportTask(ETask task) {
        cTasks.add(task);
    }

    public List<ETask> getcTasks() {
        return cTasks;
    }


    public void reportAll(boolean isPrint) {
        new ResultConsole(this).print(new ResultConsole.Printer() {

            @Override
            public void println(String var1) {
                log.append(var1);
                log.append("\n");
            }

            @Override
            public void println() {
                log.append("\n");
            }
        });
        if (isPrint) {
            new ResultConsole(this).print(new ResultConsole.Printer() {
                @Override
                public void println(String var1) {
                    Logger.msg(var1);
                }

                @Override
                public void println() {
                    Logger.msg("");
                }
            });
        }
    }

    public EProject findProject(Project project) {
        String path = project.getPath();
        EProject EProject = projectIssues.get(path);
        if (EProject == null) {
            EProject = new EProject(project.getProjectDir());
            EProject.setName(path);
            projectIssues.put(path, EProject);
        }
        return EProject;
    }

    public EProject findProject(String path) {
        int i = -1;
        EProject select = null;
        for (EProject item : projectIssues.values()) {
            int relact = relativePath(item.getDir().getAbsolutePath(), path);
            if (relact != -1) {
                if (i == -1 || relact > i) {
                    i = relact;
                    select = item;
                }
            }
        }
        if (select == null) {
            return projectIssues.get("root");
        }
        return select;
    }

    static int relativePath(String parent, String child) {
        String[] basePaths = new File(parent).getPath().split(File.separator);
        String[] dPaths = new File(child).getPath().split(File.separator);
        if (basePaths.length > dPaths.length) {
            return -1;
        }
        for (int i = 0; i < basePaths.length; i++) {
            if (!basePaths[i].equals(dPaths[i])) {
                return -1;
            }
        }
        return basePaths.length;
    }

}
