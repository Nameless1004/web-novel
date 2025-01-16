package com.webnovel.domain.novel.service;

import com.querydsl.core.types.Order;
import com.webnovel.common.dto.CustomPage;
import com.webnovel.common.dto.ResponseDto;
import com.webnovel.common.exceptions.InvalidRequestException;
import com.webnovel.domain.novel.dto.NovelListDto;
import com.webnovel.domain.novel.dto.NovelOrderCondition;
import com.webnovel.domain.novel.enums.NovelSearchCondition;
import com.webnovel.domain.novel.repository.NovelRepository;
import com.webnovel.domain.user.entity.User;
import com.webnovel.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class NovelSearchServiceImpl implements NovelSearchService {

    private final NovelRepository novelRepository;

    @Override
    @Transactional(readOnly = true)
    public ResponseDto<CustomPage<NovelListDto>> searchNovelByKeyword(String searchCondition, String orderCondition, String direction, String keyword, int page, int size) {
        NovelSearchCondition sc = NovelSearchCondition.parse(searchCondition);
        NovelOrderCondition oc = NovelOrderCondition.parse(orderCondition);
        Order order = parseOrder(direction);
        Pageable pageable = PageRequest.of(page - 1, size);

        return ResponseDto.of(HttpStatus.OK, novelRepository.search(sc, oc, order, keyword, pageable));
    }

    private Order parseOrder(String order) {
        Order[] values = Order.values();
        for (Order orderValue : values) {
            if(orderValue.name().equalsIgnoreCase(order)) {
                return orderValue;
            }
        }

        throw new InvalidRequestException("Invalid Order condition: " +  order);
    }
}
