package core;

import constants.Language;
import org.testng.annotations.DataProvider;

import static constants.Language.*;
import static constants.Texts.*;

public class DataProvidersForSpeller {

    @DataProvider
    public Object[][] correctTextsProvider() {
        return new Object[][]{
                {ENGLISH, ENG_CORRECT},
                {RUSSIAN, RUS_CORRECT},
                {UKRAINIAN, UKR_CORRECT}
        };
    }

    @DataProvider
    public Object[][] misspelledTextsProvider() {
        return new Object[][]{
                {ENGLISH, ENG_MISSPELLED},
                {RUSSIAN, RUS_MISSPELLED},
                {UKRAINIAN, UKR_MISSPELLED}
        };
    }

    @DataProvider
    public Object[][] textsWithDigitsProvider() {
        return new Object[][]{
                {ENGLISH, ENG_WITH_DIGITS},
                {RUSSIAN, RUS_WITH_DIGITS},
                {UKRAINIAN, UKR_WITH_DIGITS}
        };
    }

    @DataProvider
    public Object[][] textsWithLinksProvider() {
        return new Object[][]{
                {ENGLISH, ENG_WITH_URL},
                {RUSSIAN, RUS_WITH_URL},
                {UKRAINIAN, UKR_WITH_URL}
        };
    }

    @DataProvider
    public Object[][] properNamesWithLowerCaseProvider() {
        return new Object[][]{
                {ENGLISH, ENG_NO_CAPITALS},
                {RUSSIAN, RUS_NO_CAPITALS},
                {UKRAINIAN, UKR_NO_CAPITALS}
        };
    }

    @DataProvider
    public Object[][] mixOfMisspelledTextsAndLanguagesProvider() {
        String[] texts = {RUS_MISSPELLED, UKR_MISSPELLED, ENG_MISSPELLED};
        Language[] languages = {ENGLISH, RUSSIAN, UKRAINIAN};
        return new Object[][]{
                {languages, texts}
        };
    }
}
