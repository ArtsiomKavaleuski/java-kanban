package com.koval.kanban.service;

public class ManagerSaveException extends Exception {
    public ManagerSaveException(final String message) {
        super(message);
    }
    public ManagerSaveException(final String message, Exception e){
        super(message, e);
    }
}
