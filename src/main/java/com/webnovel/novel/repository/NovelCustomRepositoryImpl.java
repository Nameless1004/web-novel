package com.webnovel.novel.repository;

import com.querydsl.core.types.Expression;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.webnovel.novel.dto.NovelDetailsDto;
import com.webnovel.novel.entity.*;
import com.webnovel.user.entity.QUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class NovelCustomRepositoryImpl implements NovelCustomRepository {

    private final JPAQueryFactory queryFactory;

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
                .join(novelTags.tag, tag)
                .where(novelTags.novel.eq(novel))
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
}
