using System;
using System.Collections.Generic;
using UnityEngine;
using ChessGame.API.DTO;
using ChessGame.UI; // 👈 thêm namespace cho PromotionUI

namespace ChessGame.Managers
{
    public class PieceSelector : MonoBehaviour
    {
        [Header("References (assign in inspector or auto-find)")]
        public InputManager inputManager;
        public BoardManager boardManager;
        public HighlightManager highlightManager;
        public PromotionUI promotionUI; // 👈 thêm tham chiếu UI phong tốt

        [Header("Game data (set từ MoveResponseLoader hoặc GameManager)")]
        public MoveResponse currentResponse;

        // Sự kiện để GameManager hoặc APIManager bắt gửi move
        public event Action<string, string, string> OnMoveChosen; // 👈 thêm promotionChoice

        private string _selectedFrom = null;
        private List<string> _currentTargets = new List<string>();

        private void Start()
        {
            if (inputManager == null) inputManager = FindObjectOfType<InputManager>();
            if (boardManager == null) boardManager = FindObjectOfType<BoardManager>();
            if (highlightManager == null) highlightManager = FindObjectOfType<HighlightManager>();
            if (promotionUI == null) promotionUI = FindObjectOfType<PromotionUI>();

            if (inputManager != null)
            {
                inputManager.OnPieceClicked += HandlePieceClick;
                inputManager.OnBoardClicked += HandleBoardClick;
            }
            else
            {
                Debug.LogWarning("[PieceSelector] InputManager not found in scene.");
            }
        }

        private void OnDestroy()
        {
            if (inputManager != null)
            {
                inputManager.OnPieceClicked -= HandlePieceClick;
                inputManager.OnBoardClicked -= HandleBoardClick;
            }
        }

        private void HandlePieceClick(GameObject pieceObj)
        {
            string clickedSquare = boardManager.WorldToSquare(pieceObj.transform.position).ToUpperInvariant();

            if (!string.IsNullOrEmpty(_selectedFrom) && _currentTargets.Contains(clickedSquare))
            {
                TryExecuteMove(_selectedFrom, clickedSquare);
                return;
            }

            if (!string.IsNullOrEmpty(_selectedFrom) &&
                _selectedFrom.Equals(clickedSquare, StringComparison.OrdinalIgnoreCase))
            {
                ClearSelection();
                return;
            }

            _selectedFrom = clickedSquare;
            _currentTargets = GetTargetsForFrom(clickedSquare, currentResponse);

            if (_currentTargets != null && _currentTargets.Count > 0)
            {
                highlightManager.ShowHighlightsFromSquares(_currentTargets, boardManager);
            }
            else
            {
                highlightManager.ClearHighlights();
            }
        }

        private void HandleBoardClick(Vector3 worldPos)
        {
            if (string.IsNullOrEmpty(_selectedFrom)) return;

            string clickedSquare = boardManager.WorldToSquare(worldPos).ToUpperInvariant();

            if (_currentTargets.Contains(clickedSquare))
            {
                TryExecuteMove(_selectedFrom, clickedSquare);
            }
            else
            {
                ClearSelection();
            }
        }

        /// <summary>
        /// Xử lý logic gửi nước đi hoặc phong tốt
        /// </summary>
        private void TryExecuteMove(string from, string to)
        {
            Debug.Log($"[PieceSelector] Move chosen: {from} -> {to}");

            // Kiểm tra nếu nước đi này là phong tốt
            if (IsPawnPromotion(from, to))
            {
                Debug.Log("[PieceSelector] Detected pawn promotion. Showing promotion UI...");
                promotionUI?.Show(choice =>
                {
                    SendMove(from, to, choice); // gửi kèm quân phong
                });
            }
            else
            {
                SendMove(from, to);
            }

            ClearSelection();
        }

        /// <summary>
        /// Gửi nước đi lên backend hoặc trigger event
        /// </summary>
        private void SendMove(string from, string to, string promotionChoice = null)
        {
            // Gọi event ra GameManager
            OnMoveChosen?.Invoke(from, to, promotionChoice);

            // Gửi qua MoveResponseLoader nếu có
            var loader = FindObjectOfType<MoveResponseLoader>();
            if (loader != null)
            {
                var move = new MoveDTO { from = from, to = to, promotionChoice = promotionChoice };
                StartCoroutine(loader.SendNextMove(move));
            }

            // Di chuyển ngay trên client
            boardManager?.MovePiece(from, to);
        }

        private void ClearSelection()
        {
            _selectedFrom = null;
            _currentTargets.Clear();
            highlightManager?.ClearHighlights();
        }

        private List<string> GetTargetsForFrom(string fromSquare, MoveResponse resp)
        {
            var result = new List<string>();
            if (resp == null || resp.possibleMoves == null) return result;

            foreach (var move in resp.possibleMoves)
            {
                if (move == null) continue;
                if (string.Equals(move.from, fromSquare, StringComparison.OrdinalIgnoreCase))
                    result.Add(move.to.ToUpperInvariant());
            }

            return result;
        }

        public void SetCurrentResponse(MoveResponse resp)
        {
            currentResponse = resp;
            ClearSelection();
        }

        /// <summary>
        /// Kiểm tra xem có phải nước phong tốt không
        /// </summary>
        private bool IsPawnPromotion(string from, string to)
        {
            // Tìm quân cờ tại vị trí "from"
            var piece = boardManager.GetPieceAtSquare(from);
            if (piece == null) return false;

            // Kiểm tra nếu là tốt
            string name = piece.name.ToLower();
            if (!name.Contains("pawn")) return false;

            // Kiểm tra hàng phong tốt (tùy màu)
            int row = boardManager.SquareToRow(to);
            return row == 0 || row == 7;
        }

        public void OnHighlightClicked(string square)
        {
            if (!string.IsNullOrEmpty(_selectedFrom))
            {
                TryExecuteMove(_selectedFrom, square);
            }
        }
    }
}
