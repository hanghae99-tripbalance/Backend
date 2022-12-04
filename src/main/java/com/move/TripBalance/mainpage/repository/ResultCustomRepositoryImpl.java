package com.move.TripBalance.mainpage.repository;

import com.move.TripBalance.mainpage.Result;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.move.TripBalance.mainpage.QResult.result;

@Repository
@RequiredArgsConstructor
public class ResultCustomRepositoryImpl implements ResultCustomRepository{

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<Result> findByLocation(String districtName) {
        return jpaQueryFactory.selectFrom(result)
                .where(result.location.eq(districtName))
                .fetch();
    }

    // 지역명과 방문 형태로 찾기
    @Override
    public Result findByLocationAndType(String districtName, String comp) {
        return jpaQueryFactory.selectFrom(result)
                .where(
                        result.location.eq(districtName),
                        result.type.eq(comp)
                )
                .fetchOne();
    }

    // 지역명과 연령대로 찾기
    @Override
    public Result findByLocationAndAge(String districtName, String age) {
        return jpaQueryFactory.selectFrom(result)
                .where(
                        result.location.eq(districtName),
                        result.age.eq(age)
                )
                .fetchOne();
    }

    // 지역명과 성별로 찾기
    @Override
    public Result findByLocationAndGender(String districtName, String gender) {
        return jpaQueryFactory.selectFrom(result)
                .where(
                        result.location.eq(districtName),
                        result.gender.eq(gender)
                )
                .fetchOne();
    }
}
