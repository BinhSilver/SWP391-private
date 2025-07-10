<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Chọn Phương Thức Thanh Toán</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
    <style>
        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            line-height: 1.6;
            margin: 0;
            padding: 20px;
            background-color: #f5f5f5;
        }
        .container {
            max-width: 800px;
            margin: 0 auto;
            background: white;
            padding: 30px;
            border-radius: 10px;
            box-shadow: 0 2px 4px rgba(0,0,0,0.1);
        }
        .payment-info {
            background: #f8f9fa;
            padding: 20px;
            border-radius: 5px;
            margin: 20px 0;
            text-align: center;
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
        .payment-methods {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
            gap: 20px;
            margin: 30px 0;
        }
        .payment-method {
            border: 2px solid #e9ecef;
            border-radius: 10px;
            padding: 20px;
            text-align: center;
            cursor: pointer;
            transition: all 0.3s ease;
        }
        .payment-method:hover {
            border-color: #007bff;
            transform: translateY(-5px);
        }
        .payment-method.selected {
            border-color: #28a745;
            background-color: #f8fff9;
        }
        .payment-method i {
            font-size: 2em;
            margin-bottom: 10px;
            color: #007bff;
        }
        .payment-method h3 {
            margin: 10px 0;
            color: #343a40;
        }
        .payment-method p {
            color: #6c757d;
            font-size: 0.9em;
            margin: 0;
        }
        .buttons {
            display: flex;
            gap: 10px;
            justify-content: center;
            margin-top: 30px;
        }
        .btn {
            padding: 12px 25px;
            border: none;
            border-radius: 5px;
            cursor: pointer;
            font-weight: 500;
            text-decoration: none;
            transition: all 0.3s ease;
        }
        .btn-primary {
            background-color: #007bff;
            color: white;
        }
        .btn-primary:hover {
            background-color: #0056b3;
            transform: translateY(-2px);
        }
        .btn-secondary {
            background-color: #6c757d;
            color: white;
        }
        .btn-secondary:hover {
            background-color: #545b62;
            transform: translateY(-2px);
        }
        .payment-logos {
            display: flex;
            justify-content: center;
            align-items: center;
            gap: 15px;
            margin-top: 20px;
        }
        .payment-logos img {
            height: 30px;
            opacity: 0.7;
            transition: opacity 0.3s ease;
        }
        .payment-logos img:hover {
            opacity: 1;
        }
    </style>
</head>
<body>
    <div class="container">
        <h2 style="text-align: center;">Chọn Phương Thức Thanh Toán</h2>
        
        <div class="payment-info">
            <div class="amount">
                Số tiền: <%= String.format("%,d", (Integer)request.getAttribute("amount")) %>₫
            </div>
            <div class="description">
                <%= request.getAttribute("description") %>
            </div>
        </div>

        <div class="payment-methods">
            <div class="payment-method" onclick="selectPaymentMethod('qr')">
                <i class="fas fa-qrcode"></i>
                <h3>QR Code (PayOS)</h3>
                <p>Quét mã QR để thanh toán nhanh chóng qua ứng dụng ngân hàng</p>
            </div>
            
            <div class="payment-method" onclick="selectPaymentMethod('card')">
                <i class="fas fa-credit-card"></i>
                <h3>Thẻ Tín Dụng/Ghi Nợ</h3>
                <p>Thanh toán an toàn qua cổng thanh toán</p>
            </div>
            
            <div class="payment-method" onclick="selectPaymentMethod('ewallet')">
                <i class="fas fa-wallet"></i>
                <h3>Ví Điện Tử</h3>
                <p>MoMo, ZaloPay, VNPay, ...</p>
            </div>
            
            <div class="payment-method" onclick="selectPaymentMethod('bank')">
                <i class="fas fa-university"></i>
                <h3>Chuyển Khoản Ngân Hàng</h3>
                <p>Chuyển khoản trực tiếp đến tài khoản của chúng tôi</p>
            </div>
        </div>

        <div class="buttons">
            <button onclick="proceedPayment()" class="btn btn-primary">Tiếp Tục Thanh Toán</button>
            <a href="javascript:history.back()" class="btn btn-secondary">Quay Lại</a>
        </div>

        <div class="payment-logos">
            <img src="https://upload.wikimedia.org/wikipedia/commons/thumb/5/5e/Visa_Inc._logo.svg/2560px-Visa_Inc._logo.svg.png" alt="Visa">
            <img src="https://upload.wikimedia.org/wikipedia/commons/thumb/2/2a/Mastercard-logo.svg/1280px-Mastercard-logo.svg.png" alt="Mastercard">
            <img src="https://download.logo.wine/logo/Momo_(payment_service)/Momo_(payment_service)-Logo.wine.png" alt="MoMo">
            <img src="https://cdn.haitrieu.com/wp-content/uploads/2022/10/Icon-ZaloPay.png" alt="ZaloPay">
        </div>
    </div>

    <script>
        let selectedMethod = '';

        function selectPaymentMethod(method) {
            selectedMethod = method;
            // Remove selected class from all methods
            document.querySelectorAll('.payment-method').forEach(el => {
                el.classList.remove('selected');
            });
            // Add selected class to clicked method
            event.currentTarget.classList.add('selected');
        }

        function proceedPayment() {
            if (!selectedMethod) {
                alert('Vui lòng chọn phương thức thanh toán');
                return;
            }

            // Redirect based on selected payment method
            switch(selectedMethod) {
                case 'qr':
                    window.location.href = '<%= request.getAttribute("paymentUrl") %>';
                    break;
                case 'card':
                    // Thay thế URL này bằng URL xử lý thanh toán thẻ của bạn
                    window.location.href = 'CardPayment';
                    break;
                case 'ewallet':
                    // Thay thế URL này bằng URL xử lý thanh toán ví điện tử của bạn
                    window.location.href = 'EWalletPayment';
                    break;
                case 'bank':
                    // Thay thế URL này bằng URL xử lý chuyển khoản ngân hàng của bạn
                    window.location.href = 'BankTransfer';
                    break;
            }
        }
    </script>
</body>
</html> 