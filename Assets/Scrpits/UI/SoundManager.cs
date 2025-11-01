using UnityEngine;

public class SoundManager : MonoBehaviour
{
    public static SoundManager Instance;

    [Header("Music & SFX Clips")]
    public AudioClip backgroundMusic;
    public AudioClip moveClip;
    public AudioClip clickClip;
    public AudioClip winClip;
    public AudioClip loseClip;
    public AudioClip drawClip;

    [Header("Settings")]
    [Range(0f, 1f)] public float musicVolume = 1f;
    [Range(0f, 1f)] public float sfxVolume = 1f;
    [Range(0f, 1f)] public float moveVolume = 1f;

    private AudioSource musicSource;
    private AudioSource sfxSource;

    private void Awake()
    {
        // Singleton
        if (Instance == null)
        {
            Instance = this;
            DontDestroyOnLoad(gameObject);
            InitializeAudioSources();
            LoadSettings();
        }
        else
        {
            Destroy(gameObject);
        }
    }

    private void InitializeAudioSources()
    {
        // Tạo AudioSource cho nhạc nền
        musicSource = gameObject.AddComponent<AudioSource>();
        musicSource.playOnAwake = false;
        musicSource.loop = true;

        // Tạo AudioSource cho hiệu ứng âm thanh
        sfxSource = gameObject.AddComponent<AudioSource>();
        sfxSource.playOnAwake = false;
        sfxSource.loop = false;

        // Phát nhạc nền
        if (backgroundMusic != null)
        {
            musicSource.clip = backgroundMusic;
            musicSource.volume = musicVolume;
            musicSource.Play();
        }
    }

    // ==== PLAY SOUND ====
    public void PlayMoveSound()
    {
        if (moveClip) sfxSource.PlayOneShot(moveClip, moveVolume);
    }

    public void PlayClickSound()
    {
        if (clickClip) sfxSource.PlayOneShot(clickClip, sfxVolume);
    }

    public void PlayWinSound()
    {
        if (winClip) sfxSource.PlayOneShot(winClip, sfxVolume);
    }

    public void PlayLoseSound()
    {
        if (loseClip) sfxSource.PlayOneShot(loseClip, sfxVolume);
    }

    public void PlayDrawSound()
    {
        if (drawClip) sfxSource.PlayOneShot(drawClip, sfxVolume);
    }

    // ==== VOLUME CONTROL ====
    public void SetMusicVolume(float value)
    {
        musicVolume = value;
        musicSource.volume = value;
        PlayerPrefs.SetFloat("MusicVolume", value);
    }

    public void SetSFXVolume(float value)
    {
        sfxVolume = value;
        PlayerPrefs.SetFloat("SFXVolume", value);
    }

    public void SetMoveVolume(float value)
    {
        moveVolume = value;
        PlayerPrefs.SetFloat("MoveVolume", value);
    }

    private void LoadSettings()
    {
        musicVolume = PlayerPrefs.GetFloat("MusicVolume", 1f);
        sfxVolume = PlayerPrefs.GetFloat("SFXVolume", 1f);
        moveVolume = PlayerPrefs.GetFloat("MoveVolume", 1f);

        if (musicSource != null)
            musicSource.volume = musicVolume;
    }
}
