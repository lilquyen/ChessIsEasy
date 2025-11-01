using UnityEngine;
using UnityEngine.UI;
using UnityEngine.SceneManagement;

public class MainMenuUI : MonoBehaviour
{
    [Header("UI Panels")]
    public GameObject settingPanel;
    [SerializeField] private Button settingButton;

    private void Start()
    {
        // ✅ Đảm bảo panel Setting luôn ẩn khi bắt đầu
        if (settingPanel != null)
        {
            settingPanel.SetActive(false);
        }
        else
        {
            Debug.LogWarning("⚠ settingPanel chưa được gán trong Inspector!");
        }

        // Gán sự kiện cho nút Setting nếu có
        if (settingButton != null)
        {
            settingButton.onClick.RemoveAllListeners();
            settingButton.onClick.AddListener(OnSettingButtonClicked);
        }
        else
        {
            Debug.LogWarning("⚠ Chưa gán Setting Button vào MainMenuUI!");
        }
    }

    public void OnPlayButtonClicked()
    {
        SoundManager.Instance?.PlayClickSound();
        SceneManager.LoadScene("GameScene");
    }

    public void OnSettingButtonClicked()
    {
        Debug.Log("CLICKED SETTING BUTTON");
        SoundManager.Instance?.PlayClickSound();

        if (settingPanel != null)
        {
            Debug.Log("Panel found, activating...");
            settingPanel.SetActive(true);
            Debug.Log($"✅ Setting panel active: {settingPanel.activeSelf}");
        }
        else
        {
            Debug.LogError("❌ settingPanel IS NULL in build!");
        }
    }

    public void OnQuitButtonClicked()
    {
        SoundManager.Instance?.PlayClickSound();
        Application.Quit();
        Debug.Log("Game exited! (Không thoát trong Editor, chỉ khi build)");
    }

    public void OnCloseSettingClicked()
    {
        SoundManager.Instance?.PlayClickSound();
        if (settingPanel != null)
        {
            settingPanel.SetActive(false);
            Debug.Log("Setting panel closed");
        }
    }
}
