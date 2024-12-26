package com.webnovel.novel.service;

import com.webnovel.common.exceptions.AccessDeniedException;
import com.webnovel.common.exceptions.DuplicatedException;
import com.webnovel.novel.entity.Novel;
import com.webnovel.novel.repository.NovelRepository;
import com.webnovel.security.jwt.AuthUser;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@Transactional
@RequiredArgsConstructor
public class NovelValidator {

    private final NovelRepository novelRepository;

    /**
     * 타이틀 중복 검사
     * @param title
     */
    public void checkDuplicatedNovelTitle(String title) {
        if(novelRepository.existsByTitle(title)) {
            throw new DuplicatedException(title + "은(는) 이미 존재하는 소설명입니다.");
        }
    }

    /**
     * 권한 검사
     * @param authUser
     * @param novel
     */
    public void checkAuthority(AuthUser authUser, Novel novel) {
        if(!authUser.getUsername().equals(novel.getAuthor().getUsername())) {
            throw new AccessDeniedException();
        }
    }
}
