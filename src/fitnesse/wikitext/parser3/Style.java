package fitnesse.wikitext.parser3;

import fitnesse.html.HtmlTag;

public class Style {
  public static Symbol parse(Parser parser) {
    String contents = parser.peek(0).getContent() + parser.peek(1).getContent();
    String cssClass = contents.substring(7);
    if (!parser.peek(1).isType(TokenType.TEXT) || cssClass.length() == 0) {
      return parser.makeError("Expected class name", 2);
    }
    if (!parser.peek(2).isStartType()) {
      return parser.makeError("Expected { ( or [", 2);
    }
    Symbol result = new BranchSymbol(SymbolType.STYLE);
    parser.advance();
    parser.advance();
    result.add(new LeafSymbol(SymbolType.TEXT, cssClass)); //todo: could be STYLE content?
    Token start = parser.peek(0);
    parser.advance();
    Symbol list = parser.parseList(SymbolType.LIST, Terminator.make(start, contents));
    if (list.hasError()) return list;
    result.add(list); //todo: could be STYLE children?
    return result;
  }

  public static String translate(Symbol symbol, Translator translator) {
    return HtmlTag.name("span").attribute("class", symbol.getContent(0)).body(translator.translate(symbol.getBranch(1))).htmlInline();
  }
}
