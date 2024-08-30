package com.zjgsu.studentmanagement.Util;

public class student {
    private final String name;
    private final String sex;
    private final String id;
    private final String password;
    private final String number;
    private final int MathScore;
    private final int ChineseScore;
    private final int EnglishScore;
    private final int order;

    public student(int chineseScore, int englishScore, String id, int mathScore, String name, String number, String password, String sex, int order) {

        this.id = id;
        this.name = name;
        this.number = number;
        this.password = password;
        this.sex = sex;
        ChineseScore = chineseScore;
        EnglishScore = englishScore;
        MathScore = mathScore;
        this.order = order;
    }

    public int getChineseScore() {
        return ChineseScore;
    }

    public int getEnglishScore() {
        return EnglishScore;
    }

    public String getId() {
        return id;
    }

    public int getMathScore() {
        return MathScore;
    }

    public String getName() {
        return name;
    }

    public String getNumber() {
        return number;
    }

    public String getPassword() {
        return password;
    }

    public String getSex() {
        return sex;
    }

    public int getOrder() {
        return order;
    }

}
