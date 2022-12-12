/**
 *
 */
package org.draeger.massmailtool;

import static java.text.MessageFormat.format;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridBagLayout;
import java.beans.PropertyChangeEvent;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.UIManager;

import de.zbit.AppConf;
import de.zbit.gui.BaseFrame;
import de.zbit.gui.GUIOptions;
import de.zbit.gui.GUITools;
import de.zbit.gui.csv.CSVImporterV2;
import de.zbit.gui.layout.LayoutHelper;
import de.zbit.io.csv.CSVReader;
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

  /**
   * @param appConf
   */
  public MassMailToolUI(AppConf appConf) {
    super(appConf);
    // TODO Auto-generated constructor stub
  }


  @Override
  public boolean closeFile() {
    // TODO Auto-generated method stub
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
    split = new JSplitPane();
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
    button.setEnabled(false);
    south.add(button);
    p.add(south, BorderLayout.SOUTH);
    split.setRightComponent(p);
    split.setDividerLocation(0);
    return split;
  }


  @Override
  protected File[] openFile(File... files) {
    // TODO: Restrict to one file!
    GeneralFileFilter filterCSV = SBFileFilter.createCSVFileFilter();
    if ((files == null) || (files.length == 0)) {
      SBPreferences prefs = SBPreferences.getPreferencesFor(GUIOptions.class);
      files = GUITools.openFileDialog(this, prefs.get(GUIOptions.OPEN_DIR), false, true, JFileChooser.FILES_AND_DIRECTORIES, filterCSV);
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
        // TODO: Do in Background
        CSVReader io = CSVImporterV2.showDialog(this, files[0].getAbsolutePath(), "Titel");
        io.getData();
        System.out.println(Arrays.toString(io.getHeader()));
        // TODO: Add to main component!
      } catch (IOException exc) {
        exc.printStackTrace();
      }
    }
    return files;
  }


  @Override
  public File saveFileAs() {
    // TODO Auto-generated method stub
    return null;
  }

}
