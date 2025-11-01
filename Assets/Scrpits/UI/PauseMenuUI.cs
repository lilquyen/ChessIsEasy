using UnityEngine;
using UnityEngine.SceneManagement;

public class PauseMenuUI : MonoBehaviour
{
    public GameObject pauseMenuUI;
    public GameObject settingUI;

    public void TogglePause()
    {
        bool isActive = pauseMenuUI.activeSelf;
        pauseMenuUI.SetActive(!isActive);
        Time.timeScale = isActive ? 1f : 0f;
    }

    public void ResumeGame()
    {
        pauseMenuUI.SetActive(false);
        Time.timeScale = 1f;
    }

    public void RestartGame()
    {
        Time.timeScale = 1f;
        SceneManager.LoadScene(SceneManager.GetActiveScene().name);
    }

    public void GoToMenu()
    {
        Time.timeScale = 1f;
        SceneManager.LoadScene("MainMenu");
    }
    public void OpenSetting()
    {
        pauseMenuUI.SetActive(false);
        settingUI.SetActive(true);
    }
    public void CloseSetting()
    {
        settingUI.SetActive(false);
        pauseMenuUI.SetActive(true);
    }
}
