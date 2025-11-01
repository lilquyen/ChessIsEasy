using UnityEngine;
using ChessGame.API.DTO;
using ChessGame.UI;
using System.Collections;

namespace ChessGame.Managers
{
    public class GameManager : MonoBehaviour
    {
        [Header("References")]
        public APIManager apiManager;
        public BoardManager boardManager;
        public PromotionUI promotionUI; 

        private void Start()
        {
            if (apiManager == null)
                apiManager = FindObjectOfType<APIManager>();
            if (boardManager == null)
                boardManager = FindObjectOfType<BoardManager>();
            if (promotionUI == null)
                promotionUI = FindObjectOfType<PromotionUI>();
            try
            {
                string serverPath = System.IO.Path.Combine(Application.dataPath, "../run_server.bat");

                if (System.IO.File.Exists(serverPath))
                {
                    Debug.Log("[GameManager] Đang khởi động backend server...");
                    System.Diagnostics.Process.Start(new System.Diagnostics.ProcessStartInfo
                    {
                        FileName = serverPath,
                        UseShellExecute = true,
                        CreateNoWindow = false
                    });
                }
                else
                {
                    Debug.LogWarning($"[GameManager] Không tìm thấy file run_server.bat tại: {serverPath}");
                }
            }
            catch (System.Exception ex)
            {
                Debug.LogError($"[GameManager] Lỗi khi khởi động server: {ex.Message}");
            }

            // Gọi API bắt đầu game
            StartCoroutine(apiManager.StartGame(OnGameStarted));
        }

        private void OnGameStarted(MoveResponse response)
        {
            if (response == null)
            {
                Debug.LogError("[GameManager] Không nhận được phản hồi từ backend!");
                return;
            }

            if (response.success)
            {
                boardManager.RenderBoard(response.gameState);
                Debug.Log($"[GameManager] Game started: {response.message}");
            }
            else
            {
                Debug.LogError($"[GameManager] Game start failed: {response.message}");
            }
        }

        /// <summary>
        /// Gửi nước đi từ người chơi lên backend (kèm quân phong nếu có)
        /// </summary>
        public void PlayerMove(string from, string to, string promotionChoice = null)
        {
            MoveDTO move = new MoveDTO(from, to, promotionChoice);

            Debug.Log($"[GameManager] Gửi nước đi: {from} -> {to}" +
                      (promotionChoice != null ? $" (phong thành {promotionChoice})" : ""));
            StartCoroutine(apiManager.NextMove(move, OnMoveExecuted));
        }

        private void OnMoveExecuted(MoveResponse response)
        {
            if (response == null)
            {
                Debug.LogError("[GameManager] Không có phản hồi khi gửi nước đi!");
                return;
            }

            if (response.success)
            {
                boardManager.RenderBoard(response.gameState);
                Debug.Log($"[GameManager] Nước đi hợp lệ: {response.message}");
            }
            else
            {
                Debug.LogWarning($"[GameManager] Nước đi không hợp lệ: {response.message}");
            }
        }

        /// <summary>
        /// Phát hiện và xử lý phong tốt.
        /// </summary>
        public void HandlePawnPromotion(string from, string to)
        {
            GameObject movingPiece = boardManager.GetPieceAtSquare(from);
            if (movingPiece == null)
            {
                Debug.LogWarning($"[GameManager] Không tìm thấy quân ở {from}");
                return;
            }

            // Lấy thông tin quân cờ
            string[] parts = movingPiece.name.Split('_');
            if (parts.Length < 2)
            {
                Debug.LogWarning($"[GameManager] Format tên quân không hợp lệ: {movingPiece.name}");
                return;
            }

            string color = parts[0].ToUpper();
            string type = parts[1].ToUpper();

            int toRank = int.Parse(to[1].ToString());
            bool isPromotionRank = (color == "WHITE" && toRank == 8) || (color == "BLACK" && toRank == 1);

            if (type == "PAWN" && isPromotionRank)
            {
                Debug.Log($"[GameManager] Phát hiện phong tốt: {color} {type} từ {from} -> {to}");

                if (promotionUI != null)
                {
                    promotionUI.Show(choice =>
                    {
                        Debug.Log($"[GameManager] Người chơi chọn phong thành {choice}");
                        PlayerMove(from, to, choice);
                    });
                }
                else
                {
                    Debug.LogWarning("[GameManager] promotionUI chưa được gán!");
                    PlayerMove(from, to);
                }
            }
            else
            {
                PlayerMove(from, to);
            }
        }
    }
}
