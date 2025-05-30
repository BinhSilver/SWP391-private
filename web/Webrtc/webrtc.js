let localStream;
let peerConnection;
let config = { iceServers: [{ urls: "stun:stun.l.google.com:19302" }] };

function startWebRTC(roomId, userId) {
    navigator.mediaDevices.getUserMedia({ video: true, audio: true }).then(stream => {
        document.getElementById("localVideo").srcObject = stream;
        localStream = stream;

        peerConnection = new RTCPeerConnection(config);
        stream.getTracks().forEach(track => peerConnection.addTrack(track, stream));

        peerConnection.ontrack = e => {
            document.getElementById("remoteVideo").srcObject = e.streams[0];
        };

        // WebSocket signaling connection
      const ws = new WebSocket(`ws://localhost:8080/SWP_HUY/signaling/${roomId}/${userId}`);


        ws.onmessage = async (message) => {
            const data = JSON.parse(message.data);
            if (data.offer) {
                await peerConnection.setRemoteDescription(new RTCSessionDescription(data.offer));
                const answer = await peerConnection.createAnswer();
                await peerConnection.setLocalDescription(answer);
                ws.send(JSON.stringify({ answer }));
            } else if (data.answer) {
                await peerConnection.setRemoteDescription(new RTCSessionDescription(data.answer));
            } else if (data.candidate) {
                await peerConnection.addIceCandidate(new RTCIceCandidate(data.candidate));
            }
        };

        peerConnection.onicecandidate = event => {
            if (event.candidate) {
                ws.send(JSON.stringify({ candidate: event.candidate }));
            }
        };
    });
}
