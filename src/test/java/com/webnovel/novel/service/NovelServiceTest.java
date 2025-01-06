package com.webnovel.novel.service;

import com.webnovel.domain.novel.entity.Episode;
import com.webnovel.domain.novel.entity.EpisodeViewLog;
import com.webnovel.domain.novel.entity.Novel;
import com.webnovel.domain.novel.enums.EpisodeStatus;
import com.webnovel.domain.novel.enums.NovelStatus;
import com.webnovel.domain.novel.repository.EpisodeRepository;
import com.webnovel.domain.novel.repository.EpisodeViewLogRepository;
import com.webnovel.domain.novel.repository.NovelRepository;
import com.webnovel.domain.user.entity.User;
import com.webnovel.domain.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.Rollback;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@SpringBootTest
@Rollback(value = false)

class NovelServiceTest {

    @Autowired
    private NovelRepository novelRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private EpisodeRepository episodeRepository;
    @Autowired
    private EpisodeViewLogRepository episodeViewLogRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    public void test() {
        Novel novel = novelRepository.findById(1l).orElse(null);
        User user = userRepository.findById(1l).orElse(null);

        // 에피소드 노벨1000개 생성
        int i = 0;
        var list = new ArrayList<Novel>();
        for(int index = 0; index < 10000; index++) {
            list.add(new Novel(user, "ti", "z", NovelStatus.PUBLISHING, LocalDateTime.now()));
        }

        novelRepository.saveAll(list);
    }

    private static final int BATCH_SIZE = 500;
    private void executeBatchInsert(List<Object[]> batchArgs) {
        String sql = "INSERT INTO episode ( novel_id, title, content, status, episode_number, recommendation_count, view_count, author_review) VALUES (?, ?, ?, ?, ?, ?,?,?)";
        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
                    @Override
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        var command = batchArgs.get(i);
                        ps.setLong(1, (Long) command[0]);
                        ps.setString(2, (String) command[1]);
                        ps.setString(3, (String) command[2]);
                        ps.setString(4, (String) command[3]);
                        ps.setInt(5, (Integer) command[4]);
                        ps.setInt(6, (Integer) command[5]);
                        ps.setInt(7, (Integer) command[6]);
                        ps.setString(8, (String) command[7]);
                    }

                    @Override
                    public int getBatchSize() {
                        return batchArgs.size();
                    }
                }
        );
    }

    @Test
    public void testBulkInsertWithJdbcTemplate() {
        var random = new Random();
        List<Object[]> batchArgs = new ArrayList<>();
        int id = 1;
        for (int index = 0; index < 10000; index++) {
            int novelId = index + 1;
            Novel novel = novelRepository.findById((long) novelId).orElse(null);

            if (novel != null) {

                for (int j = 0; j < 300; j++) {
                    batchArgs.add(new Object[] {
                            novel.getId(),
                            String.valueOf(index),
                            "a",
                            EpisodeStatus.FREE.name(),
                            j,
                            0,
                            0,
                            "zz"
                    });

                    // Batch size를 초과하면 인서트
                    if (batchArgs.size() >= BATCH_SIZE) {
                        executeBatchInsert(batchArgs);
                        batchArgs.clear();
                    }
                }
            }
        }

        // 남은 데이터 인서트
        if (!batchArgs.isEmpty()) {
            executeBatchInsert(batchArgs);
        }
    }

    @Test
    public void test3() {
        User user = userRepository.findById(1L).orElse(null);
        List<Object[]> batchArgs = new ArrayList<>();
        Random random = new Random();
        for(int index = 0; index < 1000000; index++) {
            long episodeRandom = random.nextLong(3000000) + 1;
            Episode episode = episodeRepository.findById(episodeRandom).orElse(null);
            episode.increaseViewcount();
            Episode episode1 = episodeRepository.saveAndFlush(episode);
            episodeViewLogRepository.saveAndFlush(new EpisodeViewLog(user, episode1));
        }
    }

}