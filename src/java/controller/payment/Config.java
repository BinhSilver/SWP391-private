package controller.payment;

import vn.payos.PayOS;

public class Config {

    public static final String clientId = "8e6560c3-f1cd-4924-a4ba-af56901551b0";
    public static final String apiKey = "13c9fffd-6610-47be-a559-4563e90260f3";
    public static final String checksumKey = "93390554342dc0692c1fe39c6ddfe6ffca7e29bfbca40714a1dda27b688092e9";
    public static final String returnUrl = "http://localhost:8080/TestMerge/ReturnFromPayOS"; //TODO: change to the correct url

    public static PayOS payOS() {
        return new PayOS(clientId, apiKey, checksumKey);
    }

}
