package com.chess.chess_sever.models;

public class RookMoveValidator implements MoveValidator {
    @Override 
    public boolean isValidMove(Piece piece, Position from, Position to, ChessBoard chessBoard) {
        if (to.getRow() < 0 || to.getRow() >= 8 || to.getCol() < 0 || to.getCol() >= 8) {
            return false;
        }
        int rowDiff = Math.abs(from.getRow() - to.getRow());
        int colDiff = Math.abs(from.getCol() - to.getCol());
        if(rowDiff != 0 && colDiff != 0) {
            return false; // xe chỉ đi thẳng
        }

        // Kiểm tra cản đường
        int stepRow = Integer.compare(to.getRow(), from.getRow()); 
        int stepCol = Integer.compare(to.getCol(), from.getCol());
        int currentRow = from.getRow() + stepRow;
        int currentCol = from.getCol() + stepCol;
        while(currentRow != to.getRow() || currentCol != to.getCol()) {
            if(chessBoard.getPieceAt(new Position(currentRow, currentCol)) != null) {
                return false; // có quân cản đường
            }
            currentRow += stepRow;
            currentCol += stepCol;
        }
        Piece targetPiece = chessBoard.getPieceAt(to);
        return targetPiece == null || targetPiece.getColor() != piece.getColor();
    }
}
