// Assets/Scripts/API/DTO/MoveDTO.cs
using System;
using Newtonsoft.Json;

namespace ChessGame.API.DTO
{
    [Serializable]
    public class MoveDTO
    {
        // Các trường public này sẽ được serialize thành JSON keys "from", "to" và "promotionChoice"
        [JsonProperty("from")]
        public string from;

        [JsonProperty("to")]
        public string to;

        [JsonProperty("promotionChoice")]
        public string promotionChoice; // thêm trường phong tốt (có thể null nếu không phong)

        public MoveDTO() { }

        // Các alias tiện lợi để code hiện tại (nơi dùng fromSquare/toSquare) vẫn hoạt động.
        // Đánh dấu JsonIgnore để tránh bị serialize trùng.
        [JsonIgnore]
        public string fromSquare
        {
            get => from;
            set => from = value;
        }

        [JsonIgnore]
        public string toSquare
        {
            get => to;
            set => to = value;
        }

        // Hàm khởi tạo (constructor) để tạo MoveDTO dễ hơn
        public MoveDTO(string from, string to, string promotionChoice)
        {
            this.from = from;
            this.to = to;
            this.promotionChoice = promotionChoice;
        }
    }
}
