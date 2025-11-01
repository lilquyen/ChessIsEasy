package com.chess.chess_sever.models;

public class BishopMoveValidator implements MoveValidator {
    @Override
    public boolean isValidMove(Piece piece, Position from, Position to, ChessBoard chessBoard) {
        if (to.getRow() < 0 || to.getRow() >= 8 || to.getCol() < 0 || to.getCol() >= 8) {
            return false;
        }
        int rowDiff = Math.abs(from.getRow() - to.getRow());
        int colDiff = Math.abs(from.getCol() - to.getCol());
        if(rowDiff == colDiff) {
            int rowStep = (to.getRow() - from.getRow()) / rowDiff;
            int colStep = (to.getCol() - from.getCol()) / colDiff;
            for(int i = 1; i < rowDiff; i++) {
                Position intermediatePos = new Position(from.getRow() + i * rowStep, from.getCol() + i * colStep);
                if(chessBoard.getPieceAt(intermediatePos) != null) {
                    return false; // Có quân cản đường
                }
            }
            Piece targetPiece = chessBoard.getPieceAt(to);
            return targetPiece == null || targetPiece.getColor() != piece.getColor();
        }
        return false;
    }
}
