package com.todo.user.util;


import com.cloudinary.Cloudinary;
import com.todo.user.config.CloudinaryConfig;
import com.todo.user.enums.FileType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Component
public class CloudinaryUtil {

    private static final Logger LOGGER = LogManager.getLogger(CloudinaryUtil.class);

    @Autowired
    private CloudinaryConfig config;

    public String uploadFile(MultipartFile multipartFile) {
        String url = null;
        if (null != multipartFile) {
            Map<String, Object> map = new HashMap<>();
            map.put("cloud_name", config.getCloudName());
            map.put("api_key", config.getApiKey());
            map.put("api_secret", config.getApiSecret());
            String filename = multipartFile.getOriginalFilename();
            String extension = filename.substring(filename.lastIndexOf('.') + 1);
            try {
                File file = File.createTempFile("img", extension);
                multipartFile.transferTo(file);
                Cloudinary cloudinary = new Cloudinary(map);
                Map<?, ?> result = null;
                Map<String, Object> request = new HashMap<>();
                request.put("resource_type", "auto");
                result = cloudinary.uploader().upload(file, request);
                url = (String) result.get("secure_url");
            } catch (IllegalStateException | IOException e1) {
                LOGGER.debug("file not found");
            }

        }

        return url;
    }

    public String uploadFile(String base64ImageData) {
        String url = null;
        if (null != base64ImageData) {
            Map<String, Object> map = new HashMap<>();
            map.put("cloud_name", config.getCloudName());
            map.put("api_key", config.getApiKey());
            map.put("api_secret", config.getApiSecret());
            try {
                Map<?, ?> result = null;
                Cloudinary cloudinary = new Cloudinary(map);
                Map<String, Object> request = new HashMap<>();
                request.put("resource_type", "auto");
                String[] image64 = base64ImageData.split(",");
                File outputfile = new File(UUID.randomUUID().toString());
                ImageIO.write(decodeToImage(image64[1]), image64[0].split(";")[0].split("/")[1].toString(), outputfile);
                result = cloudinary.uploader().upload(outputfile.getAbsolutePath(), request);
                outputfile.delete();
                url = (String) result.get("secure_url");
            } catch (IOException e) {
                LOGGER.catching(e);
            }

        }

        return url;
    }

    public static BufferedImage decodeToImage(String imageString) {

        BufferedImage image = null;
        byte[] imageByte;
        try {
            imageByte = Base64.getDecoder().decode(imageString);
            ByteArrayInputStream bis = new ByteArrayInputStream(imageByte);
            image = ImageIO.read(bis);
            bis.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return image;
    }


    public String uploadFileNew(String base64Data, FileType fileType) {

        String url = null;
        if (null != base64Data) {
            Map<String, Object> map = new HashMap<>();
            map.put("cloud_name", config.getCloudName());
            map.put("api_key", config.getApiKey());
            map.put("api_secret", config.getApiSecret());
            try {
                Map<?, ?> result = null;
                Cloudinary cloudinary = new Cloudinary(map);
                Map<String, Object> request = new HashMap<>();
                request.put("resource_type", "auto");

                // Extracting base64 data from JSON array
                String[] fileData = base64Data.substring(0, base64Data.length()).split(",");


                // Decoding base64 data based on file type
                switch (fileType) {
                    case IMAGE:
                        String extention = fileData[0].split("/")[1].split(";")[0];
                        String base64File = fileData[1].trim();
                        if(extention=="pdf"){
                            return null;
                        }
                        BufferedImage decodedImage = decodeToImageNew(base64File);
                        if (decodedImage != null) {
                            // Write BufferedImage to temporary file
                            File outputFile = File.createTempFile("temp", "." + extention);
                            ImageIO.write(decodedImage, extention, outputFile);
                            // Upload file to Cloudinary
                            result = cloudinary.uploader().upload(outputFile, request);
                            // Delete temporary file
                            outputFile.delete();
                            // Get the secure URL from Cloudinary response
                            url = (String) result.get("secure_url");
                        } else {
                            LOGGER.error("Failed to decode base64 image data.");
                        }
                        break;

                    case AUDIO:
                        // Decode audio base64 data
                        String base64FileAudio = fileData[1].trim();
                        byte[] audioBytes = decodeToAudio(base64FileAudio);
                        if (audioBytes == null) {
                            LOGGER.error("Failed to decode base64 audio data.");
                            // Handle decoding failure
                            return null;
                        }

                        // Write audio data to temporary file
                        File audioFile = File.createTempFile("temp", ".mp3");
                        try (FileOutputStream fos = new FileOutputStream(audioFile)) {
                            fos.write(audioBytes);
                        } catch (IOException e) {
                            LOGGER.error("Failed to write audio data to file: {}", e.getMessage());
                            // Handle error appropriately
                            return null;
                        }
                        // Upload audio file to Cloudinary
                        try {
                            result = cloudinary.uploader().upload(audioFile, request);
                            url = (String) result.get("secure_url");
                        } catch (IOException e) {
                            LOGGER.error("Failed to upload audio file to Cloudinary: {}", e.getMessage());
                            // Handle error appropriately
                            return null;
                        } finally {
                            // Delete temporary audio file
                            if (!audioFile.delete()) {
                                LOGGER.warn("Failed to delete temporary audio file");
                                // Handle failure to delete file
                            }
                        }
                        break;
                    case VIDEO:
                        // Handle video base64 data
                        String base64FileVideo = fileData[1].trim();

                        byte[] videoBytes = Base64.getDecoder().decode(base64FileVideo);
                        File videoFile = null;
                        try {
                            // Create a temporary file with the ".mp4" extension
                            videoFile = File.createTempFile("temp", ".mp4");
                            // Write the decoded video bytes to the temporary file
                            try (FileOutputStream fos = new FileOutputStream(videoFile)) {
                                fos.write(videoBytes);
                            }
                            // Upload the video file to Cloudinary
                            result = cloudinary.uploader().upload(videoFile, request);
                            // Retrieve the secure URL of the uploaded video
                            url = (String) result.get("secure_url");
                        } catch (IOException e) {
                            LOGGER.error("Error uploading video file to Cloudinary: {}", e.getMessage());
                        } finally {
                            // Delete the temporary file after uploading
                            if (videoFile != null && videoFile.exists()) {
                                videoFile.delete();
                            }
                        }
                        break;
                    case PDF:
                        byte[] pdfBytes1 = Base64.getDecoder().decode(base64Data);
                        File pdfFile = File.createTempFile("temp", ".pdf");
                        try (FileOutputStream fos = new FileOutputStream(pdfFile)) {
                            assert pdfBytes1 != null;
                            fos.write(pdfBytes1);
                        }
                        Map<String, Object> params = new HashMap<>();
                        params.put("resource_type", "auto");
                        params.put("content_type", "pdf");
                        result = cloudinary.uploader().upload(pdfFile, params);
                        url = (String) result.get("secure_url");
                        break;

                    default:
                        LOGGER.error("Unsupported file type.");
                        break;
                }
            } catch (IOException e) {
                LOGGER.error("Error uploading file to Cloudinary: {}", e.getMessage());
            }
        }
        return url;
    }

    public static byte[] decodeToAudio(String audioString) {
        try {
            // Ensure proper padding
            if (audioString.length() % 4 != 0) {
                // Add padding '=' characters until the length is a multiple of 4
                int paddingCount = 4 - (audioString.length() % 4);
                for (int i = 0; i < paddingCount; i++) {
                    audioString += "=";
                }
            }
            return Base64.getDecoder().decode(audioString);
        } catch (IllegalArgumentException e) {
            // Log the problematic input
            System.err.println("Invalid base64 string for audio: " + audioString);
            // Log the exception details
            e.printStackTrace();
            // Return null or handle the error as needed
            return null;
        }
    }

    public static BufferedImage decodeToImageNew(String imageString) {
        BufferedImage image = null;
        byte[] imageByte;
        try {
            imageByte = Base64.getDecoder().decode(imageString);
            ByteArrayInputStream bis = new ByteArrayInputStream(imageByte);
            image = ImageIO.read(bis);
            bis.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return image;
    }

    public Object uploadFileImage(MultipartFile file1) {
        Map<String, Object> map = new HashMap<>();
        map.put("cloud_name", config.getCloudName());
        map.put("api_key", config.getApiKey());
        map.put("api_secret", config.getApiSecret());
        String filename = file1.getOriginalFilename();
        String extension = filename.substring(filename.lastIndexOf('.') + 1);
        try {
            File file = File.createTempFile("image", extension);
            file1.transferTo(file);
            Cloudinary cloudinary = new Cloudinary(map);
            Map<?, ?> result = null;
            Map<String, Object> request = new HashMap<>();
            request.put("resource_type", "image");
            result = cloudinary.uploader().upload(file, request);
            return (String) result.get("secure_url");
        } catch (IOException e) {
            LOGGER.info("error");
            return null;
        }
    }

}


