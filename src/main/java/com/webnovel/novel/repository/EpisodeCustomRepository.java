package com.webnovel.novel.repository;

import com.webnovel.common.dto.CustomPage;
import com.webnovel.novel.dto.EpisodeDetailsDto;
import com.webnovel.novel.dto.EpisodeListDto;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface EpisodeCustomRepository {

    public CustomPage<EpisodeListDto> getEpisodeLists(Pageable pageable, long novelId);
    public Optional<EpisodeDetailsDto> getEpisode(long episodeId);
}
