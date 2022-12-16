/**
 *
 */
package org.draeger.massmailtool;

import static java.text.MessageFormat.format;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridBagLayout;
import java.awt.Window;
import java.awt.event.ActionListener;
import java.beans.EventHandler;
import java.beans.PropertyChangeEvent;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;
import java.util.logging.Logger;
import java.util.prefs.PreferenceChangeEvent;
import java.util.prefs.PreferenceChangeListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.table.TableModel;

import org.apache.commons.text.StringSubstitutor;

import de.zbit.AppConf;
import de.zbit.gui.BaseFrame;
import de.zbit.gui.GUIOptions;
import de.zbit.gui.GUITools;
import de.zbit.gui.csv.CSVImporterV2;
import de.zbit.gui.layout.LayoutHelper;
import de.zbit.gui.prefs.PreferencesDialog;
import de.zbit.io.csv.CSVReader;
import de.zbit.io.csv.CSVWriter;
import de.zbit.io.filefilter.GeneralFileFilter;
import de.zbit.io.filefilter.SBFileFilter;
import de.zbit.util.ResourceManager;
import de.zbit.util.StringUtil;
import de.zbit.util.prefs.SBPreferences;
import de.zbit.util.progressbar.AbstractProgressBar;
import de.zbit.util.progressbar.gui.ProgressBarSwing;


/**
 * @author Andreas Dr&auml;ger
 */
public class MassMailToolUI extends BaseFrame implements PreferenceChangeListener {


  /** Generated serial version identifier */
  private static final long serialVersionUID = -4624807465999504777L;

  /** A {@link Logger} for this class. */
  private static final transient Logger logger = Logger.getLogger(MassMailToolUI.class.getName());

  /** Localization support. */
  private static final transient ResourceBundle bundle = ResourceManager.getBundle(MassMailToolUI.class.getPackageName() + ".Messages");

  static {
    // Get rid of Uni Tübingen branding
    UIManager.put("UT_WBMW_mathnat_4C_380x45", null);
  }

  private JSplitPane split;
  private JButton button;
  private JTextField subject;
  private JTextArea message;
  private JTable table;
  private EMailSender sender;

  /**
   * @param appConf
   */
  public MassMailToolUI(AppConf appConf) {
    super(appConf);
    // TODO Auto-generated constructor stub
  }


  @Override
  public boolean closeFile() {
    if (table != null) {
      // TODO: Check change
      split.setLeftComponent(new JPanel());
      GUITools.setEnabled(false, getJMenuBar(), getJToolBar(),
        BaseAction.FILE_CLOSE, BaseAction.FILE_SAVE_AS);
      button.setEnabled(false);
      table = null;
      return true;
    }
    return false;
  }


  @Override
  public URL getURLAboutMessage() {
    return getClass().getResource(bundle.getString(getClass().getPackage().getName() + ".About"));
  }


  @Override
  public URL getURLLicense() {
    return getClass().getResource(bundle.getString(getClass().getPackage().getName() + ".License"));
  }


  @Override
  public URL getURLOnlineHelp() {
    return getClass().getResource(bundle.getString(getClass().getPackage().getName() + ".Help"));
  }


  @Override
  public void propertyChange(PropertyChangeEvent evt) {
    // TODO Auto-generated method stub
  }


  @Override
  protected JToolBar createJToolBar() {
    JToolBar toolbar = createDefaultToolBar();
    // This is just a workaround because for some reason the help icon dosn't
    // get painted anymore. However, I want to avoid messing around in SysBio.
    for (int i = 0; i < toolbar.getComponentCount(); i++) {
      Component component = toolbar.getComponent(i);
      if (component instanceof JButton) {
        JButton button = (JButton) component;
        if (button.getIcon() == null) {
          button.setIcon(UIManager.getIcon("ICON_HELP_16"));
        }
      }
    }
    return toolbar;
  }


  @Override
  protected Component createMainComponent() {
    split = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
    split.setLeftComponent(new JPanel());
    split.addPropertyChangeListener(evt -> {
      GUITools.setEnabled(!split.getLeftComponent().getClass().getName().endsWith("JPanel"), getJMenuBar(),
        getJToolBar(), BaseAction.FILE_CLOSE, BaseAction.FILE_SAVE_AS);
    });
    JPanel p = new JPanel(new BorderLayout());
    GridBagLayout l = new GridBagLayout();
    JPanel center = new JPanel(l);
    subject = new JTextField();
    message = new JTextArea();
    subject.setBorder(BorderFactory.createTitledBorder(format(" {0} ", bundle.getString("SUBJECT"))));
    message.setBorder(BorderFactory.createTitledBorder(format(" {0} ", bundle.getString("MESSAGE"))));
    //JScrollPane sSbj = new JScrollPane(subject, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    JScrollPane sMsg = new JScrollPane(message, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    LayoutHelper.addComponent(center, l, subject, 0, 0, 1, 1, 1d, 0d);
    LayoutHelper.addComponent(center, l, sMsg, 0, 1, 1, 1, 1d, 1d);
    p.add(center, BorderLayout.CENTER);
    JPanel south = new JPanel();
    button = new JButton(bundle.getString("SEND"));
    button.setToolTipText(StringUtil.toHTMLToolTip(bundle.getString("SEND_TOOLTIP")));
    button.addActionListener(EventHandler.create(ActionListener.class, this, "sendMail"));
    button.setEnabled(false);
    south.add(button);
    p.add(south, BorderLayout.SOUTH);
    split.setRightComponent(p);
    split.setDividerLocation(0);
    return split;
  }

  @SuppressWarnings("unchecked")
  public void sendMail() {
    SBPreferences prefs = SBPreferences.getPreferencesFor(MailServerOptions.class);
    boolean send = true;
    if ((prefs.getString(MailServerOptions.EMAIL_ADDR).length() == 0) ||
        (prefs.getString(MailServerOptions.PASSWD).length() == 0) ||
        (prefs.getString(MailServerOptions.USER_NAME).length() == 0) ||
        (prefs.getString(MailServerOptions.SMTP_SERVER).length() == 0)) {
      send = PreferencesDialog.showPreferencesDialog(MailServerOptions.class);
    }
    if (send) {
      int forgotSomething = JOptionPane.NO_OPTION;
      if (subject.getText().trim().length() == 0) {
        forgotSomething = GUITools.showQuestionMessage(this, bundle.getString("EMPTY_SUBJECT"),
          bundle.getString("CONFIRMATION_TITLE"), JOptionPane.YES_NO_OPTION);
      } else if (message.getText().trim().length() == 0) {
        forgotSomething = GUITools.showQuestionMessage(this, bundle.getString("EMPTY_MESSAGE"),
          bundle.getString("CONFIRMATION_TITLE"), JOptionPane.YES_NO_OPTION);
      }
      if ((forgotSomething == JOptionPane.NO_OPTION)
          && (JOptionPane.YES_OPTION == GUITools.showQuestionMessage(this,
            bundle.getString("SEND_NOW"), bundle.getString("CONFIRMATION_TITLE"),
            JOptionPane.YES_NO_OPTION))) {
        logger.info(bundle.getString("STARTING_WORK"));
        setInputAreasEnabled(false);
        AbstractProgressBar memoryBar = statusBar.showProgress();
        ProgressBarSwing progressBar = (ProgressBarSwing) memoryBar;
        progressBar.getProgressBar().setStringPainted(true);
        Map<String, Object> placeholder = new HashMap<>();
        SBPreferences pref = SBPreferences.getPreferencesFor(ParseOptions.class);
        if (sender == null) {
          sender = new EMailSender(
            prefs.get(MailServerOptions.SMTP_SERVER),
            Integer.parseInt(prefs.get(MailServerOptions.PORT).toString()),
            Boolean.parseBoolean(prefs.get(MailServerOptions.AUTHENTIFICATION).toString()),
            Boolean.parseBoolean(prefs.get(MailServerOptions.START_TLS).toString()),
            prefs.get(MailServerOptions.USER_NAME),
            prefs.get(MailServerOptions.PASSWD));
        }
        Window window = this;
        GUITools.setEnabled(false, getJMenuBar(), getJToolBar(),
          BaseAction.FILE_OPEN, BaseAction.FILE_CLOSE, BaseAction.FILE_SAVE,
          BaseAction.FILE_SAVE_AS);
        TableModel model = table.getModel();
        final String EMAIL = SBPreferences.getPreferencesFor(ParseOptions.class).get(ParseOptions.RECIPIENT_EMAIL);
        SwingWorker<String[], String[]> replaceAndSendWorker = new SwingWorker<>() {

          @Override
          protected String[] doInBackground() throws Exception {
            String tokenStart = pref.get(ParseOptions.TOKEN_START);
            String tokenEnd = pref.get(ParseOptions.TOKEN_END);
            String result[] = {"", ""};
            for (int i = 0; i < model.getRowCount(); i++) {
              for (int j = 0; j < model.getColumnCount(); j++) {
                placeholder.put(model.getColumnName(j), model.getValueAt(i, j));
              }
              result[0] = replace(subject.getText(), placeholder, tokenStart, tokenEnd);
              result[1] = replace(message.getText(), placeholder, tokenStart, tokenEnd);

              String recipient = placeholder.get(EMAIL).toString();
              if ((recipient == null) || recipient.isEmpty()) {
                throw new Exception(format(bundle.getString("UNKNOWN_EMAIL_ADDR"), EMAIL));
              } else {
                logger.info(format(bundle.getString("SENDING_MESSAGE"), recipient));
                sender.send(pref.get(MailServerOptions.EMAIL_ADDR), result[0], result[1], recipient);
              }
            }
            return result;
          }

          @Override
          protected void process(List<String[]> chunks) {
            progressBar.drawProgressBar(
              ((((int) progressBar.getCallNumber()) + chunks.size()) * 100) / model.getRowCount(),
              -1, null);
            super.process(chunks);
          }

          @Override
          protected void done() {
            super.done();
            try {
              get();
              logger.info(format(bundle.getString("SENDING_COMPLETED"), table.getRowCount()));
            } catch (InterruptedException | ExecutionException exc) {
              GUITools.showErrorMessage(window, exc);
            }
            progressBar.finished();
            statusBar.hideProgress();
            setInputAreasEnabled(true);
            GUITools.setEnabled(true, window, BaseAction.FILE_OPEN, BaseAction.FILE_CLOSE, BaseAction.FILE_SAVE, BaseAction.FILE_SAVE_AS);
          }
        };
        replaceAndSendWorker.execute();
      }
    }
  }


  /**
   * @param placeholder
   * @param tokenStart
   * @param tokenEnd
   * @param mssg
   * @return A new String in which all place holders have been filled with values.
   */
  public String replace(String mssg, Map<String, Object> placeholder, String tokenStart,
    String tokenEnd) {
    StringSubstitutor sub = new StringSubstitutor(placeholder);
    sub.setVariablePrefix(tokenStart);
    sub.setVariableSuffix(tokenEnd);
    String lines[] = mssg.split(System.lineSeparator());
    // initial length = character count + line break count
    StringWriter writer = new StringWriter(mssg.length() + lines.length);
    String result;
    for (int i = 0; i < lines.length; i++) {
      result = lines[i];
      result = sub.replace(result).replaceAll("\\s+", " ");
      writer.append(result);
      writer.append(System.lineSeparator());
    }
    writer.flush();
    result = writer.toString();
    logger.fine(result);
    return result;
  }


  /**
   *
   */
  private void setInputAreasEnabled(boolean enabled) {
    table.setEnabled(enabled);
    subject.setEnabled(enabled);
    message.setEnabled(enabled);
    button.setEnabled(enabled);
  }


  @Override
  protected File[] openFile(File... files) {
    GeneralFileFilter filterCSV = SBFileFilter.createCSVFileFilter();
    if ((files == null) || (files.length == 0)) {
      SBPreferences prefs = SBPreferences.getPreferencesFor(GUIOptions.class);
      files = GUITools.openFileDialog(this, prefs.get(GUIOptions.OPEN_DIR),
        false, false, JFileChooser.FILES_ONLY, filterCSV);
    }
    // if files are provided, they are checked
    if ((files != null) && (files.length > 0)) {
      List<File> accepted = new LinkedList<File>();
      List<File> notAccepted = new LinkedList<File>();
      for (File file : files) {
        if (filterCSV.accept(file)) {
          // TODO: ignore empty files here?
          accepted.add(file);
        } else {
          notAccepted.add(file);
        }
      }
      files = accepted.toArray(new File[0]);
      try {
        CSVReader reader = new CSVReader(files[0].getAbsolutePath());
        reader.setContainsHeaders(true);
        reader.setAutoDetectContentStart(false);
        CSVReader io = CSVImporterV2.showDialog(this, reader, bundle.getString("TITLE_TABLE_READER"));
        logger.info(format(bundle.getString("READING_CSV_FILE"), files[0].getName()));
        String[] header = io.getHeader();
        String[][] data = io.getData();
        if ((data != null) && (header != null)) {
          table = new JTable(data, header);
          split.setLeftComponent(
            new JScrollPane(table,
              JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
              JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED));
          split.setDividerLocation(.25d);
          button.setEnabled(true);
        }
        SBPreferences prefs = SBPreferences.getPreferencesFor(GUIOptions.class);
        prefs.put(GUIOptions.OPEN_DIR, files[0].getParentFile());
        GUITools.setEnabled(false, getJMenuBar(), getJToolBar(), BaseAction.FILE_OPEN);
        GUITools.setEnabled(true, getJMenuBar(), getJToolBar(), BaseAction.FILE_CLOSE, BaseAction.FILE_SAVE, BaseAction.FILE_SAVE_AS);
        logger.info(format(bundle.getString("READING_CSV_FILE_SUCCESSFUL"), files[0].getName()));
      } catch (IOException exc) {
        exc.printStackTrace();
      }
    }
    return files;
  }


  @Override
  public File saveFileAs() {
    if (table != null) {
      GeneralFileFilter filterCSV = SBFileFilter.createCSVFileFilter();
      SBPreferences prefs = SBPreferences.getPreferencesFor(GUIOptions.class);
      File file = GUITools.saveFileDialog(this, prefs.get(GUIOptions.SAVE_DIR),
        false, false, JFileChooser.FILES_ONLY, filterCSV);
      if ((file != null) && filterCSV.accept(file)) {
        if (!file.exists() || (GUITools.overwriteExistingFileDialog(this, file) == JOptionPane.YES_OPTION)) {
          // TODO: Can be configurable which char to use
          CSVWriter writer = new CSVWriter(';');
          final Window w = this;
          SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {

            @Override
            protected Void doInBackground() throws Exception {
              writer.write(table, file);
              return null;
            }

            @Override
            protected void done() {
              try {
                get();
              } catch (Exception exc) {
                GUITools.showErrorMessage(w, exc);
              }
            }
          };
          worker.execute();
          return file;
        }
      }
    }
    return null;
  }


  @Override
  public void preferenceChange(PreferenceChangeEvent evt) {
    // Just make sure the connection to the email server is established upon any change.
    sender = null;
  }

}
