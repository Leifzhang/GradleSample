package com.kronos.plugin.monitor.scan.err;

import com.kronos.plugin.monitor.repo.LogFile;
import com.kronos.plugin.monitor.repo.data.BuildErr;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;


public class BuildErrPrinter {
    LogFile file;

    public BuildErrPrinter(LogFile file) {
        this.file = file;
    }

    public void execute(BuildErr err) {
        if (err != null && err.throwable != null) {
            Writer tt = new StringWriter();
            PrintWriter printWriter = new PrintWriter(tt);
            err.throwable.printStackTrace(printWriter);
            file.append(tt.toString());
        }
    }
}
