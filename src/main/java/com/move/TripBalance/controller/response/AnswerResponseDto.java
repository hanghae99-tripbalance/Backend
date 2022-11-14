package com.move.TripBalance.controller.response;

import com.move.TripBalance.domain.Question;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class AnswerResponseDto {

    private Question question;

}
