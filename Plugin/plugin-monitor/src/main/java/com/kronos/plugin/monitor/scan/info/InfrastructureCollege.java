package com.kronos.plugin.monitor.scan.info;

import com.kronos.plugin.monitor.repo.data.Infrastructure;
import com.kronos.plugin.monitor.utils.EnvUserNameProvider;
import com.kronos.plugin.monitor.utils.GitEmailProvider;
import com.kronos.plugin.monitor.utils.GitNameProvider;
import com.kronos.plugin.monitor.utils.HostnameProvider;
import com.kronos.plugin.monitor.utils.JavaEnvUserNameProvider;
import com.kronos.plugin.monitor.utils.PidProvider;

import org.gradle.api.invocation.Gradle;

import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.util.Properties;

public class InfrastructureCollege {

    public Infrastructure execute(Gradle gradle) {
        Infrastructure infrastructure = new Infrastructure();
        infrastructure.setGitEmail(new GitEmailProvider().get());
        infrastructure.setGitUser(new GitNameProvider().get());
        infrastructure.setBuildOwner(System.getProperty("build_owner"));
        if (infrastructure.getBuildOwner() == null) {
            infrastructure.setBuildOwner(System.getenv().get("build_owner"));
        }
        infrastructure.setEnvUserName(new EnvUserNameProvider().get());
        infrastructure.setJavaEnvUserName(new JavaEnvUserNameProvider().get());
        infrastructure.setHostname(new HostnameProvider().get());
        infrastructure.setMaxWorkerCount(gradle.getStartParameter().getMaxWorkerCount());
        Runtime rt = Runtime.getRuntime();
        Properties props = System.getProperties();
        infrastructure.setRuntimeFreeMemory(rt.freeMemory() / 1024 / 1024 + "MB");
        infrastructure.setRuntimeMaxMemory(rt.maxMemory() / 1024 / 1024 + "MB");
        infrastructure.setRuntimeTotalMemory(rt.totalMemory() / 1024 / 1024 + "MB");
        OperatingSystemMXBean osmb = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
        //   infrastructure.setFreeMemory(osmb.getFreePhysicalMemorySize() / 1024 / 1024 + "MB");
        // infrastructure.setTotalMemory(osmb.getTotalPhysicalMemorySize() / 1024 / 1024 + "MB");
        infrastructure.setSystem(props.get("os.name") + " - " + props.get("os.version"));
        infrastructure.setUsername(props.get("user.name").toString());
        infrastructure.setJavaVersion(props.getProperty("java.vm.specification.vendor") +
                " - " + props.getProperty("java.vm.specification.version"));
        infrastructure.setCpu(rt.availableProcessors());
        infrastructure.setPid(new PidProvider().get());
        return infrastructure;
    }

    public static void main(String[] args) {
        Properties props = System.getProperties(); //系统属性
        System.out.println("Java的运行环境版本：" + props.getProperty("java.version"));
        System.out.println("Java的运行环境供应商：" + props.getProperty("java.vendor"));
        System.out.println("Java供应商的URL：" + props.getProperty("java.vendor.url"));
        System.out.println("Java的安装路径：" + props.getProperty("java.home"));
        System.out.println("Java的虚拟机规范版本：" + props.getProperty("java.vm.specification.version"));
        System.out.println("Java的虚拟机规范供应商：" + props.getProperty("java.vm.specification.vendor"));
        System.out.println("Java的虚拟机规范名称：" + props.getProperty("java.vm.specification.name"));
        System.out.println("Java的虚拟机实现版本：" + props.getProperty("java.vm.version"));
        System.out.println("Java的虚拟机实现供应商：" + props.getProperty("java.vm.vendor"));
        System.out.println("Java的虚拟机实现名称：" + props.getProperty("java.vm.name"));
        System.out.println("Java运行时环境规范版本：" + props.getProperty("java.specification.version"));
        System.out.println("Java运行时环境规范供应商：" + props.getProperty("java.specification.vender"));
        System.out.println("Java运行时环境规范名称：" + props.getProperty("java.specification.name"));
        System.out.println("Java的类格式版本号：" + props.getProperty("java.class.version"));
        System.out.println("Java的类路径：" + props.getProperty("java.class.path"));
        System.out.println("加载库时搜索的路径列表：" + props.getProperty("java.library.path"));
        System.out.println("默认的临时文件路径：" + props.getProperty("java.io.tmpdir"));
        System.out.println("一个或多个扩展目录的路径：" + props.getProperty("java.ext.dirs"));
        System.out.println("操作系统的名称：" + props.getProperty("os.name"));
        System.out.println("操作系统的构架：" + props.getProperty("os.arch"));
        System.out.println("操作系统的版本：" + props.getProperty("os.version"));
        System.out.println("文件分隔符：" + props.getProperty("file.separator"));   //在 unix 系统中是＂／＂
        System.out.println("路径分隔符：" + props.getProperty("path.separator"));   //在 unix 系统中是＂:＂
        System.out.println("行分隔符：" + props.getProperty("line.separator"));   //在 unix 系统中是＂/n＂
        System.out.println("用户的账户名称：" + props.getProperty("user.name"));
        System.out.println("用户的主目录：" + props.getProperty("user.home"));
        System.out.println("用户的当前工作目录：" + props.getProperty("user.dir"));
        OperatingSystemMXBean osmb = ManagementFactory.getOperatingSystemMXBean();
        Runtime rt = Runtime.getRuntime();

        //   System.out.println("Memory_totalspace " + osmb.getTotalPhysicalMemorySize() / 1024 / 1024 + "MB");
        System.out.println("Memory_totalspace " + rt.totalMemory() / 1024 / 1024 + "MB");
        //  System.out.println("Memory_freeSpace " + osmb.getFreePhysicalMemorySize() / 1024 / 1024 + "MB");
        //   System.out.println("Memory_usedSpace " + (osmb.getTotalPhysicalMemorySize() - osmb.getFreePhysicalMemorySize()) / 1024 / 1024 + "MB");

    }
}
