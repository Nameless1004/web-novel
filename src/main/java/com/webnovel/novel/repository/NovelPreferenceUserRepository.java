package com.webnovel.novel.repository;

import com.webnovel.novel.entity.NovelPreferenceUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NovelPreferenceUserRepository extends JpaRepository<NovelPreferenceUser, Integer> {
}
