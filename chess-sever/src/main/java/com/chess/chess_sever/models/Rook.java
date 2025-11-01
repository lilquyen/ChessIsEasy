package com.chess.chess_sever.models;

import java.util.ArrayList;
import java.util.List;

public class Rook extends Piece{
    public Rook (PieceType pieceType, PieceColor color, Position position, String id)
    {
        super(pieceType, color, position, id);
    }

    @Override
    public Piece copy() {
        return new Rook(PieceType.ROOK, this.color, new Position(this.position.getRow(), this.position.getCol()), this.id);
    }

    @Override
    public List<Position> getAvailableMoves(ChessBoard chessBoard) {
        List<Position> moves = new ArrayList<>();
        int row = position.getRow();
        int col = position.getCol();

        int[] dir_row = {-1, 1, 0, 0};
        int[] dir_col = {0, 0, -1, 1};

        for (int i = 0; i < 4; i++) {
            int new_row = row + dir_row[i];
            int new_col = col + dir_col[i];
            while (new_row >= 0 && new_row < 8 && new_col >= 0 && new_col < 8) {
                Position to = new Position(new_row, new_col);
                RookMoveValidator validator = new RookMoveValidator();
                boolean isValidMove = validator.isValidMove(this, position, to, chessBoard);
                if(isValidMove) {
                    moves.add(to);
                } else {
                    break; 
                }
                new_row += dir_row[i];
                new_col += dir_col[i];
            }
        }
        return moves;
    }
}
