package com.webnovel.domain.novel.service;

import com.webnovel.common.annotations.ExecutionTimeLog;
import com.webnovel.common.dto.CustomPage;
import com.webnovel.common.dto.ResponseDto;
import com.webnovel.common.exceptions.NotFoundException;
import com.webnovel.domain.novel.dto.*;
import com.webnovel.domain.novel.entity.*;
import com.webnovel.domain.novel.enums.NovelStatus;
import com.webnovel.domain.novel.repository.*;
import com.webnovel.domain.security.jwt.AuthUser;
import com.webnovel.domain.user.entity.User;
import com.webnovel.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
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
    private final ApplicationEventPublisher eventPublisher;

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

        eventPublisher.publishEvent(new NovelStatusChangedEvent(savedNovel));

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
        // 제목 그대로이면 중복검사 안하기
        if(!novel.getTitle().equals(request.getTitle())) {
            novelValidator.checkDuplicatedNovelTitle(request.getTitle());
        }

        List<Tag> tags = tagRepository.findAllById(request.getTagIds());
        List<NovelTags> novelTags = tags.stream()
                .map(x -> new NovelTags(novel, x))
                .toList();

        // event

        novelTagRepository.deleteAllByNovel(novel);
        List<NovelTags> newTags = novelTagRepository.saveAllAndFlush(novelTags);
        novel.update(request, newTags);
        NovelStatus prevStatus = novel.getStatus();
        Novel updatedNovel = novelRepository.save(novel);

        // 이전 상태와 다르면 로깅
        if(prevStatus != updatedNovel.getStatus()) {
            eventPublisher.publishEvent(new NovelStatusChangedEvent(updatedNovel));
        }

        return ResponseDto.of(HttpStatus.OK, "성공적으로 수정 됐습니다.");
    }


    @Override
    public ResponseDto<Void> deleteNovel(AuthUser authUser, long novelId) {
        Novel novel = novelRepository.findByNovelIdOrElseThrow(novelId);
        novelRepository.delete(novel);
        return ResponseDto.of(HttpStatus.OK, "성공적으로 삭제됐습니다.");
    }

    @Override
    public ResponseDto<NovelDetailsDto> getNovelDetails(long novelId) {
        return ResponseDto.of(HttpStatus.OK, novelRepository.getNovelDetails(novelId)
                .orElseThrow(() -> new NotFoundException("Novel not found / id: " + novelId)));
    }

    /**
     * 소설 목록 페이징 조회
     *
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

    @ExecutionTimeLog
    @Override
    @Cacheable(cacheNames = "realtime_hot_novels", key = "#hour", cacheManager = "cacheManager")
    public ResponseDto<CustomPage<HotNovelResponseDto>> getRealtimeHotNovels(String option, int hour, int page, int size) {
        CustomPage<HotNovelResponseDto> realtimeHotNovelList = novelRepository.getRealtimeHotNovelList(option, hour, PageRequest.of(page - 1, size));
        log.info("Cache Miss");
        return ResponseDto.of(HttpStatus.OK, realtimeHotNovelList);
    }

    @Override
    public ResponseDto<CustomPage<NovelListDto>> getMyNovelList(AuthUser authUser, int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<Novel> result = novelRepository.findAllByAuthorIdOrderByLastUpdatedAtDesc(authUser.getId(), pageable);
        List<NovelListDto> list = result.getContent().stream()
                .map(NovelListDto::new)
                .toList();
        return ResponseDto.of(HttpStatus.OK, new CustomPage<>(list, pageable, result.getTotalElements()));
    }


    /**
     * 선호 작품 등록
     *
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
     *
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
     * 총 추천 수
     *
     * @param novel
     * @return
     */
    private long getTotalRecommendationCount(Novel novel) {
        return episodeRepository.getTotalRecommendationCount(novel.getId());
    }

    /**
     * 총 조회 수
     *
     * @param novel
     * @return
     */
    private long getTotalViewCount(Novel novel) {
        return episodeRepository.getTotalViewCount(novel.getId());
    }
}
