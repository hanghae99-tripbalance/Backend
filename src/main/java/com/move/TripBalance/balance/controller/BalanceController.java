package com.move.TripBalance.balance.controller;

import com.move.TripBalance.balance.MemberAnswer;
import com.move.TripBalance.balance.controller.response.GameResponseDto;
import com.move.TripBalance.balance.repository.MemberAnswerRepository;
import com.move.TripBalance.balance.repository.MemberCurrentAnswerRepository;
import com.move.TripBalance.balance.repository.QuestionTreeRepository;
import com.move.TripBalance.balance.controller.response.MemberAnswerDto;
import com.move.TripBalance.balance.MemberCurrentAnswer;
import com.move.TripBalance.balance.Question;
import com.move.TripBalance.balance.QuestionTree;
import com.move.TripBalance.balance.repository.QuestionRepository;

import com.move.TripBalance.balance.service.BalanceService;
import com.move.TripBalance.member.Member;
import com.move.TripBalance.member.repository.MemberRepository;
import com.move.TripBalance.shared.domain.UserDetailsImpl;
import com.move.TripBalance.shared.exception.PrivateResponseBody;
import com.move.TripBalance.shared.exception.StatusCode;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;


import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@RequestMapping("/tb")
@RestController
public class BalanceController {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private QuestionTreeRepository questionTreeRepository;

    @Autowired
    private MemberCurrentAnswerRepository memberCurrentAnswerRepository;

    @Autowired
    private MemberAnswerRepository memberAnswerRepository;

    private final BalanceService balanceService;


    /**
     * 문제의 상세 내용 가져오기
     * @param questionId 문제번호
     * @return
     */
    @GetMapping("/question/{questionId}")
    public Question findQuestionById(@PathVariable Long questionId) {
        Optional<Question> optionalQuestion = questionRepository.findById(questionId);
        if (!optionalQuestion.isPresent()) {
            throw new RuntimeException(String.format("ID[%s} not found", questionId));
        }

        return optionalQuestion.get();
    }

    @ResponseBody
    @PostMapping("/questionanswer/{lastId}")
    public ResponseEntity<PrivateResponseBody> questionanswer(@PathVariable Long lastId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return balanceService.questionanswer(lastId, userDetails);
    }


//    /**
//     * 회원의 답변 저장하기
//     * @param id 회원번호
//     * @param memberAnswerDto 회원의 답변 내용
//     * @return
//     */
//    @PostMapping("/member/{id}/answer")
//    public MemberCurrentAnswer updateMemberAnswer(@PathVariable Long id, @RequestBody MemberAnswerDto memberAnswerDto) {
//        Optional<Question> optionalQuestion = questionRepository.findById(memberAnswerDto.getQuestionId());
//        if (!optionalQuestion.isPresent()) {
//            throw new RuntimeException(String.format("ID[%s} not found", id));
//        }
//
//        Optional<MemberCurrentAnswer> optionalMemberCurrentAnswer = memberCurrentAnswerRepository.findById(id);
//        MemberCurrentAnswer ca;
//        if (!optionalMemberCurrentAnswer.isPresent()) {
//            ca = new MemberCurrentAnswer();
//            ca.setMemberId(id);
//            ca.setQuestionId(1L);
//        } else {
//            ca = optionalMemberCurrentAnswer.get();
//        }
//
//        Question question = optionalQuestion.get();
//        // 마지막 문제인지 체크
//        if (question.getLeftId() == null || question.getRightId() == null) {
//            memberCurrentAnswerRepository.deleteById(id);
//            MemberAnswer answer = new MemberAnswer();
//            answer.setLastQuestionId(question.getId());
//            answer.setMemberId(question.getId());
//            answer.setLastAnswer(memberAnswerDto.isCheckLeft());
//
//            memberAnswerRepository.save(answer);
//            return null;
//        } else {
//            Long nextQuestionId;
//            if (memberAnswerDto.isCheckLeft()) {
//                nextQuestionId = question.getLeftId();
//            } else {
//                nextQuestionId = question.getRightId();
//            }
//            ca.setQuestionId(nextQuestionId);
//            MemberCurrentAnswer savedMemberCurrentAnswer = memberCurrentAnswerRepository.save(ca);
//
//            return savedMemberCurrentAnswer;
//        }
//    }

    /**
     * 마지막 문제로 전체 문제 추적
     * @param lastId 마지막 문제(leaf 노드)의 id
     * @return
     */
    @GetMapping("/question/{lastId}/tree")
    public List<Question> getQuestionTreeById(@PathVariable Long lastId) {
        Optional<QuestionTree> optionalQuestionTree = questionTreeRepository.findById(lastId);
        if (!optionalQuestionTree.isPresent()) {
            throw new RuntimeException(String.format("ID[%s} not found", lastId));
        }

        QuestionTree questionTree = optionalQuestionTree.get();

        // question tree 에 있는 question 목록을 가져와서 매핑

        List<Question> questionList = new ArrayList<>();
        Optional<Question> question1 = questionRepository.findById(questionTree.getQuestion1());
        Optional<Question> question2 = questionRepository.findById(questionTree.getQuestion2());
        Optional<Question> question3 = questionRepository.findById(questionTree.getQuestion3());
        Optional<Question> question4 = questionRepository.findById(questionTree.getQuestion4());
        Optional<Question> question5 = questionRepository.findById(questionTree.getQuestion5());
        Optional<Question> question6 = questionRepository.findById(questionTree.getLastId());
        questionList.add(question1.get());
        questionList.add(question2.get());
        questionList.add(question3.get());
        questionList.add(question4.get());
        questionList.add(question5.get());
        questionList.add(question6.get());


        return questionList;
    }

    //    /**
//     * 회원의 현재 문제 가져오기
//     * @param memberId 회원번호
//     * @return
//     */
//    @GetMapping("/member/{memberId}/question")
//    public MemberCurrentAnswer findCurrentAnswerByMemberId(@PathVariable Long memberId) {
//        Optional<MemberCurrentAnswer> memberCurrentAnswer = memberCurrentAnswerRepository.findById(memberId);
//        MemberCurrentAnswer res;
//        if (!memberCurrentAnswer.isPresent()) {
//            res = new MemberCurrentAnswer();
//            res.setMemberId(memberId);
//            res.setQuestionId(1L);
//        } else {
//            res = memberCurrentAnswer.get();
//        }
//
//        return res;
//    }


    /**
     * 회원의 답변 저장하기
     * @param memberId 회원번호
     * @param memberAnswerDto 회원의 답변 내용
     * @return
     */
    @PostMapping("/member/answer")
    public ResponseEntity<PrivateResponseBody> updateMemberAnswer(UserDetailsImpl userDetails, @RequestBody MemberAnswerDto memberAnswerDto ) {
        // 퀴즈 확인
        Optional<Question> optionalQuestion = questionRepository.findById(memberAnswerDto.getQuestionId());
        if (!optionalQuestion.isPresent()) {
            throw new RuntimeException(String.format("ID[%s} not found", userDetails));
        }

        //회원 정보 가져오기
        Member member = userDetails.getMember();

        if (member == null){
            return new ResponseEntity<>(new PrivateResponseBody(StatusCode.OK,"다음문제로"), HttpStatus.OK) ;
        }

        Optional<MemberCurrentAnswer> optionalMemberCurrentAnswer = memberCurrentAnswerRepository.findById(member.getMemberId());

        MemberCurrentAnswer ca;
        if (!optionalMemberCurrentAnswer.isPresent()) {
            ca = new MemberCurrentAnswer();
            ca.setMemberId(member.getMemberId());
            ca.setQuestionId(1L);
        } else {
            ca = optionalMemberCurrentAnswer.get();
        }

        Question question = optionalQuestion.get();
        // 마지막 문제인지 체크
        if (question.getLeftId() == null || question.getRightId() == null) {
//            memberCurrentAnswerRepository.deleteById(memberId);
            MemberAnswer answer = new MemberAnswer();
            answer.setLastQuestionId(question.getId());
            answer.setMemberId(question.getId());
            answer.setLastAnswer(memberAnswerDto.isCheckLeft());

            memberAnswerRepository.save(answer);
            return null;
        } else {
            Long nextQuestionId;
            if (memberAnswerDto.isCheckLeft()) {
                nextQuestionId = question.getLeftId();
            } else {
                nextQuestionId = question.getRightId();
            }
            ca.setQuestionId(nextQuestionId);
            MemberCurrentAnswer savedMemberCurrentAnswer = memberCurrentAnswerRepository.save(ca);

            return new ResponseEntity<>(new PrivateResponseBody(StatusCode.OK,savedMemberCurrentAnswer), HttpStatus.OK) ;
        }
    }


}