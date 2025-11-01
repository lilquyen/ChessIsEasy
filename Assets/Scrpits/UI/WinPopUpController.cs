using UnityEngine;
using UnityEngine.SceneManagement;
using ChessGame.Managers;

public class EndPopupController : MonoBehaviour
{
    [Header("References")]
    public GameObject blackWins;
    public GameObject whiteWins;
    public GameObject drawBanner;
    public GameObject menuButton;
    public GameObject playAgainButton;

    private MoveResponseLoader moveLoader;

    private void Awake()
    {
        // Ẩn tất cả con khi khởi tạo
        if (blackWins) blackWins.SetActive(false);
        if (whiteWins) whiteWins.SetActive(false);
        if (drawBanner) drawBanner.SetActive(false);
        if (menuButton) menuButton.SetActive(false);
        if (playAgainButton) playAgainButton.SetActive(false);
    }

    private void Start()
    {
        moveLoader = FindFirstObjectByType<MoveResponseLoader>();

        // Đảm bảo popup ẩn hoàn toàn ban đầu
        gameObject.SetActive(false);
    }

    public void ShowWinner(string winner)
    {
        Debug.Log($"[EndPopup] Hiện kết quả: {winner}");

        // Bật popup
        gameObject.SetActive(true);

        // Reset toàn bộ trước
        if (blackWins) blackWins.SetActive(false);
        if (whiteWins) whiteWins.SetActive(false);
        if (drawBanner) drawBanner.SetActive(false);

        // Chuẩn hóa chuỗi winner để tránh lỗi khoảng trắng / chữ hoa
        string normalized = winner.Trim().ToLower();

        if (normalized == "black")
        {
            blackWins.SetActive(true);
            Debug.Log("[EndPopup] Black thắng!");
        }
        else if (normalized == "white")
        {
            whiteWins.SetActive(true);
            Debug.Log("[EndPopup] White thắng!");
        }
        else if (normalized == "draw")
        {
            drawBanner.SetActive(true);
            Debug.Log("[EndPopup] Hòa!");
        }

        // Hiện nút chức năng
        if (menuButton) menuButton.SetActive(true);
        if (playAgainButton) playAgainButton.SetActive(true);
    }

    public void OnMenuClicked()
    {
        Debug.Log("[EndPopup] Về menu chính...");
        SceneManager.LoadScene("MainMenu");
    }

    public void OnPlayAgainClicked()
    {
        Debug.Log("[EndPopup] Chơi lại từ đầu...");
        gameObject.SetActive(false);

        if (moveLoader != null)
        {
            moveLoader.StartCoroutine("StartGameFromServer");
        }
        else
        {
            Debug.LogWarning("[EndPopup] Không tìm thấy MoveResponseLoader để khởi động lại game!");
        }
    }
}
