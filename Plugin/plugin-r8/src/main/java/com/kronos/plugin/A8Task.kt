package com.kronos.plugin

import com.android.build.api.variant.BuiltArtifactsLoader
import com.android.tools.r8.A8
import com.android.tools.r8.Diagnostic
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
        val A8_ERROR_PATTERN = "(?:.*from \\\\S* ([^( ]*)\\\\.)|(?:.*from ([^( ]*)\\\\Z)"
        val pattern = Pattern.compile(A8_ERROR_PATTERN)
        val set = HashSet<String>()
        val fileNames = HashSet<String>()
        val a8Ignore = System.getenv().containsKey("A8_ERROR_IGNORE")
        var errorCount = 0
        val error_msg = StringBuilder()
        try {
            error_msg.append("快编二进制检查错误信息如下：\n")
            val builder = com.android.tools.r8.A8Command.builder(object :
                com.android.tools.r8.DiagnosticsHandler {
                override fun error(error: Diagnostic?) {
                    super.error(error)
                    System.err.println("Error : " + error?.getDiagnosticMessage())
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
            System.out.println("存在 ${errorCount} 个异常。请查看上面信息。")
            /*  System.out.println(
                  "结束 Dex Api 检查 " + new Date ().format(
                      "yyyy/MM-dd/HH:mm:ss"
                  )
              )*/
            if (a8Ignore) {
                System.out.println("生成的APK存在问题")
            } else {
                val error_file = File("build/a8_error_msg.txt")
                if (error_file.exists()) {
                    error_file.delete()
                }
                /* val moduleInfo = backtrackModule(set, fileNames, error_msg, it.project)
                 val printWriter = error_file.newPrintWriter()
                 printWriter.write(error_msg.toString())
                 printWriter.flush()
                 printWriter.close()
                 if (error_file.exists()) {
                     println("a8_error_msg.txt生成成功")
                 }
                 throw  RuntimeException(
                     "API 检查失败。 存在 ${errorCount} 个异常。检查代码问题。\n" + moduleInfo + "忽略规则定义在 a8 配置文件：./.buildscripts/a8_ruls.txt"
                 )*/
            }
        } else {

        }

    }


    fun backtrackModule(
        path: Set<String>,
        fileNames: Set<String>,
        errorMsg: StringBuilder,
        project: Project
    ): String? {
        val moduleNames = HashMap<String, String>()
        //  project.rootDir
        /*  project.rootDir.eachFileRecurse(groovy.io.FileType.FILES) {
              if (it.absolutePath != it.canonicalPath) return
              if (!it.name.contains(".") || !fileNames.contains(it.name.split("\\.")[0])) return
              def canonicalPath = it . canonicalPath def srcIndex = canonicalPath . indexOf ("src")
              if (srcIndex < 0) return
              def firstSlashIndexAfterSrcIndex = canonicalPath . indexOf ("/", srcIndex+4)+1
              def packagePath = canonicalPath . substring (canonicalPath.indexOf(
                  "/",
                  firstSlashIndexAfterSrcIndex
              ) + 1)
              canonicalPath = packagePath.split("\\.")[0].replaceAll('/', '.')
              if (!path.contains(canonicalPath)) return
              String[] components = it . canonicalPath . split ('/')
              def srcComponentIndex = - 1
              for (int i = 0; i < components.length; i++) {
              if ("src" == components[i]) {
                  srcComponentIndex = i
              }
          }
              if (srcComponentIndex > 0) {
                  def moduleName = components [srcComponentIndex - 1]
                  def suffixPart = it . canonicalPath . substring (srcIndex)
                  def prefixPart = project . rootDir . canonicalPath +"/"
                  moduleNames[moduleName] = it.canonicalPath - suffixPart - prefixPart
              }
          }
          if (!moduleNames.isEmpty()) {
              def sb = new StringBuffer("\n\n--------------------------------------------------------------------------------\n")
              def head = "A8 报错涉及下述模块：\n"
              sb.append(head)
              moduleNames.each {
                  sb.append("\t模块名：${it.key}, 相对路径: ${it.value}\n")
              }
              sb.append("\n\n")
              sb.append("如果是仓内有权限模块报错，可根据报错切到对应模块，解决掉报错或者修改 maven.yaml 下 dummy 字段（该字段仅用于生成模块下的 commit）然后进行提交，再 push 到远端促使该模块进行重编\n")
              sb.append("对于无权限或三方组件，可通过升级根目录下 build_version.txt 对应业务的 build_version，触发全部重编，一次重编大概 30min+，如非必要请勿更新\n")
              sb.append("A8 报错可先参照 https://info.bilibili.co/x/qwpHC 解决，如有疑问联系 @xiaolingtong(xiaolingtong@bilibili.com)\n")
              sb.append("-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------\n\n")
              def s = sb . toString ()
              errorMsg.append(s)
              return s
          }*/
        return null
    }
}