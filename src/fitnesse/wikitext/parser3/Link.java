package fitnesse.wikitext.parser3;

import fitnesse.html.HtmlTag;

public class Link {
  public static Symbol parse(Parser parser) {
    Symbol result = parser.peek(0).asSymbol(SymbolType.LINK);
    parser.advance();
    new Parser(parser).parseToTerminator(result, LINK_TERMINATOR);
    return result;
  }

  public static String translate(Symbol symbol, Translator translator) {
    String path = symbol.translateContent(translator);
    return makeLink(path, path, "");
  }

  public static String makeWikiLink(External external, String wikiPath, String trailer, String description) {
    return external.buildLink(wikiPath, description, trailer);
  }

  public static String makeLink(String description, String path, String attributes) {
    int files = path.indexOf("//files/");
    return HtmlTag.name("a").attribute("href", files < 0 ? path : path.substring(files + 2)).body(description).htmlInline();
  }

  private static final Terminator LINK_TERMINATOR = new Terminator(token -> !token.isType(TokenType.TEXT), "non-text");
}