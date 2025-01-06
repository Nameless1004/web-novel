package com.webnovel.domain.novel.repository;

import com.webnovel.domain.novel.entity.NovelPreferenceUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NovelPreferenceUserRepository extends JpaRepository<NovelPreferenceUser, Integer> {
}
