package com.move.TripBalance.member;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.move.TripBalance.balance.GameResult;
import com.move.TripBalance.shared.domain.Timestamped;
import com.move.TripBalance.post.Post;
import lombok.*;
import org.hibernate.Hibernate;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Member extends Timestamped {

    //고유 아이디
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long memberId;

    //이메일
    @Column(nullable = false)
    private String email;

    //닉네임
    @Column(nullable = false)
    private String nickName;

    //비밀번호
    @Column(nullable = false)
    @JsonIgnore
    private String pw;

    //자기소개
    @Column
    private String self;

    //프로필 사진
    @Column
    private String profileImg;

    //SNS
    @JsonIgnore
    @OneToOne(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private SNS sns = new SNS();

    //게시글
    @OneToMany(mappedBy = "member",fetch = FetchType.LAZY,cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Post> posts = new ArrayList<>();

    @OneToMany(mappedBy = "member",fetch = FetchType.LAZY,cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GameTest> gameTests = new ArrayList<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) {
            return false;
        }
        Member member = (Member) o;
        return memberId != null && Objects.equals(memberId, member.memberId);
    }

    // 프로필 이미지 업데이트
    public void updateProfileImg(MyImgRequestDto requestDto){
        this.profileImg = requestDto.getProfileImg();
    }

    // 자기소개 업데이트
    public void updateSelf(String self){
        this.self = self;
    }
    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    public boolean validatePassword(PasswordEncoder passwordEncoder, String password) {
        return passwordEncoder.matches(password, this.pw);
    }
}