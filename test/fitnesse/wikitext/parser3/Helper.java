package fitnesse.wikitext.parser3;

import static org.junit.Assert.assertEquals;

public class Helper {

  public static void assertScansWord(String word, String tokenType) {
    assertScans("Text=" + word + "hi", word + "hi");
    assertScans("Text=" + word + "!see", word + "!see");
    assertScans(tokenType + "=" + word + " ,Text=hi", word + " hi");
    assertScans("Text=hi," + tokenType + "=" + word + " ,Text=there", "hi" + word + " there");
    assertScans("Text=" + word.substring(0,1) + ",BlankSpace= ,Text=" + word.substring(1),
      word.substring(0,1) + " " + word.substring(1));
  }

  public static void assertScansWordAtStart(String word, String tokenType) {
    assertScans("Text=" + word + "hi", word + "hi");
    assertScans("Text=" + word + "!see", word + "!see");
    assertScans(tokenType + "=" + word + " ,Text=hi", word + " hi");
    assertScans("Text=hi" + word + ",BlankSpace= ,Text=there", "hi" + word + " there");
    assertScans("Text=hi,NewLine=\n," + tokenType + "=" + word + " ,Text=there", "hi\n" + word + " there");
    assertScans("Text=" + word.substring(0,1) + ",BlankSpace= ,Text=" + word.substring(1),
      word.substring(0,1) + " " + word.substring(1));
  }
  public static void assertScans(String expected, String input) {
    String result = expected + (expected.length() > 0 ? "," : "") + "End";
    assertEquals(input, result, scan(input));
  }

  public static void assertParses(String expected, String input) {
    assertParses(expected, input, makeExternal());
  }

  public static void assertParses(String expected, String input, FakeExternal external) {
    String result = "LIST" + (expected.length() > 0 ? "(" + expected + ")" : expected);
    assertEquals(input, result, parse(input, external).toString());
  }

  public static void assertTranslates(String expected, String input) {
    assertTranslates(expected, input, makeExternal());
  }

  public static void assertTranslates(String expected, String input, FakeExternal external) { //todo: deal with newlines, could be platform-specific
    Symbol syntaxTree = parse(input, external);
    assertEquals(input, expected, new HtmlTranslator(external, syntaxTree).translate(syntaxTree));
  }

  public static Symbol parse(String input) {
    return parse(input, makeExternal());
  }

  public static Symbol parse(String input, FakeExternal external) {
    return Parser.parse(input, ParseRules.make(external, external));
  }

  public static FakeExternal makeExternal() { return new FakeExternal(new FakeSourcePage());}

  public static String toError(String message) { return " <span class=\"fail\">" + message + "</span> "; }

  private static String scan(String input) {
    TokenSource source = new TokenSource(input, TokenTypes.WIKI_PAGE_TYPES);
    StringBuilder result = new StringBuilder();
    do {
      Token token = source.take();
      if (result.length() > 0) result.append(",");
      result.append(token.toString());
    } while (source.getPrevious().getType() != TokenType.END);
    return result.toString();
  }
}
