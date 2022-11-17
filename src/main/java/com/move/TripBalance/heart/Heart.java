package com.move.TripBalance.heart;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.move.TripBalance.member.Member;
import com.move.TripBalance.post.Post;
import com.move.TripBalance.shared.domain.Timestamped;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.procedure.spi.ParameterRegistrationImplementor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Heart extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long heartId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="memberId")
    private Member member;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="postId")
    private Post post;

    @JsonIgnore
    private LocalDateTime createdAt;

}
