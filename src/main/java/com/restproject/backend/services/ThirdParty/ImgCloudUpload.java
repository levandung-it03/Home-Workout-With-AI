package com.restproject.backend.services.ThirdParty;

import com.cloudinary.Cloudinary;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ImgCloudUpload {
    Cloudinary cloudinary;

    public Map<String, String> uploadAndReturnInfo(String folder, MultipartFile multipartFile) throws IOException {
        BufferedImage scaledImage = Thumbnails.of(multipartFile.getInputStream()).height(300).asBufferedImage();

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        String formatName = getFormatName(Objects.requireNonNull(multipartFile.getOriginalFilename()));

        ImageIO.write(scaledImage, formatName, outputStream);
        byte[] scaledImageBytes = outputStream.toByteArray();

        var publicId = "home_workout_with_ai/" + folder + "/" + UUID.randomUUID();
        return Map.ofEntries(
            Map.entry("publicId", publicId),
            Map.entry("url", cloudinary.uploader()
                .upload(scaledImageBytes, Map.of("public_id", publicId))
                .get("url").toString())
        );
    }

    public void remove(String publicId) throws IOException {
        cloudinary.uploader().destroy(publicId, Map.of("resource_type", "image"));
    }

    private String getFormatName(String filename) {
        //--Extract the file extension and return the appropriate format
        String format = filename.substring(filename.lastIndexOf('.') + 1);
        return switch (format.toLowerCase()) {
            case "jpg", "jpeg" -> "jpeg";
            case "png" -> "png";
            case "gif" -> "gif";
            default -> "jpeg"; // default format
        };
    }
}
