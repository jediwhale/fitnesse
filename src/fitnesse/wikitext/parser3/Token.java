package fitnesse.wikitext.parser3;

public class Token {
  public Token(TokenType type, String content) {
    this.type = type;
    this.content = content;
  }

  public Token(TokenType type) {
    this(type, "");
  }

  public TokenType getType() { return type; }
  public String getContent() { return content; }

  public boolean isType(TokenType type) { return this.type == type; }

  public boolean isStartType() {
    return isType(TokenType.BRACE_START) || isType(TokenType.BRACKET_START) || isType(TokenType.PARENTHESIS_START);
  }

  public boolean isEndOfLine() {
    return isType(TokenType.NEW_LINE) || isType(TokenType.END) || isType(TokenType.NESTING_END);
  }

  public boolean isWord() {
    return isType(TokenType.TEXT) && content.chars().allMatch(c -> Character.isLetterOrDigit(c) || c == '_' || c == '.');
  }

  public String toString() {
    return type.toString() + (content.length() > 0 ? "=" + content : "");
  }

  private final TokenType type;
  private final String content;

}
