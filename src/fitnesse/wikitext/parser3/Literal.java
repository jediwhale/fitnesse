package fitnesse.wikitext.parser3;

public class Literal {

  public  static void scan(Content content, TokenList tokens) {
    new Scanner(TokenType.LITERAL_END, text -> TokenType.TEXT)
      .scan(content, tokens);
  }

  public static Symbol parse(Parser parser) {
    parser.advance();
    Symbol result = parser.parseList(parser.peek(-1));
    if (result.hasError()) return result;
    result.addFirst(Symbol.inputText(TokenType.LITERAL_START));
    result.add(Symbol.inputText(TokenType.LITERAL_END));
    return result;
  }

}
