// Định nghĩa các biến cần thiết
// Lấy thông tin user từ JSP
const currentUserId = parseInt(document.querySelector('meta[name="current-user-id"]')?.getAttribute('content') || '0');
const currentUsername = document.querySelector('meta[name="current-username"]')?.getAttribute('content') || 'Unknown';

// Debug logging
console.log("Current User ID:", currentUserId);
console.log("Current Username:", currentUsername);
console.log("Current User ID type:", typeof document.querySelector('meta[name="current-user-id"]')?.getAttribute('content'));
console.log("Current User ID parsed:", currentUserId);

let currentChatUserId = null;
let currentChatUserName = null;
let autoLoadInterval = null;

// Theo dõi tin nhắn đang được chọn
let selectedMessageId = null;

const ws = new WebSocket("ws://" + location.host + "/SWP_HUY/chat");
ws.onopen = function() { console.log("WebSocket connected"); };
ws.onclose = function() { console.log("WebSocket closed"); };
ws.onerror = function(err) { console.error("WebSocket error", err); };

ws.onmessage = function(event) {
    console.log("WebSocket message received:", event.data);
    let msg;
    try {
        msg = JSON.parse(event.data);
        console.log("Parsed message:", msg);
    } catch (e) {
        console.error("Failed to parse WebSocket message:", e, "Raw data:", event.data);
        return; // Bỏ qua nếu không parse được
    }
    
    if (msg.type === "unread_list") {
        console.log("Processing unread_list with senders:", msg.senders);
        msg.senders.forEach(function(senderId) { markUserAsUnread(senderId); });
        return;
    }
    if (msg.type === "block_status") {
        if ((msg.status === "blocked_by_me" || msg.status === "unblocked_by_me") && currentChatUserId !== msg.blockedId) return;
        if ((msg.status === "blocked_me" || msg.status === "unblocked_me") && currentChatUserId !== msg.blockerId) return;

        const messageInput = document.getElementById("messageInput");
        const sendBtn = document.querySelector(".chat-input-wrap button");
        const blockBtn = document.getElementById("blockBtn");
        const unblockBtn = document.getElementById("unblockBtn");
        const blockNotice = document.getElementById("blockNotice");

        if (msg.status === "blocked_by_me" || msg.status === "blocked_me") {
            messageInput.style.display = "none";
            sendBtn.style.display = "none";
            blockBtn.style.display = "none";
            unblockBtn.style.display = msg.status === "blocked_by_me" ? "inline-block" : "none";
            blockNotice.textContent = msg.status === "blocked_by_me" ? "Bạn đã chặn người dùng này" : "Bạn đã bị chặn";
            blockNotice.style.display = "block";
        } else {
            messageInput.style.display = "block";
            sendBtn.style.display = "inline-block";
            blockBtn.style.display = "inline-block";
            unblockBtn.style.display = "none";
            blockNotice.style.display = "none";
        }
        return;
    }
    if (msg.type === "recall") {
        handleRecallMessage(msg);
        return;
    }
    if (msg.type === "block") {
        alert(msg.message);
        return;
    }
    
    console.log("Checking message for current chat:", {
        fromUserId: msg.fromUserId,
        toUserId: msg.toUserId,
        currentChatUserId: currentChatUserId,
        currentUserId: currentUserId
    });
    
    if ((msg.fromUserId === currentChatUserId && msg.toUserId === currentUserId) ||
        (msg.toUserId === currentChatUserId && msg.fromUserId === currentUserId)) {
        console.log("Adding message to chat box with sender:", msg.fromUsername || "Unknown");
        addMessageToChatBox(msg);
    }
    if (msg.fromUserId !== currentUserId && msg.fromUserId !== currentChatUserId) {
        markUserAsUnread(msg.fromUserId);
    } else {
        markMessagesAsRead(msg.fromUserId);
    }
};

function selectChatUser(userId, username) {
    console.log("selectChatUser called with:", { userId, username });
    document.querySelector(".chat-main").style.display = "flex";
    currentChatUserId = userId;
    currentChatUserName = username;
    document.getElementById("chatWith").textContent = username;

    document.querySelectorAll("#userList li").forEach(function(item) {
        item.classList.toggle("selected", parseInt(item.dataset.userId) === userId);
    });

    document.getElementById("messageInput").style.display = "block";
    document.querySelector(".chat-input-wrap button").style.display = "inline-block";
    document.getElementById("blockNotice").style.display = "none";

    loadChatHistory(userId);
    markMessagesAsRead(userId);

    // Bắt đầu tự động load lại chat mỗi 1 giây khi chọn người dùng
    if (typeof autoLoadInterval !== 'undefined') {
        clearInterval(autoLoadInterval); // Xóa interval cũ nếu có
    }
    autoLoadInterval = setInterval(() => {
        if (currentChatUserId) {
            loadChatHistory(currentChatUserId);
        }
    }, 3000); // 3000ms = 3 giây

    fetch("/SWP_HUY/checkBlock?user1=" + currentUserId + "&user2=" + userId)
        .then(function(res) { return res.json(); })
        .then(function(data) {
            const blockBtn = document.getElementById("blockBtn");
            const unblockBtn = document.getElementById("unblockBtn");
            const messageInput = document.getElementById("messageInput");
            const sendBtn = document.querySelector(".chat-input-wrap button");
            const blockNotice = document.getElementById("blockNotice");

            if (data.blockedByMe) {
                blockBtn.style.display = "none";
                unblockBtn.style.display = "inline-block";
                messageInput.style.display = "none";
                sendBtn.style.display = "none";
                blockNotice.textContent = "Bạn đã chặn người dùng này";
                blockNotice.style.display = "block";
            } else if (data.blockedMe) {
                blockBtn.style.display = "none";
                unblockBtn.style.display = "none";
                messageInput.style.display = "none";
                sendBtn.style.display = "none";
                blockNotice.textContent = "Bạn đã bị người dùng này chặn";
                blockNotice.style.display = "block";
            } else {
                blockBtn.style.display = "inline-block";
                unblockBtn.style.display = "none";
                messageInput.style.display = "block";
                sendBtn.style.display = "inline-block";
                blockNotice.style.display = "none";
            }
        });

    const dropdownBtn = document.getElementById("userDropDown");
    const dropdownContent = document.getElementById("userDropDownContent");
    dropdownBtn.onclick = function(e) {
        e.stopPropagation();
        dropdownContent.style.display = dropdownContent.style.display === "flex" ? "none" : "flex";
    };
}

function addMessageToChatBox(msg) {
    const chatBox = document.getElementById("chatBox");
    const isSentByMe = msg.senderId === currentUserId; // Sử dụng senderId từ lịch sử chat
    const senderName = isSentByMe ? currentUsername : (msg.fromUsername || currentChatUserName || "Người nhận");
    const messageId = msg.messageId || Date.now(); // Đảm bảo messageId có giá trị
    const content = msg.content || "Nội dung trống";
    const isRecalled = msg.isRecall || msg.type === "recall";
    const timeText = new Date(msg.sentAt || Date.now()).toLocaleString("vi-VN", { hour: "2-digit", minute: "2-digit" });

    console.log("Adding message:", { senderName, content, isSentByMe, messageId });

    const wrapper = document.createElement("div");
    wrapper.className = "chat-msg " + (isSentByMe ? "me" : "");
    wrapper.dataset.messageId = messageId;
    wrapper.onclick = function(e) { showRecallButton(messageId, e); }; // Thêm sự kiện click

    // Thêm avatar
    const avatarImg = document.createElement("img");
    avatarImg.src = "${pageContext.request.contextPath}/assets/avatar/nam.jpg"; // Thay bằng avatar động nếu có
    avatarImg.alt = "Avatar";
    avatarImg.className = "chat-msg-avatar";
    avatarImg.style.width = "32px";
    avatarImg.style.height = "32px";
    avatarImg.style.borderRadius = "50%";
    avatarImg.style.marginRight = "10px";

    const contentBox = document.createElement("div");
    contentBox.className = "chat-msg-content";

    // Thêm tên người gửi
    const senderSpan = document.createElement("span");
    senderSpan.className = "chat-msg-sender";
    senderSpan.textContent = senderName;
    senderSpan.style.fontSize = "12px";
    senderSpan.style.fontWeight = "bold";
    senderSpan.style.marginBottom = "4px";
    contentBox.appendChild(senderSpan);

    // Thêm nội dung tin nhắn
    if (isRecalled) {
        const p = document.createElement("p");
        p.className = "msg-recalled";
        p.textContent = "Tin nhắn đã bị thu hồi";
        contentBox.appendChild(p);
    } else {
        const p = document.createElement("p");
        p.textContent = content;
        contentBox.appendChild(p);
    }

    // Thêm timestamp
    const timeSpan = document.createElement("span");
    timeSpan.className = "chat-msg-timestamp";
    timeSpan.textContent = timeText;
    timeSpan.style.fontSize = "10px";
    timeSpan.style.color = "#666";
    timeSpan.style.alignSelf = "flex-end";
    contentBox.appendChild(timeSpan);

    // Thêm nút thu hồi (ẩn mặc định)
    if (isSentByMe && !isRecalled) {
        const actions = document.createElement("div");
        actions.className = "chat-actions";
        actions.style.display = "none"; // Ẩn mặc định
        const recallBtn = document.createElement("button");
        recallBtn.textContent = "Thu hồi";
        recallBtn.onclick = function(e) { e.stopPropagation(); recallMessage(messageId); };
        actions.appendChild(recallBtn);
        contentBox.appendChild(actions);
    }

    wrapper.appendChild(avatarImg);
    wrapper.appendChild(contentBox);
    chatBox.appendChild(wrapper);
    chatBox.scrollTop = chatBox.scrollHeight;
}

function showRecallButton(messageId, event) {
    event.stopPropagation(); // Ngăn sự kiện lan ra ngoài
    if (selectedMessageId) {
        const prevActions = document.querySelector(`.chat-msg[data-message-id="${selectedMessageId}"] .chat-actions`);
        if (prevActions) prevActions.style.display = "none";
    }

    const messageElement = document.querySelector(`.chat-msg[data-message-id="${messageId}"]`);
    const actions = messageElement.querySelector(".chat-actions");
    if (actions) {
        actions.style.display = "block";
        selectedMessageId = messageId;
    }
}

// Ẩn nút thu hồi khi click ra ngoài
document.addEventListener("click", function(e) {
    if (!e.target.closest(".chat-msg") && selectedMessageId) {
        const actions = document.querySelector(`.chat-msg[data-message-id="${selectedMessageId}"] .chat-actions`);
        if (actions) actions.style.display = "none";
        selectedMessageId = null;
    }
});

function handleRecallMessage(msg) {
    const messageElement = document.querySelector(".chat-msg[data-message-id='" + msg.messageId + "']");
    if (messageElement) {
        const contentBox = messageElement.querySelector(".chat-msg-content");
        contentBox.innerHTML = "<p class='msg-recalled'>Tin nhắn đã bị thu hồi</p>";
        const actions = messageElement.querySelector(".chat-actions");
        if (actions) actions.style.display = "none";
        selectedMessageId = null;
    }
}

function sendMessage() {
    const input = document.getElementById("messageInput");
    const content = input.value.trim();
    if (!currentChatUserId) return alert("Vui lòng chọn người dùng để chat");
    if (!content) return alert("Tin nhắn không được để trống");

    const msg = {
        type: "message",
        fromUserId: currentUserId,
        fromUsername: currentUsername,
        toUserId: currentChatUserId,
        content: content,
        sentAt: new Date().toISOString()
    };

    console.log("Sending message:", msg);
    ws.send(JSON.stringify(msg));
    input.value = "";
}

function loadChatHistory(userId) {
    fetch("/SWP_HUY/getChatHistory?user1=" + currentUserId + "&user2=" + userId)
        .then(function(res) {
            if (!res.ok) throw new Error("Không tải được lịch sử trò chuyện");
            return res.json();
        })
        .then(function(data) {
            const chatBox = document.getElementById("chatBox");
            chatBox.innerHTML = "";
            console.log("Loaded chat history:", data.messages);
            if (data.messages && Array.isArray(data.messages)) {
                data.messages.forEach(addMessageToChatBox);
            } else {
                console.warn("No valid messages array in chat history:", data);
            }
        })
        .catch(function(err) {
            console.error(err);
            document.getElementById("chatBox").innerHTML = "<p style='color: red;'>Không tải được lịch sử trò chuyện.</p>";
        });
}

function markUserAsUnread(userId) {
    const li = document.querySelector("#userList li[data-user-id='" + userId + "']");
    if (li && !li.classList.contains("user-unread")) {
        li.classList.add("user-unread");
    }
}

function markMessagesAsRead(fromUserId) {
    ws.send(JSON.stringify({ type: "read", fromUserId: fromUserId }));
    clearUnreadMark(fromUserId);
}

function clearUnreadMark(userId) {
    const li = document.querySelector("#userList li[data-user-id='" + userId + "']");
    if (li) li.classList.remove("user-unread");
}

function blockUser() {
    if (!currentChatUserId) return;
    ws.send(JSON.stringify({ type: "block", fromUserId: currentUserId, blockedId: currentChatUserId }));
}

function unblockUser() {
    if (!currentChatUserId) return;
    ws.send(JSON.stringify({ type: "unblock", fromUserId: currentUserId, blockedId: currentChatUserId }));
}

function recallMessage(messageId) {
    if (!currentChatUserId) return alert("Vui lòng chọn người dùng");
    if (confirm("Bạn có chắc chắn muốn thu hồi tin nhắn này?")) {
        ws.send(JSON.stringify({
            type: "recall",
            messageId: messageId,
            fromUserId: currentUserId,
            toUserId: currentChatUserId
        }));
    }
}

function searchUsers(keyword) {
    if (keyword.trim() === "") {
        fetchInitialUserList();
        return;
    }

    fetch("/SWP_HUY/searchUsers?keyword=" + encodeURIComponent(keyword), {
        method: "GET",
        headers: {
            "Content-Type": "application/json"
        }
    })
    .then(function(response) {
        if (!response.ok) {
            throw new Error("HTTP error! Status: " + response.status);
        }
        return response.json();
    })
    .then(function(data) {
        if (data.error) {
            console.error(data.error);
            document.getElementById("userList").innerHTML = "<li>" + data.error + "</li>";
            return;
        }
        updateUserList(data);
    })
    .catch(function(error) {
        console.error("Lỗi khi tìm kiếm người dùng:", error);
        document.getElementById("userList").innerHTML = "<li>Lỗi khi tìm kiếm người dùng. Vui lòng thử lại.</li>";
    });
}

function fetchInitialUserList() {
    fetch("/SWP_HUY/chatUsers", {
        method: "GET"
    })
    .then(function(response) {
        if (!response.ok) {
            throw new Error("HTTP error! Status: " + response.status);
        }
        return response.text();
    })
    .then(function(html) {
        const parser = new DOMParser();
        const doc = parser.parseFromString(html, "text/html");
        const userList = doc.querySelector("#userList").innerHTML;
        document.getElementById("userList").innerHTML = userList;
    })
    .catch(function(error) {
        console.error("Lỗi khi tải danh sách người dùng:", error);
        document.getElementById("userList").innerHTML = "<li>Lỗi khi tải danh sách người dùng. Vui lòng thử lại.</li>";
    });
}

function updateUserList(users) {
    const userList = document.getElementById("userList");
    userList.innerHTML = "";

    if (users.length === 0) {
        userList.innerHTML = "<li>Không tìm thấy người dùng</li>";
        return;
    }

    users.forEach(function(user) {
        if (user.userID !== parseInt(currentUserId)) {
            const li = document.createElement("li");
            li.setAttribute("data-user-id", user.userID);
            li.onclick = function() { selectChatUser(user.userID, user.fullName); };
            li.innerHTML = "<img src='/SWP_HUY/assets/avatar/nam.jpg' alt='Avatar' style='width: 48px; height: 48px; border-radius: 50%;'>" +
                           "<strong>" + user.fullName + "</strong>";
            userList.appendChild(li);
        }
    });
}

document.getElementById("searchUser").addEventListener("input", function(e) {
    searchUsers(e.target.value);
});

document.addEventListener("click", function(event) {
    const dropdown = document.getElementById("userDropDownContent");
    if (!event.target.closest(".dropdown")) dropdown.style.display = "none";
});