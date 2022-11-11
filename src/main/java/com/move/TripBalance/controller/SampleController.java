package com.move.TripBalance.controller;

import com.move.TripBalance.controller.response.MemberAnswerDto;
import com.move.TripBalance.domain.*;
import com.move.TripBalance.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
@RequestMapping("/tb")
@RestController
public class SampleController {

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

    /**
     * 회원의 현재 문제 가져오기
     * @param id 회원번호
     * @return
     */
    @GetMapping("/member/{id}/question")
    public MemberCurrentAnswer findCurrentAnswerByMemberId(@PathVariable Long id) {
        Optional<MemberCurrentAnswer> memberCurrentAnswer = memberCurrentAnswerRepository.findById(id);
        MemberCurrentAnswer res;
        if (!memberCurrentAnswer.isPresent()) {
            res = new MemberCurrentAnswer();
            res.setMemberId(id);
            res.setQuestionId(1L);
        } else {
            res = memberCurrentAnswer.get();
        }

        return res;
    }

    /**
     * 문제의 상세 내용 가져오기
     * @param id 문제번호
     * @return
     */
    @GetMapping("/question/{id}")
    public Question findQuestionById(@PathVariable Long id) {
        Optional<Question> optionalQuestion = questionRepository.findById(id);
        if (!optionalQuestion.isPresent()) {
            throw new RuntimeException(String.format("ID[%s} not found", id));
        }

        return optionalQuestion.get();
    }

    /**
     * 회원의 답변 저장하기
     * @param id 회원번호
     * @param memberAnswerDto 회원의 답변 내용
     * @return
     */
    @PostMapping("/member/{id}/answer")
    public MemberCurrentAnswer updateMemberAnswer(@PathVariable Long id, @RequestBody MemberAnswerDto memberAnswerDto) {
        Optional<Question> optionalQuestion = questionRepository.findById(memberAnswerDto.getQuestionId());
        if (!optionalQuestion.isPresent()) {
            throw new RuntimeException(String.format("ID[%s} not found", id));
        }

        Optional<MemberCurrentAnswer> optionalMemberCurrentAnswer = memberCurrentAnswerRepository.findById(id);
        MemberCurrentAnswer ca;
        if (!optionalMemberCurrentAnswer.isPresent()) {
            ca = new MemberCurrentAnswer();
            ca.setMemberId(id);
            ca.setQuestionId(1L);
        } else {
            ca = optionalMemberCurrentAnswer.get();
        }

        Question question = optionalQuestion.get();
        // 마지막 문제인지 체크
        if (question.getLeftId() == null || question.getRightId() == null) {
            memberCurrentAnswerRepository.deleteById(id);
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

            return savedMemberCurrentAnswer;
        }
    }

    /**
     * 마지막 문제로 전체 문제 추적
     * @param id 마지막 문제(leaf 노드)의 id
     * @return
     */
    @GetMapping("/question/{id}/tree")
    public List<Question> getQuestionTreeById(@PathVariable Long id) {
        Optional<QuestionTree> optionalQuestionTree = questionTreeRepository.findById(id);
        if (!optionalQuestionTree.isPresent()) {
            throw new RuntimeException(String.format("ID[%s} not found", id));
        }

        QuestionTree questionTree = optionalQuestionTree.get();

        // question tree 에 있는 question 목록을 가져와서 매핑

        List<Question> questionList = new ArrayList<>();
        Optional<Question> question1 = questionRepository.findById(questionTree.getQuestion1());
        Optional<Question> question2 = questionRepository.findById(questionTree.getQuestion2());
        Optional<Question> question3 = questionRepository.findById(questionTree.getQuestion3());
        Optional<Question> question4 = questionRepository.findById(questionTree.getQuestion4());
        Optional<Question> question5 = questionRepository.findById(questionTree.getLastId());
        questionList.add(question1.get());
        questionList.add(question2.get());
        questionList.add(question3.get());
        questionList.add(question4.get());
        questionList.add(question5.get());


        return questionList;
    }
}

