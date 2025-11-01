package com.chess.chess_sever.DataTransferObject;

import com.chess.chess_sever.models.Piece;

import lombok.Getter;

@Getter
public class PieceInfo {
    private String id;         
    private String type;      
    private String color;      
    private int row;           
    private int col;            
    private boolean hasMoved;  // dung cho nhap thanh
    
    public PieceInfo(Piece piece) {
        this.id = piece.getId();
        this.type = piece.getType().toString();
        this.color = piece.getColor().toString();
        this.row = piece.getPosition().getRow();
        this.col = piece.getPosition().getCol();
        this.hasMoved = piece.isHasMoved();
    }

    public String getId() {
        return id;
    }
    public String getType() {
        return type;
    }
    public String getColor() {
        return color;
    }
    public int getRow() {
        return row;
    }
    public int getCol() {
        return col;
    }
    public boolean isHasMoved() {
        return hasMoved;
    }
}

