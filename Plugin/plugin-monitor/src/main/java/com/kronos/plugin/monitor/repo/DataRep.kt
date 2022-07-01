package com.kronos.plugin.monitor.repo

import com.kronos.plugin.monitor.utils.Logger
import org.gradle.api.invocation.Gradle
import java.io.File
import java.lang.ref.WeakReference

/**
 *
 *  @Author LiABao
 *  @Since 2022/5/30
 *
 */
class DataRep {

    companion object {
        private val sLogRep = DataRep()

        fun getRep(): DataRep {
            return sLogRep
        }

        @JvmStatic
        fun getLogFieFrom(
            gradle: Gradle, owner: String,
            date: String, time: String, uuid: String
        ): File {
            val rootLog = getRootLogDir(gradle);
            //   val paths = [owner, date, time + "_" + uuid].join(File.separator)
            val paths = "${owner}/${date}/${time}_${uuid}"
            val log = File(rootLog, paths);
            return log
        }

        fun getRootLogDir(gradle: Gradle): File {
            return File(fawkesBuildData(gradle))
        }


        private fun fawkesBuildData(root: Gradle): String {
            return root.gradleUserHomeDir.canonicalPath + File.separator + "local_build_scan" + File.separator + "v1"
        }


    }

    private lateinit var gradle: WeakReference<Gradle>
    private lateinit var owner: String
    private lateinit var date: String
    private lateinit var time: String
    private lateinit var uuid: String
    private var initCount: Int = 0
    private val logFileMap = HashMap<ReportTypeFile, LogFile>()


    fun setup(
        gradle: Gradle, owner: String, date: String,
        time: String,
        uuid: String
    ) {
        this.gradle = WeakReference(gradle)
        this.owner = owner
        this.date = date
        this.time = time
        this.uuid = uuid
        logFileMap.clear();
        Logger.log("DataRep init: $initCount")
        initCount++
    }


    fun getFile(reportTypeFile: ReportTypeFile): File {
        return File(getDir(), reportTypeFile.realFileName())
    }

    fun getLogFile(reportTypeFile: ReportTypeFile): LogFile {
        var logF = logFileMap[reportTypeFile]
        if (logF == null) {
            logF = LogFile(getFile(reportTypeFile), reportTypeFile);
            logFileMap[reportTypeFile] = logF
        }
        return logF
    }


    fun getDir(): File {
        return getLogFieFrom(requireNotNull(gradle.get()), owner, date, time, uuid)
    }

}


enum class ReportTypeFile(val fileName: String, val title: String, val type: Type) {
    BUILD_LOG("build", "编译详情", Html()),
    CONSOLE_LOG("console", "编译日志信息", Html()),
    PROCESS_LOG("process", "编译进程信息", Html()),
    VCS_STATUS("vcs_status", "代码仓库信息", Html()),
    INFRASTRUCTURE("infrastructure", "编译环境信息", Html()),
    BUILD_ERR("build_err", "编译错误堆栈", Html()),
    BUILD_REPORT("error_log", "编译错误分析", Html()),
    BUILD_TASK_COSTA("build_task", "任务耗时", Html())
}

fun ReportTypeFile.realFileName(): String {
    return fileName + "." + type.type()
}

class Html : Type {

    override fun type(): String {
        return "html"
    }

    override fun start(title: String?): String {
        return "<html><head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" /><title>${title}</title></head><body><pre style=\"word-wrap: break-word;margin: 20px; white-space: pre-wrap;\">"

    }

    override fun end(): String {
        return "</pre></body></html>"
    }

}

interface Type {
    fun type(): String
    fun start(title: String?): String
    fun end(): String
}