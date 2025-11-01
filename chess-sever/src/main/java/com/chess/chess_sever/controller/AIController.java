package com.chess.chess_sever.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.chess.chess_sever.DataTransferObject.GameState;
import com.chess.chess_sever.DataTransferObject.MoveResponse;
import com.chess.chess_sever.models.PieceColor;
import com.chess.chess_sever.service.AIService;
import com.chess.chess_sever.service.GameService;
import com.chess.chess_sever.service.MoveRequest;
import com.chess.chess_sever.service.Request;

@RestController
@RequestMapping("/api/ai-chess")
public class AIController {

    @Autowired
    private AIService aiService;

    @Autowired
    private GameService gameService;



    @GetMapping("start")
    public MoveResponse startGame() {
        GameState gameState = gameService.initGame();
        List<MoveRequest> possibleMove = gameState.getAllPossibleMoves(gameState, gameState.getCurrentTurn());
        return new MoveResponse(true, "Game started", gameState, false, false, false, null, possibleMove);
    }

    @PostMapping("next-move")
    public MoveResponse getNextMove(@RequestBody Request request) {

        MoveRequest moveRequest = new MoveRequest(request.getFrom(), request.getTo());
        String promotionChoice = null;
        if(request.getPromotionChoice() != null) {
            promotionChoice = request.getPromotionChoice();
        }
        GameState gameState = gameService.getCurrentGameState();
        if (gameState == null) {
            return new MoveResponse(false, "Game state is not initialized. Please start the game first.", null, false, false, false, null, null);
        }

        List<MoveRequest> possibleMoves = gameState.getAllPossibleMoves(gameState, gameState.getCurrentTurn());
        boolean isValidMove = false;
        for (MoveRequest move : possibleMoves) {
            if (move.equals(moveRequest)) {
                isValidMove = true;
                break;
            }
        }
        if (!isValidMove) {
            return new MoveResponse(false, "Invalid player move", gameState, false, false, false, null, null);
        }

        gameState.makeMove(moveRequest, promotionChoice);
        gameService.updateGameState(gameState);

        boolean whiteKingExists = gameState.doesKingExist(PieceColor.WHITE);
        boolean blackKingExists = gameState.doesKingExist(PieceColor.BLACK);

        if (!whiteKingExists) {
            return new MoveResponse(true, "BlackWins", gameState, false, false, false, null, null);
        }
        if (!blackKingExists) {
            return new MoveResponse(true, "WhiteWins", gameState, false, false, false, null, null);
        }

        // Xử lý nước đi của AI
        MoveRequest aiMoveRequest = aiService.getNextMove(gameState);
        if (aiMoveRequest == null) {
            if(gameState.isKingInCheck(PieceColor.BLACK)) {
                return new MoveResponse(true, "WhiteWins", gameState, false, false, false, null, null);
            }
            return new MoveResponse(true, "Draw", gameState, false, false, false, null, null);
        }
        gameState.makeMove(aiMoveRequest, "QUEEN");

        // Cập nhật trạng thái trò chơi sau nước đi của AI
        gameState.setIncheck(gameState.isKingInCheck(gameState.getCurrentTurn()));
        gameState.setCheckmate(gameState.isCheckMate(gameState.getCurrentTurn()));
        gameState.setStalemate(gameState.isStalemate(gameState.getCurrentTurn()));

        // Lấy danh sách các nước đi hợp lệ sau khi AI thực hiện nước đi
        possibleMoves = gameState.getAllPossibleMoves(gameState, gameState.getCurrentTurn());
        if(possibleMoves.isEmpty()) {
            if(gameState.isKingInCheck(gameState.getCurrentTurn())) {
                if(gameState.getCurrentTurn() == PieceColor.WHITE) {
                    return new MoveResponse(true, "BlackWins", gameState, false, false, false, null, null);
                } else {
                    return new MoveResponse(true, "WhiteWins", gameState, false, false, false, null, null);
                }
            } else {
                return new MoveResponse(true, "Draw", gameState, false, false, false, null, null);
            }
        }
        gameService.updateGameState(gameState);

        // Tạo phản hồi
        return new MoveResponse(
            true,
            // "AI move executed: " + aiMoveRequest.getFrom() + " to " + aiMoveRequest.getTo(),
            gameState.getMessage(),
            gameState,  
            true,
            true,
            true,
            "",
            possibleMoves
        );
    }
}
