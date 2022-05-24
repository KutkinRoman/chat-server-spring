package chat.security.jwt;

public enum TokenType {

    BEARER("Bearer "),
    TOKEN("token");

    private final String header;

    TokenType (String header) {
        this.header = header;
    }

    public String getHeader () {
        return header;
    }
}
