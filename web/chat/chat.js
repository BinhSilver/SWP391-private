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
let ws = null;
let reconnectAttempts = 0;
const maxReconnectAttempts = 5;
const reconnectDelay = 3000; // 3 seconds
let autoLoadInterval = null;
let isSelectingUser = false;

// Fallback to HTTP polling if WebSocket fails
let useHttpPolling = false;
let pollingInterval = null;

// WebSocket connection with retry mechanism
function connectWebSocket() {
    try {
        const wsUrl = "wss://wasabii.id.vn/Wasabii/chat"; // Hardcode để đảm bảo đúng endpoint
        console.log("Attempting to connect to WebSocket:", wsUrl);
        
        ws = new WebSocket(wsUrl);
        
        ws.onopen = function () {
            console.log("WebSocket connected successfully");
            reconnectAttempts = 0; // Reset reconnect attempts
            useHttpPolling = false; // Disable HTTP polling
            if (pollingInterval) {
                clearInterval(pollingInterval);
                pollingInterval = null;
            }
            if (currentUserId) {
                ws.send(JSON.stringify({ type: "register", userId: currentUserId }));
            }
        };
        
        ws.onclose = function (event) {
            console.log("WebSocket closed:", event.code, event.reason);
            if (reconnectAttempts < maxReconnectAttempts) {
                console.log(`Attempting to reconnect... (${reconnectAttempts + 1}/${maxReconnectAttempts})`);
                setTimeout(connectWebSocket, reconnectDelay);
                reconnectAttempts++;
            } else {
                console.error("Max reconnection attempts reached. Switching to HTTP polling.");
                useHttpPolling = true;
                startHttpPolling();
            }
        };
        
        ws.onerror = function (err) {
            console.error("WebSocket error:", err);
        };

        ws.onmessage = function (event) {
            console.log("WebSocket message received:", event.data);
            handleMessage(event.data);
        };
    } catch (error) {
        console.error("Failed to create WebSocket connection:", error);
        useHttpPolling = true;
        startHttpPolling();
    }
}

function startHttpPolling() {
    console.log("Starting HTTP polling as fallback");
    if (pollingInterval) {
        clearInterval(pollingInterval);
    }
    
    // Poll for new messages every 3 seconds
    pollingInterval = setInterval(function() {
        if (currentChatUserId) {
            loadChatHistory(currentChatUserId);
        }
    }, 3000);
}

function handleMessage(data) {
    let msg;
    try {
        msg = JSON.parse(data);
        console.log("Parsed message:", msg);
    } catch (e) {
        console.error("Failed to parse message:", e, "Raw data:", data);
        return;
    }

    if (msg.type === "unread_list") {
        console.log("Processing unread_list with senders:", msg.senders);
        msg.senders.forEach(function (senderId) {
            markUserAsUnread(senderId);
        });
        return;
    }
    if (msg.type === "block_status") {
        if ((msg.status === "blocked_by_me" || msg.status === "unblocked_by_me") && currentChatUserId !== msg.blockedId)
            return;
        if ((msg.status === "blocked_me" || msg.status === "unblocked_me") && currentChatUserId !== msg.blockerId)
            return;

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
}

// Initialize WebSocket connection
connectWebSocket();

// Keep connection alive with ping
setInterval(() => {
    if (ws && ws.readyState === WebSocket.OPEN) {
        ws.send(JSON.stringify({ type: "ping" }));
    }
}, 30000);

function selectChatUser(userId, username) {
    if (isSelectingUser) {
        console.log("selectChatUser skipped due to debounce");
        return;
    }
    isSelectingUser = true;
    console.log("selectChatUser called with:", { userId, username });

    document.querySelector(".chat-main").style.display = "flex";
    currentChatUserId = userId;
    currentChatUserName = username;
    document.getElementById("chatWith").textContent = username;

    document.querySelectorAll("#userList li").forEach(function (item) {
        item.classList.toggle("selected", parseInt(item.dataset.userId) === userId);
    });

    document.getElementById("messageInput").style.display = "block";
    document.querySelector(".chat-input-wrap button").style.display = "inline-block";
    document.getElementById("blockNotice").style.display = "none";

    loadChatHistory(userId);
    markMessagesAsRead(userId);

    // Auto-refresh chat history
    if (autoLoadInterval) {
        clearInterval(autoLoadInterval);
    }
    autoLoadInterval = setInterval(() => {
        if (currentChatUserId) {
            loadChatHistory(currentChatUserId);
        }
    }, 2500);

    fetch(getApiUrl(API_CONFIG.CHECK_BLOCK) + "?user1=" + currentUserId + "&user2=" + userId)
        .then(function (res) {
            if (!res.ok) throw new Error("Failed to check block status: " + res.status);
            return res.json();
        })
        .then(function (data) {
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
        })
        .catch(function (err) {
            console.error("Error checking block status:", err);
        });

    const dropdownBtn = document.getElementById("userDropDown");
    const dropdownContent = document.getElementById("userDropDownContent");
    dropdownBtn.onclick = function (e) {
        e.stopPropagation();
        dropdownContent.style.display = dropdownContent.style.display === "flex" ? "none" : "flex";
    };

    setTimeout(() => { isSelectingUser = false; }, 500);
}

function addMessageToChatBox(msg) {
    const chatBox = document.getElementById("chatBox");
    const isSentByMe = msg.senderId === currentUserId;
    const senderName = isSentByMe ? currentUsername : (msg.fromUsername || currentChatUserName || "Người nhận");
    const messageId = msg.messageId || Date.now();
    const content = msg.content || "Nội dung trống";
    const isRecalled = msg.isRecall || msg.type === "recall";
    const timeText = new Date(msg.sentAt || Date.now()).toLocaleString("vi-VN", { hour: "2-digit", minute: "2-digit" });

    console.log("Adding message:", { senderName, content, isSentByMe, messageId });

    const wrapper = document.createElement("div");
    wrapper.className = "chat-msg " + (isSentByMe ? "me" : "");
    wrapper.dataset.messageId = messageId;

    const avatarImg = document.createElement("img");
    avatarImg.src = getResourceUrl(RESOURCE_CONFIG.AVATAR_PATH + "nam.jpg");
    avatarImg.alt = "Avatar";
    avatarImg.className = "chat-msg-avatar";
    avatarImg.style.width = "32px";
    avatarImg.style.height = "32px";
    avatarImg.style.borderRadius = "50%";
    avatarImg.style.marginRight = "10px";

    const contentBox = document.createElement("div");
    contentBox.className = "chat-msg-content";

    const senderSpan = document.createElement("span");
    senderSpan.className = "chat-msg-sender";
    senderSpan.textContent = senderName;
    senderSpan.style.fontSize = "12px";
    senderSpan.style.fontWeight = "bold";
    senderSpan.style.marginBottom = "4px";
    contentBox.appendChild(senderSpan);

    if (isRecalled) {
        const p = document.createElement("p");
        p.className = "msg-recalled";
        p.textContent = "Tin nhắn đã bị thu hồi";
        contentBox.appendChild(p);
    } else {
        const p = document.createElement("p");
        p.textContent = content;
        contentBox.appendChild(p);

        if (isSentByMe) {
            const actions = document.createElement("div");
            actions.className = "chat-actions";
            actions.style.display = "none";
            const recallBtn = document.createElement("button");
            recallBtn.className = "recall-btn";
            recallBtn.innerHTML = '<i class="fas fa-undo"></i>';
            recallBtn.onclick = function () {
                recallMessage(messageId);
                actions.style.display = "none";
            };
            actions.appendChild(recallBtn);
            contentBox.appendChild(actions);

            wrapper.addEventListener("click", function (e) {
                e.stopPropagation();
                const allActions = document.querySelectorAll(".chat-actions");
                allActions.forEach(a => a.style.display = "none");
                actions.style.display = "block";
            });
        }
    }

    const timeSpan = document.createElement("span");
    timeSpan.className = "chat-msg-timestamp";
    timeSpan.textContent = timeText;
    timeSpan.style.fontSize = "10px";
    timeSpan.style.color = "#666";
    timeSpan.style.alignSelf = "flex-end";
    contentBox.appendChild(timeSpan);

    wrapper.appendChild(avatarImg);
    wrapper.appendChild(contentBox);
    chatBox.appendChild(wrapper);
    chatBox.scrollTop = chatBox.scrollHeight;
}

document.addEventListener("click", function (e) {
    const allActions = document.querySelectorAll(".chat-actions");
    allActions.forEach(a => {
        if (!e.target.closest(".chat-msg")) {
            a.style.display = "none";
        }
    });
});

function handleRecallMessage(msg) {
    const messageElement = document.querySelector(".chat-msg[data-message-id='" + msg.messageId + "']");
    if (messageElement) {
        const contentBox = messageElement.querySelector(".chat-msg-content");
        contentBox.innerHTML = "<p class='msg-recalled'>Tin nhắn đã bị thu hồi</p>";
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
    
    // Try WebSocket first, fallback to HTTP if needed
    if (ws && ws.readyState === WebSocket.OPEN) {
        try {
            ws.send(JSON.stringify(msg));
            input.value = "";
            return;
        } catch (error) {
            console.error("Failed to send message via WebSocket:", error);
        }
    }
    
    // Fallback to HTTP POST if WebSocket is not available
    if (useHttpPolling) {
        sendMessageViaHttp(msg, input);
    } else {
        alert("Kết nối chat bị lỗi. Vui lòng tải lại trang.");
    }
}

function sendMessageViaHttp(msg, input) {
    fetch(getApiUrl(API_CONFIG.SEND_MESSAGE), {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(msg)
    })
    .then(response => {
        if (response.ok) {
            input.value = "";
            console.log("Message sent via HTTP successfully");
        } else {
            throw new Error('Failed to send message');
        }
    })
    .catch(error => {
        console.error("Failed to send message via HTTP:", error);
        alert("Không thể gửi tin nhắn. Vui lòng thử lại.");
    });
}

function loadChatHistory(userId) {
    const url = getApiUrl(API_CONFIG.GET_CHAT_HISTORY) + "?user1=" + currentUserId + "&user2=" + userId;
    console.log("Fetching chat history from:", url);
    fetch(url)
        .then(function (res) {
            if (!res.ok) throw new Error("Không tải được lịch sử trò chuyện: " + res.status);
            return res.json();
        })
        .then(function (data) {
            const chatBox = document.getElementById("chatBox");
            chatBox.innerHTML = "";
            console.log("Loaded chat history:", data.messages);
            if (data.messages && Array.isArray(data.messages)) {
                data.messages.forEach(addMessageToChatBox);
            } else {
                console.warn("No valid messages array in chat history:", data);
                chatBox.innerHTML = "<p>Không có tin nhắn nào.</p>";
            }
        })
        .catch(function (err) {
            console.error("Error loading chat history:", err);
            document.getElementById("chatBox").innerHTML = "<p style='color: red; text-align: center;'>Không tải được lịch sử trò chuyện.</p>";
        });
}

function markUserAsUnread(userId) {
    const li = document.querySelector("#userList li[data-user-id='" + userId + "']");
    if (li && !li.classList.contains("user-unread")) {
        li.classList.add("user-unread");
    }
}

function markMessagesAsRead(fromUserId) {
    if (ws && ws.readyState === WebSocket.OPEN) {
        try {
            ws.send(JSON.stringify({ type: "read", fromUserId: fromUserId }));
        } catch (error) {
            console.error("Failed to mark messages as read:", error);
        }
    }
    clearUnreadMark(fromUserId);
}

function clearUnreadMark(userId) {
    const li = document.querySelector("#userList li[data-user-id='" + userId + "']");
    if (li) li.classList.remove("user-unread");
}

function blockUser() {
    if (!currentChatUserId) return alert("Vui lòng chọn người dùng để chặn");
    if (!ws || ws.readyState !== WebSocket.OPEN) {
        alert("Kết nối chat bị lỗi. Vui lòng tải lại trang.");
        return;
    }
    try {
        ws.send(JSON.stringify({ type: "block", fromUserId: currentUserId, blockedId: currentChatUserId }));
    } catch (error) {
        console.error("Failed to block user:", error);
        alert("Không thể chặn người dùng. Vui lòng thử lại.");
    }
}

function unblockUser() {
    if (!currentChatUserId) return alert("Vui lòng chọn người dùng để mở chặn");
    if (!ws || ws.readyState !== WebSocket.OPEN) {
        alert("Kết nối chat bị lỗi. Vui lòng tải lại trang.");
        return;
    }
    try {
        ws.send(JSON.stringify({ type: "unblock", fromUserId: currentUserId, blockedId: currentChatUserId }));
    } catch (error) {
        console.error("Failed to unblock user:", error);
        alert("Không thể mở chặn người dùng. Vui lòng thử lại.");
    }
}

function recallMessage(messageId) {
    if (!currentChatUserId) return alert("Vui lòng chọn người dùng");
    if (!ws || ws.readyState !== WebSocket.OPEN) {
        alert("Kết nối chat bị lỗi. Vui lòng tải lại trang.");
        return;
    }
    if (confirm("Bạn có chắc chắn muốn thu hồi tin nhắn này?")) {
        try {
            ws.send(JSON.stringify({
                type: "recall",
                messageId: messageId,
                fromUserId: currentUserId,
                toUserId: currentChatUserId
            }));
        } catch (error) {
            console.error("Failed to recall message:", error);
            alert("Không thể thu hồi tin nhắn. Vui lòng thử lại.");
        }
    }
}

function searchUsers(keyword) {
    if (keyword.trim() === "") {
        fetchInitialUserList();
        return;
    }

    fetch(getApiUrl(API_CONFIG.SEARCH_USERS) + "?keyword=" + encodeURIComponent(keyword), {
        method: "GET",
        headers: { "Content-Type": "application/json" }
    })
        .then(function (response) {
            if (!response.ok) throw new Error("HTTP error! Status: " + response.status);
            return response.json();
        })
        .then(function (data) {
            if (data.error) {
                console.error(data.error);
                document.getElementById("userList").innerHTML = "<li>" + data.error + "</li>";
                return;
            }
            updateUserList(data);
        })
        .catch(function (error) {
            console.error("Lỗi khi tìm kiếm người dùng:", error);
            document.getElementById("userList").innerHTML = "<li>Lỗi khi tìm kiếm người dùng. Vui lòng thử lại.</li>";
        });
}

function fetchInitialUserList() {
    fetch(getApiUrl(API_CONFIG.CHAT_USERS), { method: "GET" })
        .then(function (response) {
            if (!response.ok) throw new Error("HTTP error! Status: " + response.status);
            return response.text();
        })
        .then(function (html) {
            const parser = new DOMParser();
            const doc = parser.parseFromString(html, "text/html");
            const userList = doc.querySelector("#userList").innerHTML;
            document.getElementById("userList").innerHTML = userList;
        })
        .catch(function (error) {
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

    users.forEach(function (user) {
        if (user.userID !== parseInt(currentUserId)) {
            const li = document.createElement("li");
            li.setAttribute("data-user-id", user.userID);
            li.onclick = function () {
                selectChatUser(user.userID, user.fullName);
            };
            li.innerHTML = "<img src='" + window.contextPath + "/avatar?userId=" + user.userID + "&v=" + (user.avatar ? user.avatar.hashCode ? user.avatar.hashCode() : Date.now() : Date.now()) + "' alt='Avatar' style='width: 48px; height: 48px; border-radius: 50%;'>" +
                "<strong>" + user.fullName + "</strong>";
            userList.appendChild(li);
        }
    });
}

document.getElementById("searchUser").addEventListener("input", function (e) {
    searchUsers(e.target.value);
});

document.addEventListener("click", function (e) {
    const dropdown = document.getElementById("userDropDownContent");
    if (!e.target.closest(".dropdown")) dropdown.style.display = "none";
});