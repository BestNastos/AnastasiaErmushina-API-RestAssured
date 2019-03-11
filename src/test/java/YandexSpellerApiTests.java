import beans.YandexSpellerAnswer;
import org.testng.annotations.Test;

import java.util.List;

import static constants.ErrorCode.*;
import static constants.Format.*;
import static constants.Language.*;
import static constants.Option.*;
import static constants.Texts.*;
import static core.YandexSpellerServiceObj.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

public class YandexSpellerApiTests {

    @Test
    public void checkCorrectTexts() {
        List<String> result = getStringResult(
                requestBuilder()
                        .setLanguage(ENGLISH, RUSSIAN, UKRAINIAN)
                        .setText(ENG_CORRECT, RUS_CORRECT, UKR_CORRECT)
                        .buildRequest()
                        .sendGetRequest());
        assertThat("API reported errors in correct text: " + result, result.isEmpty());
    }

    @Test //BUG WAS FOUND
    public void checkMisspelledTexts() {
        String[] texts = {ENG_MISSPELLED, RUS_MISSPELLED, UKR_MISSPELLED};
        List<String> result = getStringResult(
                requestBuilder()
                        .setLanguage(ENGLISH, RUSSIAN, UKRAINIAN)
                        .setText(texts)
                        .buildRequest()
                        .sendGetRequest());
        if (result.size() != texts.length) {
            for (String text : texts) {
                assertThat("API failed to find spelling error in text: " + text, result.contains(text));
            }
        }
    }

    @Test
    public void checkErrorCodeForMisspelling(){
        List<YandexSpellerAnswer> answers = getAnswers(requestBuilder()
                .setLanguage(RUSSIAN)
                .setText(RUS_MISSPELLED)
                .buildRequest()
                .sendGetRequest());
        if (!answers.isEmpty()) {
            assertThat("API displays wrong error code: " + answers.get(0).code + " instead of: "
                    + ERROR_UNKNOWN_WORD.code, answers.get(0).code == ERROR_UNKNOWN_WORD.code);
        } else checkMisspelledTexts();
    }

    @Test //BUG WAS FOUND
    public void checkIncorrectTextsWithDigits() {
        String[] texts = {ENG_WITH_DIGITS, RUS_WITH_DIGITS, UKR_WITH_DIGITS};
        List<String> result = getStringResult(
                requestBuilder()
                        .setLanguage(ENGLISH, RUSSIAN, UKRAINIAN)
                        .setText(texts)
                        .buildRequest()
                        .sendGetRequest());
        if (result.size() != texts.length) {
            for (String text : texts) {
                assertThat("API failed to find error in text with digits: " + text,
                        result.contains(text));
            }
        }
    }

    @Test //BUG WAS FOUND
    public void checkIncorrectTextsWithLinks() {
        String[] texts = {ENG_WITH_URL, RUS_WITH_URL, UKR_WITH_URL};
        List<String> result = getStringResult(
                requestBuilder()
                        .setLanguage(ENGLISH, RUSSIAN, UKRAINIAN)
                        .setText(texts)
                        .buildRequest()
                        .sendGetRequest());
        if (result.size() != texts.length) {
            for (String text : texts) {
                assertThat("API failed to find error in text with URL: " + text,
                        result.contains(text));
            }
        }
    }

    @Test //BUG WAS FOUND
    public void checkIncorrectProperNamesWithLowerCase() {
        String[] texts = {ENG_NO_CAPITALS, RUS_NO_CAPITALS, UKR_NO_CAPITALS};
        List<String> result = getStringResult(requestBuilder()
                .setLanguage(ENGLISH, RUSSIAN, UKRAINIAN)
                .setText(texts)
                .buildRequest()
                .sendGetRequest());
        if (result.size() != texts.length) {
            for (String text : texts) {
                assertThat("API failed to find error in proper names with lower case: " + text,
                        result.contains(text));
            }
        }
    }

    @Test
    public void checkIncorrectLanguageParameter() {
        requestBuilder()
                .setLanguage(INCORRECT_LANGUAGE)
                .setText(ENG_CORRECT)
                .buildRequest()
                .sendPostRequest()
                .then().assertThat()
                .specification(badResponseSpecification())
                .body(containsString("SpellerService: Invalid parameter 'lang'"));
    }

    @Test
    public void checkIgnoreDigitsOption() {
        List<String> result = getStringResult(
                requestBuilder()
                        .setLanguage(RUSSIAN, ENGLISH, UKRAINIAN)
                        .setText(RUS_WITH_DIGITS, ENG_WITH_DIGITS, UKR_WITH_DIGITS)
                        .setOptions(IGNORE_DIGITS)
                        .buildRequest()
                        .sendGetRequest());
        assertThat("API reported errors in text with digits despite 'ignore digits' option: " + result,
                result.isEmpty());
    }

    @Test
    public void checkIgnoreUrlsOption() {
        List<String> result = getStringResult(
                requestBuilder()
                        .setLanguage(UKRAINIAN, RUSSIAN, ENGLISH)
                        .setText(UKR_WITH_URL, RUS_WITH_URL, ENG_WITH_URL)
                        .setOptions(IGNORE_URLS)
                        .buildRequest()
                        .sendGetRequest());
        assertThat("API reported errors in text with URL despite 'ignore URLs' option: " + result,
                result.isEmpty());
    }

    @Test
    public void checkCorrectFormatOption() {
        requestBuilder()
                .setLanguage(ENGLISH)
                .setText(ENG_CORRECT)
                .setFormat(HTML)
                .buildRequest()
                .sendPostRequest()
                .then().assertThat()
                .specification(goodResponseSpecification());
    }

    @Test
    public void checkIncorrectFormatOption() {
        requestBuilder()
                .setLanguage(ENGLISH)
                .setText(ENG_CORRECT)
                .setFormat(INCORRECT_FORMAT)
                .buildRequest()
                .sendPostRequest()
                .then().assertThat()
                .specification(badResponseSpecification())
                .and()
                .body(containsString("SpellerService: Invalid parameter 'format'"));
    }
}
