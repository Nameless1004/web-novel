package com.webnovel.novel.repository;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.webnovel.common.dto.CustomPage;
import com.webnovel.common.exceptions.InvalidRequestException;
import com.webnovel.novel.dto.*;
import com.webnovel.novel.entity.*;
import com.webnovel.novel.enums.NovelStatus;
import com.webnovel.user.entity.QUser;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class NovelCustomRepositoryImpl implements NovelCustomRepository {

    private final JPAQueryFactory queryFactory;
    private final EpisodeViewLogRepository episodeViewLogRepository;

    @Override
    public Optional<NovelDetailsDto> getNovelDetails(long novelId) {
        QNovel novel = QNovel.novel;
        QNovelTags novelTags = QNovelTags.novelTags;
        QTag tag = QTag.tag;
        QNovelSubscribers novelSubscribers = QNovelSubscribers.novelSubscribers;
        QNovelPreferenceUser novelPreferenceUser = QNovelPreferenceUser.novelPreferenceUser;
        QEpisode episode = QEpisode.episode;
        List<String> tags = queryFactory.select(tag.name)
                .from(novelTags)
                .join(novelTags.novel, novel)
                .join(novelTags.tag, tag)
                .fetch();

        NovelDetailsDto novelDetailsDto = queryFactory.select(Projections.constructor(NovelDetailsDto.class,
                        novel.id,
                        novel.title,
                        novel.synopsis,
                        novel.author.nickname,
                        JPAExpressions.select(novelSubscribers.count())
                                .from(novelSubscribers)
                                .where(novelSubscribers.novel.eq(novel)),
                        JPAExpressions.select(novelPreferenceUser.count())
                                .from(novelPreferenceUser)
                                .where(novelPreferenceUser.novel.eq(novel)),
                        JPAExpressions.select(episode.viewCount.sum())
                                .from(episode)
                                .where(episode.novel.eq(novel)),
                        JPAExpressions.select(episode.recommendationCount.sum())
                                .from(episode)
                                .where(episode.novel.eq(novel)),
                        JPAExpressions.select(episode.count())
                                .from(episode)
                                .where(episode.novel.eq(novel))
                ))
                .from(novel)
                .join(novel.author, QUser.user)
                .where(novel.id.eq(novelId))
                .fetchFirst();
        // 2. NovelDetailsDto 생성
        if(novelDetailsDto != null) {
            novelDetailsDto.addTag(tags);
        }
        return Optional.ofNullable(novelDetailsDto);
    }

    @Override
    public CustomPage<HotNovelResponseDto> getRealtimeHotNovelList(String option, int hour, Pageable pageable) {
        if(option.equals("view")) {
            QNovel novel = QNovel.novel;
            QEpisode episode = QEpisode.episode;
            QEpisodeViewLog episodeViewLog = QEpisodeViewLog.episodeViewLog;
            QUser user = QUser.user;
            QTag tag = QTag.tag;
            QNovelTags novelTags = QNovelTags.novelTags;

            Long totalCount = queryFactory.select(novel.id.count())
                    .from(novel)
                    .fetchFirst();

            if(totalCount == null || totalCount == 0) {
                return new CustomPage<>(new ArrayList<HotNovelResponseDto>(), pageable, 0);
            }


            List<Tuple> tuples = queryFactory.select(novel, novel.id.count())
                    .from(episodeViewLog)
                    .leftJoin(episodeViewLog.novel, novel)
                    .leftJoin( novel.author, QUser.user)
                    .where(episodeViewLog.hour.eq(hour))
                    .groupBy(novel.id)
                    .orderBy(novel.id.count().desc())
                    .offset(pageable.getOffset())
                    .limit(pageable.getPageSize())
                    .fetch();

            List<Long> novelIds = tuples.stream().map(x->x.get(novel).getId()).toList();

            Map<Long, List<String>> collect = queryFactory.select(novelTags.novel.id, tag.name)
                    .from(novelTags)
                    .join(novelTags.novel, novel)
                    .join(novelTags.tag, tag)
                    .where(novel.id.in(novelIds))
                    .fetch()
                    .stream()
                    .collect(Collectors.groupingBy(tuple -> tuple.get(
                            novel.id), Collectors.mapping(tuple -> tuple.get(tag.name), Collectors.toList())));

            List<HotNovelResponseDto> list = tuples.stream()
                    .map(x -> (new HotNovelResponseDto(x.get(novel.id.count()), x.get(novel), collect.get(x.get(novel).getId()))))
                    .toList();

            return new CustomPage<>(list, pageable, totalCount);
        } else if(option.equals("recommendation")) {

        } else {
            throw new InvalidRequestException("잘못된 옵션입니다.");
        }

        return null;
    }
}
