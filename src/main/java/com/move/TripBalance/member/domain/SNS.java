package com.move.TripBalance.member.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.move.TripBalance.mypage.controller.request.MyPageRequestDto;
import lombok.*;

import javax.persistence.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class SNS {

    //고유 아이디
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long SNSId;

    //인스타 그램
    @Column
    private String insta;

    //블로그
    @Column
    private String blog;

    //facebook
    @Column
    private String facebook;

    //유투브
    @Column
    private String youtube;

    @JsonIgnore
    @JoinColumn(name = "memberId", nullable = false)
    @OneToOne(fetch = FetchType.LAZY)
    private Member member;

    public void updateinsta(MyPageRequestDto requestDto){
        this.insta = requestDto.getInsta();
    }
    public void updatefacebook(MyPageRequestDto requestDto){
        this.facebook = requestDto.getFacebook();
    }
    public void updateyoutube(MyPageRequestDto requestDto){
        this.youtube = requestDto.getYoutube();
    }
    public void updateblog(MyPageRequestDto requestDto){
        this.blog = requestDto.getBlog();
    }







}
