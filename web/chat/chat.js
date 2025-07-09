// Định nghĩa các biến cần thiết
const currentUserId = parseInt(document.querySelector('meta[name="current-user-id"]')?.getAttribute('content') || '0');
const currentUsername = document.querySelector('meta[name="current-username"]')?.getAttribute('content') || 'Unknown';
console.log("Current User ID:", currentUserId);
console.log("Current Username:", currentUsername);
console.log("Current User ID type:", typeof document.querySelector('meta[name="current-user-id"]')?.getAttribute('content'));
console.log("Current User ID parsed:", currentUserId);

let currentChatUserId = null;
let currentChatUserName = null;
let autoLoadInterval = null;
let selectedMessageId = null;
const urlParams = new URLSearchParams(window.location.search);
const initialTargetUserId = parseInt(urlParams.get('targetUserId') || '0');
const initialTargetUsername = decodeURIComponent(urlParams.get('targetUsername') || '');

const ws = new WebSocket("ws://" + location.host + "/SWP_HUY/chat");
ws.onopen = () => console.log("WebSocket connected");
ws.onclose = () => console.log("WebSocket closed");
ws.onerror = (err) => console.error("WebSocket error", err);

ws.onmessage = function(event) {
    console.log("WebSocket message received:", event.data);
    let msg;
    try {
        msg = JSON.parse(event.data);
        console.log("Parsed message:", msg);
    } catch (e) {
        console.error("Failed to parse WebSocket message:", e, "Raw data:", event.data);
        return;
    }
    
    if (msg.type === "unread_list") {
        msg.senders.forEach(markUserAsUnread);
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
    if (msg.type === "callInvite") {
        if (msg.toUserId === currentUserId) {
            currentChatUserId = msg.fromUserId;
            currentChatUserName = msg.fromUsername;
            showCallInvite(msg.fromUsername);
        }
        return;
    }
    
    if ((msg.fromUserId === currentChatUserId && msg.toUserId === currentUserId) ||
        (msg.toUserId === currentChatUserId && msg.fromUserId === currentUserId)) {
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

    document.querySelectorAll("#userList li").forEach(item => {
        item.classList.toggle("selected", parseInt(item.dataset.userId) === userId);
    });

    document.getElementById("messageInput").style.display = "block";
    document.querySelector(".chat-input-wrap button").style.display = "inline-block";
    document.getElementById("blockNotice").style.display = "none";

    loadChatHistory(userId);
    markMessagesAsRead(userId);

    if (typeof autoLoadInterval !== 'undefined') clearInterval(autoLoadInterval);
    autoLoadInterval = setInterval(() => { if (currentChatUserId) loadChatHistory(currentChatUserId); }, 3000);

    fetch("/SWP_HUY/checkBlock?user1=" + currentUserId + "&user2=" + userId)
        .then(res => res.json())
        .then(data => {
            const blockBtn = document.getElementById("blockBtn");
            const unblockBtn = document.getElementById("unblockBtn");
            const messageInput = document.getElementById("messageInput");
            const sendBtn = document.querySelector(".chat-input-wrap button");
            const blockNotice = document.getElementById("blockNotice");

            if (data.blockedByMe) {
                blockBtn.style.display = "none"; unblockBtn.style.display = "inline-block";
                messageInput.style.display = "none"; sendBtn.style.display = "none";
                blockNotice.textContent = "Bạn đã chặn người dùng này"; blockNotice.style.display = "block";
            } else if (data.blockedMe) {
                blockBtn.style.display = "none"; unblockBtn.style.display = "none";
                messageInput.style.display = "none"; sendBtn.style.display = "none";
                blockNotice.textContent = "Bạn đã bị người dùng này chặn"; blockNotice.style.display = "block";
            } else {
                blockBtn.style.display = "inline-block"; unblockBtn.style.display = "none";
                messageInput.style.display = "block"; sendBtn.style.display = "inline-block";
                blockNotice.style.display = "none";
            }
        });

    const dropdownBtn = document.getElementById("userDropDown");
    const dropdownContent = document.getElementById("userDropDownContent");
    const callVideoButton = document.getElementById("callVideoButton");
    dropdownBtn.onclick = (e) => { e.stopPropagation(); dropdownContent.style.display = dropdownContent.style.display === "flex" ? "none" : "flex"; };

    if (callVideoButton) callVideoButton.style.display = "inline-block";
}

window.onload = function() {
    if (initialTargetUserId && initialTargetUserId !== currentUserId) {
        console.log("Auto-starting video call with targetUserId:", initialTargetUserId, "and username:", initialTargetUsername);
        selectChatUser(initialTargetUserId, initialTargetUsername);
    } else {
        fetchInitialUserList();
    }
};

function addMessageToChatBox(msg) {
    const chatBox = document.getElementById("chatBox");
    const isSentByMe = msg.fromUserId === currentUserId;
    const senderName = isSentByMe ? currentUsername : (msg.fromUsername || currentChatUserName || "Người nhận");
    const messageId = msg.messageId || Date.now();
    const content = msg.content || "Nội dung trống";
    const isRecalled = msg.isRecall || msg.type === "recall";
    const timeText = new Date(msg.sentAt || Date.now()).toLocaleString("vi-VN", { hour: "2-digit", minute: "2-digit" });

    const wrapper = document.createElement("div");
    wrapper.className = "chat-msg " + (isSentByMe ? "me" : "");
    wrapper.dataset.messageId = messageId;
    wrapper.onclick = (e) => showRecallButton(messageId, e);

    const avatarImg = document.createElement("img");
    avatarImg.src = "${pageContext.request.contextPath}/assets/avatar/nam.jpg";
    avatarImg.alt = "Avatar"; avatarImg.className = "chat-msg-avatar";
    avatarImg.style.width = "32px"; avatarImg.style.height = "32px";
    avatarImg.style.borderRadius = "50%"; avatarImg.style.marginRight = "10px";

    const contentBox = document.createElement("div");
    contentBox.className = "chat-msg-content";

    const senderSpan = document.createElement("span");
    senderSpan.className = "chat-msg-sender"; senderSpan.textContent = senderName;
    senderSpan.style.fontSize = "12px"; senderSpan.style.fontWeight = "bold"; senderSpan.style.marginBottom = "4px";
    contentBox.appendChild(senderSpan);

    if (isRecalled) {
        const p = document.createElement("p"); p.className = "msg-recalled"; p.textContent = "Tin nhắn đã bị thu hồi";
        contentBox.appendChild(p);
    } else {
        const p = document.createElement("p"); p.textContent = content; contentBox.appendChild(p);
    }

    const timeSpan = document.createElement("span");
    timeSpan.className = "chat-msg-timestamp"; timeSpan.textContent = timeText;
    timeSpan.style.fontSize = "10px"; timeSpan.style.color = "#666"; timeSpan.style.alignSelf = "flex-end";
    contentBox.appendChild(timeSpan);

    if (isSentByMe && !isRecalled) {
        const actions = document.createElement("div"); actions.className = "chat-actions"; actions.style.display = "none";
        const recallBtn = document.createElement("button"); recallBtn.textContent = "Thu hồi";
        recallBtn.onclick = (e) => { e.stopPropagation(); recallMessage(messageId); }; actions.appendChild(recallBtn);
        contentBox.appendChild(actions);
    }

    wrapper.appendChild(avatarImg); wrapper.appendChild(contentBox);
    chatBox.appendChild(wrapper); chatBox.scrollTop = chatBox.scrollHeight;
}

function showRecallButton(messageId, event) {
    event.stopPropagation();
    if (selectedMessageId) {
        const prevActions = document.querySelector(`.chat-msg[data-message-id="${selectedMessageId}"] .chat-actions`);
        if (prevActions) prevActions.style.display = "none";
    }
    const messageElement = document.querySelector(`.chat-msg[data-message-id="${messageId}"]`);
    const actions = messageElement.querySelector(".chat-actions");
    if (actions) { actions.style.display = "block"; selectedMessageId = messageId; }
}

document.addEventListener("click", function(e) {
    if (!e.target.closest(".chat-msg") && selectedMessageId) {
        const actions = document.querySelector(`.chat-msg[data-message-id="${selectedMessageId}"] .chat-actions`);
        if (actions) actions.style.display = "none"; selectedMessageId = null;
    }
});

function handleRecallMessage(msg) {
    const messageElement = document.querySelector(".chat-msg[data-message-id='" + msg.messageId + "']");
    if (messageElement) {
        const contentBox = messageElement.querySelector(".chat-msg-content");
        contentBox.innerHTML = "<p class='msg-recalled'>Tin nhắn đã bị thu hồi</p>";
        const actions = messageElement.querySelector(".chat-actions");
        if (actions) actions.style.display = "none"; selectedMessageId = null;
    }
}

function sendMessage() {
    const input = document.getElementById("messageInput");
    const content = input.value.trim();
    if (!currentChatUserId) return alert("Vui lòng chọn người dùng để chat");
    if (!content) return alert("Tin nhắn không được để trống");

    const msg = { type: "message", fromUserId: currentUserId, fromUsername: currentUsername, toUserId: currentChatUserId, content: content, sentAt: new Date().toISOString() };
    console.log("Sending message:", msg); ws.send(JSON.stringify(msg)); input.value = "";
}

function loadChatHistory(userId) {
    fetch("/SWP_HUY/getChatHistory?user1=" + currentUserId + "&user2=" + userId)
        .then(res => { if (!res.ok) throw new Error("Không tải được lịch sử trò chuyện"); return res.json(); })
        .then(data => { const chatBox = document.getElementById("chatBox"); chatBox.innerHTML = ""; if (data.messages && Array.isArray(data.messages)) data.messages.forEach(addMessageToChatBox); else console.warn("No valid messages array in chat history:", data); })
        .catch(err => { console.error(err); document.getElementById("chatBox").innerHTML = "<p style='color: red;'>Không tải được lịch sử trò chuyện.</p>"; });
}

function markUserAsUnread(userId) {
    const li = document.querySelector("#userList li[data-user-id='" + userId + "']");
    if (li && !li.classList.contains("user-unread")) li.classList.add("user-unread");
}

function markMessagesAsRead(fromUserId) {
    ws.send(JSON.stringify({ type: "read", fromUserId: fromUserId })); clearUnreadMark(fromUserId);
}

function clearUnreadMark(userId) {
    const li = document.querySelector("#userList li[data-user-id='" + userId + "']"); if (li) li.classList.remove("user-unread");
}

function blockUser() { if (!currentChatUserId) return; ws.send(JSON.stringify({ type: "block", fromUserId: currentUserId, blockedId: currentChatUserId })); }
function unblockUser() { if (!currentChatUserId) return; ws.send(JSON.stringify({ type: "unblock", fromUserId: currentUserId, blockedId: currentChatUserId })); }
function recallMessage(messageId) { if (!currentChatUserId) return alert("Vui lòng chọn người dùng"); if (confirm("Bạn có chắc chắn muốn thu hồi tin nhắn này?")) ws.send(JSON.stringify({ type: "recall", messageId: messageId, fromUserId: currentUserId, toUserId: currentChatUserId })); }

function startVideoCall(targetUserId, targetUsername) {
    if (targetUserId && targetUserId !== currentUserId) {
        window.location.href = "/SWP_HUY/videocall.jsp?targetUserId=" + targetUserId + "&targetUsername=" + encodeURIComponent(targetUsername);
    } else {
        alert("Vui lòng chọn người dùng hợp lệ để gọi video!");
    }
}

function showCallInvite(fromUsername) {
    const modal = document.getElementById("callInviteModal");
    const callInviteFrom = document.getElementById("callInviteFrom");
    callInviteFrom.textContent = fromUsername + " muốn gọi video với bạn";
    modal.style.display = "block";
}

function acceptCall() {
    const modal = document.getElementById("callInviteModal");
    modal.style.display = "none";
    window.location.href = "${pageContext.request.contextPath}/videocall.jsp?targetUserId=" + currentChatUserId + "&targetUsername=" + encodeURIComponent(currentChatUserName);
}

function rejectCall() {
    const modal = document.getElementById("callInviteModal");
    ws.send(JSON.stringify({ type: "callRejected", fromUserId: currentUserId, toUserId: currentChatUserId }));
    modal.style.display = "none";
}

function searchUsers(keyword) {
    if (keyword.trim() === "") { fetchInitialUserList(); return; }
    fetch("/SWP_HUY/searchUsers?keyword=" + encodeURIComponent(keyword), { method: "GET", headers: { "Content-Type": "application/json" } })
        .then(response => { if (!response.ok) throw new Error("HTTP error! Status: " + response.status); return response.json(); })
        .then(data => { if (data.error) { console.error(data.error); document.getElementById("userList").innerHTML = "<li>" + data.error + "</li>"; return; } updateUserList(data); })
        .catch(error => { console.error("Lỗi khi tìm kiếm người dùng:", error); document.getElementById("userList").innerHTML = "<li>Lỗi khi tìm kiếm người dùng. Vui lòng thử lại.</li>"; });
}

function fetchInitialUserList() {
    fetch("/SWP_HUY/chatUsers", { method: "GET" })
        .then(response => { if (!response.ok) throw new Error("HTTP error! Status: " + response.status); return response.text(); })
        .then(html => { const parser = new DOMParser(); const doc = parser.parseFromString(html, "text/html"); document.getElementById("userList").innerHTML = doc.querySelector("#userList").innerHTML; })
        .catch(error => { console.error("Lỗi khi tải danh sách người dùng:", error); document.getElementById("userList").innerHTML = "<li>Lỗi khi tải danh sách người dùng. Vui lòng thử lại.</li>"; });
}

function updateUserList(users) {
    const userList = document.getElementById("userList");
    userList.innerHTML = "";
    if (users.length === 0) { userList.innerHTML = "<li>Không tìm thấy người dùng</li>"; return; }
    users.forEach(user => { if (user.userID !== parseInt(currentUserId)) { const li = document.createElement("li"); li.setAttribute("data-user-id", user.userID); li.onclick = () => selectChatUser(user.userID, user.fullName); li.innerHTML = "<img src='/SWP_HUY/assets/avatar/nam.jpg' alt='Avatar' style='width: 48px; height: 48px; border-radius: 50%;'><strong>" + user.fullName + "</strong>"; userList.appendChild(li); } });
}

document.getElementById("searchUser").addEventListener("input", function(e) { searchUsers(e.target.value); });
document.addEventListener("click", function(event) { const dropdown = document.getElementById("userDropDownContent"); if (!event.target.closest(".dropdown")) dropdown.style.display = "none"; });