package com.suscipio_solutions.consecro_mud.core.interfaces;

import java.util.Enumeration;

import com.suscipio_solutions.consecro_mud.Behaviors.interfaces.Behavior;
import com.suscipio_solutions.consecro_mud.Common.interfaces.ScriptingEngine;


/**
*
* Something that can behave -- means almost everything!
*/
public interface Behavable
{
	/**
	 * Add a new behavior to this object.  After calling this method,
	 * recoverPhyStats() should be called next in case this behavior object modifies the stats.
	 * A Behavior with a given ID() can only be added once per object.
	 * @see com.suscipio_solutions.consecro_mud.Behaviors.interfaces.Behavior
	 * @see com.suscipio_solutions.consecro_mud.core.interfaces.Affectable#recoverPhyStats()
	 * @param to The behavior object to add.
	 */
	public void addBehavior(Behavior to);

	/**
	 * Delete a behavior from this object.  After calling this method,
	 * recoverPhyStats() should be called next in case this behavior object modified the stats.
	 * @see com.suscipio_solutions.consecro_mud.Behaviors.interfaces.Behavior
	 * @see com.suscipio_solutions.consecro_mud.core.interfaces.Affectable#recoverPhyStats()
	 * @param to The behavior object to remove.
	 */
	public void delBehavior(Behavior to);

	/**
	 * The number of behaviors this object has.
	 * @see com.suscipio_solutions.consecro_mud.Behaviors.interfaces.Behavior
	 * @return the number of behaviors
	 */
	public int numBehaviors();

	/**
	 * Returns a behavior object on this object. May return null even if the index
	 * is correct to mark a race condition.
	 * @see com.suscipio_solutions.consecro_mud.Behaviors.interfaces.Behavior
	 * @see com.suscipio_solutions.consecro_mud.core.interfaces.PhysicalAgent#numBehaviors()
	 * @param index which object to return
	 * @return the behavior object
	 */
	public Behavior fetchBehavior(int index);

	/**
	 * Returns a behavior object listed on this object. The object will
	 * be the one with the same ID() string as passed in.
	 * @see com.suscipio_solutions.consecro_mud.Behaviors.interfaces.Behavior
	 * @see CMObject#ID()
	 * @return the behavior object
	 */
	public Behavior fetchBehavior(String ID);

	/**
	 * Returns an enumerator of all the behaviors on this object.
	 * @see com.suscipio_solutions.consecro_mud.Behaviors.interfaces.Behavior
	 * @return an enumerator of all the behaviors on this object.
	 */
	public Enumeration<Behavior> behaviors();

	/**
	 * Applies the given code to each behavior on this object
	 * @param applier code to execute against each object
	 */
	public void eachBehavior(final EachApplicable<Behavior> applier);

	/**
	 * Add a new runnable script to this object.  Objects which are
	 * not mobs or areas will gain a temporary tick service for
	 * this script.
	 * @see com.suscipio_solutions.consecro_mud.Common.interfaces.ScriptingEngine
	 * @param s the scripting engine, fully populated, to add
	 */
	public void addScript(ScriptingEngine s);

	/**
	 * Removes all behaviors from this object.
	 */
	public void delAllBehaviors();

	/**
	 * Remove a running script from this object.
	 * @see com.suscipio_solutions.consecro_mud.Common.interfaces.ScriptingEngine
	 * @param s the specific scripting engine to remove
	 */
	public void delScript(ScriptingEngine s);

	/**
	 * Removes all executing scripts from this object.
	 */
	public void delAllScripts();

	/**
	 * Return the number of scripts running on this object
	 * @return number of scripts
	 */
	public int numScripts();

	/**
	 * Retreive one of the enumerated scripts running on this
	 * object
	 * @see com.suscipio_solutions.consecro_mud.Common.interfaces.ScriptingEngine
	 * @param x which script to return
	 * @return the scripting engine
	 */
	public ScriptingEngine fetchScript(int x);

	/**
	 * Returns an enumerator of all the scripts on this object.
	 * @see com.suscipio_solutions.consecro_mud.Common.interfaces.ScriptingEngine
	 * @return an enumerator of all the scripts on this object.
	 */
	public Enumeration<ScriptingEngine> scripts();

	/**
	 * Applies the given code to each scripting engine on this object
	 * @param applier code to execute against each object
	 */
	public void eachScript(final EachApplicable<ScriptingEngine> applier);
}
