package com.kronos.plugin.monitor.scan.analyse.task.issue;

import com.kronos.plugin.monitor.scan.analyse.ErrorCodeConstant;
import com.kronos.plugin.monitor.scan.analyse.ErrorReport;
import com.kronos.plugin.monitor.scan.analyse.bean.EIssue;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class KotlinBuilderErrorAnalyse implements IssueMatch {
    private ErrorReport errorReport;

    public KotlinBuilderErrorAnalyse(ErrorReport errorReport) {
        this.errorReport = errorReport;
    }

    static Pattern issuePattern = Pattern.compile("^e: (.*): \\((\\d+), (\\d+)\\): (.*)");

    @Override
    public boolean found(String txt) {
        Matcher matcher = issuePattern.matcher(txt);
        if (matcher.find()) {
            EIssue issue = new EIssue();
            issue.setFile(matcher.group(1));
            issue.setLine(Integer.valueOf(matcher.group(2)));
            issue.setMsg(txt);
            issue.setTool("kotlin");
            issue.setErrorCode(ErrorCodeConstant.KOTLIN_ISSUE);
            errorReport.report(issue);
        }
        return false;
    }

    @Override
    public boolean eioStart(String txt) {
        return issuePattern.matcher(txt).find();
    }

}
