package fitnesse.wikitext.parser3;

import fitnesse.html.HtmlTag;

class WikiList {
  static Symbol parse(Parser parser) {
    Symbol result = new BranchSymbol(SymbolType.WIKI_LIST, parser.peek(0).getContent());
    TokenType listType = parser.peek(0).getType();
    while(parser.peek(0).isType(listType)) {
      parser.advance();
      if (parser.peek(0).isType(TokenType.BLANK_SPACE)) parser.advance();
      Symbol item = new BranchSymbol(SymbolType.LIST);
      while (!parser.peek(0).isEndOfLine()) item.add(parser.parseCurrent());
      parser.advance();
      result.add(item);
    }
    return result;
  }

  static String translate(Symbol symbol, Translator translator) {
    String listType = symbol.getContent().contains("*") ? "ul" : "ol";
    return symbol.collectBranches(
      child -> new HtmlTag("li", translator.translate(child)), new HtmlTag(listType), HtmlTag::add)
      .html();
  }
}
