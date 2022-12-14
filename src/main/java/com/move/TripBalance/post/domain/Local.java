package com.move.TripBalance.post.domain;

public enum Local {

    수도권(1),
    경상_강원도(2),
    충청_전라도(3),
    제주도(4),
    기타(5);

    private int num;

    Local(int num ) {
        this.num = num ;
    }
    public static Local partsValue(int num) {
        switch (num) {
            case 1:
                return 수도권;
            case 2:
                return 경상_강원도;
            case 3:
                return 충청_전라도;
            case 4:
                return 제주도;
            case 5:
                return 기타;

        }
        return null;
    }
}
