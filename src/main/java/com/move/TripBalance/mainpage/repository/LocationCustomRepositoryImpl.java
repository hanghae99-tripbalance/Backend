package com.move.TripBalance.mainpage.repository;

import com.move.TripBalance.mainpage.Location;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.move.TripBalance.mainpage.QLocation.location;

@Repository
@RequiredArgsConstructor
public class LocationCustomRepositoryImpl implements LocationCustomRepository{

    private final JPAQueryFactory jpaQueryFactory;

    // 파라미터 값과 같은 Location 만 추출
    @Override
    public Location findByResult(String resultString){

        // 모든 location 추출
        return jpaQueryFactory.selectFrom(location)
                .where(location.result.eq(resultString))
                .fetchOne();
    }

    // 모든 Location 결과를 페이징 처리
    @Override
    public Page<Location> findAll(Pageable pageable){

        List<Location> locationPage = getLocationList(pageable);

        Long count = getLocationCount();

        // 페이징과 관련된 정보 반환
        return new PageImpl<>(locationPage, pageable, count);
    }

    // 페이징 처리를 위한 count 쿼리
    private Long getLocationCount(){

        Long count = jpaQueryFactory
                .select(location.count())
                .from(location)
                .fetchOne();
        return count;
    }

    // 페이징 처리 된 location List
    private List<Location> getLocationList(Pageable pageable){

        // 페이징 처리를 위해 페이지 번호와 페이지 사이즈 가져옴
        List<Location> locationPage = jpaQueryFactory
                .selectFrom(location)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
        return locationPage;
    }
}
