package com.webnovel.novel.schedules;

import com.webnovel.novel.repository.EpisodeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.*;

@Component
@Slf4j
@RequiredArgsConstructor
public class ViewCountSaveSchedule {

    private final RedisTemplate<String, Object> redisTemplate;
    private final EpisodeRepository episodeRepository;

    //@Scheduled(cron = "0 0/1 * * * ?")
    @Scheduled(fixedRate = 10000)
    @Transactional
    public void schedule() throws InterruptedException {
        Set<String> keys = redisTemplate.keys("viewCount*");
        if(Objects.requireNonNull(keys).isEmpty()) return;
        Map<Long, Long> updates = new HashMap<>();
        for( String viewCountKey : keys) {
            while (!redisTemplate.opsForValue().setIfAbsent(viewCountKey+"#lock", 1L, Duration.ofMinutes(5))) {
                Thread.sleep(100);
            }

            try {
                var episodeId = extractEpisodeIdFromKey(viewCountKey);
                Integer r = (Integer) redisTemplate.opsForValue().get(viewCountKey);
                updates.put(episodeId, r.longValue());
                episodeRepository.updateViewcount(episodeId, r.longValue());
                redisTemplate.delete(viewCountKey);
            } finally {
                redisTemplate.delete(viewCountKey);
                redisTemplate.delete(viewCountKey+"#lock");
            }
        }
        log.info("test");
    }

    private Long extractEpisodeIdFromKey(String viewCountKey) {
        return Long.parseLong(viewCountKey.replace("viewCount::", ""));
    }
}

