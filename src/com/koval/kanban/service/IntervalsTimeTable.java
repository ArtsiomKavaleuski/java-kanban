package com.koval.kanban.service;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.TreeMap;

public class IntervalsTimeTable {
    private final LocalDateTime START_DATE_TIME = LocalDateTime.of(2024, Month.AUGUST, 1, 0,0);
    private final LocalDateTime END_DATE_TIME = LocalDateTime.of(2025, Month.SEPTEMBER,1,0,0);
    public final int MINUTES_INTERVAL = 1;

    public TreeMap<LocalDateTime, Boolean> getTimeIntervals() {
        TreeMap<LocalDateTime, Boolean> tempMap = new TreeMap<>();
        LocalDateTime temp = START_DATE_TIME;
        while(true) {
            if(temp.isBefore(END_DATE_TIME)) {
                tempMap.put(temp, false);
                temp = temp.plusMinutes(MINUTES_INTERVAL);
            } else {
                break;
            }
        }
        return tempMap;
    }
}
