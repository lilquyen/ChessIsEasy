using UnityEngine;
using UnityEngine.UI;
using System;

namespace ChessGame.UI
{
    public class PromotionUI : MonoBehaviour
    {
        [Header("Promotion Buttons")]
        public Button queenButton;
        public Button rookButton;
        public Button bishopButton;
        public Button knightButton;

        private Action<string> onPromotionSelected;

        private void Awake()
        {
            // Gán sự kiện ở đây — Awake chạy kể cả khi object bị disable
            queenButton.onClick.AddListener(() => SelectPromotion("QUEEN"));
            rookButton.onClick.AddListener(() => SelectPromotion("ROOK"));
            bishopButton.onClick.AddListener(() => SelectPromotion("BISHOP"));
            knightButton.onClick.AddListener(() => SelectPromotion("KNIGHT"));
        }

        private void Start()
        {
            // Ẩn popup khi bắt đầu
            gameObject.SetActive(false);
        }

        public void Show(Action<string> onSelected)
        {
            onPromotionSelected = onSelected;
            Debug.Log("[PromotionUI] Show called — Activating UI");
            gameObject.SetActive(true);
        }

        private void SelectPromotion(string choice)
        {
            Debug.Log($"[PromotionUI] Choice selected: {choice}");
            gameObject.SetActive(false);
            onPromotionSelected?.Invoke(choice);
        }
    }
}
