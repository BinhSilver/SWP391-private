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
        try (InputStream input = Config.class.getClassLoader().getResourceAsStream("payOSAPI.properties")) {
            if (input == null) {
                System.out.println("WARNING: Không thể tìm thấy file payOSAPI.properties, sử dụng giá trị mặc định");
                throw new IOException("Không thể tìm thấy file payOSAPI.properties");
            }
            properties.load(input);
            
            // Load các giá trị từ file properties
            clientId = properties.getProperty("payos.clientId");
            apiKey = properties.getProperty("payos.apiKey");
            checksumKey = properties.getProperty("payos.checksumKey");
            
            // Kiểm tra xem có thiếu thông tin không
            if (clientId == null || apiKey == null || checksumKey == null) {
                System.out.println("ERROR: Thiếu thông tin cấu hình PayOS trong file properties");
                throw new IOException("Thiếu thông tin cấu hình PayOS trong file properties");
            }
            
            System.out.println("SUCCESS: PayOS configuration loaded successfully");
        } catch (IOException e) {
            System.out.println("ERROR loading PayOS config: " + e.getMessage());
            e.printStackTrace();
            // Sử dụng giá trị mặc định nếu không đọc được file
            clientId = "8e6560c3-f1cd-4924-a4ba-af56901551b0";
            apiKey = "13c9fffd-6610-47be-a559-4563e90260f3";
            checksumKey = "93390554342dc0692c1fe39c6ddfe6ffca7e29bfbca40714a1dda27b688092e9";
            System.out.println("Using default PayOS configuration");
        }
    }

    // Khởi tạo PayOS instance
    public static PayOS payOS() {
        return new PayOS(clientId, apiKey, checksumKey);
    }

    // Tạo payment link với orderCode được chỉ định
    public static String createPaymentLink(int amount, String description, String returnUrl, String cancelUrl, long orderCode) {
        try {
            System.out.println("Creating payment link with amount: " + amount + ", description: " + description);
            System.out.println("Using OrderCode: " + orderCode);

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
            
            String checkoutUrl = response.getCheckoutUrl();
            System.out.println("Payment link created successfully: " + checkoutUrl);
            return checkoutUrl;
        } catch (Exception e) {
            System.out.println("ERROR creating payment link: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    // Tạo payment link (backward compatibility method - deprecated)
    @Deprecated
    public static String createPaymentLink(int amount, String description, String returnUrl, String cancelUrl) {
        long orderCode = System.currentTimeMillis();
        System.out.println("WARNING: Using deprecated createPaymentLink method. Auto-generated OrderCode: " + orderCode);
        return createPaymentLink(amount, description, returnUrl, cancelUrl, orderCode);
    }
}
