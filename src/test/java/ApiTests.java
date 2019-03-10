
import constants.Option;
import io.restassured.response.Response;
import org.hamcrest.text.MatchesPattern;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.stream.Collectors;

import static constants.Language.*;
import static constants.Option.*;
import static constants.Texts.*;
import static core.ServiceObject.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.text.MatchesPattern.*;

public class ApiTests {

    @Test
    public void checkCorrectTexts() {
        String[] texts = {ENG_CORRECT, RUS_CORRECT, UKR_CORRECT};
        Response response = requestBuilder()
                .setLanguage(ENGLISH, RUSSIAN, UKRAINIAN)
                .setText(texts)
                .buildRequest()
                .sendRequest();
        List<String> result = getAnswers(response)
                .stream()
                .map(res -> res.word)
                .collect(Collectors.toList());
        assertThat("API reported errors in correct text(s): " + result, result, hasSize(0));
    }

    @Test
    public void checkMisspelledTexts() {
        String[] texts = {ENG_MISSPELLED, RUS_MISSPELLED, UKR_MISSPELLED};
        Response response = requestBuilder()
                .setLanguage(ENGLISH, RUSSIAN, UKRAINIAN)
                .setText(texts)
                .buildRequest()
                .sendRequest();
        List<String> result = getAnswers(response)
                .stream()
                .map(res -> res.word)
                .collect(Collectors.toList());
        if (result.size() != texts.length) {
            for (String text : texts) {
                assertThat("API failed to find spelling error in text: " + text, result, contains(text));
            }
        }
    }

    @Test //BUG FOUND
    public void checkIncorrectTextsWithDigits() {
        String[] texts = {ENG_WITH_DIGITS, RUS_WITH_DIGITS, UKR_WITH_DIGITS};
        Response response = requestBuilder()
                .setLanguage(ENGLISH, RUSSIAN, UKRAINIAN)
                .setText(texts)
                .buildRequest()
                .sendRequest();
        List<String> result = getAnswers(response)
                .stream()
                .map(res -> res.word)
                .collect(Collectors.toList());
        if (result.size() != texts.length) {
            for (String text : texts) {
                assertThat("API failed to find error in text(s) mixed with gigits: " + text, result, contains(text));
            }
        }
    }

    @Test //BUG FOUND
    public void checkIncorrectTextsWithLinks() {
        String[] given = {ENG_WITH_URL, RUS_WITH_URL, UKR_WITH_URL};
        String[] expected = {ENG_CORRECT, RUS_CORRECT, UKR_CORRECT};
        Response response = requestBuilder()
                .setLanguage(ENGLISH, RUSSIAN, UKRAINIAN)
                .setText(given)
                .buildRequest()
                .sendRequest();
        List<String> actual = getAnswers(response)
                .stream()
                .map(res -> res.word)
                .collect(Collectors.toList());
        if (actual.size() != expected.length) {
            for (int i = 0; i < expected.length; i++) {
                assertThat("API failed to find error in text mixed with URLs: " + given[i],
                        actual, contains(matchesPattern(expected[i] + ".*")));
            }
        }
    }

    @Test //BUG FOUND
    public void checkIncorrectProperNamesWithLowerCase() {
        String[] texts = {ENG_NO_CAPITALS, RUS_NO_CAPITALS, UKR_NO_CAPITALS};
        Response response = requestBuilder()
                .setLanguage(ENGLISH, RUSSIAN, UKRAINIAN)
                .setText(texts)
                .buildRequest()
                .sendRequest();
        List<String> result = getAnswers(response)
                .stream()
                .map(res -> res.word)
                .collect(Collectors.toList());
        if (result.size() != texts.length) {
            for (String text : texts) {
                assertThat("API failed to find error in proper names with lower case: " + text, result, contains(text));
            }
        }
    }

    @Test
    public void checkIncorrectLanguageParameter() {
        requestBuilder()
                .setLanguage(INCORRECT_LANGUAGE)
                .setText(ENG_CORRECT)
                .buildRequest()
                .sendRequest()
                .then()
                .assertThat()
                .body(containsString("SpellerService: Invalid parameter 'lang'"));
    }

    @Test
    public void checkIgnoreDigitsOption() {
        Response response = requestBuilder()
                .setLanguage(RUSSIAN, ENGLISH, UKRAINIAN)
                .setText(RUS_WITH_DIGITS, ENG_WITH_DIGITS, UKR_WITH_DIGITS)
                .setOptions(IGNORE_DIGITS)
                .buildRequest()
                .sendRequest();
        List<String> result = getAnswers(response)
                .stream()
                .map(res -> res.word)
                .collect(Collectors.toList());
        assertThat("API reported errors in text(s) with digits despite 'ignore digits' option: " + result,
                result, hasSize(0));
    }

    @Test
    public void checkIgnoreLinksOption() {
        Response response = requestBuilder()
                .setLanguage(RUSSIAN, ENGLISH, UKRAINIAN)
                .setText(RUS_WITH_URL, ENG_WITH_URL, UKR_WITH_URL)
                .setOptions(IGNORE_URLS)
                .buildRequest()
                .sendRequest();
        List<String> result = getAnswers(response)
                .stream()
                .map(res -> res.word)
                .collect(Collectors.toList());
        assertThat("API reported errors in text(s) with URLs despite 'ignore URLs' option: " + result,
                result, hasSize(0));
    }


}
