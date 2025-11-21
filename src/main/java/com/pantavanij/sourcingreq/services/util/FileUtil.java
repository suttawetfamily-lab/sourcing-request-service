package com.pantavanij.sourcingreq.services.util;

import com.pantavanij.sourcingreq.services.exception.AppException;
import com.pantavanij.sourcingreq.services.exception.BadRequestException;
import io.minio.ErrorCode;
import io.minio.MinioClient;
import io.minio.errors.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.xmlpull.v1.XmlPullParserException;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.util.*;

@Slf4j
@Scope("prototype")
@Component
public class FileUtil {

    @Value("${minio.bucket}")
    private String bucketName;

    @Value("${minio.folder}")
    private String folderName;

    @Autowired
    private MinioClient minioClient;

    public void uploadFile(MultipartFile file, String uniqueFileName) {

        try {
            initialBucket();

            byte[] bytes = file.getBytes();
            Path tempFile = Files.createTempFile("tmp", ".tmp");
            ByteArrayInputStream bis = new ByteArrayInputStream(bytes);

            copyFile(bis, tempFile);

            String objectName = getFolderName( uniqueFileName, null);

            if (isFileExistsInBucket(uniqueFileName,  null)) {
                removeFile(uniqueFileName,  null);
            }

            Map<String, String> headerMap = new HashMap<>();
            headerMap.put("Content-Type", "application/octet-stream");

            minioClient.putObject(bucketName
                    , objectName
                    , tempFile.toString()
                    , file.getSize()
                    , headerMap
                    , null
                    , "application/octet-stream");

        } catch (Exception ex) {
            log.error("UploadFile Exception : " + ex.getMessage(), ex);
            throw new BadRequestException("UploadFile File Exception");
        }
    }

    public void uploadFileByte(byte[] bytes, String uniqueFileName, String folderName) {

        try {
            initialBucket();

            Path tempFile = Files.createTempFile("tmp", ".tmp");
            ByteArrayInputStream bis = new ByteArrayInputStream(bytes);

            copyFile(bis, tempFile);

            String objectName = getFolderName( uniqueFileName, folderName);

            if (isFileExistsInBucket(uniqueFileName,  folderName)) {
                removeFile(uniqueFileName,  folderName);
            }

            Map<String, String> headerMap = new HashMap<>();
            headerMap.put("Content-Type", "application/octet-stream");

            minioClient.putObject(bucketName
                    , objectName
                    , tempFile.toString()
                    , Long.valueOf(bytes.length)
                    , headerMap
                    , null
                    , "application/octet-stream");

        } catch (Exception ex) {
            log.error("UploadFile Exception : " + ex.getMessage(), ex);
            throw new BadRequestException("UploadFile File Exception");
        }
    }

    public String getObjectURL(String uniqueName, String folderName) throws InvalidBucketNameException, InsufficientDataException, XmlPullParserException, ErrorResponseException, NoSuchAlgorithmException, IOException, NoResponseException, InvalidKeyException, InvalidResponseException, InternalException {
        String objectName = getFolderName( uniqueName, folderName);
        //String objectUrl = minioClient.getObjectUrl(bucketName, objectName);
        String objectUrl = null;
        try {
            objectUrl = minioClient.presignedGetObject(bucketName, objectName);
        } catch (InvalidExpiresRangeException e) {
            e.printStackTrace();
        }
        return objectUrl;
    }

    public byte[] downloadFile(String uniqueName, String folderName) {
        byte[] content = null;
        try {
            boolean isExist = minioClient.bucketExists(bucketName);
            if (!isExist) {
                return new byte[0];
            }
            String objectName = getFolderName( uniqueName, folderName);
            InputStream file = minioClient.getObject(bucketName, objectName);
            content = IOUtils.toByteArray(file);
            file.close();
        } catch (ErrorResponseException ex) {
            log.error("Download File Exception : " + ex.getMessage());
            if (ex.errorResponse().code().equals(ErrorCode.NO_SUCH_OBJECT.code())) {
                throw new AppException(ex.errorResponse().message());
            } else {
                throw new AppException(ex.errorResponse().message());
            }
        } catch (IOException
                | InvalidBucketNameException | NoSuchAlgorithmException
                | InsufficientDataException | InvalidKeyException
                | NoResponseException | XmlPullParserException
                | InternalException | InvalidArgumentException
                | InvalidResponseException ex) {
            log.error("Download File Exception : " + ex.getMessage(), ex);
            throw new AppException(ex);
        }

        return content;
    }

    private void initialBucket() throws Exception {
        if (!minioClient.bucketExists(bucketName)) {
            minioClient.makeBucket(bucketName);
        }
    }

    public void copyFile(InputStream in, Path destination) throws IOException {
        Files.copy(in, destination, StandardCopyOption.REPLACE_EXISTING);
    }

    public boolean isFileExistsInBucket(String uniqueName,  String folderName) {
        try {
            String objectName = getFolderName( uniqueName, folderName);
            minioClient.getObject(bucketName, objectName);
        } catch (Exception ex) {
            return false;
        }
        return true;
    }

    public boolean removeFile(String uniqueName, String folderName) throws Exception {
        try {

            String objectName = getFolderName( uniqueName, folderName);

            if (isFileExistsInBucket(uniqueName,  folderName)) {
                minioClient.removeObject(bucketName, objectName);
                return true;
            } else {
                return false;
            }

        } catch (Exception ex) {
            log.error("Remove File Exception : " + ex.getMessage(), ex);
            throw new BadRequestException("Remove File Exception");
        }
    }

    public String getMimeType(String fileName) throws IOException {
        Path path = new File(getExtensionByStringHandling(fileName)).toPath();
        return Files.probeContentType(path);
    }

    public static String getExtensionByStringHandling(String filename) {
        return Optional.of(filename)
                .filter(f -> f.contains("."))
                .map(f -> f.substring(filename.lastIndexOf("."))).orElse("");
    }

    public static boolean validateFileSize(Long fileSize, int limitFileSize) {
        double MB = 1024 * 1024;
        double fileSizeMb = fileSize / MB;
        if (fileSizeMb > limitFileSize) {
            return false;
        }
        return true;
    }

    public static boolean isAllowedFileType(String originalFileName) {
        String fileExtension = FilenameUtils.getExtension(originalFileName);
        return Arrays.stream(ALLOWED_FILE_TYPE)
                .parallel()
                .anyMatch(fileExtension::equalsIgnoreCase);
    }

    public static String[] ALLOWED_FILE_TYPE =
            (new String[] {
                    "ppt",
                    "pptx",
                    "doc",
                    "docx",
                    "xls",
                    "xlsx",
                    "pdf",
                    "txt",
                    "jpg",
                    "jpeg",
                    "png",
                    "eml",
                    "msg"
            });

    public static String generateFileUniqueName(String originalFileName) {
        if (originalFileName == null) {
            originalFileName = "";
        }
        return RandomStringUtils.randomAlphabetic(10).toLowerCase() +
                System.currentTimeMillis() +
                "_" +
                originalFileName.replaceAll("[^a-zA-Z0-9._-]", "");
    }

    private String getFolderName( String uniqueFileName, String customFolderName) {
        String objectName = folderName + "/" + uniqueFileName;

        if(null != customFolderName && !customFolderName.trim().isEmpty()) {
            objectName = folderName + "/" +customFolderName + "/" +  uniqueFileName;
        }

        return objectName;
    }

    public static float covertByteToMB(float fileSize) {
        fileSize = fileSize / (float) (1024 * 1024);
        DecimalFormat decimalFormat = new DecimalFormat("#.####");
        return Float.parseFloat(decimalFormat.format(fileSize));
    }

    public static boolean deleteFile(String filePath) {
        File f = new File(filePath);
        return f.delete();
    }

    public static boolean deleteMultipleFiles(List<String> fileList) {
        boolean result = true;
        for (String filePath : fileList) {
            result &= deleteFile(filePath);
        }
        return result;
    }

    public static int getFileSize(URL url) {
        URLConnection conn = null;
        try {
            conn = url.openConnection();
            if(conn instanceof HttpURLConnection) {
                ((HttpURLConnection)conn).setRequestMethod("HEAD");
            }
            conn.getInputStream();
            return conn.getContentLength();
        } catch (IOException e) {
            //throw new RuntimeException(e);
            return 0;
        } finally {
            if(conn instanceof HttpURLConnection) {
                ((HttpURLConnection)conn).disconnect();
            }
        }
    }

    public static byte[] getHtmlByteArray(final String url) {
        URL htmlUrl = null; //www.java2s.com
        InputStream inStream = null;
        try {
            htmlUrl = new URL(url);
            URLConnection connection = htmlUrl.openConnection();
            connection.setRequestProperty("Authorization", "Bearer " + AppUtil.getJwtToken());
            HttpURLConnection httpConnection = (HttpURLConnection) connection;
            int responseCode = httpConnection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                inStream = httpConnection.getInputStream();
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        byte[] data = inputStreamToByte(inStream);

        return data;
    }

    public static byte[] inputStreamToByte(InputStream is) {
        try {
            ByteArrayOutputStream bytestream = new ByteArrayOutputStream();
            int ch;
            while ((ch = is.read()) != -1) {
                bytestream.write(ch);
            }
            byte imgdata[] = bytestream.toByteArray();
            bytestream.close();
            return imgdata;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static String getFileExtension(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            return "";
        }
        int lastDotIndex = fileName.lastIndexOf('.');
        return (lastDotIndex > 0) ? fileName.substring(lastDotIndex + 1) : "";
    }

    public static String getFileName(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        if (originalFilename != null && !originalFilename.isEmpty()) {
           return originalFilename;
        }
        return "";
    }

    public static long getFileSizeInBytes(MultipartFile file) {
        return Math.toIntExact(file.getSize());
    }

    public static double getFileSizeInKB(MultipartFile file) {
        return file.getSize() / 1024.0;
    }

    public static double getFileSizeInMB(MultipartFile file) {
        return file.getSize() / (1024.0 * 1024.0);
    }

    public static String getFormattedFileSize(MultipartFile file) {
        long bytes = file.getSize();

        if (bytes < 1024) {
            return bytes + " B";
        }
        if (bytes < 1024 * 1024) {
            return String.format("%.2f KB", bytes / 1024.0);
        }
        return String.format("%.2f MB", bytes / (1024.0 * 1024.0));
    }

}