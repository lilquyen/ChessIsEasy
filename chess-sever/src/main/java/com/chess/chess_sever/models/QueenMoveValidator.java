package com.chess.chess_sever.models;

public class QueenMoveValidator implements MoveValidator {
    @Override
    public boolean isValidMove(Piece piece, Position from, Position to, ChessBoard chessBoard) {
        if (to.getRow() < 0 || to.getRow() >= 8 || to.getCol() < 0 || to.getCol() >= 8) {
            return false;
        }
        RookMoveValidator rookValidator = new RookMoveValidator();
        BishopMoveValidator bishopValidator = new BishopMoveValidator();
        return rookValidator.isValidMove(piece, from, to, chessBoard) ||
               bishopValidator.isValidMove(piece, from, to, chessBoard);
    }
}
