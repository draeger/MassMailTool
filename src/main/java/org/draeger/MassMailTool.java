/**
 *
 */
package org.draeger;

import java.awt.Window;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.logging.Logger;

import de.zbit.AppConf;
import de.zbit.Launcher;
import de.zbit.gui.GUIOptions;
import de.zbit.util.Utils;
import de.zbit.util.prefs.KeyProvider;


/**
 * @author Andreas Dr&auml;ger
 *
 */
public class MassMailTool extends Launcher {

  /**
   * A {@link Logger} for this class.
   */
  private static final           Logger logger           = Logger.getLogger(MassMailTool.class.getName());


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
    // TODO Auto-generated method stub
    return null;
  }


  @Override
  public List<Class<? extends KeyProvider>> getInteractiveOptions() {
    // TODO Auto-generated method stub
    return null;
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
