package com.chess.chess_sever.models;

public class MoveValidatorFactory {
    public static MoveValidator getMoveValidator(PieceType type) {
        switch (type) {
            case PAWN:
                return new PawnMoveValidator();
            case ROOK:
                return new RookMoveValidator();
            case KNIGHT:
                return new KnightMoveValidator();
            case BISHOP:
                return new BishopMoveValidator();
            case QUEEN:
                return new QueenMoveValidator();
            case KING:
                return new KingMoveValidator();
            default:
                throw new IllegalArgumentException("Unknown PieceType: " + type);
        }
    }
}
