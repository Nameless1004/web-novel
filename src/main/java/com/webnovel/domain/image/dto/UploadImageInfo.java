package com.webnovel.domain.image.dto;

import lombok.*;

public record UploadImageInfo(
        String imageUrl,
        String imageKey) { }
