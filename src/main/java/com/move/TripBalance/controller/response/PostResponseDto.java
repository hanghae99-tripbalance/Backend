package com.move.TripBalance.controller.response;



import com.move.TripBalance.domain.Media;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PostResponseDto {
    private Long id;
    private String title;
    private String nickName;
    private String local;
    private String localdetail;
    private int pet;
    private String content;
    private List<String> mediaList;
    private List<Media> image;
    private Long heartNum;
    private boolean heartYn = false;

}
