package com.chess.chess_sever.models;

public class PawnMoveValidator implements MoveValidator {
    @Override
    public boolean isValidMove(Piece piece, Position from, Position to, ChessBoard chessBoard) {
        int direction = (piece.getColor() == PieceColor.WHITE) ? 1 : -1;
        int startRow = (piece.getColor() == PieceColor.WHITE) ? 1 : 6;
        int rowDiff = to.getRow() - from.getRow();
        int colDiff = Math.abs(to.getCol() - from.getCol());
        Piece targetPiece = chessBoard.getPieceAt(to);

        if (to.getRow() < 0 || to.getRow() >= 8 || to.getCol() < 0 || to.getCol() >= 8) {
            return false;
        }

        // Standard move
        if (colDiff == 0) {
            if (rowDiff == direction && targetPiece == null) {
                return true;
            }
            // First move double step
            if (rowDiff == 2 * direction && from.getRow() == startRow && targetPiece == null) {
                Position intermediatePos = new Position(from.getRow() + direction, from.getCol());
                if (chessBoard.getPieceAt(intermediatePos) == null) {
                    return true;
                }
            }
        }
        // Ăn chéo
        else if (colDiff == 1 && rowDiff == direction && targetPiece != null 
                    && targetPiece.getColor() != piece.getColor()) {
            return true;
        }
        // En passant
        else if (colDiff == 1 && rowDiff == direction && targetPiece == null) {
            Position enPassantTarget = chessBoard.getEnPassantTarget();
            if (enPassantTarget != null && to.equals(enPassantTarget)) {
                Position capturedPawnPosition = new Position(from.getRow(), to.getCol());
                Piece capturedPawn = chessBoard.getPieceAt(capturedPawnPosition);
                if (capturedPawn != null && capturedPawn.getType() == PieceType.PAWN 
                        && capturedPawn.getColor() != piece.getColor()) {
                    return true;
                }
            }
        }
        return false;
    }
}
