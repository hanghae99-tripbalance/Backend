package com.move.TripBalance.post.repository;

import com.move.TripBalance.post.domain.Post;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.move.TripBalance.post.QPost.post;

@Repository
@RequiredArgsConstructor
public class PostCustomRepositoryImpl implements PostCustomRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Page<Post> findAllByOrderByCreatedAtDesc(Pageable pageable) {

        Long count = jpaQueryFactory
                .select(post.count())
                .from(post)
                .fetchOne();

        List<Post> postList =jpaQueryFactory
                .selectFrom(post)
                .orderBy(post.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        return new PageImpl<>(postList, pageable, count);
    }

//    @Override
//    public Page<Post> search(String keyword, Pageable pageable) {
//        return null;
//    }
//
//    @Override
//    public List<Post> findAllByMember(Member member) {
//        return null;
//    }
//
//    @Override
//    public List<Post> findTop10ByHeartsIn(List<Heart> hearts) {
//        return null;
//    }
}
