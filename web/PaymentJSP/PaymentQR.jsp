<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Thanh Toán Qua PayOS</title>
    <style>
        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            line-height: 1.6;
            margin: 0;
            padding: 20px;
            background-color: #f5f5f5;
        }
        .container {
            max-width: 600px;
            margin: 0 auto;
            background: white;
            padding: 30px;
            border-radius: 10px;
            box-shadow: 0 2px 4px rgba(0,0,0,0.1);
        }
        .qr-container {
            text-align: center;
            margin: 20px 0;
        }
        .qr-code {
            max-width: 300px;
            margin: 20px auto;
        }
        .payment-info {
            background: #f8f9fa;
            padding: 20px;
            border-radius: 5px;
            margin: 20px 0;
        }
        .amount {
            font-size: 24px;
            color: #28a745;
            font-weight: bold;
            margin: 10px 0;
        }
        .description {
            color: #6c757d;
            margin: 10px 0;
        }
        .buttons {
            display: flex;
            gap: 10px;
            justify-content: center;
            margin-top: 20px;
        }
        .btn {
            padding: 10px 20px;
            border: none;
            border-radius: 5px;
            cursor: pointer;
            font-weight: 500;
            text-decoration: none;
        }
        .btn-primary {
            background-color: #007bff;
            color: white;
        }
        .btn-primary:hover {
            background-color: #0056b3;
        }
        .btn-secondary {
            background-color: #6c757d;
            color: white;
        }
        .btn-secondary:hover {
            background-color: #545b62;
        }
        .timer {
            text-align: center;
            margin: 20px 0;
            font-size: 18px;
            color: #dc3545;
        }
        .navbar {
            background-color: #007bff;
            padding: 10px 20px;
            color: white;
            display: flex;
            align-items: center;
        }
        .navbar .back-btn {
            background: none;
            border: none;
            color: white;
            font-size: 18px;
            margin-right: 10px;
            cursor: pointer;
            text-decoration: none;
        }
        .navbar-title {
            font-size: 20px;
            font-weight: bold;
        }
    </style>
</head>
<body>
    <div class="navbar">
        <a href="<%= request.getContextPath() %>/index.jsp" class="back-btn">&larr; Quay về Trang Chủ</a>
        <span class="navbar-title">Thanh Toán</span>
    </div>
    <div class="container">
        <h2 style="text-align: center;">Quét Mã QR Để Thanh Toán</h2>
        
        <div class="payment-info">
            <div class="amount">
Số tiền: <%= String.format("%,d", (Integer)request.getAttribute("amount")) %>₫
            </div>
            <div class="description">
                <%= request.getAttribute("description") %>
            </div>
        </div>

        <div class="qr-container">
            <img src="<%= request.getAttribute("qrCode") %>" alt="Mã QR Thanh Toán" class="qr-code">
        </div>

        <div class="timer">
            Mã QR có hiệu lực trong <span id="countdown">15:00</span>
        </div>

        <div class="buttons">
            <a href="<%= request.getAttribute("paymentUrl") %>" target="_blank" class="btn btn-primary">
                Mở Ứng Dụng Ngân Hàng
            </a>
            <a href="javascript:history.back()" class="btn btn-secondary">Quay Lại</a>
        </div>
    </div>

    <script>
        // Countdown timer
        function startTimer(duration, display) {
            var timer = duration, minutes, seconds;
            var countdown = setInterval(function () {
                minutes = parseInt(timer / 60, 10);
                seconds = parseInt(timer % 60, 10);

                minutes = minutes < 10 ? "0" + minutes : minutes;
                seconds = seconds < 10 ? "0" + seconds : seconds;

                display.textContent = minutes + ":" + seconds;

                if (--timer < 0) {
                    clearInterval(countdown);
                    display.textContent = "Đã hết hạn";
                }
            }, 1000);
        }

        window.onload = function () {
            var fifteenMinutes = 60 * 15,
                display = document.querySelector('#countdown');
            startTimer(fifteenMinutes, display);
        };
    </script>
</body>
</html>