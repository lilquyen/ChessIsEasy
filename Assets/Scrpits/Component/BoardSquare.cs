using UnityEngine;

public class BoardSquare : MonoBehaviour
{
    public int row;
    public int col;
    public bool isHighlighted;

    public void Setup(int r, int c, bool highlight = false)
    {
        row = r;
        col = c;
        isHighlighted = highlight;
    }
}
