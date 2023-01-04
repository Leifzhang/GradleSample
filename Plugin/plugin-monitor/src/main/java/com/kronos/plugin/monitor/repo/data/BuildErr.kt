package com.kronos.plugin.monitor.repo.data

import org.gradle.tooling.Failure

class BuildErr(val throwable: List<Failure>)