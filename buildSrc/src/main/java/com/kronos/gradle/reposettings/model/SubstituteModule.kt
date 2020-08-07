package com.kronos.gradle.reposettings.model

import org.gradle.api.InvalidUserDataException

class SubstituteModule(targetModule: String, project: String) {
    //也可以是一个本地模块 比如 (:app)
    val targetModule: String = targetModule

    val project: String = project

    var moduleGroup: String = ""

    var moduleName: String = ""

    init {
        validModule(targetModule)
        validProject(project)
    }

    /**
     * notation must be in the format "{group}:{module}:{version}". or "{group}:{module}"
     *
     * @return
     */
    private fun validModule(notation: String) {
        val gav = notation.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        if (gav.size > 3 || gav.size < 2) {
            throw InvalidUserDataException(
                "Invalid module component notation: " + notation + " : must be a valid 3 part identifier, eg.: org.gradle:gradle:1.0" + "or" +
                        " valid 2 part identifier , eg.: org.gradle:" +
                        "gradle"
            )
        }
        moduleGroup = gav[0]

        moduleName = gav[1]


    }

    private fun validProject(notation: String) {
        if (!notation.startsWith(":")) {
            throw InvalidUserDataException("Invalid project notation: $notation: must be start with \":\" character")
        }
    }


}
