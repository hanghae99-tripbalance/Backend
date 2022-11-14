package com.move.TripBalance.domain;


public enum LocalDetail {
    서울(1),
    인천(2),
    가평(3),
    용인(4),
    파주(5),
    속초(6),
    강릉(7),
    춘천(8),
    양양(9),
    평창(10),
    부산(11),
    거제(12),
    통영(13),
    포항(14),
    경주(15),
    안동(16),
    여수(17),
    목포(18),
    담양(19),
    보성(20),
    해남(21),
    전주(22),
    천안(23),
    태안(24),
    보령(25),
    공주(26),
    단양(27),
    대구(28),
    대전(29),
    광주(30),
    울산(31),
    서귀포(32),
    기타(33);

    private int num;

    LocalDetail(int num ) {
        this.num = num ;
    }
    public static LocalDetail partsValue(int num) {
        switch (num) {
            case 1:return 서울;
            case 2:return 인천;
            case 3:return 가평;
            case 4:return 용인;
            case 5:return 파주;
            case 6:return 속초;
            case 7:return 강릉;
            case 8:return 춘천;
            case 9:return 양양;
            case 10:return 평창;
            case 11:return 부산;
            case 12:return 거제;
            case 13:return 통영;
            case 14:return 포항;
            case 15:return 경주;
            case 16:return 안동;
            case 17:return 여수;
            case 18:return 목포;
            case 19:return 담양;
            case 20:return 보성;
            case 21:return 해남;
            case 22:return 전주;
            case 23:return 천안;
            case 24:return 태안;
            case 25:return 보령;
            case 26:return 공주;
            case 27:return 단양;
            case 28:return 대구;
            case 29:return 대전;
            case 30:return 광주;
            case 31:return 울산;
            case 32:return 서귀포;
            case 33:return 기타;
        }
        return null;
    }
}
