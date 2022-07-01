package com.kronos.plugin.monitor.scan.info

import com.kronos.plugin.monitor.repo.data.Infrastructure
import com.kronos.plugin.monitor.utils.*
import com.sun.management.UnixOperatingSystemMXBean
import org.gradle.api.invocation.Gradle
import java.lang.management.ManagementFactory

class InfrastructureCollege {

    fun execute(gradle: Gradle?): Infrastructure {
        val infrastructure = Infrastructure()
        infrastructure.gitEmail = GitEmailProvider().get()
        infrastructure.gitUser = GitNameProvider().get()
        infrastructure.buildOwner = System.getProperty("build_owner")
        if (infrastructure.buildOwner == null) {
            infrastructure.buildOwner = System.getenv()["build_owner"]
        }
        infrastructure.envUserName = EnvUserNameProvider().get()
        infrastructure.javaEnvUserName = JavaEnvUserNameProvider().get()
        infrastructure.hostname = HostnameProvider().get()
        gradle?.apply {
            infrastructure.maxWorkerCount = gradle.startParameter.maxWorkerCount
        }
        val rt = Runtime.getRuntime()
        val props = System.getProperties()
        infrastructure.runtimeFreeMemory = (rt.freeMemory().div(1024).div(1024).toString()) + "MB"
        infrastructure.runtimeMaxMemory = rt.maxMemory().div(1024).div(1024).toString() + "MB"
        infrastructure.runtimeTotalMemory = rt.totalMemory().div(1024).div(1024).toString() + "MB"
        val osmb = ManagementFactory.getOperatingSystemMXBean() as UnixOperatingSystemMXBean
        infrastructure.freeMemory =
            (osmb.freePhysicalMemorySize.div(1024).div(1024).toString() + "MB")
        infrastructure.totalMemory =
            (osmb.totalPhysicalMemorySize.div(1024).div(1024).toString() + "MB")
        infrastructure.system = props["os.name"].toString() + " - " + props["os.version"]
        infrastructure.username = props["user.name"].toString()
        infrastructure.javaVersion = props.getProperty("java.vm.specification.vendor") +
                " - " + props.getProperty("java.vm.specification.version")
        infrastructure.cpu = rt.availableProcessors()
        infrastructure.pid = PidProvider().get()
        return infrastructure
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            val props = System.getProperties() //系统属性
            println("Java的运行环境版本：" + props.getProperty("java.version"))
            println("Java的运行环境供应商：" + props.getProperty("java.vendor"))
            println("Java供应商的URL：" + props.getProperty("java.vendor.url"))
            println("Java的安装路径：" + props.getProperty("java.home"))
            println("Java的虚拟机规范版本：" + props.getProperty("java.vm.specification.version"))
            println("Java的虚拟机规范供应商：" + props.getProperty("java.vm.specification.vendor"))
            println("Java的虚拟机规范名称：" + props.getProperty("java.vm.specification.name"))
            println("Java的虚拟机实现版本：" + props.getProperty("java.vm.version"))
            println("Java的虚拟机实现供应商：" + props.getProperty("java.vm.vendor"))
            println("Java的虚拟机实现名称：" + props.getProperty("java.vm.name"))
            println("Java运行时环境规范版本：" + props.getProperty("java.specification.version"))
            println("Java运行时环境规范供应商：" + props.getProperty("java.specification.vender"))
            println("Java运行时环境规范名称：" + props.getProperty("java.specification.name"))
            println("Java的类格式版本号：" + props.getProperty("java.class.version"))
            println("Java的类路径：" + props.getProperty("java.class.path"))
            println("加载库时搜索的路径列表：" + props.getProperty("java.library.path"))
            println("默认的临时文件路径：" + props.getProperty("java.io.tmpdir"))
            println("一个或多个扩展目录的路径：" + props.getProperty("java.ext.dirs"))
            println("操作系统的名称：" + props.getProperty("os.name"))
            println("操作系统的构架：" + props.getProperty("os.arch"))
            println("操作系统的版本：" + props.getProperty("os.version"))
            println("文件分隔符：" + props.getProperty("file.separator")) //在 unix 系统中是＂／＂
            println("路径分隔符：" + props.getProperty("path.separator")) //在 unix 系统中是＂:＂
            println("行分隔符：" + props.getProperty("line.separator")) //在 unix 系统中是＂/n＂
            println("用户的账户名称：" + props.getProperty("user.name"))
            println("用户的主目录：" + props.getProperty("user.home"))
            println("用户的当前工作目录：" + props.getProperty("user.dir"))
            val osmb = ManagementFactory.getOperatingSystemMXBean()
            val rt = Runtime.getRuntime()

            //   System.out.println("Memory_totalspace " + osmb.getTotalPhysicalMemorySize() / 1024 / 1024 + "MB");
            println("Memory_totalspace " + rt.totalMemory() / 1024 / 1024 + "MB")
            //  System.out.println("Memory_freeSpace " + osmb.getFreePhysicalMemorySize() / 1024 / 1024 + "MB");
            //   System.out.println("Memory_usedSpace " + (osmb.getTotalPhysicalMemorySize() - osmb.getFreePhysicalMemorySize()) / 1024 / 1024 + "MB");
        }
    }
}