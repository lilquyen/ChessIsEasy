using System;

namespace ChessGame.Data
{
    [Serializable]
    public class ChessBoard
    {
        public PieceData[] allPieces;
        public string[] matrixBoard;
    }

    [Serializable]
    public class GameState
    {
        public ChessBoard chessBoard;
        public string currentTurn;
        public bool inCheck;
        public bool checkmate;
        public bool stalemate;
        public string message;
    }
}
