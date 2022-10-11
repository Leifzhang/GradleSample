package com.kronos.plugin

import com.android.build.api.variant.BuiltArtifactsLoader
import com.android.tools.r8.A8
import com.android.tools.r8.A8Command
import com.android.tools.r8.Diagnostic
import com.android.tools.r8.DiagnosticsHandler
import com.android.tools.r8.utils.DexResourceProvider
import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction
import java.io.File
import java.nio.file.Path
import java.util.regex.Pattern

/**
 * @Author LiABao
 * @Since 2022/10/11
 */
abstract class A8Task : DefaultTask() {

    @get:Internal
    abstract val classPath: ListProperty<Path>

    @get:InputFiles
    abstract val apkFolder: DirectoryProperty

    @get:Internal
    abstract val builtArtifactsLoader: Property<BuiltArtifactsLoader>


    @TaskAction
    fun taskAction() {
        val builtArtifacts = builtArtifactsLoader.get().load(apkFolder.get())
            ?: throw RuntimeException("Cannot load APKs")
        builtArtifacts.elements.forEach {
            val apk = File(it.outputFile)
            a8Check(apk)
        }
    }


    private fun a8Check(apk: File) {
        logger.lifecycle("开始 Dex Api 检查 " + time())
        val a8ErrorPattern = "(?:.*from \\\\S* ([^( ]*)\\\\.)|(?:.*from ([^( ]*)\\\\Z)"
        val pattern = Pattern.compile(a8ErrorPattern)
        val set = HashSet<String>()
        val fileNames = HashSet<String>()
        val a8Ignore = System.getenv().containsKey("A8_ERROR_IGNORE")
        var errorCount = 0
        val error_msg = StringBuilder()
        try {
            error_msg.append("快编二进制检查错误信息如下：\n")
            val builder = A8Command.builder(object : DiagnosticsHandler {
                override fun error(error: Diagnostic?) {
                    super.error(error)
                    logger.error("Error : " + error?.diagnosticMessage)
                    val matcher = pattern.matcher(error?.diagnosticMessage?.trim())
                    if (matcher.find()) {
                        val groupOne = matcher.group(1)
                        val fullQualifiedName = groupOne ?: matcher.group(2)
                        set.add(fullQualifiedName)
                        fileNames.add(
                            fullQualifiedName.substring(
                                fullQualifiedName.lastIndexOf(
                                    '.'
                                ) + 1
                            ).trim()
                        )
                    }
                    error_msg.append("Error : " + error?.diagnosticMessage + "\n")
                    errorCount++
                }
            })
            builder.addLibraryFiles(classPath.get())
            builder.addA8Rule(File("./.buildscripts/a8_ruls.txt").toPath())
            builder.addProgramResourceProvider(
                DexResourceProvider.fromArchive(
                    apk.toPath()
                )
            )
            val build = builder.build()
            A8.run(build, true)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        if (errorCount > 0) {
            logger.error("存在 $errorCount 个异常。请查看上面信息。")
            logger.error("结束 Dex Api 检查 " + time())
            if (a8Ignore) {
                logger.error("生成的APK存在问题")
            } else {
                val error_file = File("build/a8_error_msg.txt")
                if (error_file.exists()) {
                    error_file.delete()
                }
                val moduleInfo = backtrackModule(set, fileNames, error_msg, project) ?: return
                error_file.appendText(error_msg.toString())
                if (error_file.exists()) {
                    println("a8_error_msg.txt生成成功")
                }
                throw  RuntimeException(
                    "API 检查失败。 存在 $errorCount 个异常。检查代码问题。\n" + moduleInfo + "忽略规则定义在 a8 配置文件：./.buildscripts/a8_ruls.txt"
                )
            }
        } else {
            logger.error("结束 Dex Api 检查 " + time())
        }
    }


    private fun backtrackModule(
        path: Set<String>,
        fileNames: Set<String>,
        errorMsg: StringBuilder,
        project: Project
    ): String? {
        val moduleNames = HashMap<String, String>()
        project.rootDir.walkTopDown().forEach {
            if (it.absolutePath != it.canonicalPath) return null
            if (!it.name.contains(".") || !fileNames.contains(it.name.split("\\.")[0])) return null
            var canonicalPath = it.canonicalPath
            val srcIndex = canonicalPath.indexOf("src")
            if (srcIndex < 0) return null
            val firstSlashIndexAfterSrcIndex = canonicalPath.indexOf("/", srcIndex + 4) + 1
            val packagePath = canonicalPath.substring(
                canonicalPath.indexOf(
                    "/",
                    firstSlashIndexAfterSrcIndex
                ) + 1
            )
            canonicalPath = packagePath.split("\\.")[0].replace('/', '.')
            if (!path.contains(canonicalPath)) return null
            val components = it.canonicalPath.split('/')
            var srcComponentIndex = -1
            components.forEach {
                if ("src" == it) {
                    srcComponentIndex = components.indexOf(it)
                }
            }
            if (srcComponentIndex > 0) {
                val moduleName = components[srcComponentIndex - 1]
                val suffixPart = it.canonicalPath.substring(srcIndex)
                val prefixPart = project.rootDir.canonicalPath + "/"
                //moduleNames[moduleName] = (it.canonicalPath - suffixPart) - prefixPart
            }
        }
        if (moduleNames.isNotEmpty()) {
            val sb =
                StringBuffer("\n\n--------------------------------------------------------------------------------\n")
            val head = "A8 报错涉及下述模块：\n"
            sb.append(head)
            moduleNames.forEach {
                sb.append("\t模块名：${it.key}, 相对路径: ${it.value}\n")
            }
            sb.append("\n\n")
            sb.append("-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------\n\n")
            val s = sb.toString()
            errorMsg.append(s)
            return s
        }
        return null
    }
}
