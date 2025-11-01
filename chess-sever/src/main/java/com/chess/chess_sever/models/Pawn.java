package com.chess.chess_sever.models;

import java.util.ArrayList;
import java.util.List;

public class Pawn extends Piece {
    public Pawn (PieceType pieceType, PieceColor color, Position position, String id)
    {
        super(pieceType, color, position, id);
    }

    @Override
    public Piece copy() {
        return new Pawn(PieceType.PAWN, this.color, new Position(this.position.getRow(), this.position.getCol()), this.id);
    }

    @Override
    public List<Position> getAvailableMoves(ChessBoard chessBoard) {
        List<Position> moves = new ArrayList<>();
        int row = position.getRow();
        int col = position.getCol();

        int dir = (color == PieceColor.WHITE) ? 1 : -1;

        int new_row = row + dir;
        if (new_row >= 0 && new_row < 8) {
            Position to = new Position(new_row, col);
            PawnMoveValidator validator = new PawnMoveValidator();
            boolean isValidMove = validator.isValidMove(this, position, to, chessBoard);
            if (isValidMove) {
                moves.add(to);
            }

            // First move double step
            if ((row == 1 && color == PieceColor.WHITE) || (row == 6 && color == PieceColor.BLACK)) {
                int twoStep = row + 2 * dir;
                if (twoStep >= 0 && twoStep < 8) {
                    to = new Position(twoStep, col);
                    isValidMove = validator.isValidMove(this, position, to, chessBoard);
                    if (isValidMove) {
                        moves.add(to);
                    }
                }
            }

            // Diagonal captures
            if (col - 1 >= 0) {
                to = new Position(new_row, col - 1);
                isValidMove = validator.isValidMove(this, position, to, chessBoard);
                if (isValidMove) {
                    moves.add(to);
                }
            }
            if (col + 1 < 8) {
                to = new Position(new_row, col + 1);
                isValidMove = validator.isValidMove(this, position, to, chessBoard);
                if (isValidMove) {
                    moves.add(to);
                }
            }
        }
        return moves;
    }
}
