package fitnesse.wikitext.parser3;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

class TokenTypes {
  private static final TokenTypes CORE_TYPES = new TokenTypes(
    Arrays.asList(
      TokenType.ALIAS_START,
      TokenType.ALIAS_MIDDLE,
      TokenType.ALIAS_END,
      TokenType.ANCHOR_NAME,
      TokenType.ANCHOR_REFERENCE,
      TokenType.BOLD_ITALIC,
      TokenType.BULLET_LIST,
      TokenType.CENTER,
      TokenType.COLLAPSIBLE_END,
      TokenType.COLLAPSIBLE_START,
      TokenType.COMMENT,
      TokenType.CONTENTS,
      TokenType.EXPRESSION_START,
      TokenType.EXPRESSION_END,
      TokenType.HASH_TABLE,
      TokenType.HEADER,
      TokenType.HEADINGS,
      TokenType.HELP,
      TokenType.HORIZONTAL_RULE,
      TokenType.IMAGE,
      TokenType.INCLUDE,
      TokenType.LINK,
      TokenType.LITERAL_START,
      TokenType.META,
      TokenType.NESTING_START,
      TokenType.NESTING_END,
      TokenType.NOTE,
      TokenType.NUMBERED_LIST,
      TokenType.PATH,
      TokenType.PLAIN_TEXT_TABLE_START,
      TokenType.PLAIN_TEXT_TABLE_END,
      TokenType.PREFORMAT_START,
      TokenType.PREFORMAT_END,
      TokenType.SEE,
      TokenType.STYLE,
      TokenType.TABLE_START,
      TokenType.TABLE_END,

      TokenType.LITERAL_END,
      TokenType.NEW_LINE,
      TokenType.BLANK_SPACE,
      TokenType.BOLD,
      TokenType.BRACE_START,
      TokenType.BRACE_END,
      TokenType.BRACKET_START,
      TokenType.BRACKET_END,
      TokenType.CELL_DELIMITER,
      TokenType.ITALIC,
      TokenType.PARENTHESIS_START,
      TokenType.PARENTHESIS_END,
      TokenType.STRIKE
    ),
    Arrays.asList(
      TokenType.LAST_MODIFIED,
      TokenType.TODAY
    ));

  static final TokenTypes WIKI_PAGE_TYPES = new TokenTypes(CORE_TYPES)
    .addFirst(TokenType.VARIABLE_VALUE) //must be first
    .add(TokenType.DEFINE);

  static final TokenTypes DEFINE_TYPES = new TokenTypes(CORE_TYPES)
    .addFirst(TokenType.VARIABLE_TOKEN) //must be first
    .add(TokenType.DEFINE_NESTED);

  static final TokenTypes HASH_TABLE_TYPES = new TokenTypes(WIKI_PAGE_TYPES)
    .add(TokenType.COLON)
    .add(TokenType.COMMA);

  static final TokenTypes STANDARD_TABLE_TYPES = new TokenTypes(WIKI_PAGE_TYPES)
    .add(TokenType.NESTING_PSEUDO_START)
    .add(TokenType.NESTING_PSEUDO_END);

  static final TokenTypes NO_LINK_TABLE_TYPES = new TokenTypes(STANDARD_TABLE_TYPES)
    .remove(TokenType.LINK);

  TokenTypes(List<TokenType> delimiters) {
    this(delimiters, Collections.emptyList());
  }

  TokenTypes(List<TokenType> delimiters, List<TokenType> keywords) {
    this.delimiters = delimiters;
    this.keywords = keywords;
  }

  TokenTypes(TokenTypes other) {
    delimiters = new ArrayList<>(other.delimiters);
    keywords = new ArrayList<>(other.keywords);
  }

  //todo: replace with function to search delimiters
  List<TokenType> getDelimiters() {
    return delimiters;
  }

  //todo: replace with function to search keywords
  List<TokenType> getKeywords() {
    return keywords;
  }

  private TokenTypes add(TokenType type) {
    delimiters.add(type);
    return this;
  }

  private TokenTypes addFirst(TokenType type) {
    delimiters.add(0, type);
    return this;
  }

  private TokenTypes remove(TokenType type) {
    delimiters.remove(type);
    return this;
  }

  private final List<TokenType> delimiters;
  private final List<TokenType> keywords;
}
