package com.chess.chess_sever.service;

import com.chess.chess_sever.DataTransferObject.GameState;
import com.chess.chess_sever.DataTransferObject.PieceInfo;
import com.chess.chess_sever.models.ChessBoard;
import com.chess.chess_sever.models.PieceColor;

import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class GameService {
    private ChessBoard chessBoard;
    private GameState currentGameState;

    public void setupBoard(ChessBoard chessBoard) {
        chessBoard.initializeBoard();
    }

    public GameState initGame() {
        this.chessBoard = new ChessBoard(); // Gán cho biến toàn cục
        setupBoard(this.chessBoard);
        List<PieceInfo> pieces = this.chessBoard.getAllPieces();
        GameState gameStateInit = new GameState(this.chessBoard, pieces, PieceColor.WHITE, false, false, false, false, "Game started");
        this.currentGameState = gameStateInit;
        return gameStateInit;
    }

    public GameState getCurrentGameState() {
        if (currentGameState == null) {
            throw new IllegalStateException("Game state is not initialized. Please start the game first.");
        }
        return currentGameState;
    }

    public void updateGameState(GameState gameState) {
        this.currentGameState = gameState;
    }
}