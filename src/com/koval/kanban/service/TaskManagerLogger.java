package com.koval.kanban.service;

import java.io.File;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class TaskManagerLogger {
    private static Logger log;

    public TaskManagerLogger(String className) {
        log = Logger.getLogger(className);
        try {
            File dir = new File("src/com/koval/kanban/log");
            if (!dir.exists()) {
                dir.mkdirs();
            }
            FileHandler fh = new FileHandler("src/com/koval/kanban/log/FileBackedTaskManagerLogs.log",
                    true);
            log.addHandler(fh);
            fh.setFormatter(new SimpleFormatter());
            log.setLevel(Level.ALL);
            log.info("Логирование начато");
        } catch (SecurityException | IOException e) {
            log.log(Level.SEVERE, "Произошла ошибка при работе с FileHandler.", e);
        }
    }

    public Logger getLogger() {
        return log;
    }
}
