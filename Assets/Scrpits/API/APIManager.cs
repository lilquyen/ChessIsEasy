using System.Collections;
using UnityEngine;
using UnityEngine.Networking;
using ChessGame.API.DTO; // chỉ cần dùng DTO, bỏ Data vì không dùng đến
using Newtonsoft.Json;

namespace ChessGame.Managers
{
    public class APIManager : MonoBehaviour
    {
        // Đường dẫn đến backend API
        private string baseUrl = "http://localhost:8080/api/ai-chess";

        // Gọi API /start để khởi tạo bàn cờ mới
        public IEnumerator StartGame(System.Action<MoveResponse> callback)
        {
            using (UnityWebRequest req = UnityWebRequest.Get(baseUrl + "/start"))
            {
                yield return req.SendWebRequest();

                if (req.result != UnityWebRequest.Result.Success)
                {
                    Debug.LogError("[APIManager] StartGame error: " + req.error);
                    callback?.Invoke(new MoveResponse { success = false, message = req.error });
                }
                else
                {
                    var res = JsonConvert.DeserializeObject<MoveResponse>(req.downloadHandler.text);
                    callback?.Invoke(res);
                }
            }
        }

        // Gửi nước đi lên backend (API /move)
        public IEnumerator NextMove(MoveDTO move, System.Action<MoveResponse> callback)
        {
            string json = JsonConvert.SerializeObject(move);
            using (UnityWebRequest req = new UnityWebRequest(baseUrl + "/next-move", "POST"))
            {
                byte[] bodyRaw = System.Text.Encoding.UTF8.GetBytes(json);
                req.uploadHandler = new UploadHandlerRaw(bodyRaw);
                req.downloadHandler = new DownloadHandlerBuffer();
                req.SetRequestHeader("Content-Type", "application/json");

                yield return req.SendWebRequest();

                if (req.result != UnityWebRequest.Result.Success)
                {
                    Debug.LogError("[APIManager] NextMove error: " + req.error);
                    callback?.Invoke(new MoveResponse { success = false, message = req.error });
                }
                else
                {
                    var res = JsonConvert.DeserializeObject<MoveResponse>(req.downloadHandler.text);
                    callback?.Invoke(res);
                }
            }
        }
    }
}
