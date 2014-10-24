package com.suscipio_solutions.consecro_mud.Abilities.interfaces;


/**
 * TriggeredAffect is an ability interface to denote those properties
 * that are typically non-removable inherent attributes of the things they affect.
 */
public interface TriggeredAffect extends Ability
{
	/** denotes a property whose affects are triggered always */
	public final static int TRIGGER_ALWAYS=1;
	/** denotes a property whose affects are triggered by entering the thing*/
	public final static int TRIGGER_ENTER=2;
	/** denotes a property whose affects are triggered by being hit by the thing*/
	public final static int TRIGGER_BEING_HIT=4;
	/** denotes a property whose affects are triggered by wearing/wielding the thing*/
	public final static int TRIGGER_WEAR_WIELD=8;
	/** denotes a property whose affects are triggered by getting the thing */
	public final static int TRIGGER_GET=16;
	/** denotes a property whose affects are triggered by using/eating/drinking the thing*/
	public final static int TRIGGER_USE=32;
	/** denotes a property whose affects are triggered by putting the thing somewhere*/
	public final static int TRIGGER_PUT=64;
	/** denotes a property whose affects are triggered by mounting the thing*/
	public final static int TRIGGER_MOUNT=128;
	/** denotes a property whose affects are triggered by putting something in or dropping the thing*/
	public final static int TRIGGER_DROP_PUTIN=64;
	/** denotes a property whose affects are triggered by hitting somethign with the thing*/
	public final static int TRIGGER_HITTING_WITH=128;


	/**
	 * This method returns a mask of TRIGGER_* constants denoting what triggers the properties
	 * @see TriggeredAffect#TRIGGER_ALWAYS
	 *
	 * @return  a mask of TRIGGER_* constants denoting what triggers the properties
	 */
	public int triggerMask();
}
