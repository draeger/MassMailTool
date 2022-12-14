/**
 *
 */
package org.draeger.massmailtool;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.text.StringSubstitutor;
import org.junit.jupiter.api.Test;


/**
 * @author Andreas Dr&auml;ger
 *
 */
class TestTextReplacement {

  @Test
  void test() {
    String greeting = "Sehr geehrte${Geschlechtssuffix} ${Anrede} ${Titel} ${Name},";
    String inText   = "Daran, können Sie, werte${Geschlechtssuffix} ${Anrede} ${Titel} ${Name},";//\ngut die Wichtigkeit des Themas erkennen.";

    Map<String, Object> placeholder = new HashMap<>();
    placeholder.put("Geschlechtssuffix", "r");
    placeholder.put("Anrede", "Herr");
    placeholder.put("Titel", "Dr.");
    placeholder.put("Name", "Frankenstein");
    StringSubstitutor sub = new StringSubstitutor(placeholder);
    sub.setVariablePrefix("${");
    sub.setVariableSuffix("}");
    String resultGreeting = sub.replace(greeting).replaceAll("\\s+", " ");
    String resultInText = sub.replace(inText).replaceAll("\\s+", " ");
    assertEquals(resultGreeting, "Sehr geehrter Herr Dr. Frankenstein,");
    assertEquals(resultInText, "Daran, können Sie, werter Herr Dr. Frankenstein,");//\ngut die Wichtigkeit des Themas erkennen.");

    placeholder.put("Geschlechtssuffix", "");
    placeholder.put("Anrede", "");
    placeholder.put("Titel", "");
    placeholder.put("Name", "Damen und Herren");
    resultGreeting = sub.replace(greeting).replaceAll("\\s+", " ");
    assertEquals(resultGreeting, "Sehr geehrte Damen und Herren,");

    placeholder.put("Geschlechtssuffix", "");
    placeholder.put("Anrede", "Frau");
    placeholder.put("Titel", "");
    placeholder.put("Name", "Müller");
    resultGreeting = sub.replace(greeting).replaceAll("\\s+", " ");
    assertEquals(resultGreeting, "Sehr geehrte Frau Müller,");

    placeholder.put("Titel", "Prof. Dr.");
    resultGreeting = sub.replace(greeting).replaceAll("\\s+", " ");
    assertEquals(resultGreeting, "Sehr geehrte Frau Prof. Dr. Müller,");

    placeholder.put("Geschlechtssuffix", "r");
    placeholder.put("Anrede", "Herr");
    resultGreeting = sub.replace(greeting).replaceAll("\\s+", " ");
    assertEquals(resultGreeting, "Sehr geehrter Herr Prof. Dr. Müller,");
  }
}
