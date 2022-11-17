package com.move.TripBalance.member;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.move.TripBalance.mypage.controller.request.SNSRequestDto;
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

    public void updateinsts(SNSRequestDto requestDto){
        this.insta = "https://www.instagram.com/" + requestDto.getInsta();
    }
    public void updatefacebook(SNSRequestDto requestDto){
        this.facebook = "https://www.facebook.com/" +requestDto.getFacebook();
    }
    public void updateyoutube(SNSRequestDto requestDto){
        this.youtube = "https://www.youtube.com/" + requestDto.getYoutube();
    }
    public void updateblog(SNSRequestDto requestDto){
        this.blog = "https://blog.naver.com/" + requestDto.getBlog();
    }







}
