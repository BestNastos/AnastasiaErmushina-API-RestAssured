package constants;

public enum Option {
    DEFAULT(0),
    IGNORE_DIGITS(2),
    IGNORE_URLS(4),
    FIND_REPEAT_WORDS(8),
    IGNORE_CAPITALIZATION(512);

    public int value;

    Option(int value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}
