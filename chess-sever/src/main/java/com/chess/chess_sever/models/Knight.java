package com.chess.chess_sever.models;

import java.util.ArrayList;
import java.util.List;

public class Knight extends Piece {
    public Knight(PieceType pieceType, PieceColor color, Position position, String id)
    {
        super(pieceType, color, position, id);
    }

    @Override
    public Piece copy() {
        return new Knight(PieceType.KNIGHT, this.color, new Position(this.position.getRow(), this.position.getCol()), this.id);
    }

    @Override
    public List<Position> getAvailableMoves(ChessBoard chessBoard) {
        List<Position> moves = new ArrayList<>();
        int row = position.getRow();
        int col = position.getCol();

        int[] dir_row = {-1, 1, 2, 2, 1, -1, -2, -2};
        int[] dir_col = {2, 2, 1, -1, -2, -2, -1, 1};
        KnightMoveValidator validator = new KnightMoveValidator();


        for (int i = 0; i < 8; i++) {
            int new_row = row + dir_row[i];
            int new_col = col + dir_col[i];
            if (new_row >= 0 && new_row < 8 && new_col >= 0 && new_col < 8) {
                Position to = new Position(new_row, new_col);
                
                boolean isValidMove = validator.isValidMove(this, position, to, chessBoard);
                if(isValidMove) {
                    moves.add(to);
                } else {
                    continue; // Không thể đi tiếp trong hướng này
                }
            }
        }
        return moves;
    }
}
