package com.webnovel.novel.service;

import com.webnovel.common.dto.CustomPage;
import com.webnovel.common.dto.ResponseDto;
import com.webnovel.novel.dto.*;
import com.webnovel.novel.entity.*;
import com.webnovel.novel.enums.NovelStatus;
import com.webnovel.novel.repository.*;
import com.webnovel.security.jwt.AuthUser;
import com.webnovel.user.entity.User;
import com.webnovel.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
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
    private final NovelPreferenceUserRepository novelPreferenceUserRepository;
    private final NovelSubscribersRepository novelSubscribersRepository;
    private final UserRepository userRepository;
    private final TagRepository tagRepository;
    private final NovelValidator novelValidator;
    private final NovelTagRepository novelTagRepository;

    @Override
    public ResponseDto<NovelCreateResponseDto> createNovel(AuthUser authUser, NovelCreateRequestDto request) {
        User user = userRepository.findByUsernameOrElseThrow(authUser.getUsername());

        // 타이틀 중복 검사
        novelValidator.checkDuplicatedNovelTitle(request.getTitle());

        Novel savedNovel = novelRepository.save(new Novel(user, request.getTitle(), request.getSynopsis(), NovelStatus.PUBLISHING, LocalDateTime.now()));

        List<Tag> tags = tagRepository.findAllById(request.getTagIds());

        List<NovelTags> novelTags = tags.stream()
                .map(x -> new NovelTags(savedNovel, x))
                .toList();

        novelTagRepository.saveAll(novelTags);

        List<String> tagNames = tags.stream()
                .map(Tag::getName)
                .toList();

        return ResponseDto.of(HttpStatus.CREATED,
                NovelCreateResponseDto.builder()
                    .novelId(savedNovel.getId())
                    .tag(tagNames)
                    .authorUsername(authUser.getUsername())
                    .summary(savedNovel.getSynopsis())
                    .build());
    }

    @Override
    public ResponseDto<Void> updateNovel(AuthUser authUser, long novelId, NovelUpdateDto request) {
        Novel novel = novelRepository.findByNovelIdOrElseThrow(novelId);
        novelValidator.checkAuthority(authUser, novel);
        // 타이틀 중복 검사
        novelValidator.checkDuplicatedNovelTitle(request.getTitle());

        List<Tag> tags = tagRepository.findAllById(request.getTagIds());
        List<NovelTags> novelTags = tags.stream()
                .map(x -> new NovelTags(novel, x))
                .toList();

        novelTagRepository.deleteAllByNovel(novel);
        List<NovelTags> newTags = novelTagRepository.saveAllAndFlush(novelTags);
        novel.update(request, newTags);

        return ResponseDto.of(HttpStatus.OK, "성공적으로 수정 됐습니다.");
    }

    @Override
    public ResponseDto<Void> deleteNovel(AuthUser authUser, long novelId) {
        Novel novel = novelRepository.findByNovelIdOrElseThrow(novelId);
        novelRepository.delete(novel);
        return ResponseDto.of(HttpStatus.OK, "성공적으로 삭제됐습니다.");
    }

    @Override
    public ResponseDto<NovelInfoResponseDto> getNovelDetails(long novelId) {
        Novel novel = novelRepository.findByNovelIdOrElseThrow(novelId);
        User author = novel.getAuthor();

        long episodeCount = episodeRepository.countEpisodeByNovelId(novelId);

        NovelInfoResponseDto build = NovelInfoResponseDto.builder()
                .novelId(novelId)
                .title(novel.getTitle())
                .summary(novel.getSynopsis())
                .novelStatus(novel.getStatus().name())
                .author(author.getName())
                .viewCount(0)
                .episodeCount(episodeCount)
                .recommendationCount(0)
                .preferenceCount(0)
                .notificationCount(0)
                .tags(null)
                .build();
        return null;
    }

    /**
     * 소설 목록 페이징 조회
     * @param page
     * @param size
     * @return
     */
    @Override
    public ResponseDto<CustomPage<NovelListDto>> getNovelList(int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<Novel> result = novelRepository.findAllByOrderByLastUpdatedAtDesc(pageable);
        List<NovelListDto> list = result.getContent().stream()
                .map(NovelListDto::new)
                .toList();

        return ResponseDto.of(HttpStatus.OK, new CustomPage<>(list, pageable, result.getTotalElements()));
    }

    /**
     * 선호 작품 등록
     * @param authUser
     * @param novelId
     * @return
     */
    public ResponseDto<Void> registPreferenceNovel(AuthUser authUser, long novelId) {
        Novel novel = novelRepository.findByNovelIdOrElseThrow(novelId);
        NovelPreferenceUser novelSubscribers = new NovelPreferenceUser(novel, authUser.toUserEntity());
        novelPreferenceUserRepository.save(novelSubscribers);
        return ResponseDto.of(HttpStatus.OK, "성공적으로 등록됐습니다.");
    }

    /**
     * 알림 신청
     * @param authUser
     * @param novelId
     * @return
     */
    @Override
    public ResponseDto<Void> subscribeNovel(AuthUser authUser, long novelId) {
        Novel novel = novelRepository.findByNovelIdOrElseThrow(novelId);
        NovelSubscribers novelSubscribers = new NovelSubscribers(novel, authUser.toUserEntity());
        novelSubscribersRepository.save(novelSubscribers);
        return ResponseDto.of(HttpStatus.OK, "성공적으로 등록됐습니다.");
    }

    /**
     * 에피소드 등록
     * @param authUser
     * @param novelId
     * @param requestDto
     * @return
     */
    @Override
    public ResponseDto<Void> addEpisode(AuthUser authUser, long novelId, EpisodeCreateRequestDto requestDto) {
        Novel novel = novelRepository.findByNovelIdOrElseThrow(novelId);
        novelValidator.checkAuthority(authUser, novel);

        Episode newEpisode = new Episode(novel, requestDto.getTitle(), requestDto.getAuthorReview(), requestDto.getContent(), getLastEpisodeNumber(novel));
        episodeRepository.save(newEpisode);

        return ResponseDto.of(HttpStatus.CREATED, "성공적으로 추가됐습니다.");
    }

    /**
     * 에피소드 수정
     * @param authUser
     * @param novelId
     * @param episodeId
     * @param updateDto
     * @return
     */
    @Override
    public ResponseDto<Void> updateEpisode(AuthUser authUser, long novelId, long episodeId, EpisodeUpdateDto updateDto) {
        Novel novel = novelRepository.findByNovelIdOrElseThrow(novelId);
        Episode episode = episodeRepository.findByIdOrElseThrow(episodeId);
        episode.update(updateDto);

        novelValidator.checkAuthority(authUser, novel);

        return ResponseDto.of(HttpStatus.OK, "성공적으로 수정됐습니다.");
    }

    /**
     * 에피소드 삭제
     * @param novelId
     * @param episodeId
     * @return
     */
    @Override
    public ResponseDto<Void> deleteEpisode(long novelId, long episodeId) {
        Novel novel = novelRepository.findByNovelIdOrElseThrow(novelId);
        Episode episode = episodeRepository.findByIdOrElseThrow(episodeId);
        episodeRepository.delete(episode);
        return ResponseDto.of(HttpStatus.OK, "성공적으로 삭제됐습니다.");
    }

    /**
     * 마지막 회차 번호
     * @param novel
     * @return
     */
    private int getLastEpisodeNumber(Novel novel) {
        return episodeRepository.countEpisodeByNovelId(novel.getId());
    }

    /**
     * 총 추천 수
     * @param novel
     * @return
     */
    private long getTotalRecommendationCount(Novel novel) {
        return episodeRepository.getTotalRecommendationCount(novel.getId());
    }

    /**
     * 총 조회 수
     * @param novel
     * @return
     */
    private long getTotalViewCount(Novel novel) {
        return episodeRepository.getTotalViewCount(novel.getId());
    }
}
