package com.move.TripBalance.balance.service;

import com.move.TripBalance.balance.Question;
import com.move.TripBalance.balance.QuestionTree;
import com.move.TripBalance.balance.repository.GameTestRepository;
import com.move.TripBalance.balance.repository.QuestionRepository;
import com.move.TripBalance.balance.repository.QuestionTreeRepository;
import com.move.TripBalance.member.Member;
import com.move.TripBalance.balance.GameTest;
import com.move.TripBalance.shared.domain.UserDetailsImpl;
import com.move.TripBalance.shared.exception.PrivateResponseBody;
import com.move.TripBalance.shared.exception.StatusCode;
import com.move.TripBalance.shared.jwt.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BalanceService {

    private final QuestionTreeRepository questionTreeRepository;
    private final QuestionRepository questionRepository;
    private final TokenProvider tokenProvider;
    private final GameTestRepository gameTestRepository;

    @Transactional
    public ResponseEntity<PrivateResponseBody> questionanswer(Long lastId, UserDetailsImpl userDetails) {

        QuestionTree questionTree = isPresentAnswer(lastId);

        Long id = questionTree.getLastId();

        Question question = isPresentTrip(id);

    //회원이 맞다면 멤버 아이디와 함께 답변 저장
        if (userDetails != null) {
            //회원 정보 가져오기
            Member member = userDetails.getMember();

        GameTest gameTest1 = GameTest.builder()
                .answer1(questionTree.getQuestion2())
                .answer2(questionTree.getQuestion3())
                .answer3(questionTree.getQuestion4())
                .answer4(questionTree.getQuestion5())
                .answer5(questionTree.getLastId())
                .gameResult(question.getTrip())
                .member(member)
                .build();

            gameTestRepository.save(gameTest1);

            return new ResponseEntity<>(new PrivateResponseBody<>(StatusCode.OK, gameTest1), HttpStatus.OK);
    }else {
            //회원이 아니라면 답변만 저장
            GameTest gameTest2 = GameTest.builder()
                    .answer1(questionTree.getQuestion2())
                    .answer2(questionTree.getQuestion3())
                    .answer3(questionTree.getQuestion4())
                    .answer4(questionTree.getQuestion5())
                    .answer5(questionTree.getLastId())
                    .gameResult(question.getTrip())
                    .build();

            gameTestRepository.save(gameTest2);

            return new ResponseEntity<>(new PrivateResponseBody<>(StatusCode.OK, gameTest2), HttpStatus.OK);
        }

    }

    @Transactional(readOnly = true)
    public QuestionTree isPresentAnswer(Long id) {
        Optional<QuestionTree> optionalQuestionTree = questionTreeRepository.findById(id);
        return optionalQuestionTree.orElse(null);
    }

    @Transactional(readOnly = true)
    public Question isPresentTrip(Long id) {
        Optional<Question> optionalQuestion = questionRepository.findById(id);
        return optionalQuestion.orElse(null);
    }

}
