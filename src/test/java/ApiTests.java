
import io.restassured.response.Response;
import org.testng.annotations.Test;

import java.util.List;
import java.util.stream.Collectors;

import static constants.Language.*;
import static constants.Texts.*;
import static core.ServiceObject.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.hasSize;

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
    public void checkIncorrectTextsWithLinks(){
        String[] texts = {ENG_WITH_URL, RUS_WITH_URL, UKR_WITH_URL};
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
                assertThat("API failed to find error in text(s) mixed with URLs: " + text, result, contains(text));
            }
        }
    }

    @Test //BUG FOUND
    public void checkProperNamesWithLowerCase(){
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


}
