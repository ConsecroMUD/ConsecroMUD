package com.suscipio_solutions.consecro_mud.core.interfaces;


/**
 * An interface for objects capable of modifying economic conditions
 */
public interface Economics extends Environmental
{
	/**
	 * A string describing how pricing for this ShopKeeper will differ based on customer attributes
	 * such as race.
	 * @return the string describing price prejudicing
	 */
	public String prejudiceFactors();
	/**
	 * A string describing how pricing for this ShopKeeper will differ based on customer attributes
	 * such as race.
	 * @return the string describing price prejudicing
	 */
	public String finalPrejudiceFactors();
	/**
	 * Sets the string describing how pricing for this ShopKeeper will differ based on customer attributes
	 * such as race.
	 * @param factors the string describing price prejudicing
	 */
	public void setPrejudiceFactors(String factors);
	/**
	 * A string set describing how pricing for this ShopKeeper will differ based on item masks
	 * The format for each string is a floating point number followers by a space and a zapper mask
	 * @return an array of the strings describing price adjustments
	 */
	public String[] finalItemPricingAdjustments();
	/**
	 * A string set describing how pricing for this ShopKeeper will differ based on item masks
	 * The format for each string is a floating point number followers by a space and a zapper mask
	 * @return an array of the strings describing price adjustments
	 */
	public String[] itemPricingAdjustments();
	/**
	 * Sets the string set describing how pricing for this ShopKeeper will differ based on item masks
	 * The format for each string is a floating point number followers by a space and a zapper mask
	 * @param factors the string describing price prejudicing
	 */
	public void setItemPricingAdjustments(String[] factors);
	/**
	 * Returns the mask used to determine if a customer is ignored by the ShopKeeper.
	 * @see com.suscipio_solutions.consecro_mud.Libraries.interfaces.MaskingLibrary
	 * @return the mask used
	 */
	public String finalIgnoreMask();
	/**
	 * Returns the mask used to determine if a customer is ignored by the ShopKeeper.
	 * @see com.suscipio_solutions.consecro_mud.Libraries.interfaces.MaskingLibrary
	 * @return the mask used
	 */
	public String ignoreMask();
	/**
	 * Sets the mask used to determine if a customer is ignored by the ShopKeeper.
	 * @see com.suscipio_solutions.consecro_mud.Libraries.interfaces.MaskingLibrary
	 * @param factors the mask to use
	 */
	public void setIgnoreMask(String factors);
	/**
	 * Returns a description of the buying budget of the shopkeeper.  Format is
	 * an amount of base currency followed by HOUR,WEEK,DAY,MONTH or YEAR.
	 * @return the string for the shopkeepers buying budget
	 */
	public String budget();
	/**
	 * Returns a description of the buying budget of the shopkeeper.  Format is
	 * an amount of base currency followed by HOUR,WEEK,DAY,MONTH or YEAR.
	 * @return the string for the shopkeepers buying budget
	 */
	public String finalBudget();
	/**
	 * Sets a description of the buying budget of the shopkeeper.  Format is
	 * an amount of base currency followed by HOUR,WEEK,DAY,MONTH or YEAR.
	 * @param factors the string for the shopkeepers buying budget
	 */
	public void setBudget(String factors);
	/**
	 * Returns a string describing the percentage in the drop of the price at
	 * which this ShopKeeper will buy back items based on the number already
	 * in his inventory.  The format is a number representing the percentage
	 * price drop per normal item followed by a space, followed by a number
	 * representing the percentage price drop per raw resource item. A value
	 * of "0 0" would mean no drop in price for either,  ever.
	 * @return the price dropping percentage rule for this shopkeeper
	 */
	public String finalDevalueRate();
	/**
	 * Returns a string describing the percentage in the drop of the price at
	 * which this ShopKeeper will buy back items based on the number already
	 * in his inventory.  The format is a number representing the percentage
	 * price drop per normal item followed by a space, followed by a number
	 * representing the percentage price drop per raw resource item. A value
	 * of "0 0" would mean no drop in price for either,  ever.
	 * @return the price dropping percentage rule for this shopkeeper
	 */
	public String devalueRate();
	/**
	 * Sets a string describing the percentage in the drop of the price at
	 * which this ShopKeeper will buy back items based on the number already
	 * in his inventory.  The format is a number representing the percentage
	 * price drop per normal item followed by a space, followed by a number
	 * representing the percentage price drop per raw resource item. A value
	 * of "0 0" would mean no drop in price for either,  ever.
	 * @param factors the price dropping percentage rule for this shopkeeper
	 */
	public void setDevalueRate(String factors);
	/**
	 * Returns the number of ticks between totally resetting this ShopKeepers
	 * inventory back to what it was.
	 *
	 * @return the number of ticks between total resets of inventory
	 */
	public int finalInvResetRate();
	/**
	 * Returns the number of ticks between totally resetting this ShopKeepers
	 * inventory back to what it was.
	 *
	 * @return the number of ticks between total resets of inventory
	 */
	public int invResetRate();
	/**
	 * Sets the number of ticks between totally resetting this ShopKeepers
	 * inventory back to what it was.
	 *
	 * @param ticks the number of ticks between total resets of inventory
	 */
	public void setInvResetRate(int ticks);
}
