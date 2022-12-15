/**
 *
 */
package org.draeger.massmailtool;

import static java.text.MessageFormat.format;

import java.util.Properties;
import java.util.ResourceBundle;
import java.util.logging.Logger;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import de.zbit.util.ResourceManager;

/**
 * Inspired by <a href=
 * "https://www.geeksforgeeks.org/send-email-using-java-program/">www.geeksforgeeks.org/send-email-using-java-program</a>
 *
 * @author Andreas Dr&auml;ger
 */
public class EMailSender {

  /** A logger for this class */
  private static final transient Logger logger = Logger.getLogger(EMailSender.class.getName());

  /** Localization support. */
  private static final transient ResourceBundle bundle = ResourceManager.getBundle(EMailSender.class.getPackageName() + ".Messages");

  /**
   * The email session
   */
  private Session session;

  /**
   * Setting up mail server.
   *
   * @param host The email server
   * @param port
   * @param auth
   * @param startTLS
   * @param user
   * @param passwd
   */
  public EMailSender(String host, int port, boolean auth, boolean startTLS,
    String user, String passwd) {

    logger.info(format(bundle.getString("CONNECTING_TO_SERVER"), host));
    Properties p = System.getProperties();
    p.put("mail.smtp.host", host);
    p.put("mail.smtp.port", Integer.toString(port));
    p.put("mail.smtp.auth", Boolean.toString(auth));
    p.put("mail.smtp.socketFactory.port", Integer.toString(port));
    p.put("mail.smtp.starttls.enable", Boolean.toString(startTLS));

    if (startTLS) {
      p.put("mail.smtp.starttls.required", "true");
    }

    // mail user name and password
    p.setProperty("mail.user", user);
    p.setProperty("mail.password", passwd);
    // SSL Factory
    if (auth) {
      p.put("mail.smtp.ssl.protocols", "TLSv1.2");
      p.put("mail.smtp.socketFactory.class",
        javax.net.ssl.SSLSocketFactory.class.getName());
      p.put("mail.smtp.socketFactory.fallback", "false");

      // creating session object to get properties
      session = Session.getDefaultInstance(p, new javax.mail.Authenticator() {
        // override the getPasswordAuthentication method
        @Override
        protected PasswordAuthentication getPasswordAuthentication() {
          return new PasswordAuthentication(user, passwd);
        }
      });
    } else {
      session = Session.getDefaultInstance(p);
    }
  }

  /**
   * Sends the given message from the given address to the given recipient.
   *
   * @param recipient email ID of recipient
   * @param sender email ID of the sender
   * @param sbj
   * @param mssg
   * @throws MessagingException
   * @throws AddressException
   */
  public void send(String recipient, String sender, String sbj, String mssg) throws AddressException, MessagingException {
    // MimeMessage object.
    MimeMessage message = new MimeMessage(session);

    // Set From Field: adding senders email to from field.
    message.setFrom(new InternetAddress(sender));

    // Set To Field: adding recipient's email to from field.
    message.addRecipient(Message.RecipientType.TO, new InternetAddress(recipient));

    // Set Subject: subject of the email
    message.setSubject(sbj);

    // set body of the email.
    message.setText(mssg);

    // Send email.
    Transport.send(message);
    logger.info(format(bundle.getString("MESSAGE_SENT"), recipient));
  }

}
