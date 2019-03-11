
import io.restassured.response.Response;
import org.testng.annotations.Test;
import java.util.List;

import static constants.Format.*;
import static constants.Language.*;
import static constants.Option.*;
import static constants.Texts.*;
import static core.YandexSpellerServiceObj.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.text.MatchesPattern.*;

public class YandexSpellerApiTests {

    @Test
    public void checkCorrectTexts() {
        String[] texts = {ENG_CORRECT, RUS_CORRECT, UKR_CORRECT};
        Response response = requestBuilder()
                .setLanguage(ENGLISH, RUSSIAN, UKRAINIAN)
                .setText(texts)
                .buildRequest()
                .sendGetRequest();
        List<String> result = getStringResult(response);
        assertThat("API reported errors in correct text(s): " + result, result, hasSize(0));
    }

    @Test //BUG WAS FOUND (API SHOWS NO ERROR IN ENGLISH WORD 'jnew')
    public void FcheckMisspelledTexts() {
        String[] texts = {ENG_MISSPELLED, RUS_MISSPELLED, UKR_MISSPELLED};
        Response response = requestBuilder()
                .setLanguage(ENGLISH, RUSSIAN, UKRAINIAN)
                .setText(texts)
                .buildRequest()
                .sendGetRequest();
        List<String> result = getStringResult(response);
        if (result.size() != texts.length) {
            for (String text : texts) {
                assertThat("API failed to find spelling error in text: " + text, result, contains(text));
            }
        }
    }

    @Test //BUG WAS FOUND (API SHOWS ERROR ONLY IN RUSSIAN TEXT)
    public void FcheckIncorrectTextsWithDigits() {
        String[] texts = {ENG_WITH_DIGITS, RUS_WITH_DIGITS, UKR_WITH_DIGITS};
        Response response = requestBuilder()
                .setLanguage(ENGLISH, RUSSIAN, UKRAINIAN)
                .setText(texts)
                .buildRequest()
                .sendGetRequest();
        List<String> result = getStringResult(response);
        if (result.size() != texts.length) {
            for (String text : texts) {
                assertThat("API failed to find error in text mixed with gigits: " + text, result, contains(text));
            }
        }
    }

    @Test //BUG WAS FOUND (API SHOWS ERROR ONLY IN ENGLISH TEXT)
    public void FcheckIncorrectTextsWithLinks() {
        String[] given = {ENG_WITH_URL, RUS_WITH_URL, UKR_WITH_URL};
        String[] expected = {ENG_CORRECT, RUS_CORRECT, UKR_CORRECT};
        Response response = requestBuilder()
                .setLanguage(ENGLISH, RUSSIAN, UKRAINIAN)
                .setText(given)
                .buildRequest()
                .sendGetRequest();
        List<String> actual = getStringResult(response);
        if (actual.size() != expected.length) {
            for (int i = 0; i < expected.length; i++) {
                assertThat("API failed to find error in text mixed with URLs: " + given[i],
                        actual, contains(matchesPattern(expected[i] + ".*")));
            }
        }
    }

    @Test //BUG WAS FOUND (API ALLOWS LOWER CASE IN PROPER NAMES IN ALL LANGUAGES)
    public void FcheckIncorrectProperNamesWithLowerCase() {
        String[] texts = {ENG_NO_CAPITALS, RUS_NO_CAPITALS, UKR_NO_CAPITALS};
        Response response = requestBuilder()
                .setLanguage(ENGLISH, RUSSIAN, UKRAINIAN)
                .setText(texts)
                .buildRequest()
                .sendGetRequest();
        List<String> result = getStringResult(response);
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
                .sendPostRequest()
                .then().assertThat()
                .specification(badResponseSpecification())
                .body(containsString("SpellerService: Invalid parameter 'lang'"));
    }

    @Test
    public void checkIgnoreDigitsOption() {
        Response response = requestBuilder()
                .setLanguage(RUSSIAN, ENGLISH, UKRAINIAN)
                .setText(RUS_WITH_DIGITS, ENG_WITH_DIGITS, UKR_WITH_DIGITS)
                .setOptions(IGNORE_DIGITS)
                .buildRequest()
                .sendGetRequest();
        List<String> result = getStringResult(response);
        assertThat("API reported errors in text(s) with digits despite 'ignore digits' option: " + result,
                result, hasSize(0));
    }

    @Test //BUG WAS FOUND (API ALLOWS NO URLS DESPITE 'IGNORE URLS' OPTION)
    public void FcheckIgnoreUrlsOption() {
        Response response = requestBuilder()
                .setLanguage(RUSSIAN, ENGLISH, UKRAINIAN)
                .setText(RUS_WITH_URL, ENG_WITH_URL, UKR_WITH_URL)
                .setOptions(IGNORE_URLS)
                .buildRequest()
                .sendGetRequest();
        List<String> result = getStringResult(response);
        assertThat("API reported errors in text with URLs despite 'ignore URLs' option: " + result,
                result, hasSize(0));
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
