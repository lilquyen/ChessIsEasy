using UnityEngine;
using System.Collections;
using ChessGame.API.DTO;
using ChessGame.Data;
using ChessGame.UI;

namespace ChessGame.Managers
{
    public class MoveResponseLoader : MonoBehaviour
    {
        [Header("References")]
        public APIManager apiManager;
        public PieceSelector pieceSelector;
        public BoardManager boardManager;
        public EndPopupController endPopup;

        private void Start()
        {
            if (apiManager == null)
                apiManager = FindFirstObjectByType<APIManager>();

            if (pieceSelector == null)
                pieceSelector = FindFirstObjectByType<PieceSelector>();

            if (boardManager == null)
                boardManager = FindFirstObjectByType<BoardManager>();

            // Khởi tạo game khi bắt đầu
            StartCoroutine(StartGameFromServer());
        }

        // =============================
        // START GAME
        // =============================
        private IEnumerator StartGameFromServer()
        {
            Debug.Log("[MoveResponseLoader] Gọi API /start để khởi tạo bàn cờ...");

            yield return apiManager.StartGame((MoveResponse response) =>
            {
                if (response == null || !response.success)
                {
                    Debug.LogError("[MoveResponseLoader] Không nhận được phản hồi hợp lệ từ backend!");
                    return;
                }
                Debug.Log($"[Backend Message] {response.message}");
                Debug.Log($"[MoveResponseLoader] Đã load bàn cờ ban đầu ({response.possibleMoves?.Length ?? 0} nước đi).");

                // Render bàn cờ khởi đầu
                if (response.gameState != null)
                    boardManager.RenderBoard(response.gameState);

                // Gửi dữ liệu nước đi hợp lệ cho PieceSelector
                pieceSelector?.SetCurrentResponse(response);
            });
        }

        // =============================
        // GỬI NƯỚC ĐI NGƯỜI CHƠI
        // =============================
        public IEnumerator SendNextMove(MoveDTO move)
        {
            Debug.Log($"[MoveResponseLoader] Gửi nước đi: {move.from} → {move.to}");

            yield return apiManager.NextMove(move, (MoveResponse response) =>
            {
                //if (response == null || !response.success)
                //{
                //    Debug.LogError("[MoveResponseLoader] Lỗi phản hồi từ backend khi gửi nước đi!");
                //    Debug.LogError($"[MoveResponseLoader] Response message: {response.message}");
                //    return;
                //}
                Debug.Log($"[Backend Message] {response.message}");
                Debug.Log($"[MoveResponseLoader] Backend trả về {response.possibleMoves?.Length ?? 0} nước đi hợp lệ mới");

                // Di chuyển quân cờ trên board
                AnimateBoardMove(move.from, move.to);

                // Cập nhật bàn cờ mới từ backend (nếu có)
                if (response.gameState != null)
                    boardManager.RenderBoard(response.gameState);

                // Cập nhật MoveResponse để highlight nước đi tiếp theo
                pieceSelector?.SetCurrentResponse(response);
                if (response.message == "BlackWins" || response.message == "WhiteWins" || response.message == "Draw")
                {
                    Debug.Log("[MoveResponseLoader] Trò chơi kết thúc: " + response.message);
                    HandleGameEnd(response.message);
                }
            });
        }

        // =============================
        // DI CHUYỂN QUÂN TRÊN BÀN CỜ
        // =============================
        private void AnimateBoardMove(string from, string to)
        {
            if (boardManager == null)
            {
                Debug.LogWarning("[MoveResponseLoader] Không có BoardManager được gán!");
                return;
            }

            GameObject movingPiece = boardManager.GetPieceAtSquare(from);
            if (movingPiece == null)
            {
                Debug.LogWarning($"[MoveResponseLoader] Không tìm thấy quân cờ ở {from} để di chuyển!");
                return;
            }

            Vector3 targetPos = boardManager.SquareToWorld(to);

            // Nếu có quân ở ô đích → ăn
            GameObject targetPiece = boardManager.GetPieceAtSquare(to);
            if (targetPiece != null && targetPiece != movingPiece)
            {
                Destroy(targetPiece);
            }

            // Gọi hàm SmoothMove từ BoardManager
            StartCoroutine(SafeMoveCoroutine(movingPiece, targetPos));
        }

        // =============================
        // HÀM XỬ LÝ KHI KẾT THÚC GAME
        // =============================

        private void HandleGameEnd(string message)
        {
            if (endPopup == null)
            {
                Debug.LogWarning("[MoveResponseLoader] Không tìm thấy EndPopupController!");
                return;
            }
            if (string.IsNullOrEmpty(message))
            {
                Debug.LogWarning("[MoveResponseLoader] message rỗng, không xác định được người thắng!");
                return;
            }

            message = message.Trim().ToLower();

            Debug.Log($"[MoveResponseLoader] Trò chơi kết thúc: {message}");

            if (message.Contains("black"))
            {
                endPopup.ShowWinner("Black");
                SoundManager.Instance.PlayLoseSound();
            }
            else if (message.Contains("white"))
            {
                endPopup.ShowWinner("White");
                SoundManager.Instance.PlayWinSound();
            }
            else
            {
                endPopup.ShowWinner("Draw");
                SoundManager.Instance.PlayDrawSound();
            }
        }


        private IEnumerator SafeMoveCoroutine(GameObject piece, Vector3 targetPos)
        {
            if (piece == null || boardManager == null) yield break;

            yield return boardManager.StartCoroutine(
                boardManager.SafeSmoothMove(piece, targetPos, boardManager.moveDuration)
            );
        }
    }
}
