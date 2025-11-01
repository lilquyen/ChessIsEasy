using UnityEngine;
using UnityEngine.UI;

public class SettingManager : MonoBehaviour
{
    [Header("Volume Sliders")]
    public Slider musicSlider;
    public Slider sfxSlider;
    public Slider moveSlider;

    [Header("Buttons")]
    public Button applyButton;
    public Button resetButton;
    public Button exitButton;

    private float tempMusic;
    private float tempSFX;
    private float tempMove;

    private void Start()
    {
        // Khởi tạo slider theo SoundManager
        if (SoundManager.Instance != null)
        {
            musicSlider.value = SoundManager.Instance.musicVolume;
            sfxSlider.value = SoundManager.Instance.sfxVolume;
            moveSlider.value = SoundManager.Instance.moveVolume;
        }

        // Lưu giá trị tạm thời ban đầu
        tempMusic = musicSlider.value;
        tempSFX = sfxSlider.value;
        tempMove = moveSlider.value;

        // Sự kiện kéo slider
        musicSlider.onValueChanged.AddListener(OnMusicChanged);
        sfxSlider.onValueChanged.AddListener(OnSFXChanged);
        moveSlider.onValueChanged.AddListener(OnMoveChanged);

        // Sự kiện nút
        applyButton.onClick.AddListener(ApplySettings);
        resetButton.onClick.AddListener(ResetSettings);
        exitButton.onClick.AddListener(ExitSettings);
    }

    // ==== Khi kéo thanh ====
    private void OnMusicChanged(float value)
    {
        SoundManager.Instance?.SetMusicVolume(value);
    }

    private void OnSFXChanged(float value)
    {
        SoundManager.Instance?.SetSFXVolume(value);
        // Có thể phát tiếng click nhẹ khi test volume:
        // SoundManager.Instance?.PlayClickSound();
    }

    private void OnMoveChanged(float value)
    {
        SoundManager.Instance?.SetMoveVolume(value);
    }

    // ==== Các nút ====
    private void ApplySettings()
    {
        // Lưu lại giá trị
        PlayerPrefs.SetFloat("MusicVolume", musicSlider.value);
        PlayerPrefs.SetFloat("SFXVolume", sfxSlider.value);
        PlayerPrefs.SetFloat("MoveVolume", moveSlider.value);
        PlayerPrefs.Save();

        // Cập nhật tạm thời
        tempMusic = musicSlider.value;
        tempSFX = sfxSlider.value;
        tempMove = moveSlider.value;

        Debug.Log("Settings applied!");
    }

    private void ResetSettings()
    {
        // Đưa về mặc định
        musicSlider.value = 1f;
        sfxSlider.value = 1f;
        moveSlider.value = 1f;

        // Cập nhật sound ngay
        SoundManager.Instance?.SetMusicVolume(1f);
        SoundManager.Instance?.SetSFXVolume(1f);
        SoundManager.Instance?.SetMoveVolume(1f);

        Debug.Log("Settings reset to default!");
    }

    private void ExitSettings()
    {
        // Khôi phục giá trị cũ (nếu chưa Apply)
        musicSlider.value = tempMusic;
        sfxSlider.value = tempSFX;
        moveSlider.value = tempMove;

        SoundManager.Instance?.SetMusicVolume(tempMusic);
        SoundManager.Instance?.SetSFXVolume(tempSFX);
        SoundManager.Instance?.SetMoveVolume(tempMove);

        // Ẩn panel
        gameObject.SetActive(false);

        Debug.Log("Settings closed without applying changes.");
    }
}
