/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.nmichael.efa;

import javax.swing.*;
import java.awt.*;
import java.util.*;

/**
 *
 * @author nick
 */
public class Mnemonics {

  // Mnemonic Cache
  private static MnemonicCache mnemonicCache = new MnemonicCache();

  private static boolean isMnemonicFree(Window w, char c) {
      return mnemonicCache.get(w, c) == null;
  }

  private static boolean isMnemonicFreeOrCleared(Window w, char c) {
      MnemonicHolder mh = mnemonicCache.get(w, c);
      return (mh == null || mh.clearMnemonics());
  }

  private static void reserveMnemonic(Window w, char c, AbstractButton b, JLabel l, boolean explicit) {
      mnemonicCache.put(w, c, b, l, explicit);
  }

  /**
   * Clears the mnemonic cache for a window w.
   * This method *must* be called whenever a window is closed to avoid memory leaks!
   * @param w window to be released from cache with all its associated mnemonic holders
   */
  public static void clearCache(Window w) {
      mnemonicCache.clear(w);
  }

  private static boolean allowedMnemonic(char c) {
      // @todo: which characters are allowed to contain mnemonics?
      // e.g. Character.isLetter('�')==true, but '�' cannot be a mnemonic!
      // probably use KeyCode instead of char for mnemonics (preferred way)
      return (c != '&' &&
//              (Character.isLetter(c) || Character.isDigit(c)) );
              ( (c >= '0' && c <= '9') || (c >= 'a' && c <= 'z') ) );
  }

  private static char getMnemonic(Window w, String s, AbstractButton b, JLabel l) {
      if (w == null || s == null) return 0x0;

      s = s.toLowerCase();

      // explicit Mnemonic?
      char m = getMnemonic(s);
      if (m != 0x0) {
          if (isMnemonicFreeOrCleared(w,m)) {
              reserveMnemonic(w, m, b, l, true);
              return m;
          }
      }

      // generic Mnemonic?
      for (int i=0; i<s.length(); i++) {
          m = s.charAt(i);
          if (allowedMnemonic(m) && isMnemonicFree(w,m)) {
              reserveMnemonic(w, m, b, l, false);
              return m;
          }
      }

      return 0x0;
  }

  /**
   * Sets the displayed text and mnemonic for a label.
   * If s contains a mnemonic marked with "&", this mnemonic is being preferred
   * and may be grabbed from another component that already has this mnemonic.
   * Otherwise, this method determines a unique mnemonic for this label inside this
   * frame by itself, if any unique mnemonics are available.
   *
   * @param w the Window containing this label
   * @param l the label
   * @param s the text to be displayed
   */
  public static void setLabel(Window w, JLabel l, String s) {
      if (w == null || l == null || s == null) return;
      l.setText(stripMnemonics(s));
      char key = getMnemonic(w, s, null, l);
      if (key != 0x0) {
          l.setDisplayedMnemonic(key);
      }
  }

  /**
   * Sets the displayed text and mnemonic for a button.
   * If s contains a mnemonic marked with "&", this mnemonic is being preferred
   * and may be grabbed from another component that already has this mnemonic.
   * Otherwise, this method determines a unique mnemonic for this button inside this
   * frame by itself, if any unique mnemonics are available.
   *
   * @param w the Window containing this button
   * @param b this button
   * @param s the text to be displayed
   */
  public static void setButton(Window w, AbstractButton b, String s) {
      if (w == null || b == null || s == null) return;
      b.setText(stripMnemonics(s));
      char key = getMnemonic(w, s, b, null);
      if (key != 0x0) {
          b.setMnemonic(key);
      }
  }

  /**
   * Sets the displayed text for a menu button.
   * This method currently does *not* set mnemonics and is intended for "inner" menu buttons (within a menu)
   * that are not supposed to get global mnemonics (global as related to their frame). This implementation
   * currently does not support mnemonics local to a specific menu.
   *
   * @param w the Window containing this menu button
   * @param b this button
   * @param s the text to be displayed
   */
  public static void setMenuButton(Window w, AbstractButton b, String s) {
      if (w == null || b == null || s == null) return;
      b.setText(stripMnemonics(s));
  }

  /**
   * Checks whether the supplied string contains mnemonics (masked as "&").
   * @param s the string
   * @return true, if the string contains mnemonics
   */
  public static boolean containsMnemonics(String s) {
      return (getMnemonic(s) != 0x0);
  }

  /**
   * Checks whether the supplied string contains mnemonics (masked as "&") or
   * ampassants (masked as "&&").
   * @param s the string
   * @return true, if the string contains mnemonics or ampassants
   */
  public static boolean containsMnemonicsOrAmp(String s) {
      return (s.indexOf("&") >= 0);
  }

  /**
   * Retrieves the mnemonic masked with "&" from the supplied string.
   * @param s the string
   * @return the mnemonic
   */
  public static char getMnemonic(String s) {
      if (s == null) {
          return 0x0;
      }
      s = s.toLowerCase();
      int pos = 0;
      do {
          pos = s.indexOf("&",pos);
          if (pos < 0) {
              break;
          }
          if (s.length()>pos+1 && allowedMnemonic(s.charAt(pos+1))) {
              return s.charAt(pos+1);
          } else {
              pos += 2;
          }
      } while (pos < s.length());
      return 0x0;
  }

  /**
   * Strinps any mnemonics from the supplied string and transformes escaped
   * ampassants ("&&") into real ampassants ("&").
   * @param s the string
   * @return the string with stripped mnemonics
   */
  public static String stripMnemonics(String s) {
      // @todo: That's a hack! Maybe use a regex to replace?
      s = EfaUtil.replace(s, "&&", "�$$�", true);
      s = EfaUtil.replace(s, "&", "", true);
      s = EfaUtil.replace(s, "�$$�", "&", true);
      return s;
  }


}
