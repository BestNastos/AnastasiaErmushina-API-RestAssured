import beans.YandexSpellerAnswer;
import constants.Text;
import core.ServiceObject;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import java.util.List;

import static constants.Language.*;
import static constants.Text.*;
import static org.testng.Assert.*;

public class ApiTests {

    @Test
    public void misspelledTextsTest(){
        Text[] textsToCheck = {ENG_MISSPELLED, RUS_MISSPELLED, UKR_MISSPELLED};
        Response response = ServiceObject
                .requestBuilder()
                .setLanguage(ENGLISH, RUSSIAN, UKRAINIAN)
                .setText(textsToCheck)
                .buildRequest()
                .sendRequest();

        List<YandexSpellerAnswer> answers = ServiceObject.getAnswers(response);
        assertEquals(answers.size(), textsToCheck.length); //if 1 word were correct size would be 2
    }

}
