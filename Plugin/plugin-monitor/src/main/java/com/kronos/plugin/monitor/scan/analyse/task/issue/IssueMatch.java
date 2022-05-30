package com.kronos.plugin.monitor.scan.analyse.task.issue;

public interface IssueMatch {
    boolean found(String txt);

    boolean eioStart(String txt);
}
