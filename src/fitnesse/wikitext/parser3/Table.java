package fitnesse.wikitext.parser3;

import fitnesse.html.HtmlTag;
import fitnesse.wikitext.shared.Names;

import java.util.Arrays;
import java.util.function.BiConsumer;

class Table {

  static Symbol parseStandard(Parser parser) {
    String tableStart = parser.peek(0).getContent();
    if (tableStart.contains("!")) {
      parser.pushTypes(LITERAL_TABLE_TYPES);
    }
    else if (tableStart.contains("^")) {
      parser.pushTypes(TokenTypes.NO_LINK_TABLE_TYPES);
    }
    else {
      parser.pushTypes(TokenTypes.STANDARD_TABLE_TYPES);
    }
    Symbol result = parseWithBarDelimiter(tableStart.contains("!") || tableStart.contains("^")  ? parser.textType(SymbolType.TEXT) : parser);
    parser.popTypes();
    return result;
  }

  static Symbol parsePlain(Parser parser) {
    return parseWithCustomDelimiter(parser.textType(SymbolType.TEXT));
  }

  private static Symbol parseWithCustomDelimiter(Parser parser) {
    Symbol result = new TaggedSymbol(SymbolType.TABLE);
    parser.advance();
    BiConsumer<Symbol, String> populateRow;
    if (parser.peek(0).isType(TokenType.TEXT)) {
      String separator = parser.advance().getContent();
      populateRow = (row, text) -> addSeparateCells(row, text, separator);
    }
    else {
      populateRow = Table::addSingleCell;
    }
    if (parser.peek(0).isType(TokenType.BLANK_SPACE)) {
      parser.advance();
      result.add(makeRow(parser, populateRow));
    }
    else {
      parser.advance(); //todo: check newline
    }
    do {
      result.add(makeRow(parser, populateRow));
    } while (!parser.peek(0).isType(TokenType.END) && !parser.peek(0).isType(TokenType.PLAIN_TEXT_TABLE_END));
    parser.advance();
    return result;
  }

  private static Symbol makeRow(Parser parser, BiConsumer<Symbol, String> populateRow) {
    Symbol row = new TaggedSymbol(SymbolType.LIST);
    String rowText = parser.parseText(new Terminator(TokenType.NEW_LINE));
    populateRow.accept(row, rowText);
    return row;
  }

  private static void addSeparateCells(Symbol row, String rowText, String separator) {
    for (String cellText : rowText.split(separator)) {
      addSingleCell(row, cellText);
    }
  }

  private static void addSingleCell(Symbol row, String rowText) {
    Symbol cell = new TaggedSymbol(SymbolType.LIST);
    cell.add(Symbol.text(rowText));
    row.add(cell);
  }

  private static Symbol parseWithBarDelimiter(Parser parser) {
    Symbol result = new TaggedSymbol(SymbolType.TABLE);
    parser.advance();
    do {
      Symbol row = new TaggedSymbol(SymbolType.LIST);
      do {
        Symbol cell = parser.parseList(SymbolType.LIST,
          new Terminator(type -> type == TokenType.CELL_DELIMITER || type == TokenType.TABLE_END, "|", ""));
        row.add(cell);
      } while (!isEndOfRow(parser.peek(-1)));
      result.add(row);
    } while (!isEndOfTable(parser.peek(-1)));
    return result;
  }

  private static boolean isEndOfRow(Token token) {
    return token.getContent().contains("\r") || token.getContent().contains("\n") || isEndOfTable(token);
  }

  private static boolean isEndOfTable(Token token) {
    return token.isType(TokenType.TABLE_END) || token.isType(TokenType.END);
  }

  static String translate(Symbol table, Translator translator) {
    HtmlTag result = table.collectBranches(child -> row(child, translator), HtmlTag.name("table"), HtmlTag::add);
    table.ifPresent(Names.CLASS, result::addAttribute);
    return result.html();
  }

  private static HtmlTag row(Symbol row, Translator translator) {
    HtmlTag result = row.collectBranches(child -> cell(child, translator), HtmlTag.name("tr"), HtmlTag::add);
    row.ifPresent(Names.CLASS, result::addAttribute);
    return result;
  }

  private static HtmlTag cell(Symbol cell, Translator translator) {
    HtmlTag result = HtmlTag.name("td").body(new CellTranslator(translator).translate(cell).trim().replaceAll(LITERAL_DELIMITER, ""));
    cell.ifPresent(Names.CLASS, result::addAttribute);
    return result;
  }

  private static final String LITERAL_DELIMITER = String.valueOf(134);

  private static final TokenTypes LITERAL_TABLE_TYPES = new TokenTypes(Arrays.asList(
    TokenType.VARIABLE_VALUE, // must be first
    TokenType.EXPRESSION_START,
    TokenType.EXPRESSION_END,
    TokenType.LITERAL_START,
    TokenType.LITERAL_END,
    TokenType.BRACE_END,
    TokenType.NEW_LINE,
    TokenType.TABLE_END,
    TokenType.CELL_DELIMITER,
    TokenType.NESTING_PSEUDO_START,
    TokenType.NESTING_PSEUDO_END
  ));

  private static class CellTranslator implements Translator {
    CellTranslator(Translator translator) {
      this.translator = translator;
    }

    @Override
    public TranslateRule findRule(SymbolType symbolType) {
      return symbolType == SymbolType.LITERAL
        // to prevent literals from being trimmed in table cells
        ? (symbol, t) -> LITERAL_DELIMITER + translator.translate(symbol) + LITERAL_DELIMITER
        : translator.findRule(symbolType);
    }

    private final Translator translator;
  }
}
