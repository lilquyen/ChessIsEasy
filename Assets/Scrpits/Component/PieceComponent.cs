using UnityEngine;
using ChessGame.Data;

public class PieceComponent : MonoBehaviour
{
    public PieceData pieceData;

    public void Init(PieceData data)
    {
        pieceData = data;
    }
}
