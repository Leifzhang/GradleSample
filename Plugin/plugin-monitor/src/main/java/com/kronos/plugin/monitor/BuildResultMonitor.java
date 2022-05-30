package com.kronos.plugin.monitor;

import com.kronos.plugin.monitor.repo.DataRep;
import com.kronos.plugin.monitor.repo.LogFile;
import com.kronos.plugin.monitor.repo.ReportTypeFile;
import com.kronos.plugin.monitor.repo.data.BuildErr;
import com.kronos.plugin.monitor.scan.err.BuildErrPrinter;

import org.gradle.BuildAdapter;
import org.gradle.BuildResult;
import org.gradle.api.initialization.Settings;


public class BuildResultMonitor {

    public void setup(Settings target) {
        target.getGradle().addListener(new BuildAdapter() {

            @Override
            public void buildFinished(BuildResult result) {
                super.buildFinished(result);
                LogFile buildError = DataRep.Companion.getRep().getLogFile(ReportTypeFile.BUILD_ERR);
                BuildErr buildErr = new BuildErr();
                buildErr.throwable = result.getFailure();
                new BuildErrPrinter(buildError).execute(buildErr);
            }

        });

    }
}
