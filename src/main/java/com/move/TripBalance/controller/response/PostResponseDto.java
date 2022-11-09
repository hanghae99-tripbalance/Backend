package com.move.TripBalance.controller.response;


//import com.move.TripBalance.domain.Media;
import com.move.TripBalance.domain.Local;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PostResponseDto {
    private String title;
    private String nickName;
    private String local;
    private int pet;
    private String content;
//    private Media media;
    private Long heartNum;
    private boolean heartYn = false;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
}
