package cn.edu.fdu.clone.recommend.enums;

import lombok.Getter;

public enum Language {
    JAVA("java", "java"),
    CPP("cpp", "cpp", "c");

    private final String desc;
    @Getter
    private final String[] suffix;

    Language(String desc, String... suffix) {
        this.desc = desc;
        this.suffix = suffix;
    }

    @Override
    public String toString() {
        return desc;
    }
}
