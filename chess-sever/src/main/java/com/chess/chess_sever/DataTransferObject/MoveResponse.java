package com.chess.chess_sever.DataTransferObject;

import java.util.List;

import com.chess.chess_sever.service.MoveRequest;

public class MoveResponse {
    private boolean success;
    private String message; 
    private GameState gameState;
    private boolean castling;
    private boolean enPassant;
    private boolean promotion;
    private String promotionPiece;
    private List<MoveRequest> possibleMoves;

    public MoveResponse(boolean success, 
                    String message, 
                    GameState gameState, 
                    boolean castling, 
                    boolean enPassant, 
                    boolean promotion, 
                    String promotionPiece, 
                    List<MoveRequest> possibleMoves) { 
    this.success = success;
    this.message = message;
    this.gameState = gameState;
    this.castling = castling;
    this.enPassant = enPassant;
    this.promotion = promotion;
    this.promotionPiece = promotionPiece;
    this.possibleMoves = possibleMoves;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public GameState getGameState() {
        return gameState;
    }

    public void setGameState(GameState gameState) {
        this.gameState = gameState;
    }

    public boolean isCastling() {
        return castling;
    }

    public void setCastling(boolean castling) {
        this.castling = castling;
    }

    public boolean isEnPassant() {
        return enPassant;
    }

    public void setEnPassant(boolean enPassant) {
        this.enPassant = enPassant;
    }

    public boolean isPromotion() {
        return promotion;
    }

    public void setPromotion(boolean promotion) {
        this.promotion = promotion;
    }

    public String getPromotionPiece() {
        return promotionPiece;
    }

    public void setPromotionPiece(String promotionPiece) {
        this.promotionPiece = promotionPiece;
    }

    public List<MoveRequest> getPossibleMoves() {
        return possibleMoves;
    }
    public void setPossibleMoves(List<MoveRequest> possibleMoves) {
        this.possibleMoves = possibleMoves;
    }
}