using System;

namespace ChessGame.Data
{
    [Serializable]
    public class PieceData
    {
        public string id;      // "W_P1"
        public string type;    // "PAWN", "ROOK", ...
        public string color;   // "WHITE", "BLACK"
        public int row;        // 0 - 7
        public int col;        // 0 - 7
        public bool hasMoved;
    }
}
