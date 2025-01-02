package com.webnovel.novel.service;

import com.webnovel.common.dto.CustomPage;
import com.webnovel.common.dto.ResponseDto;
import com.webnovel.common.exceptions.NotFoundException;
import com.webnovel.novel.dto.*;
import com.webnovel.novel.entity.*;
import com.webnovel.novel.enums.NovelStatus;
import com.webnovel.novel.repository.*;
import com.webnovel.security.jwt.AuthUser;
import com.webnovel.user.entity.User;
import com.webnovel.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    private final RedisTemplate<String, Object> redisTemplate;

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
        // 제목 그대로이면 중복검사 안하기
        if(!novel.getTitle().equals(request.getTitle())) {
            novelValidator.checkDuplicatedNovelTitle(request.getTitle());
        }

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
