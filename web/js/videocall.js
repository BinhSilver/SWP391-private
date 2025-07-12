// videocall.js
var currentUserId = parseInt(document.querySelector('meta[name="current-user-id"]').getAttribute('content') || '0');
var currentUsername = document.querySelector('meta[name="current-username"]').getAttribute('content') || 'Unknown';
console.log("Current User ID: " + currentUserId);
console.log("Current Username: " + currentUsername);

// Sử dụng biến được khởi tạo từ JSP
var email = userEmail || currentUsername; // Fallback to FullName if email not available
var password = userPassword || '';

function getDailyToken(username, password, callback) {
    console.log("Requesting token for username: " + username + ", password: " + password);
            var url = "/test/getDailyToken?username=" + encodeURIComponent(username) + "&password=" + encodeURIComponent(password);
    var xhr = new XMLHttpRequest();
    xhr.open("GET", url, true);
    xhr.onreadystatechange = function() {
        if (xhr.readyState === 4) {
            if (xhr.status === 200) {
                var token = xhr.responseText.trim();
                console.log("Token received: " + token);
                if (token) {
                    callback(token);
                } else {
                    console.error("Empty token received");
                    alert("Token rỗng, kiểm tra server!");
                    callback(null);
                }
            } else {
                console.error("Token request failed: Status " + xhr.status + " - " + xhr.responseText);
                alert("Lỗi khi lấy token: " + xhr.status + " - " + (xhr.responseText || "No details"));
                callback(null);
            }
        }
    };
    xhr.send();
}

function createRoom() {
    getDailyToken(email, password, function(token) {
        if (!token) {
            console.error("Failed to get token, stopping createRoom.");
            return;
        }
        var roomName = "room_" + Date.now();
        var apiKey = "6f4b7e9e1f388b12028895794090635c5f373d9fa7dd31110cf0c0601628f0e5"; // Thay bằng API key hợp lệ từ Daily.co
        var url = "https://api.daily.co/v1/rooms";
        var xhr = new XMLHttpRequest();
        xhr.open("POST", url, true);
        xhr.setRequestHeader("Authorization", "Bearer " + apiKey);
        xhr.setRequestHeader("Content-Type", "application/json");
        xhr.onreadystatechange = function() {
            if (xhr.readyState === 4) {
                if (xhr.status === 200) {
                    console.log("Room created successfully: ", xhr.responseText);
                    var response = JSON.parse(xhr.responseText);
                    var roomUrl = response.url; // Sẽ là https://wasabii.daily.co/room_...
                    var roomCode = roomName; // Sử dụng roomName làm mã phòng
                    displayRoomCode(roomUrl, roomCode, token);
                } else {
                    console.error("Failed to create room: Status " + xhr.status + " - " + xhr.statusText);
                    alert("Không thể tạo phòng: " + xhr.statusText);
                }
            }
        };
        console.log("Sending room creation request with name: " + roomName);
        xhr.send(JSON.stringify({ name: roomName, properties: { enable_recording: false } }));
    });
}

function displayRoomCode(roomUrl, roomCode, token) {
    var content = document.getElementById("videoCallContent");
    content.innerHTML = `
        <p>Mã phòng của bạn: <strong>${roomCode}</strong></p>
        <p>Chia sẻ mã này cho người khác để tham gia.</p>
        <button onclick="joinMeeting('${roomUrl}', '${token}')" style="padding: 10px 20px; background: #FA9DC8; color: white; border: none; border-radius: 5px;">Tham gia ngay</button>
    `;
}

function joinRoom() {
    var roomCode = document.getElementById("roomCode").value.trim();
    if (!roomCode) return alert("Vui lòng nhập mã phòng!");
    // Kiểm tra định dạng roomCode
    if (!roomCode.startsWith("room_")) {
        alert("Mã phòng phải bắt đầu bằng 'room_' (ví dụ: room_1752053775875)!");
        return;
    }
    var roomUrl = "https://wasabii.daily.co/" + roomCode; // Sử dụng domain wasabii.daily.co
    console.log("Attempting to join room with roomCode: ", roomCode, "and URL: ", roomUrl); // Debug log
    getDailyToken(email, password, function(token) {
        if (!token) return alert("Không thể lấy token!");
        console.log("Token for join: ", token);
        // Kiểm tra URL hợp lệ
        if (!roomUrl.includes("wasabii.daily.co")) {
            alert("URL phòng họp không hợp lệ. Vui lòng kiểm tra lại mã phòng!");
            return;
        }
        joinMeeting(roomUrl, token);
    });
}

function joinMeeting(roomUrl, token) {
    // Chuyển hướng đến meeting.jsp với roomUrl và token làm tham số
                    var meetingUrl = "/test/meeting.jsp?roomUrl=" + encodeURIComponent(roomUrl) + "&token=" + encodeURIComponent(token);
    console.log("Redirecting to: ", meetingUrl);
    window.location.href = meetingUrl;
}