package fitnesse.wikitext.parser3;

import org.junit.Test;

import static fitnesse.wikitext.parser3.Helper.*;

public class AliasTest {
  @Test
  public void scansAlias() {
    assertScans("AliasStart=[[,Text=hi,AliasMiddle=][,Text=there,AliasEnd=]]", "[[hi][there]]");
    assertScans("BracketStart=[,BlankSpace= ,BracketStart=[,Text=hi,AliasMiddle=][,Text=there,AliasEnd=]]", "[ [hi][there]]");
  }

  @Test
  public void parsesAlias() {
    assertParses("ALIAS(SOURCE=[[,LIST(TEXT=hi),SOURCE=][,LIST(TEXT=there),SOURCE=]])", "[[hi][there]]");
    assertParses("ALIAS(SOURCE=[[,LIST(TEXT=hi),SOURCE=][,LIST(WIKI_LINK=ThereBob),SOURCE=]])", "[[hi][ThereBob]]");
  }

  @Test
  public void translatesAlias() {
    assertTranslates(Html.anchor("Fake.there", "hi"), "[[hi][there]]");
    assertTranslates("say" + Html.anchor("Fake.there", "hi") + "now", "say[[hi][there]]now");

    assertTranslates(Html.anchor("#anchor", "tag"), "[[tag][#anchor]]");
    assertTranslates(Html.anchor("Fake.PageOne", "tag"), "[[tag][PageOne]]");
    assertTranslates(Html.anchor("Fake.>PageOne", "tag"), "[[tag][>PageOne]]");
    assertTranslates(Html.anchor("Fake.PageOne", "<i>tag</i>"), "[[''tag''][PageOne]]");
    assertTranslates(Html.anchor("Fake.PageOne", "you're it"), "[[you're it][PageOne]]");
    assertTranslates(Html.anchor("Fake.PageOne", "PageOne"), "[[PageOne][IgnoredPage]]");
    assertTranslates(Html.anchor("Fake.PageOne?edit", "tag"), "[[tag][PageOne?edit]]");
    assertTranslates(Html.anchor("files/myfile", "tag"), "[[tag][http://files/myfile]]");
    assertTranslates(Html.anchor("https://example.com/myfile", "tag"), "[[tag][https://example.com/myfile]]");
    assertTranslates(Html.anchor("notes://example.com/myfile", "tag"), "[[tag][notes://example.com/myfile]]");
    assertTranslates(Html.anchor("files/my file", "tag"), "[[tag][http://files/my file]]");
    assertTranslates(Html.anchor("Fake.other.one_of-them", "tag"), "[[tag][other.one_of-them]]");
  }

  @Test
  public void translatesErrors() {
    assertTranslates(toError("[[ Empty description") + "][there]]", "[[][there]]");
    assertTranslates(toError("[[ Empty description") + "][]]", "[[][]]");
    assertTranslates(toError("[[ Missing terminator: ][") + "hi]there]]", "[[hi]there]]");
    assertTranslates(toError("[[") + "hi" + toError("][ Empty link") + "]]", "[[hi][]]");
    assertTranslates(toError("[[") + "hi" + toError("][ Missing terminator: ]]") + "there]", "[[hi][there]");
  }


  @Test
  public void translatesWithVariableLink() {
    external.putVariable("x", "3");
    assertTranslates(Html.anchor("Fake.PageTwo3", "tag"), "[[tag][PageTwo${x}]]");
  }
}
