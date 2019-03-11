package constants;

public enum Format {
    PLAIN("plain"),
    HTML("html"),
    INCORRECT_FORMAT("incorrect format");

    public String format;

    Format(String format) {
        this.format = format;
    }
}
