using UnityEngine;
using System.Collections;
using ChessGame.Data;
using ChessGame.UI;

namespace ChessGame.Managers
{
    public class BoardManager : MonoBehaviour
    {
        [Header("References")]
        public Transform boardOrigin;

        [Header("Prefabs")]
        public GameObject pawnPrefab;
        public GameObject rookPrefab;
        public GameObject knightPrefab;
        public GameObject bishopPrefab;
        public GameObject queenPrefab;
        public GameObject kingPrefab;

        [Header("Materials")]
        public Material whiteMaterial;
        public Material blackMaterial;

        [Header("Settings")]
        public float squareSize = 1.3f;
        public float offsetY = 0.1f;
        public float moveDuration = 0.25f;

        private GameObject[,] piecesOnBoard = new GameObject[8, 8];

        // =============================
        // RENDER BOARD
        // =============================
        public void RenderBoard(GameState gameState)
        {
            ClearBoard();

            if (gameState == null || gameState.chessBoard == null || gameState.chessBoard.allPieces == null)
                return;

            foreach (var piece in gameState.chessBoard.allPieces)
            {
                if (piece == null) continue;

                int row = piece.row;
                int col = piece.col;

                GameObject prefab = GetPrefab(piece.type);
                if (prefab == null) continue;

                GameObject newPiece = Instantiate(prefab, transform);
                newPiece.transform.position = RowColToWorld(row, col);
                ApplyMaterial(newPiece, piece.color);
                newPiece.name = $"{piece.color}_{piece.type}_{piece.id}_{row}_{col}";
                piecesOnBoard[row, col] = newPiece;
            }
        }

        private void ClearBoard()
        {
            for (int r = 0; r < 8; r++)
            {
                for (int c = 0; c < 8; c++)
                {
                    if (piecesOnBoard[r, c] != null)
                    {
                        Destroy(piecesOnBoard[r, c]);
                        piecesOnBoard[r, c] = null;
                    }
                }
            }
        }

        // =============================
        // POSITION CONVERSIONS
        // =============================

        /// <summary>
        /// Chuyển từ tọa độ hàng – cột (0–7) sang vị trí 3D trên bàn cờ.
        /// </summary>
        private Vector3 RowColToWorld(int row, int col)
        {
            float halfBoard = 8 * squareSize / 2f;
            float x = (col * squareSize) - halfBoard + (squareSize / 2f);
            float z = (row * squareSize) - halfBoard + (squareSize / 2f);

            return boardOrigin.position + new Vector3(x, offsetY, z);
        }

        /// <summary>
        /// Chuyển từ ký hiệu ô (VD: "E2") sang vị trí thế giới (Vector3).
        /// </summary>
        public Vector3 SquareToWorld(string square)
        {
            int col = SquareToCol(square);
            int row = SquareToRow(square);
            return RowColToWorld(row, col);
        }

        /// <summary>
        /// Tính toán tên ô từ tọa độ thế giới (VD: Vector3 -> "E2").
        /// </summary>
        public string WorldToSquare(Vector3 worldPos)
        {
            Vector3 origin = boardOrigin.position;
            float tileSize = squareSize;

            int col = Mathf.FloorToInt((worldPos.x - origin.x + (4 * tileSize)) / tileSize);
            int row = Mathf.FloorToInt((worldPos.z - origin.z + (4 * tileSize)) / tileSize);

            col = Mathf.Clamp(col, 0, 7);
            row = Mathf.Clamp(row, 0, 7);

            char file = (char)('A' + col);
            int rank = row + 1;

            return $"{file}{rank}";
        }

        /// <summary>
        /// Chuyển từ ký hiệu ô (VD: "E2") sang chỉ số hàng (0–7)
        /// </summary>
        public int SquareToRow(string square)
        {
            if (string.IsNullOrEmpty(square) || square.Length < 2) return -1;
            char rank = square[1];
            return Mathf.Clamp(rank - '1', 0, 7);
        }

        /// <summary>
        /// Chuyển từ ký hiệu ô (VD: "E2") sang chỉ số cột (0–7)
        /// </summary>
        public int SquareToCol(string square)
        {
            if (string.IsNullOrEmpty(square) || square.Length < 2) return -1;
            char file = char.ToUpper(square[0]);
            return Mathf.Clamp(file - 'A', 0, 7);
        }

        // =============================
        // MATERIAL & PREFAB HELPERS
        // =============================

        private void ApplyMaterial(GameObject pieceObj, string color)
        {
            Material mat = (color.ToUpper() == "WHITE") ? whiteMaterial : blackMaterial;
            var renderers = pieceObj.GetComponentsInChildren<Renderer>();
            foreach (var r in renderers)
                r.material = mat;
        }

        private GameObject GetPrefab(string type)
        {
            switch (type.ToUpper())
            {
                case "PAWN": return pawnPrefab;
                case "ROOK": return rookPrefab;
                case "KNIGHT": return knightPrefab;
                case "BISHOP": return bishopPrefab;
                case "QUEEN": return queenPrefab;
                case "KING": return kingPrefab;
                default: return null;
            }
        }

        // =============================
        // GET PIECES
        // =============================

        public GameObject GetPieceAt(int row, int col)
        {
            if (row < 0 || row >= 8 || col < 0 || col >= 8)
                return null;
            return piecesOnBoard[row, col];
        }

        public GameObject GetPieceAtSquare(string square)
        {
            int col = SquareToCol(square);
            int row = SquareToRow(square);
            return GetPieceAt(row, col);
        }

        // =============================
        // MOVE PIECE (SMOOTH + SAFE)
        // =============================
        public void MovePiece(string fromSquare, string toSquare)
        {
            int fromCol = SquareToCol(fromSquare);
            int fromRow = SquareToRow(fromSquare);

            int toCol = SquareToCol(toSquare);
            int toRow = SquareToRow(toSquare);

            GameObject movingPiece = GetPieceAt(fromRow, fromCol);
            if (movingPiece == null)
            {
                Debug.LogWarning($"Không tìm thấy quân ở {fromSquare}");
                return;
            }

            // Nếu có quân ở đích → ăn
            GameObject targetPiece = GetPieceAt(toRow, toCol);
            if (targetPiece != null && targetPiece != movingPiece)
            {
                Destroy(targetPiece);
                piecesOnBoard[toRow, toCol] = null;
            }

            // Cập nhật mảng
            piecesOnBoard[fromRow, fromCol] = null;
            piecesOnBoard[toRow, toCol] = movingPiece;

            SoundManager.Instance.PlayMoveSound();
            // Di chuyển mượt
            Vector3 targetPos = RowColToWorld(toRow, toCol);
            StopAllCoroutines();
            StartCoroutine(SafeSmoothMove(movingPiece, targetPos, moveDuration));

            Debug.Log($"Di chuyển {movingPiece.name}: {fromSquare} → {toSquare}");
        }

        public IEnumerator SafeSmoothMove(GameObject piece, Vector3 targetPos, float duration)
        {
            if (piece == null) yield break;

            Vector3 startPos = piece.transform.position;
            float t = 0;

            while (t < duration)
            {
                if (piece == null) yield break;
                piece.transform.position = Vector3.Lerp(startPos, targetPos, t / duration);
                t += Time.deltaTime;
                yield return null;
            }

            if (piece != null)
                piece.transform.position = targetPos;
        }
    }
}
