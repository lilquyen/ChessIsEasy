package com.chess.chess_sever.models;

public interface MoveValidator {
    boolean isValidMove(Piece piece, Position from, Position to, ChessBoard chessBoard);
}
