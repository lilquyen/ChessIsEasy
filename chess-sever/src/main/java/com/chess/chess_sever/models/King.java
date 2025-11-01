package com.chess.chess_sever.models;

import java.util.ArrayList;
import java.util.List;

public class King  extends Piece{

    public King (PieceType pieceType, PieceColor color, Position position, String id)
    {
        super(pieceType, color, position, id);                     // Gọi con structor của cha
    }

    @Override
    public Piece copy() {
        return new King(PieceType.KING, this.color, new Position(this.position.getRow(), this.position.getCol()), this.id);
    }

    @Override
    public List<Position> getAvailableMoves(ChessBoard chessBoard) {
        List<Position> moves = new ArrayList<>();
        int row = position.getRow();
        int col = position.getCol();
        KingMoveValidator validator = new KingMoveValidator();

        int[] dir_row = {0, 1, 1, 1, 0, -1, -1, -1};
        int[] dir_col = {1, 1, 0, -1, -1, -1, 0, 1};

        for (int i = 0; i < 8; i++) {
            int new_row = row + dir_row[i];
            int new_col = col + dir_col[i];
            if (new_row >= 0 && new_row < 8 && new_col >= 0 && new_col < 8) {
                Position to = new Position(new_row, new_col);
                boolean isValidMove = validator.isValidMove(this, position, to, chessBoard);
                if(isValidMove) {
                    moves.add(to);
                } else {
                    continue; // Không thể đi tiếp trong hướng này
                }
            }
        }
        Position kingsideTarget = new Position(position.getRow(), position.getCol() + 2);
        Position queensideTarget = new Position(position.getRow(), position.getCol() - 2);

        if (validator.isCastlingValid(position, kingsideTarget, chessBoard)) {
            moves.add(kingsideTarget);
        }
        if (validator.isCastlingValid(position, queensideTarget, chessBoard)) {
            moves.add(queensideTarget);
        }
        return moves;
    }
}
