package com.webnovel.domain.novel.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.webnovel.common.dto.CustomPage;
import com.webnovel.domain.comment.entity.QComment;
import com.webnovel.domain.novel.dto.EpisodeDetailsDto;
import com.webnovel.domain.novel.dto.EpisodeListDto;
import com.webnovel.domain.novel.entity.QEpisode;
import com.webnovel.domain.novel.entity.QNovel;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class EpisodeCustomRepositoryImpl implements EpisodeCustomRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public CustomPage<EpisodeListDto> getEpisodeLists(Pageable pageable, long novelId) {
        QEpisode episode = QEpisode.episode;
        QNovel novel = QNovel.novel;
        QComment comment = QComment.comment;

        // count가 0면 early return
        Long totalCount = queryFactory.select(episode.count())
                .from(novel)
                .leftJoin(episode).on(episode.novel.id.eq(novel.id))
                .where(novel.id.eq(novelId))
                .fetchFirst();

        if(totalCount == null || totalCount == 0) {
            return new CustomPage<EpisodeListDto>(new ArrayList<EpisodeListDto>(), pageable, 0);
        }

        List<EpisodeListDto> fetch = queryFactory
                .select(Projections.constructor(EpisodeListDto.class,
                        episode.id,
                        episode.episodeNumber,
                        episode.viewCount,
                        episode.recommendationCount,
                        comment.id.count(),
                        episode.title,
                        episode.content,
                        episode.createdAt))
                .from(novel)
                .innerJoin(episode).on(episode.novel.id.eq(novel.id))
                .leftJoin(comment).on(comment.episode.id.eq(episode.id))
                .where(novel.id.eq(novelId))
                .groupBy(episode.id)
                .orderBy(episode.episodeNumber.asc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        return new CustomPage<>(fetch, pageable, totalCount);
    }

    @Override
    public Optional<EpisodeDetailsDto> getEpisode(long episodeId) {
        QEpisode episode = QEpisode.episode;
        QComment comment = QComment.comment;
        QNovel novel = QNovel.novel;

        EpisodeDetailsDto episodeDetailsDto = queryFactory.select(Projections.constructor(EpisodeDetailsDto.class, episode.id, novel.id, episode.episodeNumber, episode.viewCount, episode.recommendationCount, comment.id.count(), episode.title, episode.content, episode.authorReview))
                .from(episode)
                .leftJoin(comment).on(comment.episode.id.eq(episode.id))
                .innerJoin(episode.novel, novel)
                .where(episode.id.eq(episodeId))
                .groupBy(episode.id)
                .fetchFirst();

        if(episodeDetailsDto == null) {
            return Optional.empty();
        }

        Long previousEpisodeId = queryFactory
                .select(episode.id)
                .from(episode)
                .where(episode.novel.id.eq(episodeDetailsDto.getNovelId()), episode.episodeNumber.lt(episodeDetailsDto.getEpisodeNumber()))
                .orderBy(episode.episodeNumber.desc()) // 최신 회차 우선
                .fetchFirst(); // 첫 번째 결과만 반환

        Long nextEpisodeId = queryFactory
                .select(episode.id)
                .from(episode)
                .where(episode.novel.id.eq(episodeDetailsDto.getNovelId()), episode.episodeNumber.gt(episodeDetailsDto.getEpisodeNumber()))
                .orderBy(episode.episodeNumber.asc()) // 가장 이른 회차 우선
                .fetchFirst(); // 첫 번째 결과만 반환

        episodeDetailsDto.setNextEpisodeId(nextEpisodeId);
        episodeDetailsDto.setPrevEpisodeId(previousEpisodeId);

        return Optional.of(episodeDetailsDto);
    }
}
