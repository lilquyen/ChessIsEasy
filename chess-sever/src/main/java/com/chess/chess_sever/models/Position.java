package com.chess.chess_sever.models;

import java.util.Map;

public class Position {
    private int row, col;
    private static Map<Character, Integer> colMap = Map.of(
        'A', 0,
        'B', 1,
        'C', 2,
        'D', 3,
        'E', 4,
        'F', 5,
        'G', 6,
        'H', 7
    );

    public Position(int _row, int _col) {
        this.row = _row;
        this.col = _col;
    }


    public Position() {}


    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    @Override
    public boolean equals(Object obj) {
        if(this == obj) return true;
        if(!(obj instanceof Position other)) return false;
        return row == other.row && col == other.col;
    }

    @Override
    public String toString() {
        return "(" + row + ", " + col + ")";
    }

    public Position getPositionFromString(String s) {
        if(s.length() != 2) return null;
        if(s.charAt(0) < 'A' || s.charAt(0) > 'H') return null;
        int col = colMap.get(s.charAt(0));
        int row = s.charAt(1) - '1';
        if(row < 0 || row > 7) return null;
        return new Position(row, col);
    }

    public String toStringPosition() {
        char colChar = (char) ('A' + col);
        char rowChar = (char) ('1' + row);
        return "" + colChar + rowChar;
    } 
}   
