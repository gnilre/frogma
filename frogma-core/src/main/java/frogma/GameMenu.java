package frogma;

import java.awt.event.KeyEvent;

/**
 * <p>Title: GameMenu</p>
 * <p>Description: Interface for menus used in Frogma </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author Johannes Odland
 * @version 1.0
 */

public interface GameMenu
{
	/**
	 * Returns the menu items that is to be shown on screen
	 * @return
	 */
	public abstract String[] getMenuItemsAsStrings();
	/**
	 * returns selected menu item
	 * @return index of selected item
	 */
	public abstract int getSelectedMenuItem();
	/**
	 * Returns the menus horizontal position on screen. 0 is left,1 is right.
	 * @return
	 */
	public abstract double getPosX();
	/**
	 * Returns the menus vertical position on screen. 0 is top,1 is bottom.
	 * @return
	 */
	public abstract double getPosY();
	/**
	 * Returns Color of menu
	 * @return
	 */

	public abstract java.awt.Color getColor();
	/**
	 * Used to recive key input
	 * @param kE
	 */
	public abstract void triggerKeyEvent(KeyEvent kE);

}