package com.webnovel.novel.service;

import com.webnovel.common.dto.CustomPage;
import com.webnovel.common.dto.ResponseDto;
import com.webnovel.common.exceptions.NotFoundException;
import com.webnovel.novel.dto.EpisodeCreateRequestDto;
import com.webnovel.novel.dto.EpisodeDetailsDto;
import com.webnovel.novel.dto.EpisodeListDto;
import com.webnovel.novel.dto.EpisodeUpdateDto;
import com.webnovel.novel.entity.Episode;
import com.webnovel.novel.entity.Novel;
import com.webnovel.novel.repository.EpisodeRepository;
import com.webnovel.novel.repository.NovelRepository;
import com.webnovel.security.jwt.AuthUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class EpisodeServiceImpl implements EpisodeService {

    private final EpisodeRepository episodeRepository;
    private final NovelValidator novelValidator;
    private final NovelRepository novelRepository;
    private final RedisTemplate<String, Object> redisTemplate;

    /**
     * 조회수 증가
     *
     * @param episodeId
     * @return
     */
    @Override
    public long increaseViewCount(long episodeId) {
        // Redis 조회 수 증가
        String redisKey = "viewCount::" + episodeId;
        Long updatedViewCount = redisTemplate.opsForValue().increment(redisKey, 1);

        if (updatedViewCount == null) {
            long initValue = episodeRepository.findViewCountById(episodeId)
                    .orElseThrow(() -> new NotFoundException("Episode not found / ID " + episodeId));

            redisTemplate.opsForValue().set(redisKey, initValue);
        }

        return updatedViewCount;
    }

    @Override
    public long increaseViewCountNoncache(long episodeId) {

        var episode = episodeRepository.findById(episodeId)
                .orElseThrow(() -> new NotFoundException("Episode not found / ID " + episodeId));

        return episode.increaseViewcount();
    }

    @Override
    public long increaseViewCountPessimisticLock(long episodeId) {
        var episode = episodeRepository.findByIdWithPessimisticLock(episodeId)
                .orElseThrow(() -> new NotFoundException("Episode not found / ID " + episodeId));
        return episode.increaseViewcount();
    }

    @Override
    public long increaseViewCountOptimisticLock(long episodeId) {
        var episode = episodeRepository.findByIdWithOptimisticLock(episodeId)
                .orElseThrow(() -> new NotFoundException("Episode not found / ID " + episodeId));
        return episode.increaseViewcount();
    }


    /**
     * 조회수 조회
     *
     * @param episodeId
     * @return
     */
    @Override
    @Caching(cacheable = @Cacheable(cacheNames = "viewCount", key = "#episodeId"))
    public long getViewCount(long episodeId) {

        return episodeRepository.findViewCountById(episodeId)
                .orElseThrow(() -> new NotFoundException("Episode not found / ID " + episodeId));
    }

    @Override
    public long getViewCountNoncache(long episodeId) {
        return episodeRepository.findViewCountById(episodeId)
                .orElseThrow(() -> new NotFoundException("Episode not found / ID " + episodeId));
    }
    /**
     * 에피소드 등록
     *
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
     *
     * @param authUser
     * @param novelId
     * @param episodeId
     * @param updateDto
     * @return
     */
    @Override
    public ResponseDto<Void> updateEpisode(AuthUser authUser, long novelId, long episodeId, EpisodeUpdateDto updateDto) {
        Novel novel = novelRepository.findByNovelIdOrElseThrow(novelId);
        novelValidator.checkAuthority(authUser, novel);

        Episode episode = episodeRepository.findByIdOrElseThrow(episodeId);
        episode.update(updateDto);


        return ResponseDto.of(HttpStatus.OK, "성공적으로 수정됐습니다.");
    }

    /**
     * 에피소드 삭제
     *
     * @param novelId
     * @param episodeId
     * @return
     */
    @Override
    public ResponseDto<Void> deleteEpisode(AuthUser authUser, long novelId, long episodeId) {
        Novel novel = novelRepository.findByNovelIdOrElseThrow(novelId);
        novelValidator.checkAuthority(authUser, novel);

        Episode episode = episodeRepository.findByIdOrElseThrow(episodeId);
        episodeRepository.updateEpisodeNumbersAfterDeletion(novelId, episode.getEpisodeNumber());
        episodeRepository.delete(episode);

        return ResponseDto.of(HttpStatus.OK, "성공적으로 삭제됐습니다.");
    }

    /**
     * 에피소드 목록 조회
     * @param page
     * @param size
     * @param episodeId
     * @return
     */
    @Override
    public ResponseDto<CustomPage<EpisodeListDto>> getEpisodeList(int page, int size, long episodeId) {
        Pageable pageable = PageRequest.of(page - 1, size);
        return ResponseDto.of(HttpStatus.OK, episodeRepository.getEpisodeLists(pageable, episodeId));
    }

    /**
     * 에피소드 단건 조회
     * @param authUser
     * @param episodeId
     * @return
     */
    @Override
    public ResponseDto<EpisodeDetailsDto> getEpisodeDetails(AuthUser authUser, long episodeId) {
        EpisodeDetailsDto episodeDetailsDto = episodeRepository.getEpisode(episodeId)
                .orElseThrow(() -> new NotFoundException("Episode not found / ID " + episodeId));
        return ResponseDto.of(HttpStatus.OK, episodeDetailsDto);
    }

    /**
     * 마지막 회차 번호
     *
     * @param novel
     * @return
     */
    private int getLastEpisodeNumber(Novel novel) {
        return episodeRepository.countEpisodeByNovelId(novel.getId());
    }
}
