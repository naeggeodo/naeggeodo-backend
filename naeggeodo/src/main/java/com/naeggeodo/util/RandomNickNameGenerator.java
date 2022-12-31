package com.naeggeodo.util;

import java.util.Random;

public class RandomNickNameGenerator {
    private final String[] arr_adjective = {
            "강직한", "고요한", "고운", "기특한", "깜찍한", "근면한", "귀여운", "관대한", "깔끔한", "꾸준한", "긍정적인", "깡이 있는", "겸손한",
            "검소한", "공손한", "기운찬", "놀라운", "나눌 줄 아는", "넉넉한", "남자다운", "느긋한", "넉살 좋은", "남을 잘 돌보는", "낙천적인", "낭만적인", "다정한", "당당한", "든든한", "리더십 있는",
            "다재다능한", "또렷한", "다양한", "단호한", "대담한", "쓸만한", "로맨틱한", "믿음직한", "명랑한", "마음이 넓은", "매력적인", "맑은", "멋진", "말을 잘하는", "반듯한", "발랄한", "부드러운", "빼어난"
            , "밝은", "붙임성 있는", "싱그러운", "순진무구한", "섬세한", "적극적인", "지혜로운", "존귀한", "잘 웃는", "튼튼한", "쾌활한", "카리스마 있는", "청초한", "포용력 있는", "평화로운", "직선적인"
    };

    private final String[] arr_noun = {
            "강아지", "고양이", "거북이", "토끼", "뱀", "사자", "호랑이", "하이에나", "재규어", "알파카", "양", "담비", "딱따구리",
            "코요테", "미어캣", "퓨마", "도롱뇽", "카피바라", "오소리", "삵", "당근", "피망", "타조", "고릴라", "양배추", "피자", "햄버거",
            "올빼미", "메추라기", "유니콘", "카멜레온", "칠면조", "청설모", "달리기 선수", "마법사", "청년", "노인", "프로게이머", "푸드파이터"
    };

    public static String createRandomNickName() {
        RandomNickNameGenerator rng = new RandomNickNameGenerator();
        return rng.arr_adjective[rng.getRandomIndex(rng.arr_adjective.length)] + " " + rng.arr_noun[rng.getRandomIndex(rng.arr_noun.length)];
    }

    private int getRandomIndex(int length) {
        return new Random().nextInt(length);
    }
}
