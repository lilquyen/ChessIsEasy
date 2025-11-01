using UnityEngine;
using ChessGame.Managers;

public class HighlightSquare : MonoBehaviour
{
    public string targetSquare; // ví dụ "E4"
    private PieceSelector pieceSelector;

    private void Start()
    {
        pieceSelector = FindObjectOfType<PieceSelector>();
    }

    public void Setup(string squareName)
    {
        targetSquare = squareName;
    }

    private void OnMouseDown()
    {
        Debug.Log($"[HighlightSquare] Click vào ô highlight {targetSquare}");
        pieceSelector?.OnHighlightClicked(targetSquare);
    }
}
