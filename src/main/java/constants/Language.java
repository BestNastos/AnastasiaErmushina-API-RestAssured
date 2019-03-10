package constants;

public enum Language {

    ENGLISH("en"),
    RUSSIAN("ru"),
    UKRAINIAN("uk"),
    INCORRECT_LANGUAGE("ennn");

    public String value;

    Language(String value) {
        this.value = value;
    }
}
