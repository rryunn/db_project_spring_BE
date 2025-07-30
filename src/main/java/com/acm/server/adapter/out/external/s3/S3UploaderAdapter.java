// package com.acm.server.adapter.out.external.s3;

// import com.amazonaws.services.s3.AmazonS3;
// import com.acm.server.domain.file.port.out.FileUploadPort;
// import lombok.RequiredArgsConstructor;
// import org.springframework.stereotype.Component;
// import org.springframework.web.multipart.MultipartFile;

// import java.io.IOException;
// import java.util.UUID;

// @Component
// @RequiredArgsConstructor
// public class S3UploaderAdapter implements FileUploadPort {

//     private final AmazonS3 amazonS3;
//     private final S3Properties s3Properties;

//     @Override
//     public String upload(MultipartFile file) {
//         String key = "uploads/" + UUID.randomUUID() + "_" + file.getOriginalFilename();

//         try {
//             amazonS3.putObject(
//                     s3Properties.getBucket(),
//                     key,
//                     file.getInputStream(),
//                     null
//             );
//         } catch (IOException e) {
//             throw new RuntimeException("파일 업로드 실패", e);
//         }

//         return amazonS3.getUrl(s3Properties.getBucket(), key).toString();
//     }
// }
