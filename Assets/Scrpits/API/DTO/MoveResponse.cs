using System;
using ChessGame.Data;

namespace ChessGame.API.DTO
{
    [Serializable]
    public class MoveResponse
    {
        public bool success;
        public string message;
        public GameState gameState;
        public bool castling;
        public bool enPassant;
        public bool promotion;
        public string promotionPiece;
        public Move[] possibleMoves;
    }
}
