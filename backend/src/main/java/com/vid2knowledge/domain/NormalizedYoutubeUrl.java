package com.vid2knowledge.domain;

public record NormalizedYoutubeUrl(
        String videoId,
        String canonicalUrl
) {
}
