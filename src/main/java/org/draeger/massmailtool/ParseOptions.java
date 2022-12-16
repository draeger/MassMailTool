package org.draeger.massmailtool;

import java.util.ResourceBundle;

import de.zbit.util.ResourceManager;
import de.zbit.util.prefs.KeyProvider;
import de.zbit.util.prefs.Option;
import de.zbit.util.prefs.OptionGroup;

/**
 *
 * @author Andreas Dr&auml;ger
 */
public interface ParseOptions extends KeyProvider {

  /** Localization support. */
  static final ResourceBundle bundle = ResourceManager.getBundle(ParseOptions.class.getPackageName() + ".Messages");

  Option<String> TOKEN_START = new Option<>("TOKEN_START", String.class, bundle, "${");
  Option<String> TOKEN_END   = new Option<>("TOKEN_END",   String.class, bundle, "}");
  Option<String> RECIPIENT_EMAIL = new Option<>("RECIPIENT_EMAIL", String.class, bundle, "E-Mail");

  @SuppressWarnings("unchecked")
  OptionGroup<String> PARSING = new OptionGroup<String>("PARSING", bundle, TOKEN_START, TOKEN_END, RECIPIENT_EMAIL);

}
