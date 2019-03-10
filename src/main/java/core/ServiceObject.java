package core;

import beans.YandexSpellerAnswer;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import constants.Language;
import constants.Option;
import constants.Text;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.apache.http.HttpStatus;

import java.util.*;
import java.util.stream.Collectors;

import static constants.ParameterName.*;
import static org.hamcrest.Matchers.lessThan;

public class ServiceObject {

    private static final String SPELLER_URI = "https://speller.yandex.net/services/spellservice.json/checkTexts";
    private Map<String, List<String>> parameters;

    private ServiceObject(Map<String, List<String>> parameters) {
        this.parameters = parameters;
    }

    public static ApiRequestBuilder requestBuilder() {
        return new ApiRequestBuilder();
    }

    public static class ApiRequestBuilder {
        private Map<String, List<String>> parameters = new HashMap<>();

        public ApiRequestBuilder setLanguage(Language... lang) {
            List<String> languagesToSet = Arrays
                    .stream(lang)
                    .map(language -> language.value)
                    .collect(Collectors.toList());
            parameters.put(LANGUAGE, languagesToSet);
            return this;
        }

        public ApiRequestBuilder setOptions(Option... options) {
            int resultParameter = 0;
            for (Option option : options) {
                resultParameter = +option.value;
            }
            parameters.put(OPTIONS, Arrays.asList(String.valueOf(resultParameter)));
            return this;
        }

        public ApiRequestBuilder setFormat(String... format) {
            parameters.put(FORMAT, Arrays.asList(format));
            return this;
        }

        public ApiRequestBuilder setText(String... text) {
//            List<String> textsToCheck = Arrays
//                    .stream(text)
//                    .map(value -> value.text)
//                    .collect(Collectors.toList());
            parameters.put(TEXT, Arrays.asList(text));
            return this;
        }

        public ServiceObject buildRequest() {
            return new ServiceObject(parameters);
        }
    }

    public Response sendRequest() {
        return RestAssured
                .given(ServiceObject.requestSpecification())
                .queryParams(parameters)
                .get(SPELLER_URI)
                .prettyPeek();
    }

    public static List<YandexSpellerAnswer> getAnswers(Response response) {

        List<List<YandexSpellerAnswer>> answers = new Gson().fromJson(response.asString().trim(), new TypeToken<List<List<YandexSpellerAnswer>>>() {
        }.getType());
        return answers
                .stream()
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }


    public static ResponseSpecification responseSpecification() {
        return new ResponseSpecBuilder()
                .expectContentType(ContentType.JSON)
                .expectHeader("Connection", "keep-alive")
                .expectResponseTime(lessThan(20000L))
                .expectStatusCode(HttpStatus.SC_OK)
                .build();
    }

    public static RequestSpecification requestSpecification() {
        return new RequestSpecBuilder()
                .setAccept(ContentType.XML)
                .setRelaxedHTTPSValidation()
                .addHeader("custom header2", "header2.value")
                .addQueryParam("requestID", new Random().nextLong())
                .setBaseUri(SPELLER_URI)
                .build();
    }
}
