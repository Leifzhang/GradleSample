package com.kronos.plugin.monitor.scan.analyse.task.issue;

import com.kronos.plugin.monitor.scan.analyse.ErrorCodeConstant;
import com.kronos.plugin.monitor.scan.analyse.ErrorReport;
import com.kronos.plugin.monitor.scan.analyse.bean.EIssue;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JavaBuilderErrorAnalyse implements IssueMatch {

    private ErrorReport errorReport;
    private EIssue issue;

    public JavaBuilderErrorAnalyse(ErrorReport errorReport) {
        this.errorReport = errorReport;
    }

    public Pattern issuePattern = Pattern.compile("(e:\\s*)?(.*.\\.java):(\\d+): error: .*");
    public Pattern endPattern = Pattern.compile("\\d+ error\\s*");

    @Override
    public boolean found(String txt) {
        Matcher matcher = issuePattern.matcher(txt);
        if (matcher.find()) {
            if (issue != null) {
                errorReport.report(issue);
                issue = null;
            }
            issue = new EIssue();
            issue.setFile(matcher.group(2));
            if (!issue.getFile().startsWith(File.separator)) {
                issue.setFile(issue.getFile().substring(issue.getFile().indexOf(File.separator)));
            }
            issue.setLine(Integer.valueOf(matcher.group(3)));
            issue.setMsg(txt);
            issue.setTool("javac");
            issue.setErrorCode(ErrorCodeConstant.JAVAC_ISSUE);
            return true;
        } else if (endPattern.matcher(txt).find()) {
            if (issue != null) {
                errorReport.report(issue);
                issue = null;
            }
            return false;
        } else if (issue != null) {
            issue.setMsg(issue.getMsg() + "\n" + txt);
            return true;
        }
        if (issue != null) {
            errorReport.report(issue);
            issue = null;
        }
        return false;
    }

    @Override
    public boolean eioStart(String txt) {
        return issuePattern.matcher(txt).find();
    }

}