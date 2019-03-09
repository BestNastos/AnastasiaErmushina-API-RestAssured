package constants;

public enum Text {

    UKR_WITH_DIGITS("При8іт"),
    UKR_WITH_URL("https://speller.yandex.net/services/spellservice.json/checkTexts"),
    UKR_NO_CAPITALS("київ"),
    UKR_CORRECT("Привіт"),
    UKR_MISSPELLED("Прибіт"),

    RUS_WITH_DIGITS("При8ет"),
    RUS_WITH_URL("https://speller.yandex.net/services/spellservice.json/checkTexts"),
    RUS_NO_CAPITALS("москва"),
    RUS_CORRECT("Привет"),
    RUS_MISSPELLED("Пибет"),

    ENG_WITH_DIGITS("He11o"),
    ENG_WITH_URL("https://speller.yandex.net/services/spellservice.json/checkTexts"),
    ENG_NO_CAPITALS("london"),
    ENG_CORRECT("Hello"),
    ENG_MISSPELLED("helloj");

    public String text;

    Text(String text) {
        this.text = text;
    }
}
