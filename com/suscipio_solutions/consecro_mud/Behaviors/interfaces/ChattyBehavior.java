package com.suscipio_solutions.consecro_mud.Behaviors.interfaces;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Libraries.interfaces.MaskingLibrary;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMParms;
import com.suscipio_solutions.consecro_mud.core.CMath;


/**
 * A ChattyBehavior is a Behavior causes a mob to have a conversation,
 * or even just simply respond to a player or even another mob.
 * @see com.suscipio_solutions.consecro_mud.Behaviors.interfaces.Behavior
 */
public interface ChattyBehavior extends Behavior
{
	/**
	 * Returns the last thing actually spoken by the wielder of this
	 * behavior, or null if nothing has been said yet.
	 * @return the last thing said.
	 */
	public String getLastThingSaid();

	/**
	 * Returns the last MOB object spoken to.
	 * @return the last MOB object spoken to.
	 */
	public MOB getLastRespondedTo();

	/**
	 * A response object representing something the chatty-one will
	 * definitely be saying soon.
	 * @author bzimmerman
	 */
	@SuppressWarnings("rawtypes")
	public static class ChattyResponse
	{
		public ChattyResponse(Vector cmd, int responseDelay) { parsedCommand=cmd; delay=responseDelay;}
		public int delay;
		public Vector parsedCommand;
	}
	/**
	 * A test response is a possible response to an environmental event, such as
	 * someone speaking or acting.  It is only one possible response to one possible
	 * event, and is weighed against its neighbors for whether it is chosen.
	 * @author bzimmerman
	 */
	public static class ChattyTestResponse
	{
		public String[] responses;
		public int weight;
		public ChattyTestResponse(String resp)
		{
			weight=CMath.s_int(""+resp.charAt(0));
			responses=CMParms.parseSquiggleDelimited(resp.substring(1),true).toArray(new String[0]);
		}
	}
	/**
	 * A chatty entry embodies a test for a particular environmental event, such as
	 * someone speaking or acting, and all possible responses to that event.
	 * @author bzimmerman
	 */
	public static class ChattyEntry
	{
		public String expression;
		public ChattyTestResponse[] responses;
		public boolean combatEntry = false;
		public ChattyEntry(String expression)
		{
			if(expression.startsWith("*"))
			{
				combatEntry=true;
				expression=expression.substring(1);
			}
			this.expression=expression;
		}
	}
	/**
	 * A chatty group is a collection of particular environmental event tests, and
	 * their possible responses.  It completely embodies a particular "chat behavior"
	 * for a particular kind of chatty mob.
	 * @author bzimmerman
	 */
	public static class ChattyGroup implements Cloneable
	{
		public String[] groupNames;
		public MaskingLibrary.CompiledZapperMask[] groupMasks;
		public ChattyEntry[] entries = null;
		public ChattyGroup(String[] names, MaskingLibrary.CompiledZapperMask[] masks)
		{ groupNames=names; groupMasks=masks;}
		@Override public ChattyGroup clone(){ try{return (ChattyGroup)super.clone();}catch(final Exception e){return this;}}
	}
}
