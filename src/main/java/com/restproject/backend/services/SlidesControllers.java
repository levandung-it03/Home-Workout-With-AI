package com.restproject.backend.services;

import com.restproject.backend.dtos.general.ByIdDto;
import com.restproject.backend.dtos.general.ImageDto;
import com.restproject.backend.entities.Slides;
import com.restproject.backend.enums.ErrorCodes;
import com.restproject.backend.exceptions.ApplicationException;
import com.restproject.backend.repositories.SlidesRepository;
import com.restproject.backend.services.ThirdParty.ImgCloudUpload;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SlidesControllers {
    ImgCloudUpload imgCloudUpload;
    SlidesRepository slidesRepository;

    public List<Slides> getAllSlidesForHome() {
        return slidesRepository.findAll();
    }

    @Transactional(rollbackOn = {RuntimeException.class})
    public void deleteSlide(ByIdDto request) throws IOException {
        var storedSlide = slidesRepository.findById(request.getId())
            .orElseThrow(() -> new ApplicationException(ErrorCodes.INVALID_PRIMARY));
        imgCloudUpload.remove(storedSlide.getImagePublicId());
        slidesRepository.deleteById(request.getId());
    }

    public Slides uploadSlide(ImageDto request) throws IOException {
        var infoMap = imgCloudUpload.uploadAndReturnInfo("slides", request.getImage());
        return slidesRepository.save(
            Slides.builder().imagePublicId(infoMap.get("publicId")).imageUrl(infoMap.get("url")).build()
        );
    }
}
