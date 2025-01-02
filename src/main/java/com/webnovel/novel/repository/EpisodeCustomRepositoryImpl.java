package com.webnovel.novel.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.webnovel.comment.entity.QComment;
import com.webnovel.common.dto.CustomPage;
import com.webnovel.novel.dto.EpisodeDetailsDto;
import com.webnovel.novel.dto.EpisodeListDto;
import com.webnovel.novel.entity.QEpisode;
import com.webnovel.novel.entity.QNovel;
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
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public CustomPage<EpisodeListDto> getEpisodeLists(Pageable pageable, long novelId) {
        QEpisode episode = QEpisode.episode;
        QNovel novel = QNovel.novel;
        QComment comment = QComment.comment;

        // count가 0면 early return
        Long totalCount = queryFactory.select(episode.count())
                .from(novel)
                .leftJoin(episode).on(episode.novel.id.eq(novel.id))
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

        return Optional.ofNullable(jpaQueryFactory.select(Projections.constructor(EpisodeDetailsDto.class, episode.id, episode.episodeNumber, episode.viewCount, episode.recommendationCount, comment.id.count(), episode.title, episode.content))
                .from(episode)
                .leftJoin(comment).on(comment.episode.id.eq(episode.id))
                .where(episode.id.eq(episodeId))
                .groupBy(episode.id)
                .fetchFirst());
    }
}
