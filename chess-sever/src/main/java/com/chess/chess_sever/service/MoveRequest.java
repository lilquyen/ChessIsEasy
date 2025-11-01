package com.chess.chess_sever.service;

import java.util.Objects;

import lombok.Getter;

@Getter
public class MoveRequest {
    
    public String from;            // ô xuất phát (vd: "E2")
    public String to;              // ô đích (vd: "E4")
    // private String promotionChoice; // lựa chọn phong cấp (vd: "QUEEN"), null nếu không phải pawn promotion
    // private String gameId;          // ID ván game (cần cho online nhiều ván)

    public MoveRequest() {}

    public MoveRequest(int row, int col, int row2, int col2) {
        this.from = (char)('A' + col) + Integer.toString(row + 1);
        this.to = (char)('A' + col2) + Integer.toString(row2 + 1);
    }

    public MoveRequest(String stringPosition, String stringPosition2) {
        this.from = stringPosition;
        this.to = stringPosition2;
    }
    
    public void setFrom(String from) {
        this.from = from;
    }
    public void setTo(String to) {
        this.to = to;
    }
    public String getFrom() {
        return from;
    }
    public String getTo() {
        return to;
    }

    public String messageWhenMoved() {
        return "Move from " + from + " to " + to;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MoveRequest that = (MoveRequest) o;
        return Objects.equals(from, that.from) && Objects.equals(to, that.to);
    }

    @Override
    public int hashCode() {
        return Objects.hash(from, to);
    }
}
