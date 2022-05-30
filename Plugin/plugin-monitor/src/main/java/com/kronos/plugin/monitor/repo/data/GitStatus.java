package com.kronos.plugin.monitor.repo.data;

public class GitStatus {

    public String path;
    public String commit;
    public String branch;
    public String status;

    @Override
    public String toString() {
        return "GitStatus{" +
                "path='" + path + '\'' +
                ", commit='" + commit + '\'' +
                ", branch='" + branch + '\'' +
                ", status='" + status + '\'' +
                '}';
    }

    public String toDec() {
        return "path=" + path + "\n" +
                "branch=" + branch + "\n" +
                "commit=" + commit + "\n" +
                "status=" + status + "\n";
    }
}
