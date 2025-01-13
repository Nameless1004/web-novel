package com.webnovel.domain.image.components;

import com.webnovel.common.exceptions.ImageUploadFailedException;
import com.webnovel.domain.image.dto.UploadImageInfo;
import io.jsonwebtoken.lang.Strings;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetUrlRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.UUID;

@Slf4j(topic = "ImageManager")
@Component
@RequiredArgsConstructor
public class ImageManger {

    @Value("${spring.cloud.aws.s3.bucket}")
    private String bucketName;

    private final S3Client s3Client;


    public UploadImageInfo uploadImage(MultipartFile file) {

        if ( file == null || file.isEmpty()) {
            log.info("Image is empty");
            return new UploadImageInfo("", "");
        }

        String fileName = generateFileName(file);
        try {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .contentType(file.getContentType())
                    .contentLength(file.getSize())
                    .key(fileName)
                    .build();
            RequestBody requestBody = RequestBody.fromBytes(file.getBytes());
            s3Client.putObject(putObjectRequest, requestBody);
        } catch (IOException ex) {
            throw new ImageUploadFailedException("이미지 업로드에 실패하였습니다.");
        }

        GetUrlRequest getUrlRequest = GetUrlRequest.builder()
                .bucket(bucketName)
                .key(fileName)
                .build();
        String url = s3Client.utilities().getUrl(getUrlRequest).toString();

        return new UploadImageInfo(url, fileName);
    }

    public UploadImageInfo updateImage(MultipartFile file, String key) {
        deleteImage(key);
        return uploadImage(file);
    }

    public void deleteImage(String key) {

        if(!Strings.hasText(key)){
            return;
        }

        try {
            DeleteObjectRequest req = DeleteObjectRequest
                    .builder()
                    .bucket(bucketName)
                    .key(key)
                    .build();

            s3Client.deleteObject(req);
        } catch (Exception ex) {
            throw new ImageUploadFailedException("이미지 삭제에 실패하였습니다.");
        }
    }

    private String generateFileName(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        return UUID.randomUUID().toString() + extension;
    }
}
