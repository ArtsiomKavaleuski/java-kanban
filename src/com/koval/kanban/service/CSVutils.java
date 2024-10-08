package com.koval.kanban.service;

import com.google.gson.*;
import com.koval.kanban.model.Epic;
import com.koval.kanban.model.SubTask;
import com.koval.kanban.model.Task;
import com.koval.kanban.model.TaskTypes;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public class CSVutils {
    public static Task stringToTask(String value) {
        Task task;
        String[] split = value.split(",");
        int id = Integer.parseInt(split[0]);
        TaskTypes type = TaskTypes.valueOf(split[1]);
        String name = split[2];
        TaskStatus status = TaskStatus.valueOf(split[3]);
        String description = split[4];
        LocalDateTime startTime = null;
        if (!split[6].equals("null")) {
            startTime = LocalDateTime.parse(split[6]);
        }
        Duration duration = Duration.ofMinutes(Integer.parseInt(split[7]));

        if (type.equals(TaskTypes.SUBTASK)) {
            int epicId = Integer.parseInt(split[5]);
            task = new SubTask(name, description, id, status, epicId, startTime, duration);
        } else if (type.equals(TaskTypes.EPIC)) {
            task = new Epic(name, description, id);
        } else {
            task = new Task(name, description, id, status, startTime, duration);
        }
        return task;
    }

    public static <T extends Task> String taskToString(T task) {
        String taskString;
        if (task.getClass().equals(SubTask.class)) {
            taskString = String.format("%d,%s,%s,%s,%s,%d,%s,%d", task.getId(),
                    TaskTypes.SUBTASK, task.getName(), task.getStatus(), task.getDescription(),
                    ((SubTask) task).getEpicId(), task.getStartTime(), task.getDuration().toMinutes());
        } else if (task.getClass().equals(Epic.class)) {
            taskString = String.format("%d,%s,%s,%s,%s,,%s,%d", task.getId(), TaskTypes.EPIC, task.getName(),
                    task.getStatus(), task.getDescription(), task.getStartTime(), task.getDuration().toMinutes());
        } else {
            taskString = String.format("%d,%s,%s,%s,%s,,%s,%d", task.getId(), TaskTypes.TASK, task.getName(),
                    task.getStatus(), task.getDescription(), task.getStartTime(), task.getDuration().toMinutes());
        }
        return taskString;
    }

    public static <T extends Task> String taskToJson(T task) {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .create();
        return gson.toJson(task);
    }

    public static Task jsonToTask(String value) {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .serializeNulls()
                .create();
        JsonElement jsonElement = JsonParser.parseString(value);
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        Task task = null;
        switch (jsonObject.get("taskType").getAsString()) {
            case "TASK":
                task = gson.fromJson(value, Task.class);
                break;
            case "SUBTASK":
                task = gson.fromJson(value, SubTask.class);
                break;
            case "EPIC":
                task = gson.fromJson(value, Epic.class);
                break;
        }
        return task;
    }

    public static String tasksListToJson(List<? extends Task> tasks) {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .create();
        return gson.toJson(tasks);
    }
}





