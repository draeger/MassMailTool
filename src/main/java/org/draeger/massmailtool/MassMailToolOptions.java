/**
 *
 */
package org.draeger.massmailtool;

import java.util.ResourceBundle;

import de.zbit.util.ResourceManager;
import de.zbit.util.prefs.KeyProvider;
import de.zbit.util.prefs.Option;
import de.zbit.util.prefs.OptionGroup;


/**
 * @author Andreas Dr&auml;ger
 *
 */
public interface MassMailToolOptions extends KeyProvider {

  /** Localization support. */
  static final ResourceBundle bundle = ResourceManager.getBundle(MassMailToolOptions.class.getPackageName() + ".Messages");

  Option<String> TOKEN_START = new Option<>("TOKEN_START", String.class, bundle, "[$");
  Option<String> TOKEN_END   = new Option<>("TOKEN_END",   String.class, bundle, "$]");

  Option<String> EMAIL_ADDR  = new Option<>("EMAIL_ADDR",  String.class, bundle, "");
  Option<String> USER_NAME   = new Option<>("USER_NAME",   String.class, bundle, "");
  Option<String> SMTP_SERVER = new Option<>("SMTP_SERVER", String.class, bundle, "");
  Option<String> PASSWD      = new Option<>("PASSWD",      String.class, bundle, "");

  @SuppressWarnings("unchecked")
  OptionGroup<String> SERVER_CONFIG = new OptionGroup<String>("SERVER_CONFIG", bundle, EMAIL_ADDR, USER_NAME, SMTP_SERVER, PASSWD);

  @SuppressWarnings("unchecked")
  OptionGroup<String> PARSING = new OptionGroup<String>("PARSING", bundle, TOKEN_START, TOKEN_END);

}
