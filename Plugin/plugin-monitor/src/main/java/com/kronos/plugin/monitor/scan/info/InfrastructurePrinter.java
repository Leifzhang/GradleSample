package com.kronos.plugin.monitor.scan.info;

import com.kronos.plugin.monitor.repo.LogFile;
import com.kronos.plugin.monitor.repo.data.Infrastructure;


public class InfrastructurePrinter {

    LogFile file;

    public InfrastructurePrinter(LogFile file) {
        this.file = file;
    }

    public void execute(Infrastructure infrastructure) {
        file.append(infrastructure.toDec());
    }
}
