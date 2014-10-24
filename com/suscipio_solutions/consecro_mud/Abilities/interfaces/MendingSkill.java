package com.suscipio_solutions.consecro_mud.Abilities.interfaces;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;


/**
 * This interface denotes an ability that also incidentally is capable
 * of mending objects, usually items or mobs.
 */
public interface MendingSkill extends Ability
{
	/**
	 * Returns whether this skill can mend the given thing.
	 * @return true or false, depending upon if this skill will do the trick.
	 */
	public boolean supportsMending(Physical item);
}
