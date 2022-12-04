package com.move.TripBalance.result.repository;

import com.move.TripBalance.result.Blog;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.move.TripBalance.result.QBlog.blog;

@Repository
@RequiredArgsConstructor
public class BlogCustomRepositoryImpl implements BlogCustomRepository {

    private final JPAQueryFactory jpaQueryFactory;

    // 파라미터로 받은 지역명과 같은 블로그 정보 반환
    @Override
    public List<Blog> findAllByLocation(String location) {
        return jpaQueryFactory.selectFrom(blog)
                .where(blog.location.eq(location))
                .fetch();
    }
}
