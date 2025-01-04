package com.webnovel.novel.service;

import com.webnovel.common.dto.CustomPage;
import com.webnovel.common.dto.ResponseDto;
import com.webnovel.common.exceptions.AlreadyRecommendedException;
import com.webnovel.common.exceptions.NotFoundException;
import com.webnovel.novel.dto.EpisodeCreateRequestDto;
import com.webnovel.novel.dto.EpisodeDetailsDto;
import com.webnovel.novel.dto.EpisodeListDto;
import com.webnovel.novel.dto.EpisodeUpdateDto;
import com.webnovel.novel.entity.Episode;
import com.webnovel.novel.entity.EpisodeViewLog;
import com.webnovel.novel.entity.Novel;
import com.webnovel.novel.repository.EpisodeRepository;
import com.webnovel.novel.repository.EpisodeViewLogRepository;
import com.webnovel.novel.repository.NovelRepository;
import com.webnovel.security.jwt.AuthUser;
import com.webnovel.user.entity.User;
import io.jsonwebtoken.lang.Strings;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class EpisodeServiceImpl implements EpisodeService {

    private final EpisodeRepository episodeRepository;
    private final NovelValidator novelValidator;
    private final NovelRepository novelRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    private final RedissonClient redissonClient;
    private final EpisodeViewLogRepository episodeViewLogRepository;

    /**
     * 조회수 증가
     *
     * @param authUser
     * @param episodeId
     * @param request
     * @return
     */
    @Override
    public void increaseViewCount(AuthUser authUser, long episodeId, HttpServletRequest request)  {
        String clientIp = request.getRemoteAddr();
        String checkKey ="views::episode::" + episodeId +"user-id::"+ authUser.getId() +"::ip::" + clientIp;

        // 하루 지나면 조회수 재증가 가능
        long ttl = getSecondUntilMidnight();

        if(redisTemplate.opsForValue().setIfAbsent(checkKey, "#",ttl, TimeUnit.SECONDS)) {
            var episode = episodeRepository.findByIdWithPessimisticLock(episodeId)
                    .orElseThrow(() -> new NotFoundException("Episode not found / ID " + episodeId));

            // 로깅
            stampViewLog(authUser.toUserEntity(), episode);

            long updatedViewCount = episode.increaseViewcount();
            redisTemplate.opsForValue().set("viewCount::" + episodeId, updatedViewCount);
        }
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
    public long increaseRecommendationCount(AuthUser user, long episodeId) {

        String redisKey = "recommendationCount::episode::" + episodeId+"::user::"+user.getId();

        if (!redisTemplate.opsForValue().setIfAbsent(redisKey, UUID.randomUUID().toString().substring(7), getSecondUntilMidnight(), TimeUnit.SECONDS)) {
            throw new AlreadyRecommendedException("추천은 하루에 한 번 가능합니다.");
        }
        else {
            Episode episode = episodeRepository.findWithPessimisticLockByIdOrElseThrow(episodeId);
            long rCount = episode.increaseRecommendationCount();
            episodeRepository.save(episode);
            return rCount;
        }
    }

    @Override
    public long getRecommendationCount(long episodeId) {
        return episodeRepository.findRecommendationCountById(episodeId)
                .orElseThrow(() -> new NotFoundException("Episode not found / ID " + episodeId));
    }

    /**
     * 조회수 증가시 로깅
     * @param user
     * @param episode
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void stampViewLog(User user, Episode episode) {
        EpisodeViewLog log = new EpisodeViewLog(user, episode);
        episodeViewLogRepository.save(log);
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
     * @param novelId
     * @return
     */
    @Override
    public ResponseDto<CustomPage<EpisodeListDto>> getEpisodeList(int page, int size, long novelId) {
        Pageable pageable = PageRequest.of(page - 1, size);
        return ResponseDto.of(HttpStatus.OK, episodeRepository.getEpisodeLists(pageable, novelId));
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

    private long getSecondUntilMidnight() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime midnight = now.toLocalDate().atStartOfDay().plusDays(1);
        Duration duration = Duration.between(now, midnight);
        return duration.getSeconds();
    }
}
