package com.suscipio_solutions.consecro_mud.core.interfaces;



/**
*
* Something that can potentially be read, because it may or may not
* have writing on it.
*
* @see com.suscipio_solutions.consecro_mud.core.interfaces.Physical

*
*/
public interface Readable extends Physical
{

	/**
	 * For things that are readable, this returns the readable string
	 * for this thing.  That is to say, what the player sees when they
	 * read the door.
	 * @see com.suscipio_solutions.consecro_mud.core.interfaces.Readable#isReadable()
	 * @see com.suscipio_solutions.consecro_mud.core.interfaces.Readable#setReadable(boolean)
	 * @see com.suscipio_solutions.consecro_mud.core.interfaces.Readable#setReadableText(String)
	 * @return the readable string
	 */
	public String readableText();

	/**
	 * Returns whether this thing is readable when the player uses the READ command
	 * and targets it.
	 * @see com.suscipio_solutions.consecro_mud.core.interfaces.Readable#readableText()
	 * @see com.suscipio_solutions.consecro_mud.core.interfaces.Readable#setReadable(boolean)
	 * @see com.suscipio_solutions.consecro_mud.core.interfaces.Readable#setReadableText(String)
	 * @return true if the thing is readable.
	 */
	public boolean isReadable();

	/**
	 * Returns whether this thing is readable when the player uses the READ command
	 * and targets it.  Readable text should also be set or unset.
	 * @see com.suscipio_solutions.consecro_mud.core.interfaces.Readable#readableText()
	 * @see com.suscipio_solutions.consecro_mud.core.interfaces.Readable#isReadable()
	 * @see com.suscipio_solutions.consecro_mud.core.interfaces.Readable#setReadableText(String)
	 * @param isTrue true if the thing is readable, and false otherwise
	 */
	public void setReadable(boolean isTrue);

	/**
	 * For things that are readable, this set the readable string
	 * for this thing.  That is to say, what the player sees when they
	 * read the door.
	 * @see com.suscipio_solutions.consecro_mud.core.interfaces.Readable#isReadable()
	 * @see com.suscipio_solutions.consecro_mud.core.interfaces.Readable#setReadable(boolean)
	 * @see com.suscipio_solutions.consecro_mud.core.interfaces.Readable#readableText()
	 * @param text the readable text
	 */
	public void setReadableText(String text);

}
