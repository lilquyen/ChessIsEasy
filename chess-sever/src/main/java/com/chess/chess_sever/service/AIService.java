package com.chess.chess_sever.service;

import java.util.*;
import org.springframework.stereotype.Service;
import com.chess.chess_sever.DataTransferObject.GameState;
import com.chess.chess_sever.DataTransferObject.PieceInfo;
import com.chess.chess_sever.models.PieceColor;
import com.chess.chess_sever.models.Position;

@Service
public class AIService {

    private static final int PAWN = 100;
    private static final int KNIGHT = 300;
    private static final int BISHOP = 320;
    private static final int ROOK = 500;
    private static final int QUEEN = 900;
    private static final int KING = 10000;

    private static final int MAX_DEPTH = 3; 
    private static final int RANDOM_NOISE_RANGE = 10; 
    private static final Random random = new Random();

    public MoveRequest getNextMove(GameState gameState) {
        List<MoveRequest> possibleMoves = gameState.getAllPossibleMoves(gameState, gameState.getCurrentTurn());
        if (possibleMoves.isEmpty()) return null;

        Collections.shuffle(possibleMoves, random);

        MoveRequest bestMove = null;
        int bestScore = Integer.MIN_VALUE;
        PieceColor aiColor = gameState.getCurrentTurn();

        for (MoveRequest move : possibleMoves) {
            if (move == null || move.getFrom() == null || move.getTo() == null) continue;
            GameState simulated = simulateMoveSafe(gameState, move);
            if (simulated == null) continue;

            int score = minimax(simulated, MAX_DEPTH - 1, false, aiColor,
                    Integer.MIN_VALUE, Integer.MAX_VALUE);

            if (score > bestScore) {
                bestScore = score;
                bestMove = move;
            }
        }

        if (bestMove == null && !possibleMoves.isEmpty()) {
            bestMove = possibleMoves.get(random.nextInt(possibleMoves.size()));
        }

        return bestMove;
    }

    private int minimax(GameState state, int depth, boolean maximizing, PieceColor aiColor, int alpha, int beta) {
        if (depth == 0 || state.isCheckmate() || state.isStalemate(aiColor)) {
            return evaluateBoard(state, aiColor);
        }

        PieceColor current = maximizing ? aiColor : getOpponent(aiColor);
        List<MoveRequest> moves = state.getAllPossibleMoves(state, current);
        if (moves.isEmpty()) {
            return evaluateBoard(state, aiColor);
        }

        Collections.shuffle(moves, random);

        int bestValue = maximizing ? Integer.MIN_VALUE : Integer.MAX_VALUE;

        for (MoveRequest move : moves) {
            GameState simulated = simulateMoveSafe(state, move);
            if (simulated == null) continue;

            int eval = minimax(simulated, depth - 1, !maximizing, aiColor, alpha, beta);

            if (maximizing) {
                bestValue = Math.max(bestValue, eval);
                alpha = Math.max(alpha, eval);
            } else {
                bestValue = Math.min(bestValue, eval);
                beta = Math.min(beta, eval);
            }

            if (beta <= alpha) break;
        }

        return bestValue;
    }

    private int evaluateBoard(GameState state, PieceColor aiColor) {
        int score = 0;

        for (PieceInfo piece : state.getPieces()) {
            int value = getPieceValue(piece.getType());
            PieceColor color = PieceColor.valueOf(piece.getColor());

            if (color == aiColor) {
                score += value;
                score += positionBonus(piece.getRow(), piece.getCol(), piece.getType(), true);
            } else {
                score -= value;
                score -= positionBonus(piece.getRow(), piece.getCol(), piece.getType(), false);
            }
        }

        if (state.isKingInCheck(aiColor)) score -= 500;
        if (state.isKingInCheck(getOpponent(aiColor))) score += 500;

        score += random.nextInt(RANDOM_NOISE_RANGE + 1) - RANDOM_NOISE_RANGE / 2;

        return score;
    }

    private int positionBonus(int row, int col, String type, boolean isAI) {
        int bonus = 0;

        if ((row >= 2 && row <= 5) && (col >= 2 && col <= 5)) {
            bonus += 20;
        }

        switch (type.toUpperCase()) {
            case "KNIGHT":
            case "BISHOP":
                bonus += 10;
                break;
            case "PAWN":
                bonus += isAI ? (7 - row) * 2 : row * 2;
                break;
        }

        return bonus;
    }

    private int getPieceValue(String type) {
        switch (type.toUpperCase()) {
            case "PAWN": return PAWN;
            case "KNIGHT": return KNIGHT;
            case "BISHOP": return BISHOP;
            case "ROOK": return ROOK;
            case "QUEEN": return QUEEN;
            case "KING": return KING;
            default: return 0;
        }
    }

    private GameState simulateMoveSafe(GameState state, MoveRequest move) {
        try {
            Position from = new Position().getPositionFromString(move.getFrom());
            Position to = new Position().getPositionFromString(move.getTo());
            return state.simulateMove(from, to);
        } catch (Exception e) {
            return null;
        }
    }

    private PieceColor getOpponent(PieceColor color) {
        return (color == PieceColor.WHITE) ? PieceColor.BLACK : PieceColor.WHITE;
    }
}
