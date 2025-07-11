package controller.payment;

import vn.payos.PayOS;
import vn.payos.type.PaymentData;
import vn.payos.type.CheckoutResponseData;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Config {
    private static final Properties properties = new Properties();
    private static String clientId;
    private static String apiKey;
    private static String checksumKey;
    
    static {
        try (InputStream input = Config.class.getClassLoader().getResourceAsStream("resources/payOSAPI.properties")) {
            if (input == null) {
                throw new IOException("Không thể tìm thấy file payOSAPI.properties");
            }
            properties.load(input);
            
            // Load các giá trị từ file properties
            clientId = properties.getProperty("payos.clientId");
            apiKey = properties.getProperty("payos.apiKey");
            checksumKey = properties.getProperty("payos.checksumKey");
            
            // Kiểm tra xem có thiếu thông tin không
            if (clientId == null || apiKey == null || checksumKey == null) {
                throw new IOException("Thiếu thông tin cấu hình PayOS trong file properties");
            }
        } catch (IOException e) {
            e.printStackTrace();
            // Sử dụng giá trị mặc định nếu không đọc được file
            clientId = "8e6560c3-f1cd-4924-a4ba-af56901551b0";
            apiKey = "13c9fffd-6610-47be-a559-4563e90260f3";
            checksumKey = "93390554342dc0692c1fe39c6ddfe6ffca7e29bfbca40714a1dda27b688092e9";
        }
    }

    // Khởi tạo PayOS instance
    public static PayOS payOS() {
        return new PayOS(clientId, apiKey, checksumKey);
    }

    // Tạo payment link
    public static String createPaymentLink(int amount, String description, String returnUrl, String cancelUrl) {
        try {
            // Tạo mã đơn hàng duy nhất dựa trên timestamp
            long orderCode = System.currentTimeMillis();

            // Tạo payment data
            PaymentData paymentData = PaymentData.builder()
                    .orderCode(orderCode)
                    .amount(amount)
                    .description(description)
                    .returnUrl(returnUrl)
                    .cancelUrl(cancelUrl)
                    .signature(checksumKey)
                    .items(null)
                    .build();

            // Gọi API để tạo payment link
            CheckoutResponseData response = payOS().createPaymentLink(paymentData);
            
            return response.getCheckoutUrl();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
