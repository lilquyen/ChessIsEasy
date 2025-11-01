using System;

namespace ChessGame.Data
{
    [Serializable]
    public class MoveRequest
    {
        public string from;
        public string to;
    }

    [Serializable]
    public class Move
    {
        public string from;
        public string to;
    }
}
