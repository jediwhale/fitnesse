package fitnesse.wikitext.parser3;

import java.util.EnumMap;

public class HtmlTranslator implements Translator {
  public HtmlTranslator(External external) {
    this.external = external;

    symbolTypes = new EnumMap<>(SymbolType.class);
    symbolTypes.put(SymbolType.ALIAS, Alias::translate);
    symbolTypes.put(SymbolType.ANCHOR_NAME, AnchorName::translate);
    symbolTypes.put(SymbolType.ANCHOR_REFERENCE, AnchorReference::translate);
    symbolTypes.put(SymbolType.BOLD, Pair.translate("b"));
    symbolTypes.put(SymbolType.BOLD_ITALIC, Pair.translate("b", "i"));
    symbolTypes.put(SymbolType.DEFINE, Variable::translate);
    symbolTypes.put(SymbolType.ERROR, Error::translate);
    symbolTypes.put(SymbolType.ITALIC, Pair.translate("i"));
    symbolTypes.put(SymbolType.LINK, Link::translate);
    symbolTypes.put(SymbolType.LIST, Symbol::translateChildren);
    symbolTypes.put(SymbolType.STRIKE, Pair.translate("strike"));
    symbolTypes.put(SymbolType.STYLE, Style::translate);
    symbolTypes.put(SymbolType.TEXT, Symbol::translateContent);
    symbolTypes.put(SymbolType.WIKI_LINK, WikiPath::translate);
  }

  public HtmlTranslator(HtmlTranslator original) {
    external = original.external;
    symbolTypes = original.symbolTypes;
  }

  @Override
  public Translator substitute(SymbolType original, SymbolType substitute) {
    substitutes.put(original,  substitute);
    return this;
  }

  @Override
  public String translate(Symbol symbol) {
    SymbolType type = symbol.getType();
    if (substitutes.containsKey(type)) type = substitutes.get(type);
    return symbolTypes.get(type).translate(symbol, this);
  }

  @Override
  public Translator copy() {
    return new HtmlTranslator(this);
  }

  @Override
  public External getExternal() { return external; }

  private final External external;
  private final EnumMap<SymbolType, TranslateRule> symbolTypes;
  private final EnumMap<SymbolType, SymbolType> substitutes = new EnumMap<>(SymbolType.class);
}
