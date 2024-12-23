package com.webnovel.novel.service;

import com.webnovel.novel.dto.NovelCreateRequestDto;
import com.webnovel.novel.dto.NovelCreateResponseDto;
import com.webnovel.novel.dto.NovelInfoResponseDto;
import com.webnovel.novel.entity.Novel;
import com.webnovel.novel.repository.EpisodeRepository;
import com.webnovel.novel.repository.NovelRepository;
import com.webnovel.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NovelServiceImpl implements NovelService {

    private final NovelRepository novelRepository;
    private final EpisodeRepository episodeRepository;

    @Override
    @Transactional
    public NovelCreateResponseDto createNovel(NovelCreateRequestDto request) {
        return null;
    }

    @Override
    @Transactional
    public void deleteNovel(int novelId) {
        Novel novel = novelRepository.findByNovelIdOrElseThrow(novelId);
        novelRepository.delete(novel);
    }

    @Override
    public NovelInfoResponseDto getNovelDetails(long novelId) {
        Novel novel = novelRepository.findByNovelIdOrElseThrow(novelId);
        User author = novel.getAuthor();

        long episodeCount = episodeRepository.countEpisodeByNovelId(novelId);

        NovelInfoResponseDto build = NovelInfoResponseDto.builder()
                .novelId(novelId)
                .title(novel.getTitle())
                .summary(novel.getSummary())
                .novelStatus(novel.getStatus().name())
                .author(author.getName())
                .viewCount(0)
                .episodeCount(episodeCount)
                .recommendCount(0)
                .preferenceCount(0)
                .notificationCount(0)
                .tags(null)
                .build();
        return null;
    }
}
