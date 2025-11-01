package com.chess.chess_sever.models;

public class MoveResult {
    private boolean success;
    private String message;

    public MoveResult(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public boolean isSuccess() { 
        return success; 
    }
    public String getMessage() { 
        return message; 
    }
}
