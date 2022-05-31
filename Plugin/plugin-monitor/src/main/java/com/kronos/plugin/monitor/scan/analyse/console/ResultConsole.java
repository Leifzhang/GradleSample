package com.kronos.plugin.monitor.scan.analyse.console;


import com.kronos.plugin.monitor.scan.analyse.ErrorReport;
import com.kronos.plugin.monitor.scan.analyse.bean.EProject;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class ResultConsole {

    public List<TaskConsole> taskConsoles = new ArrayList<>();
    public List<ProjectConsole> projectConsoles = new ArrayList<>();

    public ResultConsole(ErrorReport errorReport) {

        for (int i = 0; i < errorReport.getcTasks().size(); i++) {
            taskConsoles.add(new TaskConsole(i, errorReport.getcTasks().get(i)));
        }

        List<EProject> p = errorReport.projectIssues.values().stream().filter(new Predicate<EProject>() {
            @Override
            public boolean test(EProject projectIssue) {
                return projectIssue.eIssues.size() > 0;
            }
        }).collect(Collectors.toList());
        for (int i = 0; i < p.size(); i++) {
            projectConsoles.add(new ProjectConsole(i, p.get(i)));
        }

    }

    public void print(Printer printStream) {
        printStream.println("");
        printStream.println("");
        printStream.println("---------------------------------！！编译失败报告 !!---------------------------------");
        if (taskConsoles.size() > 0) {
            printStream.println("存在 " + taskConsoles.size() + " 个任务失败");
            taskConsoles.forEach(taskConsole -> taskConsole.print(printStream));
            printStream.println("");
        }

        if (projectConsoles.size() > 0) {
            printStream.println("存在 " + projectConsoles.size() + " 个问题项目");
            projectConsoles.forEach(projectConsole -> projectConsole.print(printStream));
            printStream.println("");
        }
        printStream.println("------------------------------------------------------------------------------------");
        printStream.println("");
    }

    public interface Printer {
        public void println(String var1);

        public void println();
    }
}
