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
public interface MailServerOptions extends KeyProvider {

  /** Localization support. */
  static final ResourceBundle bundle = ResourceManager.getBundle(MailServerOptions.class.getPackageName() + ".Messages");


  Option<String>  EMAIL_ADDR  = new Option<>("EMAIL_ADDR",  String.class, bundle, "");
  Option<String>  USER_NAME   = new Option<>("USER_NAME",   String.class, bundle, "");
  Option<String>  SMTP_SERVER = new Option<>("SMTP_SERVER", String.class, bundle, "");
  Option<String>  PASSWD      = new Option<>("PASSWD",      String.class, bundle, "");
  Option<Integer> PORT        = new Option<>("PORT",        Integer.class, bundle, 465);
  Option<Boolean> AUTHENTIFICATION = new Option<>("AUTHENTIFICATION", Boolean.class, bundle, true);
  Option<Boolean> START_TLS   = new Option<>("START_TLS", Boolean.class, bundle, true);

  @SuppressWarnings("unchecked")
  OptionGroup<Object> SERVER_CONFIG    = new OptionGroup<Object>("SERVER_CONFIG", bundle,
      EMAIL_ADDR,
      USER_NAME,
      SMTP_SERVER,
      PASSWD,
      PORT,
      AUTHENTIFICATION,
      START_TLS);

}
