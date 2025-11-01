package com.chess.chess_sever.models;

import java.util.List;

public abstract class Piece {

    protected PieceColor color;
    protected PieceType type;
    protected Position position;
    private boolean hasMoved;
    protected String id;
    private MoveValidator moveValidator;

    public Piece(PieceType pieceType, PieceColor _color, Position _position, String id) {
        this.type = pieceType;
        this.color = _color;
        this.position = _position;
        this.hasMoved = false;
        this.id = id;
        this.moveValidator = MoveValidatorFactory.getMoveValidator(type);
    }

    public String getId() {
        return id;
    }
    public PieceColor getColor() {
        return color;
    }
    public PieceType getType() {
        return type;
    }
    public Position getPosition() {
        return position;
    }
    public boolean isHasMoved() {
        return hasMoved;
    }
    public void setPosition(Position position) {
        this.position = position;
    }
    public void setHasMoved(boolean hasMoved) {
        this.hasMoved = hasMoved;
    }
    
    public Piece copy() {
        Position newPosition = new Position(this.position.getRow(), this.position.getCol());
        switch (this.type) {
            case KING:
                return new King(PieceType.KING, this.color, newPosition, this.id);
            case QUEEN:
                return new Queen(PieceType.QUEEN, this.color, newPosition, this.id);
            case ROOK:
                return new Rook(PieceType.ROOK, this.color, newPosition, this.id);
            case BISHOP:
                return new Bishop(PieceType.BISHOP, this.color, newPosition, this.id);
            case KNIGHT:
                return new Knight(PieceType.KNIGHT, this.color, newPosition, this.id);
            case PAWN:
                return new Pawn(PieceType.PAWN, this.color, newPosition, this.id);
            default:
                throw new IllegalArgumentException("Unknown piece type: " + this.type);
        }
    }

    public boolean canMove(Position from, Position to, ChessBoard chessBoard) {
        return moveValidator.isValidMove(this, from, to, chessBoard);
    }

    public abstract List<Position> getAvailableMoves(ChessBoard chessBoard);
}
