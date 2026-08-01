package com.courshare.course.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.time.Duration;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/courses/presigned-url")
public class S3Controller {

    private final S3Presigner s3Presigner;
    private static final String BUCKET_NAME = "courshare-media-hls-bucket";

    public S3Controller(S3Presigner s3Presigner) {
        this.s3Presigner = s3Presigner;
    }

    @GetMapping
    public Map<String, String> getPresignedUrl(
            @RequestParam String fileName,
            @RequestParam String contentType
    ) {
        String sanitizedFileName = fileName.replaceAll("[^a-zA-Z0-9.-]", "_");
        String uniqueId = UUID.randomUUID().toString();
        
        // Key của video gốc tải lên
        String s3Key = "raw-videos/" + uniqueId + "-" + sanitizedFileName;

        // Xây dựng PutObjectRequest
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(BUCKET_NAME)
                .key(s3Key)
                .contentType(contentType)
                .build();

        // Tạo yêu cầu Presign với thời hạn 30 phút
        PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(30))
                .putObjectRequest(putObjectRequest)
                .build();

        // Sinh Presigned URL
        PresignedPutObjectRequest presignedPutObjectRequest = s3Presigner.presignPutObject(presignRequest);
        String uploadUrl = presignedPutObjectRequest.url().toString();

        // Tính toán URL đầu ra dạng HLS phát trực tuyến của học viên
        // Tên file không có đuôi mở rộng
        String fileNameNoExt = sanitizedFileName.contains(".") ? sanitizedFileName.substring(0, sanitizedFileName.lastIndexOf('.')) : sanitizedFileName;
        String folderName = uniqueId + "-" + fileNameNoExt;
        String videoUrl = String.format("https://%s.s3.ap-southeast-1.amazonaws.com/hls/raw-videos/%s/playlist.m3u8", BUCKET_NAME, folderName);

        return Map.of(
                "uploadUrl", uploadUrl,
                "videoUrl", videoUrl
        );
    }
}
