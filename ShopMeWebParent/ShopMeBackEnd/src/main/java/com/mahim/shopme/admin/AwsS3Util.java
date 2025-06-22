package com.mahim.shopme.admin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.ListObjectsRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Object;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class AwsS3Util {
    private static final Logger LOG = LoggerFactory.getLogger(AwsS3Util.class);

    private static final String BUCKET_NAME;

    static {
        BUCKET_NAME = System.getenv("AWS_BUCKET_NAME");
    }

    public static List<String> listFolder(String folderName) {
        try (S3Client s3Client = S3Client.builder().build()) {
            var listRequest = ListObjectsRequest.builder().bucket(BUCKET_NAME).prefix(folderName + "/").build();
            var listResponse = s3Client.listObjects(listRequest);

            return listResponse.contents().stream()
                    .map(S3Object::key)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            System.out.println("error occurred: " + e.getMessage());
            return Collections.emptyList();
        }
    }

    public static void uploadFile(String folderName, String fileName, InputStream inputStream) {
        try (S3Client s3Client = S3Client.builder().build(); inputStream) {
            var putRequest = PutObjectRequest.builder()
                    .bucket(BUCKET_NAME)
                    .key(folderName + "/" + fileName)
                    .contentType(getContentType(fileName))
                    .build();
            int contentLength = inputStream.available();
            var response = s3Client.putObject(putRequest, RequestBody.fromInputStream(inputStream, contentLength));
            System.out.println("response for put request: " + response.bucketKeyEnabled());
        } catch (IOException e) {
            LOG.error("Could not upload file: {} due to: ", fileName, e);
        }
    }

    public static void deleteFile(String fileName) {
        try (S3Client s3Client = S3Client.builder().build()) {
            var deleteRequest = DeleteObjectRequest.builder()
                    .bucket(BUCKET_NAME)
                    .key(fileName)
                    .build();

            s3Client.deleteObject(deleteRequest);
        } catch (Exception e) {
            LOG.error("Could not delete file: {} due to: ", fileName, e);
        }
    }

    public static void removeFolder(String folderName) {
        listFolder(folderName + "/").forEach(AwsS3Util::deleteFile);
    }

    private static String getContentType(String fileName) {
        String extension = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
        return switch (extension) {
            case "jpg", "jpeg" -> "image/jpeg";
            case "png" -> "image/png";
            case "gif" -> "image/gif";
            case "bmp" -> "image/bmp";
            case "svg" -> "image/svg+xml";
            default -> "application/octet-stream";
        };
    }

    public static void main(String[] args) throws FileNotFoundException {
        System.out.println(AwsS3Util.listFolder("test-upload"));

        String folderName = "test-upload";
        String fileName = "s3_test_2.jpg";
        String filePath = "C:\\Users\\mahim\\Pictures\\" + fileName;
        InputStream inputStream = new FileInputStream(filePath);
//        AwsS3Util.uploadFile(folderName, fileName, inputStream);

//        AwsS3Util.deleteFile("test-upload/aws_s3_test_image.png");
//        AwsS3Util.removeFolder("test-upload");
        AwsS3Util.listFolder("user-photos/1/").forEach(System.out::println);
    }
}
