package com.suscipio_solutions.consecro_mud.core.interfaces;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CharState;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CharStats;
import com.suscipio_solutions.consecro_mud.Common.interfaces.PhyStats;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;


/**
 * An interface for classes and objects which may affect mobs, rooms, items, and other Environmental types
 * By altering their stats and state objects using the layer system.
 
 *
 */
public interface StatsAffecting
{
	/**
	 * This method is called by the recoverPhyStats() method on other Environmental objects.  It is used
	 * to transform the Environmental basePhyStats() object into a finished phyStats() object,  both of
	 * which are objects implementing the PhyStats interface.  See those methods for more information.
	 * @see com.suscipio_solutions.consecro_mud.Common.interfaces.PhyStats
	 * @see Environmental
	 * @see com.suscipio_solutions.consecro_mud.core.interfaces.Affectable#basePhyStats()
	 * @see com.suscipio_solutions.consecro_mud.core.interfaces.Affectable#phyStats()
	 * @see com.suscipio_solutions.consecro_mud.core.interfaces.Affectable#recoverPhyStats()
	 * @param affected the host of the PhyStats object being affected
	 * @param affectableStats the particular PhyStats object being affected
	 */
	public void affectPhyStats(Physical affected, PhyStats affectableStats);
	/**
	 * This method is called by the recoverCharStats() method on other MOB objects.  It is used
	 * to transform the MOB baseCharStats() object into a finished charStats() object,  both of
	 * which are objects implementing the CharStats interface.  See those methods for more information.
	 * @see com.suscipio_solutions.consecro_mud.Common.interfaces.CharStats
	 * @see com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB
	 * @see com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB#baseCharStats()
	 * @see com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB#charStats()
	 * @see com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB#recoverCharStats()
	 * @param affectedMob the host of the CharStats object being affected
	 * @param affectableStats the particular CharStats object being affected
	 */
	public void affectCharStats(MOB affectedMob, CharStats affectableStats);
	/**
	 * This method is called by the recoverCharState() method on other MOB objects.  It is used
	 * to transform the MOB baseCharState() object into a finished charState() object,  both of
	 * which are objects implementing the CharState interface.  See those methods for more information.
	 * @see com.suscipio_solutions.consecro_mud.Common.interfaces.CharState
	 * @see com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB
	 * @see com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB#baseState()
	 * @see com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB#curState()
	 * @see com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB#recoverMaxState()
	 * @param affectedMob the host of the CharState object being affected
	 * @param affectableMaxState the particular CharState object being affected
	 */
	public void affectCharState(MOB affectedMob, CharState affectableMaxState);
}
