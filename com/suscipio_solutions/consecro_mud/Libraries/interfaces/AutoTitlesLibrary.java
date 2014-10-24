package com.suscipio_solutions.consecro_mud.Libraries.interfaces;
import java.util.Enumeration;

import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;

/**
 * The library for managing the various auto-titles, which are player
 * titles that can, according to a mask, be automatically added and
 * removed from players as they meet, and stop meeting, various
 * criterium.
 * @see com.suscipio_solutions.consecro_mud.Libraries.interfaces.AutoTitlesLibrary#reloadAutoTitles()
 */
public interface AutoTitlesLibrary extends CMLibrary
{

	/**
	 * Returns an enumerator of the auto-title strings themselves.
	 * The strings will substitute a * character for the players
	 * name when building the final title.
	 * @return an enumerator of the auto-title strings themselves
	 */
	public Enumeration<String> autoTitles();

	/**
	 * Returns the string mask attributed to a particular
	 * title string.  The mask is as described by the masking
	 * library.
	 * @see com.suscipio_solutions.consecro_mud.Libraries.interfaces.MaskingLibrary
	 * @param title the title itself
	 * @return the zapper mask to determine who should get this title
	 */
	public String getAutoTitleMask(String title);

	/**
	 * Returns whether the given string matches one of the defined
	 * player titles.
	 * @param title the strong to match
	 * @return true if a title of that string exists, false otherwise
	 */
	public boolean isExistingAutoTitle(String title);

	/**
	 * Scans an admin-given auto-title definition string to see
	 * if it is properly formatted for adding to the list of
	 * auto-titles.
	 * @param row the admin-entered command string
	 * @param addIfPossible true to add it to the list, false to scan-only
	 * @return true if the title meets the criterium, false if it is rejected
	 */
	public String evaluateAutoTitle(String row, boolean addIfPossible);

	/**
	 * Scans all existing titles to see if any should be added to the
	 * given mob.  If any match, the title is added to the mobs list
	 * of choices, after being customized.
	 * @param mob the mob to check for new titles for
	 * @return true if any titles were added, false otherwise
	 */
	public boolean evaluateAutoTitles(MOB mob);

	/**
	 * Forces this library to re-load its list of titles from
	 * the resource file titles.txt.
	 */
	public void reloadAutoTitles();

	/**
	 * Scans all users in the database to ensure that the given
	 * title still belongs with them.  If it doesn't, the title
	 * is removed.
	 * @param title the title to scan users qualifications for
	 */
	public void dispossesTitle(String title);
}
