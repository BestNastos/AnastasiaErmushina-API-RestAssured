import core.ServiceObject;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import static constants.Language.*;
import static constants.Text.*;

public class ApiTests {

    @Test
    public void misspelledTextsTest(){
        Response response = ServiceObject
                .requestBuilder()
                .setLanguage(ENGLISH, RUSSIAN, UKRAINIAN)
                .setText(ENG_MISSPELLED, RUS_MISSPELLED, UKR_MISSPELLED)
                .buildRequest()
                .sendRequest();
    }
}
