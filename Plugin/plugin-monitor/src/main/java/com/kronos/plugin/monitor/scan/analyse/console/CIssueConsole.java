package com.kronos.plugin.monitor.scan.analyse.console;


import com.kronos.plugin.monitor.scan.analyse.bean.EIssue;

public class CIssueConsole {

    public final int index;
    public final EIssue eIssue;

    public CIssueConsole(int index, EIssue eIssue) {
        this.index = index;
        this.eIssue = eIssue;
    }

    public void print(ResultConsole.Printer printStream) {
        printStream.println(Constant.INDEX + Constant.TAB + Constant.TAB + Constant.TAB + Constant.TAB + "相关开发 : " + eIssue.getOwner());
        printStream.println(Constant.INDEX + Constant.TAB + Constant.TAB + Constant.TAB + Constant.TAB + "详情 :  " + eIssue.getFile());
        if (eIssue.getType() != null) {
            printStream.println(Constant.INDEX + Constant.TAB + Constant.TAB + Constant.TAB + Constant.TAB + "类型 :  " + eIssue.getType());
        }
        logm(printStream, Constant.INDEX + Constant.TAB + Constant.TAB + Constant.TAB + Constant.TAB + "  ", eIssue.getMsg());
        if (eIssue.getCode() != null) {
            printStream.println(Constant.INDEX + Constant.TAB + Constant.TAB + Constant.TAB + Constant.TAB + "代码 : ");
            printStream.println();
            logm(printStream, Constant.INDEX + Constant.TAB + Constant.TAB + Constant.TAB + Constant.TAB + Constant.TAB, eIssue.getCode());
            printStream.println("");
        }
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append(Constant.TAB + Constant.TAB + Constant.TAB + "相关开发 : " + eIssue.getOwner() + "\n");
        sb.append(Constant.TAB + Constant.TAB + Constant.TAB + "详情 :  " + eIssue.getFile() + "\n");
        sb.append(Constant.TAB + Constant.TAB + Constant.TAB + "代码 : \n");
        sb.append(Constant.TAB + Constant.TAB + Constant.TAB + eIssue.getCode() + "\n");
        return sb.toString();
    }


    public void logm(ResultConsole.Printer printStream, String pr, String msg) {
        if (msg == null) {
            return;
        }
        for (String line : msg.split("\n")) {
            if (line.length() > 0) {
                printStream.println(pr + line);
            }
        }
    }
}
