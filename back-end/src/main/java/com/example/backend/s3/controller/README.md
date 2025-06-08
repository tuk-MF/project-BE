# 📦 S3 이미지 업로드 API 테스트 가이드

이 문서는 AWS S3에 이미지를 업로드하는 테스트 API 사용 방법을 설명합니다.

## ✅ 사전 준비

- AWS S3 버킷 생성 (예: `s3-work-image-tuk-mf`)
- IAM 사용자 권한 부여 (`AmazonS3FullAccess`)
- 다음 정보를 `application.properties` 또는 환경 변수에 설정:



📂 엔드포인트
POST /api/s3/upload
S3 버킷에 이미지를 업로드하고, 해당 이미지의 URL을 반환합니다.

🔸 요청 형식 (Postman 등에서 테스트)
Method: POST

URL: http://localhost:8080/api/s3/upload

Body Type: form-data

Key: file

Value: 업로드할 이미지 파일

https://s3-work-image-tuk-mf.s3.ap-northeast-2.amazonaws.com/uuid-filename.png
