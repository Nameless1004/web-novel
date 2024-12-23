package com.webnovel.novel.service;

import com.webnovel.common.dto.ResponseDto;
import com.webnovel.novel.dto.*;
import com.webnovel.novel.entity.Episode;
import com.webnovel.novel.entity.Novel;
import com.webnovel.novel.entity.NovelSubscribers;
import com.webnovel.novel.entity.Tag;
import com.webnovel.novel.enums.NovelStatus;
import com.webnovel.novel.repository.*;
import com.webnovel.security.jwt.AuthUser;
import com.webnovel.user.entity.User;
import com.webnovel.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class NovelServiceImpl implements NovelService {

    private final NovelRepository novelRepository;
    private final EpisodeRepository episodeRepository;
    private final NovelPreferenceUser novelPreferenceUser;
    private final NovelSubscribersRepository novelSubscribersRepository;
    private final UserRepository userRepository;
    private final TagRepository tagRepository;

    @Override
    public NovelCreateResponseDto createNovel(AuthUser authUser, NovelCreateRequestDto request) {
        User user = userRepository.findByUsernameOrElseThrow(authUser.getUsername());
        Novel savedNovel = novelRepository.save(new Novel(user, request.getTitle(), request.getSummary(), NovelStatus.PUBLISHING, LocalDateTime.now()));

        List<Tag> tags = request.getTags()
                .stream()
                .map(x-> new Tag(savedNovel, x))
                .toList();

        tagRepository.saveAll(tags);

        List<String> tagNames = tags.stream()
                .map(x -> x.getName())
                .toList();

        return NovelCreateResponseDto.builder()
                .novelId(savedNovel.getId())
                .tag(tagNames)
                .authorUsername(authUser.getUsername())
                .summary(savedNovel.getSummary())
                .build();
    }

    @Override
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

    public ResponseDto<Void> addEpisodeToNovel(AuthUser authUser, long novelId, EpisodeCreateRequestDto requestDto) {
        Novel novel = novelRepository.findByNovelIdOrElseThrow(novelId);
        checkAuthority(authUser, novel);

        Episode newEpisode = new Episode(novel, requestDto.getTitle(), requestDto.getAuthorReview(), requestDto.getContent(), getLastEpisodeNumber(novel));
        episodeRepository.save(newEpisode);

        return ResponseDto.of(HttpStatus.CREATED, "성공적으로 추가됐습니다.");
    }

    public ResponseDto<Void> updateEpisode(AuthUser authUser, long novelId, long episodeId, EpisodeUpdateDto updateDto) {
        Novel novel = novelRepository.findByNovelIdOrElseThrow(novelId);
        Episode episode = episodeRepository.findByIdOrElseThrow(episodeId);
        episode.update(updateDto);
        checkAuthority(authUser, novel);

        return ResponseDto.of(HttpStatus.OK, "성공적으로 수정됐습니다.");
    }

    public ResponseDto<Void> deleteEpisode(long novelId, long episodeId) {
        Novel novel = novelRepository.findByNovelIdOrElseThrow(novelId);
        Episode episode = episodeRepository.findByIdOrElseThrow(episodeId);
        episodeRepository.delete(episode);
    }

    private int getLastEpisodeNumber(Novel novel) {
        return Math.max(episodeRepository.countEpisodeByNovelId(novel.getId()) - 1, 0);
    }

    private long getTotalRecommendationCount(Novel novel) {
        return episodeRepository.getTotalRecommendationCount(novel.getId());
    }

    private long getTotalViewCount(Novel novel) {
        return episodeRepository.getTotalViewCount(novel.getId());
    }

    private void checkAuthority(AuthUser authUser, Novel novel) {
        if(!authUser.getUsername().equals(novel.getAuthor().getUsername())) {
            throw new AccessDeniedException("권한이 없습니다.");
        }
    }
}
