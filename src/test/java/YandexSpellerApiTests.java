import beans.YandexSpellerAnswer;
import constants.Language;
import core.DataProvidersForSpeller;
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

    @Test(dataProvider = "correctTextsProvider",
            dataProviderClass = DataProvidersForSpeller.class)
    public void checkCorrectTexts(Language language, String text) {
        List<String> result = getStringResult(
                requestBuilder()
                        .setLanguage(language)
                        .setText(text)
                        .buildRequest()
                        .sendGetRequest());
        assertThat("API reported errors in correct text: " + result, result.isEmpty());
    }

    //BUG WAS FOUND
    @Test(dataProvider = "misspelledTextsProvider",
            dataProviderClass = DataProvidersForSpeller.class)
    public void checkMisspelledTexts(Language language, String text) {
        List<String> result = getStringResult(
                requestBuilder()
                        .setLanguage(language)
                        .setText(text)
                        .buildRequest()
                        .sendGetRequest());
        assertThat("API failed to find spelling error in text: " + text,
                result.contains(text));
    }

    @Test
    public void checkErrorCodeForMisspelling() {
        List<YandexSpellerAnswer> answers = getAnswers(
                requestBuilder()
                        .setLanguage(RUSSIAN)
                        .setText(RUS_MISSPELLED)
                        .buildRequest()
                        .sendGetRequest());
        if (!answers.isEmpty()) {
            assertThat("API displays wrong error code: " + answers.get(0).code + " instead of: "
                    + ERROR_UNKNOWN_WORD.code, answers.get(0).code == ERROR_UNKNOWN_WORD.code);
        } else checkMisspelledTexts(RUSSIAN, RUS_MISSPELLED);
    }

    //BUGS WERE FOUND
    @Test(dataProvider = "textsWithDigitsProvider",
            dataProviderClass = DataProvidersForSpeller.class)
    public void checkIncorrectTextsWithDigits(Language language, String text) {
        List<String> result = getStringResult(
                requestBuilder()
                        .setLanguage(language)
                        .setText(text)
                        .buildRequest()
                        .sendGetRequest());
        assertThat("API failed to find error in text with digits: " + text,
                result.contains(text));


    }

    //BUGS WERE FOUND
    @Test(dataProvider = "textsWithLinksProvider",
            dataProviderClass = DataProvidersForSpeller.class)
    public void checkIncorrectTextsWithLinks(Language language, String text) {
        List<String> result = getStringResult(
                requestBuilder()
                        .setLanguage(language)
                        .setText(text)
                        .buildRequest()
                        .sendGetRequest());
        assertThat("API failed to find error in text with URL: " + text,
                result.contains(text));
    }

    //BUGS WERE FOUND
    @Test(dataProvider = "properNamesWithLowerCaseProvider",
            dataProviderClass = DataProvidersForSpeller.class)
    public void checkIncorrectProperNamesWithLowerCase(Language language, String text) {
        List<String> result = getStringResult(
                requestBuilder()
                        .setLanguage(language)
                        .setText(text)
                        .buildRequest()
                        .sendGetRequest());
        assertThat("API failed to find error in proper name with lower case: " + text,
                result.contains(text));
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

    @Test(dataProvider = "textsWithDigitsProvider",
            dataProviderClass = DataProvidersForSpeller.class)
    public void checkIgnoreDigitsOption(Language language, String text) {
        List<String> result = getStringResult(
                requestBuilder()
                        .setLanguage(language)
                        .setText(text)
                        .setOptions(IGNORE_DIGITS)
                        .buildRequest()
                        .sendGetRequest());
        assertThat("API reported errors in text with digits despite 'ignore digits' option: " + result,
                result.isEmpty());
    }

    @Test(dataProvider = "textsWithLinksProvider",
            dataProviderClass = DataProvidersForSpeller.class)
    public void checkIgnoreUrlsOption(Language language, String text) {
        List<String> result = getStringResult(
                requestBuilder()
                        .setLanguage(language)
                        .setText(text)
                        .setOptions(IGNORE_URLS)
                        .buildRequest()
                        .sendGetRequest());
        assertThat("API reported errors in text with URL despite 'ignore URLs' option: "
                + result, result.isEmpty());
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
