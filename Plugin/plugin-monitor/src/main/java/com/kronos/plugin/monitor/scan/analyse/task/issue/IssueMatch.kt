package com.kronos.plugin.monitor.scan.analyse.task.issue

interface IssueMatch {
    fun found(txt: String): Boolean
    fun eioStart(txt: String): Boolean
}