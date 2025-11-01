package com.chess.chess_sever.models;

import java.util.ArrayList;
import java.util.List;

import com.chess.chess_sever.DataTransferObject.PieceInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties({"board"})
public class ChessBoard {
    @JsonIgnore
    private Piece[][] board;
    private Position enPassantTarget;

    public ChessBoard() {
        this.board = new Piece[8][8];
    }
    
    public Position getEnPassantTarget() {
        return enPassantTarget;
    }
    public void setEnPassantTarget(Position enPassantTarget) {
        this.enPassantTarget = enPassantTarget;
    }

    public Piece[][] getBoard() {
        return board;
    }

    public void setBoard(Piece[][] board) {
        this.board = board;
    }

    public ChessBoard initializeBoard() {
        ChessBoard chessBoard = new ChessBoard();
        for (int i = 0; i < 8; i++) {
            String id_pawn = "W_P" + String.valueOf(i + 1);
            String id_bishop = "W_B" + String.valueOf(i + 1);
            String id_king = "W_K" + String.valueOf(i + 1);
            String id_knight = "W_N" + String.valueOf(i + 1);
            String id_queen = "W_Q" + String.valueOf(i + 1);
            String id_rook = "W_R" + String.valueOf(i + 1);
            board[1][i] = new Pawn(PieceType.PAWN, PieceColor.WHITE, new Position(1, i), id_pawn);
            if (i == 0 || i == 7) board[0][i] = new Rook(PieceType.ROOK, PieceColor.WHITE, new Position(0, i), id_rook);
            else if (i == 1 || i == 6) board[0][i] = new Knight(PieceType.KNIGHT, PieceColor.WHITE, new Position(0, i), id_knight);
            else if (i == 2 || i == 5) board[0][i] = new Bishop(PieceType.BISHOP, PieceColor.WHITE, new Position(0, i), id_bishop);
            else if (i == 3) board[0][i] = new Queen(PieceType.QUEEN, PieceColor.WHITE, new Position(0, i), id_queen);
            else board[0][i] = new King(PieceType.KING, PieceColor.WHITE, new Position(0, i), id_king);
        }

        for (int i = 0; i < 8; i++) {
            String id_pawn = "B_P" + String.valueOf(i + 1);
            String id_bishop = "B_B" + String.valueOf(i + 1);
            String id_king = "B_K" + String.valueOf(i + 1);
            String id_knight = "B_N" + String.valueOf(i + 1);
            String id_queen = "B_Q" + String.valueOf(i + 1);
            String id_rook = "B_R" + String.valueOf(i + 1);
            board[6][i] = new Pawn(PieceType.PAWN, PieceColor.BLACK, new Position(6, i), id_pawn);
            if (i == 0 || i == 7) board[7][i] = new Rook(PieceType.ROOK, PieceColor.BLACK, new Position(7, i), id_rook);
            else if (i == 1 || i == 6) board[7][i] = new Knight(PieceType.KNIGHT, PieceColor.BLACK, new Position(7, i), id_knight);
            else if (i == 2 || i == 5) board[7][i] = new Bishop(PieceType.BISHOP, PieceColor.BLACK, new Position(7, i), id_bishop);
            else if (i == 3) board[7][i] = new Queen(PieceType.QUEEN, PieceColor.BLACK, new Position(7, i), id_queen);
            else board[7][i] = new King(PieceType.KING, PieceColor.BLACK, new Position(7, i), id_king);
        }
        return chessBoard;
    }

    public void printBoard() {
        for (int r = 7; r >= 0; r--) {
            for (int c = 0; c < 8; c++) {
                Piece p = board[r][c];
                if (p == null) System.out.print(". ");
                else {
                    char symbol = p.getClass().getSimpleName().charAt(0);
                    System.out.print((p.getColor() == PieceColor.WHITE ? Character.toUpperCase(symbol) : Character.toLowerCase(symbol)) + " ");
                }
            }
            System.out.println();
        }
    }

    public Piece getPieceAt(Position from) {
        if (from == null) return null;
        return board[from.getRow()][from.getCol()];
    }

    public List<PieceInfo> getAllPieces() {
        List<PieceInfo> pieces = new ArrayList<>();
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                Piece p = board[r][c];
                if (p != null) {
                    pieces.add(new PieceInfo(p));
                }
            }
        }
        return pieces;
    }

    public void movePiece(Position from, Position to) {
        Piece piece = getPieceAt(from);
        if (piece != null) {
            board[to.getRow()][to.getCol()] = piece;
            board[from.getRow()][from.getCol()] = null;
            piece.setPosition(to);
            piece.setHasMoved(true);
        }
    }
    public void performCastling(Position kingFrom, Position kingTo) {
        int direction = kingTo.getCol() > kingFrom.getCol() ? 1 : -1;
        Position rookFrom = new Position(kingFrom.getRow(), direction == 1 ? 7 : 0);
        Position rookTo = new Position(kingFrom.getRow(), kingFrom.getCol() + direction);
    
        // Di chuyển vua
        movePiece(kingFrom, kingTo);
    
        // Di chuyển xe
        movePiece(rookFrom, rookTo);
    }

    public char[][] getMatrixBoard() {
        char[][] matrix = new char[8][8];
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                Piece p = board[r][c];
                if (p == null) {
                    matrix[r][c] = '.';
                } else {
                    char symbol;
                    if (p.getClass().getSimpleName().equals("Knight")) {
                        symbol = 'n'; 
                    } else {
                        symbol = p.getClass().getSimpleName().charAt(0);
                    }
                    matrix[r][c] = (p.getColor() == PieceColor.WHITE ? Character.toUpperCase(symbol) : Character.toLowerCase(symbol));
                }
            }
        }
        return matrix;
    }
    public boolean isKingInCheck(PieceColor color, Position kingPosition) {
        for (Piece[] row : board) {
            for (Piece piece : row) {
                if (piece != null && piece.getColor() != color) {
    
                    // ❗ Bỏ qua các quân vua đối phương để tránh vòng lặp đệ quy
                    if (piece instanceof King) continue;
    
                    List<Position> moves = piece.getAvailableMoves(this);
                    if (moves.contains(kingPosition)) {
                        return true; // Vua bị chiếu
                    }
                }
            }
        }
        return false; // Vua không bị chiếu
    }

    public void setPieceAt(Position to, Piece promotedPiece) {
        board[to.getRow()][to.getCol()] = promotedPiece;
    }
}
