package com.restproject.backend.services.ThirdParty;

import com.cloudinary.Cloudinary;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ImgCloudUpload {
    Cloudinary cloudinary;

    public Map<String, String> uploadAndReturnInfo(String folder, MultipartFile multipartFile) throws IOException {
        var publicId = "home_workout_with_ai/" + folder + "/" + UUID.randomUUID().toString();
        return Map.ofEntries(
            Map.entry("publicId", publicId),
            Map.entry("url", cloudinary.uploader()
                .upload(multipartFile.getBytes(), Map.of("public_id", publicId))
                .get("url").toString())
        );
    }

    public void remove(String publicId) throws IOException {
        cloudinary.uploader().destroy(publicId, Map.of());
    }
}
