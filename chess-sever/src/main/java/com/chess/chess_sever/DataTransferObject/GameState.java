package com.chess.chess_sever.DataTransferObject;

import java.util.List;
import java.util.stream.Collectors;

import lombok.Getter;

import com.chess.chess_sever.models.Bishop;
import com.chess.chess_sever.models.ChessBoard;
import com.chess.chess_sever.models.Knight;
import com.chess.chess_sever.models.Piece;
import com.chess.chess_sever.models.PieceColor;
import com.chess.chess_sever.models.PieceType;
import com.chess.chess_sever.models.Position;
import com.chess.chess_sever.models.Queen;
import com.chess.chess_sever.models.Rook;
import com.chess.chess_sever.service.MoveRequest;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Getter
public class GameState {
    public ChessBoard chessBoard;

    @JsonIgnore
    private List<PieceInfo> pieces;
    private PieceColor currentTurn;

    private boolean inCheck;
    private boolean checkmate;
    private boolean stalemate;
    // private boolean draw;
    private boolean isSimulating = false;

    private String message;

    public GameState() {}

    public GameState(ChessBoard chessBoard,
                     List<PieceInfo> pieces,
                     PieceColor currentTurn,
                     boolean inCheck,
                     boolean checkmate,
                     boolean stalemate,
                     boolean draw,
                     String message) {
        this.chessBoard = chessBoard;
        this.pieces = pieces;
        this.currentTurn = currentTurn;
        this.inCheck = inCheck;
        this.checkmate = checkmate;
        this.stalemate = stalemate;
        // this.draw = draw;
        this.message = message;
    }

    public void newGame() {
        this.chessBoard = new ChessBoard().initializeBoard();
        this.pieces = 
            java.util.Arrays.stream(this.chessBoard.getBoard())
                            .flatMap(java.util.Arrays::stream)
                            .filter(piece -> piece != null)
                            .map(PieceInfo::new)
                            .collect(Collectors.toList());
        this.currentTurn = PieceColor.WHITE;
        this.inCheck = false;
        this.checkmate = false;
        this.stalemate = false;
        // this.draw = false;
        this.message = "New game started. White's turn.";
    }

    public void setIncheck(boolean inCheck) {
        this.inCheck = inCheck;
    }   
    public void setCheckmate(boolean checkmate) {
        this.checkmate = checkmate;
    }
    public void setStalemate(boolean stalemate) {
        this.stalemate = stalemate;
    }
    // public void setDraw(boolean draw) {
    //     this.draw = draw;
    // }

    public boolean isKingInCheck(PieceColor color) {
        // Tìm vị trí của vua
        Position kingPosition = null;
        for (PieceInfo pieceInfo : pieces) {
            if (pieceInfo.getType().equals("KING") && PieceColor.valueOf(pieceInfo.getColor()) == color) {
                kingPosition = new Position(pieceInfo.getRow(), pieceInfo.getCol());
                break;
            }
        }
        if (kingPosition == null) return false; // Vua không tồn tại trên bàn cờ

        // Kiểm tra tất cả quân cờ đối phương có thể di chuyển đến vị trí vua không
        for (PieceInfo pieceInfo : pieces) {
            if (PieceColor.valueOf(pieceInfo.getColor()) != color) {
                Piece opponentPiece = chessBoard.getPieceAt(new Position(pieceInfo.getRow(), pieceInfo.getCol()));
                if (opponentPiece != null) {
                    List<Position> opponentMoves = opponentPiece.getAvailableMoves(chessBoard);
                    for (Position pos : opponentMoves) {
                        if (pos.equals(kingPosition)) {
                            return true; // Vua bị chiếu
                        }
                    }
                }
            }
        }
        return false; // Vua không bị chiếu
    }

    public boolean isCheckMate(PieceColor currentTurn) {
        List<MoveRequest> possibleMoves = getAllPossibleMoves(this, currentTurn);
        if (possibleMoves.isEmpty() && isKingInCheck(currentTurn)) {
            return true; // Chiếu hết
        }
        return false;
    }

    public boolean isStalemate (PieceColor color) {
        List<MoveRequest> possibleMoves = getAllPossibleMoves(this, color);
        if (possibleMoves.isEmpty() && !isKingInCheck(color)) {
            return true; // Hòa do bế tắc
        }
        return false;
    }

    boolean isDraw() {
        // Kiểm tra điều kiện hòa do thiếu vật chất
        int whitePieces = 0;
        int blackPieces = 0;
        for (PieceInfo pieceInfo : pieces) {
            if (!pieceInfo.getType().equals("KING")) {
                if (PieceColor.valueOf(pieceInfo.getColor()) == PieceColor.WHITE) {
                    whitePieces++;
                } else {
                    blackPieces++;
                }
            }
        }
        if (whitePieces == 0 && blackPieces == 0) {
            return true; // Chỉ còn hai vua trên bàn cờ
        }
        if (whitePieces <= 1 && blackPieces <= 1) {
            return true; // Thiếu vật chất để chiếu hết
        }
        return false;
    }
    
    public GameState simulateMove(Position from, Position to) {
        // Tạo bản sao của bàn cờ hiện tại
        ChessBoard simulatedBoard = new ChessBoard();
        Piece[][] boardCopy = new Piece[8][8];
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                if (this.chessBoard.getBoard()[r][c] != null) {
                    boardCopy[r][c] = this.chessBoard.getBoard()[r][c].copy(); // Sử dụng clone để sao chép quân cờ
                } else {
                    boardCopy[r][c] = null;
                }
            }
        }
        simulatedBoard.setBoard(boardCopy);
    
        // Thực hiện nước đi trên bản sao
        Piece piece = simulatedBoard.getPieceAt(from);
        if (piece != null) {
            simulatedBoard.movePiece(from, to);
    
            // Cập nhật trạng thái của quân cờ (hasMoved)
            piece.setPosition(to); // Cập nhật vị trí mới
            piece.setHasMoved(true); // Đánh dấu quân cờ đã di chuyển
        }
    
        // Tạo danh sách các quân cờ mới dựa trên bàn cờ đã mô phỏng
        List<PieceInfo> piecesCopy = 
            java.util.Arrays.stream(simulatedBoard.getBoard())
                            .flatMap(java.util.Arrays::stream)
                            .filter(p -> p != null)
                            .map(PieceInfo::new) // Chuyển đổi Piece thành PieceInfo
                            .collect(Collectors.toList());
    
        // Tạo trạng thái trò chơi mới
        GameState simulatedGameState = new GameState(simulatedBoard, piecesCopy, 
            (this.currentTurn == PieceColor.WHITE) ? PieceColor.BLACK : PieceColor.WHITE, 
            false, false, false, false, "");
    
        // Cập nhật trạng thái vua bị chiếu
        simulatedGameState.setIncheck(simulatedGameState.isKingInCheck(this.currentTurn));
        return simulatedGameState;
    }


    public List<MoveRequest> getAllPossibleMoves(GameState gameState, PieceColor color) { 
        List<MoveRequest> possibleMoves = gameState.getPieces().stream() 
            .filter(pieceInfo -> PieceColor.valueOf(pieceInfo.getColor()).equals(color)) 
            .flatMap(pieceInfo -> { 
                Piece piece = gameState.chessBoard.getPieceAt(new Position(pieceInfo.getRow(), pieceInfo.getCol())); 
                return piece.getAvailableMoves(gameState.chessBoard).stream() 
                    .map(pos -> new MoveRequest( 
                        new Position(pieceInfo.getRow(), pieceInfo.getCol()).toStringPosition(), 
                        pos.toStringPosition() 
                    )); 
            }) 
            // Lọc các nước đi gây chiếu tướng
            .filter(moveRequest -> {
                Position from = new Position().getPositionFromString(moveRequest.getFrom());
                Position to = new Position().getPositionFromString(moveRequest.getTo());
                GameState simulatedState = gameState.simulateMove(from, to);
                return !simulatedState.isKingInCheck(color); // Chỉ giữ các nước đi không làm vua bị chiếu
            })
            .collect(Collectors.toList()); 
        return possibleMoves; 
    }

    public void makeMove(MoveRequest moveRequest, String promotionChoice) {
        Position from = new Position().getPositionFromString(moveRequest.getFrom());
        Position to = new Position().getPositionFromString(moveRequest.getTo());
        Piece piece = chessBoard.getPieceAt(from);
        if (piece != null && piece.canMove(from, to, chessBoard)) {
            if (piece.getType() == PieceType.KING && Math.abs(from.getCol() - to.getCol()) == 2) {
                chessBoard.performCastling(from, to);
                this.message = "Castling performed from " + moveRequest.getFrom() + " to " + moveRequest.getTo() + ".";
            } 
            else if (piece.getType() == PieceType.PAWN && (to.getRow() == 0 || to.getRow() == 7)) {
                PieceType promotionType = PieceType.valueOf(promotionChoice);
                chessBoard.movePiece(from, to);
                Piece promotedPiece = createPromotedPiece(promotionType, piece.getColor(), to);
                chessBoard.setPieceAt(to, promotedPiece);
                this.message = "Pawn promoted to " + promotionChoice + " at " + moveRequest.getTo() + ".";
            }
            else if (piece.getType() == PieceType.PAWN) {
                // Kiểm tra nước đi "en passant"
                if (to.equals(chessBoard.getEnPassantTarget())) {
                    Position capturedPawnPosition = new Position(from.getRow(), to.getCol());
                    chessBoard.setPieceAt(capturedPawnPosition, null); // Xóa quân tốt bị bắt
                    this.message = "En passant move performed from " + moveRequest.getFrom() + " to " + moveRequest.getTo() + ".";
                }

                // Kiểm tra nếu quân tốt di chuyển 2 ô (để thiết lập trạng thái "en passant")
                if (Math.abs(from.getRow() - to.getRow()) == 2) {
                    Position enPassantTarget = new Position((from.getRow() + to.getRow()) / 2, from.getCol());
                    chessBoard.setEnPassantTarget(enPassantTarget);
                } else {
                    chessBoard.setEnPassantTarget(null); // Xóa trạng thái "en passant" nếu không hợp lệ
                }

                // Di chuyển quân tốt
                chessBoard.movePiece(from, to);
            } 
            else {
                chessBoard.movePiece(from, to);
                this.message = "Move made from " + moveRequest.getFrom() + " to " + moveRequest.getTo() + ".";
            }
            if (!(piece.getType() == PieceType.PAWN && Math.abs(from.getRow() - to.getRow()) == 2)) {
                chessBoard.setEnPassantTarget(null); // Xóa trạng thái "en passant" nếu không hợp lệ
            }
    
            // Cập nhật danh sách quân cờ
            this.pieces = 
                java.util.Arrays.stream(this.chessBoard.getBoard())
                                .flatMap(java.util.Arrays::stream)
                                .filter(p -> p != null)
                                .map(PieceInfo::new)
                                .collect(Collectors.toList());
    
            // Chuyển lượt
            this.currentTurn = (this.currentTurn == PieceColor.WHITE) ? PieceColor.BLACK : PieceColor.WHITE;
            this.message += " " + this.currentTurn + "'s turn.";
        } else {
            this.message = "Invalid move from " + moveRequest.getFrom() + " to " + moveRequest.getTo() + ".";
        }
    }
    
    public Piece createPromotedPiece(PieceType promotionType, PieceColor color, Position position) {
        switch (promotionType) {
            case QUEEN:
                return new Queen(PieceType.QUEEN, color, position, "promoted");
            case ROOK:
                return new Rook(PieceType.ROOK, color, position, "promoted");
            case BISHOP:
                return new Bishop(PieceType.BISHOP, color, position, "promoted");
            case KNIGHT:
                return new Knight(PieceType.KNIGHT, color, position, "promoted");
            default:
                throw new IllegalArgumentException("Invalid promotion type: " + promotionType);
        }
    }

    public PieceInfo getPieceAt(Position position) {
        for (PieceInfo piece : pieces) {
            if (piece.getRow() == position.getRow() && piece.getCol() == position.getCol()) {
                return piece;
            }
        }
        return null;
    }

    @JsonIgnore
    public Piece[][] getBoardFromGameState() {
        return this.chessBoard.getBoard();
    }

    public boolean doesKingExist(PieceColor color) {
        for (PieceInfo pieceInfo : pieces) {
            if (pieceInfo.getType().equals("KING") && PieceColor.valueOf(pieceInfo.getColor()) == color) {
                return true;
            }
        }
        return false;
    }
}