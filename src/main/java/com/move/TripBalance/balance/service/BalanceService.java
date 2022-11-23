package com.move.TripBalance.balance.service;

import com.move.TripBalance.balance.GameResult;
import com.move.TripBalance.balance.Question;
import com.move.TripBalance.balance.QuestionTree;
import com.move.TripBalance.balance.controller.response.ChoiceResponseDto;
import com.move.TripBalance.balance.controller.response.TripResponseDto;
import com.move.TripBalance.balance.repository.GameChoiceRepository;
import com.move.TripBalance.balance.repository.QuestionRepository;
import com.move.TripBalance.balance.repository.QuestionTreeRepository;
import com.move.TripBalance.member.Member;
import com.move.TripBalance.shared.domain.UserDetailsImpl;
import com.move.TripBalance.shared.exception.PrivateResponseBody;
import com.move.TripBalance.shared.exception.StatusCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BalanceService {

    private final QuestionTreeRepository questionTreeRepository;
    private final QuestionRepository questionRepository;
    private final GameChoiceRepository gameChoiceRepository;

    // 게임 시작
    @Transactional
    public ResponseEntity<PrivateResponseBody> start(UserDetailsImpl userDetails) {
        // 비회원 일때
        if(userDetails == null){

            // gameId 생성
            GameResult result = new GameResult();
            gameChoiceRepository.save(result);

            // 첫번째 문제 제출을 위해서 변수 설정
            Long a = 1L;

            // 첫번째 문제 추출
            Optional<Question> optionalQuestion = questionRepository.findById(a);

            // 객체에 gameId / 문제 담기
            List<Object> questionList = new ArrayList<>();
            questionList.add(optionalQuestion.get());
            questionList.add(result);

            //Return
            return new ResponseEntity<>(new PrivateResponseBody<>(StatusCode.OK, questionList),HttpStatus.OK );
        }// 회원일때
        else{

            // 회원 정보 확인
            Member member = userDetails.getMember();

            // memberId 할당
            GameResult gameResult = GameResult.builder()
                    .member(member)
                    .build();

            // gameId 생성
            gameChoiceRepository.save(gameResult);

            // 첫번째 문제 제출을 위해서 변수 설정
            Long a = 1L;

            // 첫번째 문제 추출
            Optional<Question> optionalQuestion = questionRepository.findById(a);

            // 객체에 gameId / 문제 담기
            List<Object> questionList = new ArrayList<>();
            questionList.add(optionalQuestion.get());
            questionList.add(gameResult);

            // Return
            return new ResponseEntity<>(new PrivateResponseBody<>(StatusCode.OK, questionList),HttpStatus.OK );
        }

    }

    //게임 다음 문제
    @Transactional
    public ResponseEntity<PrivateResponseBody> choice(Long gameId, Long questionId, UserDetailsImpl userDetails){
        Optional<Question> optionalQuestion = questionRepository.findById(questionId);
        if (!optionalQuestion.isPresent()) {
            throw new RuntimeException(String.format("ID[%s} not found", questionId));
        }

        Optional<GameResult> optionalGameResult = gameChoiceRepository.findById(gameId);
        List<Object> questionList = new ArrayList<>();
        questionList.add(optionalQuestion.get());
        questionList.add(optionalGameResult);

        return new ResponseEntity<>(new PrivateResponseBody<>(StatusCode.OK, questionList),HttpStatus.OK );
    }

    // 게임결과를 통해 여행지 및 전체 선택지 저장
    @Transactional
    public ResponseEntity<PrivateResponseBody> questionResult(Long gameId, Long lastId, UserDetailsImpl userDetails) {

        // 결과값을 통해 선택 확인
        QuestionTree questionTree = isPresentAnswer(lastId);

        // 결과값을 통해서 여행지 확인
        Long id = questionTree.getLastId();
        Question question = isPresentTrip(id);

        // 게임 아이디 확인
        GameResult gameResult = isPresentGame(gameId);

        // 결과 업데이트
        ChoiceResponseDto choiceResponseDto = ChoiceResponseDto.builder()
                .answer1(questionTree.getQuestion2())
                .answer2(questionTree.getQuestion3())
                .answer3(questionTree.getQuestion4())
                .answer4(questionTree.getQuestion5())
                .answer5(questionTree.getLastId())
                .trip(question.getTrip())
                .build();

            gameResult.update(choiceResponseDto);

            return new ResponseEntity<>(new PrivateResponseBody<>(StatusCode.OK, choiceResponseDto), HttpStatus.OK);
    }

    //게임 결과 페이지
    @Transactional
    public ResponseEntity<PrivateResponseBody> result(Long gameId){
        // 게임 아이디 확인
        GameResult gameResult = isPresentGame(gameId);

        // 객체 부여
        String trip = gameResult.getGameResult();

        Question question = isPresentdec(trip);

        TripResponseDto tripResponseDto = TripResponseDto.builder()
                .Trip(question.getTrip())
                .Tripcontent(question.getTripcontent())
                .build();

        return new ResponseEntity<>(new PrivateResponseBody<>(StatusCode.OK, tripResponseDto), HttpStatus.OK);
    }


    //답변
    @Transactional(readOnly = true)
    public QuestionTree isPresentAnswer(Long id) {
        Optional<QuestionTree> optionalQuestionTree = questionTreeRepository.findById(id);
        return optionalQuestionTree.orElse(null);
    }

    //여행지
    @Transactional(readOnly = true)
    public Question isPresentTrip(Long id) {
        Optional<Question> optionalQuestion = questionRepository.findById(id);
        return optionalQuestion.orElse(null);
    }

    //게임 아이디
    @Transactional(readOnly = true)
    public GameResult isPresentGame(Long id) {
        Optional<GameResult> optionalGameResult = gameChoiceRepository.findById(id);
        return optionalGameResult.orElse(null);
    }

    //여행지 정보
    @Transactional(readOnly = true)
    public Question isPresentdec(String trip) {
        Optional<Question> optionalQuestion1 = questionRepository.findByTrip(trip);
        return optionalQuestion1.orElse(null);
    }

}
