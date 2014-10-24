package com.suscipio_solutions.consecro_mud.Behaviors.interfaces;


/**
 * A MobileBehavior is a Behavior causes a mob to move about
 * @see com.suscipio_solutions.consecro_mud.Behaviors.interfaces.Behavior
 */
public interface MobileBehavior extends Behavior
{
	/**
	 * Suspend mobility for a specified number of ticks
	 * @param numTicks the number of ticks to suspend mobility for.
	 */
	public void suspendMobility(int numTicks);
}
