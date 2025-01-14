package com.webnovel.domain.novel.repository;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.webnovel.common.dto.CustomPage;
import com.webnovel.common.exceptions.InvalidRequestException;
import com.webnovel.domain.comment.entity.QComment;
import com.webnovel.domain.novel.dto.HotNovelResponseDto;
import com.webnovel.domain.novel.dto.NovelDetailsDto;
import com.webnovel.domain.novel.dto.NovelListDto;
import com.webnovel.domain.novel.dto.NovelOrderCondition;
import com.webnovel.domain.novel.entity.*;
import com.webnovel.domain.security.jwt.AuthUser;
import com.webnovel.domain.user.entity.QUser;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.webnovel.domain.novel.entity.QNovel.novel;
import static com.webnovel.domain.novel.entity.QNovelTags.novelTags;
import static com.webnovel.domain.novel.entity.QTag.tag;

@Repository
@RequiredArgsConstructor
public class NovelCustomRepositoryImpl implements NovelCustomRepository {

    private final JPAQueryFactory queryFactory;
    private final EpisodeViewLogRepository episodeViewLogRepository;

    @Override
    public CustomPage<NovelListDto> getNovelList(NovelOrderCondition orderCondition, Order order, Pageable pageable) {
        if(order == null || orderCondition == null) {
            throw new InvalidRequestException("잘못된 order 파라미터입니다.");
        }

        OrderSpecifier[] orderSpecifier = createOrderSpecifier(orderCondition, order);

        Long totalCount = queryFactory.select(novel.count())
                .from(novel).fetchFirst();

        if(totalCount == null) {
            return new CustomPage<>(new ArrayList<NovelListDto>(), pageable, 0);
        }

        List<NovelListDto> content = queryFactory.select(Projections.constructor(NovelListDto.class, novel.id, novel.title, novel.author.nickname, novel.publishedAt, novel.lastUpdatedAt, novel.coverImageUrl))
                .from(novel)
                .innerJoin(novel.author, QUser.user)
                .orderBy(orderSpecifier)
                .limit(pageable.getPageSize())
                .offset(pageable.getOffset())
                .fetch();

        List<Long> novelIds = content.stream().map(NovelListDto::getNovelId).toList();

        Map<Long, List<String>> tags = queryFactory.select(novelTags.novel.id, tag.name)
                .from(novelTags)
                .join(novelTags.novel, novel)
                .join(novelTags.tag, tag)
                .where(novel.id.in(novelIds))
                .fetch()
                .stream()
                .collect(Collectors.groupingBy(tuple ->
                        tuple.get(novelTags.novel.id),
                        Collectors.mapping(tuple -> tuple.get(tag.name),
                        Collectors.toList())));

        for (NovelListDto novelListDto : content) {
            if(!tags.containsKey(novelListDto.getNovelId())) {
                novelListDto.setTags(new ArrayList<>());
            } else {
                novelListDto.setTags(tags.get(novelListDto.getNovelId()));
            }
        }

        return new CustomPage<>(content, pageable, totalCount);
    }

    private OrderSpecifier[] createOrderSpecifier(NovelOrderCondition orderCondition, Order order) {
        List<OrderSpecifier> orderSpecifiers = new ArrayList<>();

        if(orderCondition.equals(NovelOrderCondition.NEW)) {
            orderSpecifiers.add(new OrderSpecifier(order, novel.publishedAt));
        }

        return orderSpecifiers.toArray(new OrderSpecifier[orderSpecifiers.size()]);
    }

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
                .innerJoin(novelTags.novel, novel)
                .innerJoin(novelTags.tag, tag)
                .where(novelTags.novel.id.eq(novelId))
                .fetch();

        NovelDetailsDto novelDetailsDto = queryFactory.select(Projections.constructor(NovelDetailsDto.class,
                        novel.id,
                        novel.title,
                        novel.synopsis,
                        novel.author.nickname,
                        novel.coverImageUrl,
                        novel.status,
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
                                .where(episode.novel.eq(novel)),
                        JPAExpressions.select(QComment.comment.id.count())
                                .from(QComment.comment)
                                .innerJoin(QComment.comment.episode, episode)
                                .innerJoin(episode.novel, novel)
                                .where(novel.id.eq(novelId))
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
            QEpisodeViewLog episodeViewLog = QEpisodeViewLog.episodeViewLog;
            QTag tag = QTag.tag;
            QNovelTags novelTags = QNovelTags.novelTags;

            Long totalCount = queryFactory.select(novel.id.count())
                    .from(novel)
                    .fetchFirst();

            if(totalCount == null || totalCount == 0) {
                return new CustomPage<>(new ArrayList<HotNovelResponseDto>(), pageable, 0);
            }

            LocalDate today = LocalDate.now();
            LocalDateTime startOfDay = today.atStartOfDay();
            LocalDateTime endOfDay = today.plusDays(1).atStartOfDay();

            List<Tuple> results = queryFactory.select(novel, novel.id.count())
                    .from(episodeViewLog)
                    .innerJoin(episodeViewLog.novel, novel)
                    .innerJoin( novel.author, QUser.user)
                    .where(episodeViewLog.hour.eq(hour).and(episodeViewLog.timestamp.between(startOfDay, endOfDay)))
                    .groupBy(novel.id)
                    .orderBy(novel.id.count().desc())
                    .offset(pageable.getOffset())
                    .limit(pageable.getPageSize())
                    .fetch();

            List<Long> novelIds = results.stream().map(x->x.get(novel).getId()).toList();

            List<Tuple> fetch = queryFactory.select(novel.id, tag.name)
                    .from(novel)
                    .innerJoin(novel.tags, novelTags)
                    .innerJoin(novelTags.tag, tag)
                    .where(novel.id.in(novelIds))
                    .fetch();

            HashMap<Long, List<String>> map = new HashMap<>();

            for(Tuple tup : fetch) {
                Long n = tup.get(novel.id);
                String t = tup.get(tag.name);
                if(!map.containsKey(n)) {
                    map.put(n, new ArrayList<>());
                }

                map.get(n).add(t);
            }

            List<HotNovelResponseDto> content = new ArrayList<>();
           for (Tuple tup : results) {
               Novel n = tup.get(novel);
               long count = tup.get(novel.id.count());
               if(!map.containsKey(n.getId())) {
                   content.add(new HotNovelResponseDto(count, n, new ArrayList<>()));
               } else {
                    content.add(new HotNovelResponseDto(count, n, map.get(n.getId())));
               }
           }

            return new CustomPage<>(content, pageable, totalCount);
        } else if(option.equals("recommendation")) {

        } else {
            throw new InvalidRequestException("잘못된 옵션입니다.");
        }

        return null;
    }

    @Override
    public CustomPage<NovelListDto> getMyNovels(AuthUser authUser, Pageable pageable) {
        QUser user = QUser.user;
        Long totalCount = queryFactory.select(novel.count())
                .from(novel)
                .join(user).on(novel.author.id.eq(authUser.getId()))
                .fetchFirst();

        if(totalCount == null) {
            return new CustomPage<>(new ArrayList<NovelListDto>(), pageable, 0);
        }

        List<NovelListDto> content = queryFactory.select(Projections.constructor(NovelListDto.class, novel.id, novel.title, novel.author.nickname, novel.publishedAt, novel.lastUpdatedAt, novel.coverImageUrl))
                .from(novel)
                .innerJoin(novel.author, user)
                .where(novel.author.id.eq(authUser.getId()))
                .orderBy(novel.lastUpdatedAt.desc())
                .limit(pageable.getPageSize())
                .offset(pageable.getOffset())
                .fetch();

        List<Long> novelIds = content.stream().map(NovelListDto::getNovelId).toList();

        Map<Long, List<String>> tags = queryFactory.select(novelTags.novel.id, tag.name)
                .from(novelTags)
                .join(novelTags.novel, novel)
                .join(novelTags.tag, tag)
                .where(novel.id.in(novelIds))
                .fetch()
                .stream()
                .collect(Collectors.groupingBy(tuple ->
                                tuple.get(novelTags.novel.id),
                        Collectors.mapping(tuple -> tuple.get(tag.name),
                                Collectors.toList())));

        for (NovelListDto novelListDto : content) {
            if(!tags.containsKey(novelListDto.getNovelId())) {
                novelListDto.setTags(new ArrayList<>());
            } else {
                novelListDto.setTags(tags.get(novelListDto.getNovelId()));
            }
        }

        return new CustomPage<>(content, pageable, totalCount);
    }
}
