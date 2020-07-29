package com.kronos.gradle.utils

import java.io.File


object CmdUtil {

    fun execute(command: String): Boolean {

        return execute(command, null)
    }

    fun execute(command: String, dir: File?): Boolean {
        return execute(command, dir, true)
    }

    fun execute(command: String, dir: File?, interruptWhenCmdFailed: Boolean): Boolean {
        val process = Runtime.getRuntime().exec(command, null, dir)
        val result = process.waitFor()
        if (result != 0) {
            val failureMsg =
                "[CMD] - failure to execute command [${command} under ${dir}\n message: ${process.errorStream.bufferedReader()
                    .readText()}"
            if (interruptWhenCmdFailed) {
                throw  CmdExecuteException(failureMsg)

            } else {
                println(failureMsg)
            }
        } else {
            println(
                "【CMD】- execute command [${command}] success\n      - result message: ${process.inputStream.bufferedReader()
                    .readText()}"
            )
        }
        return result == 0;
    }

    fun executeForOutput(command: String): String {
        return executeForOutput(command, null, true)
    }

    fun executeForOutput(command: String, dir: File?): String {
        return executeForOutput(command, dir, true)
    }

    fun executeForOutput(command: String, dir: File?, interruptWhenCmdFailed: Boolean): String {

        val process = Runtime.getRuntime().exec(command, null, dir)
        val result = process.waitFor()
        return if (result != 0) {
            val failureMsg =
                "[CMD] - failure to execute command [${command} under ${dir}\n message: ${process.errorStream.bufferedReader()
                    .readText()}"
            if (interruptWhenCmdFailed) {
                throw  RuntimeException(failureMsg)

            } else {
                println(failureMsg)
                failureMsg
            }
        } else {
            process.inputStream.bufferedReader().readText()
        }
    }

}
