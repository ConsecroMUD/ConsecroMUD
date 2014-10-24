package com.suscipio_solutions.consecro_mud.core.interfaces;



/**
* A utility interface for applying "each" code to iterable objects
*/
public interface EachApplicable<T>
{
	/**
	 * Implement the code that will apply to each object
	 * @param a the object to work on
	 */
	public void apply(final T a);
	
	/**
	 * Example class that affect phyStats
	 */
	public static class ApplyAffectPhyStats<T extends StatsAffecting> implements EachApplicable<T>
	{
		protected final Physical me;
		public ApplyAffectPhyStats(Physical me)
		{
			this.me=me;
		}
		@Override
		public void apply(T a) 
		{
			a.affectPhyStats(me, me.phyStats());
		}
	}
	
	/**
	 * Example class that recovers phyStats
	 */
	public static class ApplyRecoverPhyStats<T extends Affectable> implements EachApplicable<T>
	{
		@Override
		public void apply(T a) 
		{
			a.recoverPhyStats();
		}
	}
	
}
