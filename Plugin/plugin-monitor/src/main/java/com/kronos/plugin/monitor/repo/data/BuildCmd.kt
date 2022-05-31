package com.kronos.plugin.monitor.repo.data

import java.util.ArrayList
import java.util.HashMap

class BuildCmd {
    var initScripts: ArrayList<String> = ArrayList()
    var taskNames: ArrayList<String> = ArrayList()
    var projectProperties: HashMap<String, String> = HashMap()
    var systemPropertiesArgs: HashMap<String, String> = HashMap()
}