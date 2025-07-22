package config;

import java.io.InputStream;
import java.util.Properties;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;

public class S3Util {
    private static AmazonS3 s3;
    private static String bucket;

    static {
        try {
            Properties prop = new Properties();
            InputStream input = S3Util.class.getResourceAsStream("/resources/cloud.properties");
            prop.load(input);
            input.close();

            String accessKey = prop.getProperty("AWS_ACCESS_KEY");
            String secretKey = prop.getProperty("AWS_SECRET_KEY");
            String region = prop.getProperty("AWS_REGION");
            bucket = prop.getProperty("AWS_S3_BUCKET");

            BasicAWSCredentials awsCreds = new BasicAWSCredentials(accessKey, secretKey);
            s3 = AmazonS3ClientBuilder.standard()
                    .withRegion(region)
                    .withCredentials(new AWSStaticCredentialsProvider(awsCreds))
                    .build();
        } catch (Exception e) {
            throw new RuntimeException("Error initializing S3: " + e.getMessage(), e);
        }
    }

    public static String uploadFile(InputStream is, long size, String key, String contentType) {
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(size);
        metadata.setContentType(contentType);
        PutObjectRequest putRequest = new PutObjectRequest(bucket, key, is, metadata);
        s3.putObject(putRequest);
        // Trả về link public
        return "https://" + bucket + ".s3." + s3.getRegionName() + ".amazonaws.com/" + key;
    }

    public static void deleteFile(String key) {
        try {
            s3.deleteObject(bucket, key);
            System.out.println("[LOG][S3Util] Đã xóa file trên S3: " + key);
        } catch (Exception e) {
            System.out.println("[ERROR][S3Util] Lỗi khi xóa file trên S3: " + key + " - " + e.getMessage());
            e.printStackTrace();
        }
    }
} 