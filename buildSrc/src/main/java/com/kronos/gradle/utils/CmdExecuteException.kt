package com.kronos.gradle.utils


class CmdExecuteException(var command: String, var failureMsg: String) :
    IllegalStateException(failureMsg)
