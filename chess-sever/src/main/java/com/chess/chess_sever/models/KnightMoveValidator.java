package com.chess.chess_sever.models;

public class KnightMoveValidator implements MoveValidator {
    @Override
    public boolean isValidMove(Piece piece, Position from, Position to, ChessBoard chessBoard) {
        if (to.getRow() < 0 || to.getRow() >= 8 || to.getCol() < 0 || to.getCol() >= 8) {
            return false;
        }
        int rowDiff = Math.abs(from.getRow() - to.getRow());
        int colDiff = Math.abs(from.getCol() - to.getCol());
        if((rowDiff == 2 && colDiff == 1) || (rowDiff == 1 && colDiff == 2)) {
            Piece targetPiece = chessBoard.getPieceAt(to);
            // if(targetPiece != null) {
            //     System.out.println("    pieceTarget != null ");
            // } 
            // if (targetPiece != null && targetPiece.getColor() == piece.getColor()) {
            //     System.out.println("    because same color " + targetPiece.getColor() + "");
            // }
            return targetPiece == null || targetPiece.getColor() != piece.getColor();
        }
        return false;
    }
}
