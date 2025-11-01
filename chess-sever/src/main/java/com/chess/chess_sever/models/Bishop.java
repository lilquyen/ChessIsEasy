package com.chess.chess_sever.models;

import java.util.ArrayList;
import java.util.List;

public class Bishop extends Piece {
    public Bishop (PieceType pieceType, PieceColor color, Position position, String id)
    {
        super(pieceType, color, position, id);
        this.type = PieceType.BISHOP;
    }

    @Override
    public Piece copy() {
        return new Bishop(PieceType.BISHOP, this.color, new Position(this.position.getRow(), this.position.getCol()), this.id);
    }

    @Override
    public List<Position> getAvailableMoves(ChessBoard chessBoard) {
        List<Position> moves = new ArrayList<>();
        int row = position.getRow();
        int col = position.getCol();

        int[] dir_row = {1, 1, -1, -1};
        int[] dir_col = {-1, 1, 1, -1};

        for (int i = 0; i < 4; i++) {
            int new_row = row + dir_row[i];
            int new_col = col + dir_col[i];
            while (new_row >= 0 && new_row < 8 && new_col >= 0 && new_col < 8) {
                Position to = new Position(new_row, new_col);
                BishopMoveValidator validator = new BishopMoveValidator();
                boolean isValidMove = validator.isValidMove(this, position, to, chessBoard);
                if(isValidMove) {
                    moves.add(to);
                } else {
                    break; // Không thể đi tiếp trong hướng này
                }
                new_row += dir_row[i];
                new_col += dir_col[i];
            }
        }
        return moves;
    }
    
}
