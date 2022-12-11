/**
 * 
 */
package org.draeger;

import java.awt.Component;
import java.awt.GraphicsConfiguration;
import java.awt.HeadlessException;
import java.beans.PropertyChangeEvent;
import java.io.File;
import java.net.URL;

import javax.swing.JToolBar;

import de.zbit.AppConf;
import de.zbit.gui.BaseFrame;


/**
 * @author Andreas Dr&auml;ger
 *
 */
public class MassMailToolUI extends BaseFrame {

  /**
   * @throws HeadlessException
   */
  public MassMailToolUI() throws HeadlessException {
    // TODO Auto-generated constructor stub
  }


  /**
   * @param appConf
   */
  public MassMailToolUI(AppConf appConf) {
    super(appConf);
    // TODO Auto-generated constructor stub
  }


  /**
   * @param appConf
   * @param gc
   */
  public MassMailToolUI(AppConf appConf, GraphicsConfiguration gc) {
    super(appConf, gc);
    // TODO Auto-generated constructor stub
  }


  @Override
  public boolean closeFile() {
    // TODO Auto-generated method stub
    return false;
  }


  @Override
  public URL getURLAboutMessage() {
    // TODO Auto-generated method stub
    return null;
  }


  @Override
  public URL getURLLicense() {
    // TODO Auto-generated method stub
    return null;
  }


  @Override
  public URL getURLOnlineHelp() {
    // TODO Auto-generated method stub
    return null;
  }


  @Override
  public void propertyChange(PropertyChangeEvent evt) {
    // TODO Auto-generated method stub
  }


  @Override
  protected JToolBar createJToolBar() {
    // TODO Auto-generated method stub
    return null;
  }


  @Override
  protected Component createMainComponent() {
    // TODO Auto-generated method stub
    return null;
  }


  @Override
  protected File[] openFile(File... arg0) {
    // TODO Auto-generated method stub
    return null;
  }


  @Override
  public File saveFileAs() {
    // TODO Auto-generated method stub
    return null;
  }
}
