- [x] 회원 및 문제 저장(Initial..)
##### Member
```sql
INSERT INTO Member(name) VALUES('yuri')
```
##### Question
```aidl
   1
 2   3
4 5 6 7  
```

```sql
INSERT INTO Question(id, question, parent_id, left_id, right_id, left_answer, right_answer, trip) VALUES
                                                                                                      (1, '쉬러감 or 놀러감', null, 2, 3, '쉬러감', '놀러감', null),
                                                                                                      (2, '힐링 or 도시락', 1, 4, 5, '힐링', '도시락', null),
                                                                                                      (3, '액티비티 or 체험', 1, 6, 7, '액티비티', '체험', null),
                                                                                                      (4, '자연 or 관광', 2, 8, 9, '자연', '관광', null),
                                                                                                      (5, '식사 or 음료', 2, 10, 11, '식사', '음료', null),
                                                                                                      (6, '땅 or 물', 3, 12, 13, '땅', '물', null),
                                                                                                      (7, '역사관람 or 생태체험', 3, 14, 15, '역사관람', '생태체험', null),
                                                                                                      (8, '숲 or 하늘', 4, 16, 17, '숲', '하늘', null),
                                                                                                      (9, '멍때리기 or 노래듣기', 4, 18, 19, '멍때리기', '노래듣기', null),
                                                                                                      (10, '해산물 or 고기', 5, 20, 21, '해산물', '고기', null),
                                                                                                      (11, '무알콜 or 알콜', 5, 22, 23, '무알콜', '알콜', null),
                                                                                                      (12, '발이 땅 or 발이 안닿아', 6, 24, 25, '발이 땅', '발이 안닿아', null),
                                                                                                      (13, '챌린지 or 편안', 6, 26, 27, '챌린지', '편안', null),
                                                                                                      (14, '역사 or 한복', 7, 28, 29, '역사', '한복', null),
                                                                                                      (15, '먹이주기 or 자급자족', 7, 30, 31, '먹이주기', '자급자족', null),
                                                                                                      (16, '공원 or 죽녹원', 8, 32, 33,  '공원', '죽녹원',null),
                                                                                                      (17, '일몰 or 일출', 8, 34, 35,  '일몰', '일출',null),
                                                                                                      (18, '불멍 or 물멍', 9, 36, 37,  '불멍', '물멍',null),
                                                                                                      (19, '밤바다 or 영화', 9, 38, 39,  '밤바다', '영화',null),
                                                                                                      (20, '회 or 조개구이', 10, 40, 41,  '회', '조개구이',null),
                                                                                                      (21, '닭갈비 or 막창', 10, 42, 43,  '닭갈비', '막창',null),
                                                                                                      (22, '커피 or 녹차', 11, 44, 45,  '커피', '녹차',null),
                                                                                                      (23, '막걸리 or 소주', 11, 46, 47,  '막걸리', '소주',null),
                                                                                                      (24, '루지 or 바이킹', 12, 48, 49,  '루지', '바이킹',null),
                                                                                                      (25, '스카이다이빙 or 패러글라이딩', 12, 50, 51,  '스카이다이빙', '패러글라이딩',null),
                                                                                                      (26, '서핑 or 레프팅', 13, 52, 53,  '서핑', '레프팅',null),
                                                                                                      (27, '요트 or 워터슬라이드', 13, 54, 55,  '요트', '워터슬라이드',null),
                                                                                                      (28, '백제 or 신라', 14, 56, 57,  '백제', '신라',null),
                                                                                                      (29, '고궁 or 한옥', 14, 58, 59,  '고궁', '한옥',null),
                                                                                                      (30, '말 or 양', 15, 60, 61,  '말', '양',null),
                                                                                                      (31, '조개 or 빙어', 15, 62, 63,  '조개', '빙어',null),
                                                                                                      (32, '공원', 16, null, null, null, null,'울산'),
                                                                                                      (33, '죽녹원', 16, null, null,  null, null,'담양'),
                                                                                                      (34, '일몰', 17, null, null,  null, null, '해남'),
                                                                                                      (35, '일출', 17, null, null,  null, null, '포항'),
                                                                                                      (36, '불멍', 18, null, null, null, null,'가평'),
                                                                                                      (37, '물멍', 18, null, null, null, null,'목포' ),
                                                                                                      (38, '밤바다', 19, null, null, null, null,'여수'),
                                                                                                      (39, '영화', 19, null, null, null, null,'부산' ),
                                                                                                      (40, '회', 20, null, null, null, null,'속초'),
                                                                                                      (41, '조개구이', 20, null, null, null, null,'보령' ),
                                                                                                      (42, '닭갈비', 21, null, null, null, null,'춘천' ),
                                                                                                      (43, '막창', 21, null, null, null, null,'대구' ),
                                                                                                      (44, '커피', 22, null, null, null, null,'강릉' ),
                                                                                                      (45, '녹차', 22, null, null, null, null,'보성'),
                                                                                                      (46, '막걸리', 23, null, null, null, null,'당진'),
                                                                                                      (47, '소주', 23, null, null, null, null,'안동'),
                                                                                                      (48, '루지', 24, null, null, null, null,'통영'),
                                                                                                      (49, '바이킹', 24, null, null,null, null,'인천'),
                                                                                                      (50, '스카이다이빙', 25, null, null, null, null,'충주'),
                                                                                                      (51, '패러글라이딩', 25, null, null, null, null,'단양'),
                                                                                                      (52, '서핑', 26, null, null, null, null,'양양'),
                                                                                                      (53, '레프팅', 26, null, null, null, null,'인제'),
                                                                                                      (54, '요트', 27, null, null, null, null,'서귀포'),
                                                                                                      (55, '워터슬라이드', 27, null, null, null, null,'천안'),
                                                                                                      (56, '백제', 28, null, null, null, null,'공주'),
                                                                                                      (57, '신라', 28, null, null, null, null,'경주'),
                                                                                                      (58, '고궁', 29, null, null, null, null,'종로구'),
                                                                                                      (59, '한옥', 29, null, null, null, null,'전주'),
                                                                                                      (60, '말', 30, null, null, null, null,'태백'),
                                                                                                      (61, '양', 30, null, null, null, null,'평창'),
                                                                                                      (62, '조개', 31, null, null, null, null,'태안'),
                                                                                                      (63, '빙어', 31, null, null, null, null,'양평');
```

##### QuestionTree
```sql
INSERT INTO question_tree(last_id, question1, question2, question3, question4, question5) VALUES
                                                                                              (32, 1, 2, 4, 8, 16),
                                                                                              (33, 1, 2, 4, 8, 16),
                                                                                              (34, 1, 2, 4, 8, 17),
                                                                                              (35, 1, 2, 4, 8, 17),
                                                                                              (36, 1, 2, 4, 9, 18),
                                                                                              (37, 1, 2, 4, 9, 18),
                                                                                              (38, 1, 2, 4, 9, 19),
                                                                                              (39, 1, 2, 4, 9, 19),
                                                                                              (40, 1, 2, 5, 10, 20),
                                                                                              (41, 1, 2, 5, 10, 20),
                                                                                              (42, 1, 2, 5, 10, 21),
                                                                                              (43, 1, 2, 5, 10, 21),
                                                                                              (44, 1, 2, 5, 11, 22),
                                                                                              (45, 1, 2, 5, 11, 22),
                                                                                              (46, 1, 2, 5, 11, 23),
                                                                                              (47, 1, 2, 5, 11, 23),
                                                                                              (48, 1, 3, 6, 12, 24),
                                                                                              (49, 1, 3, 6, 12, 24),
                                                                                              (50, 1, 3, 6, 12, 25),
                                                                                              (51, 1, 3, 6, 12, 25),
                                                                                              (52, 1, 3, 6, 13, 26),
                                                                                              (53, 1, 3, 6, 13, 26),
                                                                                              (54, 1, 3, 6, 13, 27),
                                                                                              (55, 1, 3, 6, 13, 27),
                                                                                              (56, 1, 3, 7, 14, 28),
                                                                                              (57, 1, 3, 7, 14, 28),
                                                                                              (58, 1, 3, 7, 14, 29),
                                                                                              (59, 1, 3, 7, 14, 29),
                                                                                              (60, 1, 3, 7, 15, 30),
                                                                                              (61, 1, 3, 7, 15, 30),
                                                                                              (62, 1, 3, 7, 15, 31),
                                                                                              (63, 1, 3, 7, 15, 31);
```
- [x] 회원의 현재 대답 저장(Insert Member current answer)
- [x] 회원의 현재 가져오기
- [x] 회원의 최종 대답 저장
---
## APIs
### question 번호에 대한 문제 내용 가져오기
- `GET` {host}/question/{questionId}
#### Response body
```json
{
    "id": 1,
    "question": "산 or 바다?",
    "parentId": null,
    "leftId": 2,
    "rightId": 3,
    "leftAnswer": "산",
    "rightAnswer": "바다"
}
```

### member의 현재 question 번호 가져오기
- `GET` {host}/member/{memberId}/answer
```json
{
    "memberId": 1, // 회원 번호
    "questionId": 1 // 문제 번호
}
```
- 이전에 문제를 풀었던 이력이 없으면 questionId 1번을 반환 

### `POST` {host}/member/{memberId}/answer
##### Request body
```json
{
    "questionId": 4, // 문제 번호
    "checkLeft": true // 왼쪽을 선택했는지 여부
}
```

#### Response body
```json
{
    "memberId": 1, // 회원 번호
    "questionId": 2 // 문제 번호
}
```
- 마지막 문제인 경우에 현재 아무 값도 반환하지 않음. -> client가 사용함에 있어서 편리한 방향으로 수정하시면 됩니다.