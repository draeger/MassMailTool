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
import java.util.logging.Logger;

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


/**
 * @author Andreas Dr&auml;ger
 */
public class MassMailToolUI extends BaseFrame {

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
    return createDefaultToolBar();
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
    LayoutHelper.addComponent(center, l, subject, 0, 0, 1, 1, 1d, 0d);
    LayoutHelper.addComponent(center, l, message, 0, 1, 1, 1, 1d, 1d);
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
        SwingWorker<Void, Void> sendWorker = new SwingWorker<Void, Void>() {

          @Override
          protected Void doInBackground() throws Exception {
            // TODO Auto-generated method stub
            return null;
          }

        };
        logger.info(bundle.getString("STARTING_WORK"));
        setInputAreasEnabled(false);
        Map<String, Object> placeholder = new HashMap<>();
        SBPreferences pref = SBPreferences.getPreferencesFor(ParseOptions.class);
        String tokenStart = pref.get(ParseOptions.TOKEN_START);
        String tokenEnd = pref.get(ParseOptions.TOKEN_END);
        for (int i = 0; i < table.getRowCount(); i++) {
          for (int j = 0; j < table.getColumnCount(); j++) {
            placeholder.put(table.getColumnName(j), table.getValueAt(i, j));
          }
          String sbjt = subject.getText();
          String mssg = message.getText();
          StringSubstitutor sub = new StringSubstitutor(placeholder);
          sub.setVariablePrefix(tokenStart);
          sub.setVariableSuffix(tokenEnd);
          String lines[] = mssg.split("\n");
          // initial length = charcter count + linebreak count
          StringWriter writer = new StringWriter(mssg.length() + lines.length);
          for (int k = 0; k < lines.length; k++) {
            String result = lines[k];
            result = sub.replace(result).replaceAll("\\s+", " ");
            writer.append(result);
            writer.append(System.lineSeparator());
          }
          writer.flush();
          String result = writer.toString(); //sub.replace(mssg).replaceAll("\\s+", " ");
          System.out.println(result);
        }
        setInputAreasEnabled(true);
      }
    }
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
        reader.setSkipLines(0);
        CSVReader io = CSVImporterV2.showDialog(this, reader, bundle.getString("TITLE_TABLE_READER"));
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

}
