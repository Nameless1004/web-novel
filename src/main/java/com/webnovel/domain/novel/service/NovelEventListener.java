package com.webnovel.domain.novel.service;

import com.webnovel.domain.novel.entity.Novel;
import com.webnovel.domain.novel.entity.NovelEventLog;
import com.webnovel.domain.novel.repository.NovelEventLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class NovelEventListener {

    private final NovelEventLogRepository novelEventLogRepository;

    // 트랜잭션이 커밋된 후에 이벤트를 처리
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void onNovelStatusChanged(NovelStatusChangedEvent event) {
        Novel novel = event.getNovel();
        // NovelEventLog 저장 (혹은 다른 처리를 할 수 있음)
        NovelEventLog novelEventLog = new NovelEventLog(novel);
        novelEventLogRepository.save(novelEventLog);
    }
}
