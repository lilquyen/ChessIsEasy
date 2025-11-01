using System.Collections.Generic;
using UnityEngine;

namespace ChessGame.Managers
{
    /// <summary>
    /// Quản lý spawn / clear các ô highlight.
    /// - highlightPrefab: một plane/quad nhỏ, đặt y hơi cao so với mặt bàn (0.01).
    /// - highlightsParent: (tuỳ) parent để tổ chức hierarchy.
    /// </summary>
    public class HighlightManager : MonoBehaviour
    {
        [Header("Highlight Prefab (must be set)")]
        [Tooltip("Prefab should be a flat quad/plane slightly above board surface.")]
        public GameObject highlightPrefab;

        [Tooltip("Optional parent for spawned highlights.")]
        public Transform highlightsParent;

        private readonly List<GameObject> _activeHighlights = new List<GameObject>();

        public void ShowHighlightAt(Vector3 worldPosition)
        {
            if (highlightPrefab == null)
            {
                Debug.LogError("[HighlightManager] highlightPrefab is not assigned!");
                return;
            }

            worldPosition.y = 14.15f;

            GameObject h = Instantiate(highlightPrefab, worldPosition, Quaternion.identity, highlightsParent);
            _activeHighlights.Add(h);
        }

        public void ShowHighlightsPositions(List<Vector3> worldPositions)
        {
            ClearHighlights();
            if (worldPositions == null) return;
            foreach (var p in worldPositions) ShowHighlightAt(p);
        }

        /// <summary>
        /// Convenience: nhận danh sách ô dạng "A2","E4" và BoardManager để convert sang world pos.
        /// </summary>
        public void ShowHighlightsFromSquares(List<string> squares, BoardManager boardManager)
        {
            ClearHighlights();
            if (squares == null || boardManager == null) return;

            var positions = new List<Vector3>(squares.Count);
            foreach (var s in squares)
            {
                positions.Add(boardManager.SquareToWorld(s));
            }

            ShowHighlightsPositions(positions);
        }

        public void ClearHighlights()
        {
            for (int i = 0; i < _activeHighlights.Count; i++)
            {
                if (_activeHighlights[i] != null) Destroy(_activeHighlights[i]);
            }
            _activeHighlights.Clear();
        }
    }
}
