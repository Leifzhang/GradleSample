package com.kronos.plugin.monitor.scan.analyse.console;


import com.kronos.plugin.monitor.scan.analyse.bean.EProject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;
import java.util.function.Consumer;

public class ProjectConsole {

    private final int index;
    private final com.kronos.plugin.monitor.scan.analyse.bean.EProject EProject;
    private final List<CIssueConsole> CIssueConsoles = new ArrayList<>();

    public ProjectConsole(int index, EProject EProject) {
        this.index = index;
        this.EProject = EProject;
        for (int i1 = EProject.eIssues.size() - 1; i1 >= 0; i1--) {
            CIssueConsoles.add(new CIssueConsole(i1, EProject.eIssues.get(i1)));
        }
    }

    public void print(ResultConsole.Printer printStream) {
        printStream.println(Constant.INDEX + index + ". 项目:" + EProject.getName());
        printStream.println(Constant.INDEX + Constant.TAB + Constant.TAB + "地址:" + EProject.getDir().getAbsolutePath());
        if (EProject.getOwners().size() > 0) {
            printStream.println(Constant.INDEX + Constant.TAB + Constant.TAB + "owners:" + join(EProject.getOwners()));
        }

        printStream.println(Constant.INDEX + Constant.TAB + Constant.TAB + "问题:");
        for (int i1 = 0; i1 < CIssueConsoles.size(); i1++) {
            printStream.println(Constant.INDEX + Constant.TAB + Constant.TAB + Constant.TAB + i1 + ".  " + new File(CIssueConsoles.get(i1).eIssue.getFile()).getName() + " (" + CIssueConsoles.get(i1).eIssue.getTool() + ")");
            printStream.println(Constant.INDEX + Constant.TAB + Constant.TAB + Constant.TAB + "--------");
            CIssueConsoles.get(i1).print(printStream);
        }
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append(index + ". 项目:" + EProject.getName() + "\n");
        System.out.println(Constant.TAB + Constant.TAB + "地址:" + EProject.getDir().getAbsolutePath());
        System.out.println(Constant.TAB + Constant.TAB + "owners:" + join(EProject.getOwners()));
        sb.append(Constant.TAB + Constant.TAB + "问题:" + "\n");
        for (int i1 = 0; i1 < CIssueConsoles.size(); i1++) {
            sb.append(Constant.TAB + Constant.TAB + i1 + ".  " + new File(CIssueConsoles.get(i1).eIssue.getFile()).getName() + " (" + CIssueConsoles.get(i1).eIssue.getTool() + ")\n");
            sb.append(Constant.TAB + Constant.TAB + "--------\n");
            sb.append(CIssueConsoles.get(i1).toString());
        }
        return sb.toString();
    }

    private String join(List<String> owners) {
        StringJoiner stringJoiner = new StringJoiner(",");
        owners.forEach(new Consumer<String>() {
            @Override
            public void accept(String s) {
                stringJoiner.add(s);
            }
        });
        return stringJoiner.toString();
    }
}
