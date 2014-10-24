package com.suscipio_solutions.consecro_mud.Abilities.interfaces;


/**
 * HealthCondition is an ability interface to denote those properties,
 * affects, or properties that mean something is wrong with you health-wise.
 */
public interface HealthCondition extends Ability
{

	/**
	 * Returns a basic description of the health problem, as it would be
	 * observed by others.
	 * @return a description, in text
	 */
	public String getHealthConditionDesc();
}
