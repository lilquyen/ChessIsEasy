using System;
using UnityEngine;

namespace ChessGame.Managers
{
    /// <summary>
    /// Bắn raycast khi click chuột. Phát event riêng cho Piece và cho Board (vị trí world).
    /// Gán tag "Piece" cho prefabs quân cờ.
    /// </summary>
    public class InputManager : MonoBehaviour
    {
        [Header("Raycast settings")]
        public Camera playerCamera;
        public LayerMask raycastMask = ~0; // default = Everything
        public float maxDistance = 100f;

        // Instance events (subscribe/unsubscribe)
        public event Action<GameObject> OnPieceClicked;
        public event Action<Vector3> OnBoardClicked;

        private void Awake()
        {
            if (playerCamera == null)
                playerCamera = Camera.main;
        }

        private void Update()
        {
            if (Input.GetMouseButtonDown(0))
                HandleClick();
        }

        private void HandleClick()
        {
            if (playerCamera == null) return;

            Ray ray = playerCamera.ScreenPointToRay(Input.mousePosition);
            if (Physics.Raycast(ray, out RaycastHit hit, maxDistance, raycastMask))
            {
                GameObject go = hit.collider.gameObject;

                // Nếu click vào quân cờ (cần tag "Piece")
                if (go.CompareTag("Piece"))
                {
                    OnPieceClicked?.Invoke(go);
                    return;
                }

                // Ngược lại coi như click lên bàn (hoặc ô)
                OnBoardClicked?.Invoke(hit.point);
            }
        }
    }
}
