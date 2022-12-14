package org.draeger.massmailtool;

import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.util.ResourceBundle;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.MenuSelectionManager;
import javax.swing.SwingUtilities;
import javax.swing.text.JTextComponent;

import de.zbit.gui.GUITools;
import de.zbit.util.ResourceManager;

/**
 * Inspired by <a href=
 * "https://www.roseindia.net/java/example/java/swing/copy-data-from-ms.shtml">www.roseindia.net/java/example/java/swing/copy-data-from-ms.shtml</a>
 *
 * @author Andreas Dr&auml;ger
 */
public class TCPopupEventQueue extends EventQueue {
  public JPopupMenu popup;
  JTable table;
  public BasicAction cut, copy, paste, selectAll;
  /**
   * Identifies the correct command key for keystroke combinations depending on
   * the operating system and stores it in a variable for repeated use.
   */
  private static final transient int ctr_down = GUITools.isMacOSX() ? InputEvent.META_DOWN_MASK : InputEvent.CTRL_DOWN_MASK;
  /**
   * Localization support.
   */
  private static final transient ResourceBundle bundle = ResourceManager.getBundle(TCPopupEventQueue.class.getPackageName() + ".Messages");

  public TCPopupEventQueue() {
    //createPopupMenu();
  }

  public void createPopupMenu(JTextComponent text){
    cut = new CutAction(bundle.getString("CUT"), null);
    copy = new CopyAction(bundle.getString("COPY"), null);
    paste = new PasteAction(bundle.getString("PASTE"), null);
    selectAll = new SelectAllAction(bundle.getString("SELECT_ALL"), null);
    cut.setTextComponent(text);
    copy.setTextComponent(text);
    paste.setTextComponent(text);
    selectAll.setTextComponent(text);

    popup = new JPopupMenu();
    popup.add(cut);
    popup.add(copy);
    popup.add(paste);
    popup.addSeparator();
    popup.add(selectAll);
  }

  public void showPopup(Component parent, MouseEvent me){
    popup.validate();
    popup.show(parent, me.getX(), me.getY());
  }

  @Override
  protected void dispatchEvent(AWTEvent event){
    super.dispatchEvent(event);
    if(!(event instanceof MouseEvent)){
      return;
    }
    MouseEvent me = (MouseEvent)event;
    if(!me.isPopupTrigger()) {
      return;
    }
    if(!(me.getSource() instanceof Component) ) {
      return;
    }
    Component comp = SwingUtilities.getDeepestComponentAt((Component)
      me.getSource(),me.getX(), me.getY());
    if(!(comp instanceof JTextComponent)){
      return;
    }
    if(MenuSelectionManager.defaultManager().getSelectedPath().length > 0){
      return;
    }
    createPopupMenu((JTextComponent)comp);
    showPopup((Component)me.getSource(), me);
  }
  public abstract class BasicAction extends AbstractAction{
    /** Generated serial version identifier */
    private static final long serialVersionUID = 4556495321520769079L;
    JTextComponent comp;

    public BasicAction(String text, Icon icon) {
      super(text, icon);
      putValue(Action.SHORT_DESCRIPTION, text);
    }
    public void setTextComponent(JTextComponent comp){
      this.comp = comp;
    }
    @Override
    public abstract void actionPerformed(ActionEvent e);
  }
  public class CutAction extends BasicAction {
    /** Generated serial version identifier */
    private static final long serialVersionUID = 1551010266567211894L;
    public CutAction(String text, Icon icon) {
      super(text, icon);
      putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke('X', ctr_down));
    }
    @Override
    public void actionPerformed(ActionEvent e){
      comp.cut();
    }
    @Override
    public boolean isEnabled(){
      return (comp != null) && comp.isEditable() && (comp.getSelectedText() != null);
    }
  }
  public class CopyAction extends BasicAction{
    /** Generated serial version identifier */
    private static final long serialVersionUID = -6142577435494930223L;
    public CopyAction(String text, Icon icon){
      super(text,icon);
      putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke('C', ctr_down));
    }
    @Override
    public void actionPerformed(ActionEvent e) {
      comp.copy();
    }
    @Override
    public boolean isEnabled() {
      return (comp != null) && (comp.getSelectedText() != null);
    }
  }
  public class PasteAction extends BasicAction{
    /** Generated serial version identifier */
    private static final long serialVersionUID = -5618127499955619120L;
    public PasteAction(String text, Icon icon){
      super(text,icon);
      putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke('V', ctr_down));
    }
    @Override
    public void actionPerformed(ActionEvent e) {
      comp.paste();
    }
    @Override
    public boolean isEnabled() {
      Transferable content = Toolkit.getDefaultToolkit().getSystemClipboard()
          .getContents(null);
      return (comp != null) && comp.isEnabled() && comp.isEditable()
          && content.isDataFlavorSupported(DataFlavor.stringFlavor);
    }
  }

  public class SelectAllAction extends BasicAction{
    /** Generated serial version identifier */
    private static final long serialVersionUID = 936000261101334948L;
    public SelectAllAction(String text, Icon icon){
      super(text,icon);
      putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke('A', ctr_down));
    }
    @Override
    public void actionPerformed(ActionEvent e){
      comp.selectAll();
    }
    @Override
    public boolean isEnabled() {
      return (comp != null) && comp.isEnabled() && (comp.getText().length() > 0)
          && ((comp.getSelectedText() == null) ||
              (comp.getSelectedText().length() < comp.getText().length()));
    }
  }

}