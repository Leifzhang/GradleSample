package com.kronos.plugin.monitor.scan.analyse;


import com.kronos.plugin.monitor.scan.analyse.bean.EProject;
import com.kronos.plugin.monitor.scan.analyse.bean.ETask;

import org.gradle.BuildAdapter;
import org.gradle.BuildResult;
import org.gradle.api.invocation.Gradle;
import org.gradle.api.tasks.TaskExecutionException;
import org.gradle.execution.MultipleBuildFailures;

import java.util.List;

public class TaskBuildErrorAnalyse {

    private ErrorReport errorReport;

    public TaskBuildErrorAnalyse(Gradle gradle, ErrorReport errorReport) {
        this.errorReport = errorReport;
        gradle.addBuildListener(new BuildAdapter() {
            @Override
            public void buildFinished(BuildResult result) {
                if (result.getFailure() != null) {
                    analyse(result.getFailure());
                }
            }
        });
    }

    private void analyse(Throwable failure) {
        if (failure instanceof MultipleBuildFailures) {
            List<? extends Throwable> flattenedFailures = ((MultipleBuildFailures) failure).getCauses();
            for (int i = 0; i < flattenedFailures.size(); i++) {
                Throwable cause = flattenedFailures.get(i);
                if (cause instanceof TaskExecutionException) {
                    analyse((TaskExecutionException) cause);
                }
            }
            return;
        }
        Throwable cause = failure.getCause();
        if (cause instanceof TaskExecutionException) {
            analyse((TaskExecutionException) cause);
        }
    }

    private void analyse(TaskExecutionException cause) {
        EProject EProject = errorReport.findProject(cause.getTask().getProject());
        ETask eTask = new ETask();
        eTask.setTask(cause.getTask().getPath());
        eTask.setErrorCode(ErrorCodeConstant.TASK_ISSUE);
        EProject.addTask(eTask);
        errorReport.reportTask(eTask);
    }
}
