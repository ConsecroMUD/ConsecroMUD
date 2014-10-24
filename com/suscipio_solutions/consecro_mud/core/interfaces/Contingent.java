package com.suscipio_solutions.consecro_mud.core.interfaces;

import com.suscipio_solutions.consecro_mud.Behaviors.interfaces.Behavior;

/**
 * One step above a basic CMObject is an object that is contingent on its
 * environment.  It can be created and destroyed, savable, or not.
 * @see com.suscipio_solutions.consecro_mud.core.CMClass
 */
public interface Contingent extends CMObject
{
	/**
	 * Utterly and permanently destroy this object, not only removing it from the map, but
	 * causing this object to be collected as garbage by Java.  Containers, rooms. and mobs who have
	 * their destroy() method called will also call the destroy() methods on all items and other
	 * objects listed as content, recursively.
	 */
	public void destroy();
	/**
	 * Whether, if this object is in a room, whether it is appropriate to save this object to
	 * the database as a permanent feature of its container.  It always returns true except
	 * under unique circumstances.
	 * @return true, usually.
	 */
	public boolean isSavable();
	/**
	 * Whether the destroy() method has been previousy called on this object.
	 * @return whether the object is destroy()ed.
	 */
	public boolean amDestroyed();
	/**
	 * Sets whether this behavior can be saved as a permanent aspect of
	 * its host.
	 * @see Behavior#isSavable()
	 * @param truefalse whether this behavior can be saved as part of its host.
	 */
	public void setSavable(boolean truefalse);
}
