/**
 *
 */
package org.draeger.massmailtool;

import java.awt.Window;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Logger;

import de.zbit.AppConf;
import de.zbit.Launcher;
import de.zbit.gui.GUIOptions;
import de.zbit.io.csv.CSVOptions;
import de.zbit.util.ResourceManager;
import de.zbit.util.Utils;
import de.zbit.util.prefs.KeyProvider;


/**
 * @author Andreas Dr&auml;ger
 *
 */
public class MassMailTool extends Launcher {

  /** Generated serial version identifier */
  private static final long serialVersionUID = -2434389735850660436L;

  /**
   * A {@link Logger} for this class.
   */
  private static final           Logger logger         = Logger.getLogger(MassMailTool.class.getName());

  /**
   * Localization support.
   */
  private static final transient ResourceBundle bundle = ResourceManager.getBundle(MassMailTool.class.getPackageName() + ".Messages");

  /**
   * @param args
   */
  public MassMailTool(String... args) {
    super(args);
  }


  @Override
  public void commandLineMode(AppConf appConf) {
    // TODO Auto-generated method stub
  }


  @Override
  public List<Class<? extends KeyProvider>> getCmdLineOptions() {
    List<Class<? extends KeyProvider>> list = getInteractiveOptions();
    list.add(CSVOptions.class);
    list.add(GUIOptions.class);
    return list;
  }


  @Override
  public List<Class<? extends KeyProvider>> getInteractiveOptions() {
    List<Class<? extends KeyProvider>> list = new ArrayList<Class<? extends KeyProvider>>(1);
    list.add(MassMailToolOptions.class);
    MassMailToolOptions.PASSWD.setSecret(true);
    list.add(ParseOptions.class);
    return list;
  }

  /* (non-Javadoc)
   * @see de.zbit.Launcher#getLogPackages()
   */
  @Override
  public String[] getLogPackages() {
    List<String> packages = new ArrayList<String>();
    packages.addAll(Arrays.asList(super.getLogPackages()));
    packages.add("org");
    return packages.toArray(new String[] {});
  }

  /* (non-Javadoc)
   * @see de.zbit.Launcher#getOrganization()
   */
  @Override
  public String getOrganization() {
    return bundle.getString("ORGANIZATION");
  }

  @Override
  public String getProvider() {
    return bundle.getString("PROVIDER");
  }


  @Override
  public URL getURLOnlineUpdate() {
    // TODO Auto-generated method stub
    return null;
  }


  @Override
  public URL getURLlicenseFile() {
    try {
      return new URL("https://github.com/draeger/MassMailTool/blob/main/LICENSE");
    } catch (MalformedURLException exc) {
      logger.warning(Utils.getMessage(exc));
      return null;
    }
  }


  @Override
  public String getVersionNumber() {
    return "0.1";
  }


  @Override
  public short getYearOfProgramRelease() {
    return (short) 2022;
  }


  @Override
  public short getYearWhenProjectWasStarted() {
    return (short) 2022;
  }


  @Override
  public Window initGUI(AppConf appConf) {
    return new MassMailToolUI(appConf);
  }

  /* (non-Javadoc)
   * @see de.zbit.Launcher#addCopyrightToSplashScreen()
   */
  @Override
  protected boolean addCopyrightToSplashScreen() {
    return false;
  }


  /* (non-Javadoc)
   * @see de.zbit.Launcher#addVersionNumberToSplashScreen()
   */
  @Override
  protected boolean addVersionNumberToSplashScreen() {
    return false;
  }

  /* (non-Javadoc)
   * @see de.zbit.Launcher#showsGUI()
   */
  @Override
  public boolean showsGUI() {
    return !props.containsKey(GUIOptions.GUI) || props.getBooleanProperty(GUIOptions.GUI);
  }

  /**
   * @param args
   */
  public static void main(String[] args) {
    new MassMailTool(args);
  }

}
