package com.kronos.plugin.monitor.scan.analyse.task.issue;

import com.kronos.plugin.monitor.scan.analyse.ErrorReport;

import java.util.regex.Pattern;

public class CnJavaBuilderErrorAnalyse extends JavaBuilderErrorAnalyse {

    public CnJavaBuilderErrorAnalyse(ErrorReport errorReport) {
        super(errorReport);
        issuePattern = Pattern.compile("(e:\\s*)?(.*.\\.java):(\\d+): 错误: .*");
        endPattern = Pattern.compile("\\d+ 个错误\\s*");
    }

}