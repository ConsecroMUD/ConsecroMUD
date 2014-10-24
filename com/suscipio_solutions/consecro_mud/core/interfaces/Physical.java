package com.suscipio_solutions.consecro_mud.core.interfaces;

import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;


/**
*
* Something that exists in the physical world and can be affected by the
* world
* @see com.suscipio_solutions.consecro_mud.core.interfaces.Environmental
*/
public interface Physical extends Environmental, Affectable
{
	/**
	 * Returns the displayText, but as seen by the given viewer.
	 * Can differ from displayText() without being saved to the DB.
	 * Display Texts are normally the way something appears in a
	 * room, or is the roomTitle of rooms.
	 * @see Environmental#displayText()
	 * @param viewerMob the mob viewing the physical thing
	 * @return the displayText as seen by the viewer
	 */
	public String displayText(MOB viewerMob);

	/**
	 * Returns the name, but as seen by the given viewer.
	 * Can differ from name() without being saved to the DB.
	 * @see Environmental#name()
	 * @param viewerMob the mob viewing the physical thing
	 * @return the name as seen by the viewer
	 */
	public String name(MOB viewerMob);
	/**
	 * Returns the description, but as seen by the given viewer.
	 * Can differ from description() without being saved to the DB.
	 * Descriptions are normally the way something appears when
	 * looked at, or is the long description of rooms.
	 * @see Environmental#description()
	 * @param viewerMob the mob viewing the physical thing
	 * @return the description as seen by the viewer
	 */
	public String description(MOB viewerMob);
}
