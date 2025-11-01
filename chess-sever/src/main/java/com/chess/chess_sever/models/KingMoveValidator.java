package com.chess.chess_sever.models;

public class KingMoveValidator implements MoveValidator{
    @Override
    public boolean isValidMove(Piece piece, Position from, Position to, ChessBoard chessBoard) {
        if (to.getRow() < 0 || to.getRow() >= 8 || to.getCol() < 0 || to.getCol() >= 8) {
            return false;
        }
        int rowDiff = Math.abs(from.getRow() - to.getRow());
        int colDiff = Math.abs(from.getCol() - to.getCol());
        if(rowDiff <= 1 && colDiff <= 1) {
            Piece targetPiece = chessBoard.getPieceAt(to);
            return targetPiece == null || targetPiece.getColor() != piece.getColor();
        }

        if (piece.getType() == PieceType.KING && Math.abs(from.getCol() - to.getCol()) == 2) {
            return isCastlingValid(from, to, chessBoard);
        }
        return false;
    }
    public boolean isCastlingValid(Position kingFrom, Position kingTo, ChessBoard chessBoard) {
        // Xác định hướng nhập thành (trái hoặc phải)
        int direction = kingTo.getCol() > kingFrom.getCol() ? 1 : -1;
        Position rookPosition = new Position(kingFrom.getRow(), direction == 1 ? 7 : 0);
        Piece rook = chessBoard.getPieceAt(rookPosition);
    
        // Kiểm tra nếu xe tồn tại và chưa di chuyển
        if (rook == null || rook.isHasMoved() || rook.getType() != PieceType.ROOK) {
            return false;
        }
    
        // Kiểm tra nếu vua đã di chuyển
        Piece king = chessBoard.getPieceAt(kingFrom);
        if (king == null || king.isHasMoved() || king.getType() != PieceType.KING) {
            return false;
        }
    
        // Kiểm tra các ô giữa vua và xe phải trống
        int currentCol = kingFrom.getCol() + direction;
        while (currentCol != rookPosition.getCol()) {
            if (chessBoard.getPieceAt(new Position(kingFrom.getRow(), currentCol)) != null) {
                return false;
            }
            currentCol += direction;
        }
    
        // Kiểm tra vua không bị chiếu trước, trong, và sau khi nhập thành
        if (chessBoard.isKingInCheck(king.getColor(), kingFrom)) {
            return false; // Vua bị chiếu trước khi nhập thành
        }
        Position middlePosition = new Position(kingFrom.getRow(), kingFrom.getCol() + direction);
        if (chessBoard.isKingInCheck(king.getColor(), middlePosition)) {
            return false; // Vua đi qua ô bị chiếu
        }
        if (chessBoard.isKingInCheck(king.getColor(), kingTo)) {
            return false; // Vua bị chiếu sau khi nhập thành
        }
    
        return true;
    }
}
                                     