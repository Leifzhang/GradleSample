package com.kronos.plugin.dep.reposettings.model

import java.io.File

class RepoInfo(val repoManageProjectDir:File) {

    var moduleInfoMap: MutableMap<String, ModuleInfo> = mutableMapOf()

    var substituteModules: List<SubstituteModule> = mutableListOf()

    var defaultBranch: String? = null;

    var srcBuild: Boolean = false;


}