    package com.example.backend.s3;

    import com.amazonaws.services.s3.AmazonS3;
    import com.amazonaws.services.s3.model.ObjectMetadata;
    import lombok.RequiredArgsConstructor;
    import org.springframework.stereotype.Component;
    import org.springframework.web.multipart.MultipartFile;

    import java.io.IOException;
    import java.util.UUID;

    @Component
    @RequiredArgsConstructor
    public class S3Uploader {

        private final AmazonS3 amazonS3;

        private final String bucketName = "s3-work-image-tuk-mf";

        public String uploadFile(MultipartFile multipartFile) throws IOException {
            String fileName = UUID.randomUUID() + "-" + multipartFile.getOriginalFilename();

            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(multipartFile.getSize());
            metadata.setContentType(multipartFile.getContentType());

            amazonS3.putObject(bucketName, fileName, multipartFile.getInputStream(), metadata);

            return amazonS3.getUrl(bucketName, fileName).toString();
        }
    }
