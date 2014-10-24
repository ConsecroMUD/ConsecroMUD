package com.suscipio_solutions.consecro_mud.core.interfaces;


/**
 * A Drinkable object containing its own liquid material type, and liquid capacity management.
 */
public interface Drink extends PhysicalAgent, Decayable
{
	/**
	 * The amount of thirst points quenched every time this item is drank from.
	 * @see com.suscipio_solutions.consecro_mud.Common.interfaces.CharState
	 * @return amount of thirst quenched
	 */
	public int thirstQuenched();
	/**
	 * The total amount of liquid possible to be contained in this liquid container.
	 * @return total liquid contained herein.
	 */
	public int liquidHeld();
	/**
	 * The amount of liquid remaining in this liquid container.  Will always be less
	 * less than liquidHeld();
	 * @see Drink#liquidHeld()
	 * @return amount of liquid remaining in this liquid container.
	 */
	public int liquidRemaining();
	/**
	 * The material type of the liquid in this container.  Although a class implementing
	 * the Drink interface can sometimes be a liquid itself (like GenLiquidResource), most
	 * often, a Drink interface implementing class is a mob without a material to draw from
	 * or an Item having its own non-liquid material (like a leather waterskin containing milk).
	 * Either way, this is necessary.  The material types are constants in RawMaterial.
	 * @see com.suscipio_solutions.consecro_mud.Items.interfaces.RawMaterial
	 * @return the type of liquid contained herein
	 */
	public int liquidType();
	/**
	 * Sets the material type of the liquid in this container.  Although a class implementing
	 * the Drink interface can sometimes be a liquid itself (like GenLiquidResource), most
	 * often, a Drink interface implementing class is a mob without a material to draw from
	 * or an Item having its own non-liquid material (like a leather waterskin containing milk).
	 * Either way, this is necessary.  The material types are constants in RawMaterial.
	 * @see com.suscipio_solutions.consecro_mud.Items.interfaces.RawMaterial
	 * @param newLiquidType the type of liquid contained herein
	 */
	public void setLiquidType(int newLiquidType);
	/**
	 * Set the amount of thirst points quenched every time this item is drank from.
	 * @see com.suscipio_solutions.consecro_mud.Common.interfaces.CharState
	 * @param amount of thirst quenched
	 */
	public void setThirstQuenched(int amount);
	/**
	 * Sets the total amount of liquid possible to be contained in this liquid container.
	 * @param amount total liquid contained herein.
	 */
	public void setLiquidHeld(int amount);
	/**
	 * Sets the amount of liquid remaining in this liquid container.  Will always be less
	 * less than liquidHeld();
	 * @see Drink#setLiquidHeld(int)
	 * @param amount amount of liquid remaining in this liquid container.
	 */
	public void setLiquidRemaining(int amount);
	/**
	 * Whether this liquid container still contains any liquid.
	 * @return whether any liquid is left.
	 */
	public boolean containsDrink();
	/**
	 * Settable only internally, this method returns whether this entire object  is
	 * destroyed immediately after it is drank from  --  like a potion.
	 * @return Whether the item survives after drinking.
	 */
	public boolean disappearsAfterDrinking();

	/**
	 * Given the liquid source, the amount of liquid which would need to be taken
	 * from the source liquid source to fill up THIS liquid source.
	 * @param theSource the liquid source to fill up from
	 * @return the amount to take from the liquid source
	 */
	public int amountTakenToFillMe(Drink theSource);
}
