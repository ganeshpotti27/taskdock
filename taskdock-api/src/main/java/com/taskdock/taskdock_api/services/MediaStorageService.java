package com.taskdock.taskdock_api.services;

import com.taskdock.taskdock_api.dtos.media.MediaUploadResponse;
import org.springframework.web.multipart.MultipartFile;

public interface MediaStorageService {

  MediaUploadResponse upload(MultipartFile file, String folderName);

  void delete(String publicId);
}
