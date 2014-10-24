package com.suscipio_solutions.consecro_mud.Commands;
import java.util.List;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Commands.interfaces.Command;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Coins;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMProps;
import com.suscipio_solutions.consecro_mud.core.Log;
import com.suscipio_solutions.consecro_mud.core.collections.Filterer;
import com.suscipio_solutions.consecro_mud.core.interfaces.CMObject;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;


@SuppressWarnings({"unchecked","rawtypes"})
public class StdCommand implements Command
{
	protected final String ID;
	private final String[] access=null;
	public StdCommand()
	{
		final String id=this.getClass().getName();
		final int x=id.lastIndexOf('.');
		if(x>=0) 
			ID=id.substring(x+1);
		else
			ID=id;
	}
	@Override public String ID() { return ID; }
	@Override public String name() { return ID();}

	@Override public String[] getAccessWords(){return access;}
	@Override public void initializeClass(){}
	
	public String L(final String str, final String ... xs)
	{
		return CMLib.lang().fullSessionTranslation(str, xs);
	}
	
	public static String[] I(final String[] str)
	{
		for(int i=0;i<str.length;i++)
			str[i]=CMLib.lang().commandWordTranslation(str[i]);
		return str;
	}

	@Override
	public boolean execute(MOB mob, Vector commands, int metaFlags)
		throws java.io.IOException
	{
		// accepts the mob executing, and a Vector of Strings as a parm.
		// the return value is arbitrary, though false is conventional.
		return false;
	}
	@Override
	public boolean preExecute(MOB mob, Vector commands, int metaFlags, int secondsElapsed, double actionsRemaining)
		throws java.io.IOException
	{
		return true;
	}

	@Override
	public Object executeInternal(MOB mob, int metaFlags, Object... args) throws java.io.IOException
	{
		// fake it!
		final Vector commands = new Vector();
		commands.add(getAccessWords()[0]);
		for(final Object o : args)
			commands.add(o.toString());
		return Boolean.valueOf(execute(mob,commands,metaFlags));
	}

	public boolean checkArguments(Class[][] fmt, Object... args)
	{
		for (final Class[] element : fmt)
		{
			final Class[] ff=element;
			if(ff.length==args.length)
			{
				boolean check=true;
				for(int i=0;i<ff.length;i++)
				{
					if((args[i]!=null)
					&&(ff[i]!=null)
					&&(!ff[i].isAssignableFrom(args[i].getClass())))
					{
						check=false;
						break;
					}
				}
				if(check)
					return true;
			}
		}
		final StringBuilder str=new StringBuilder("");
		str.append(L("Illegal arguments. Sent: "));
		for(final Object o : args)
			if(o==null)
				str.append(L("null "));
			else
				str.append(o.getClass().getSimpleName()).append(" ");
		str.append(L(". Correct: "));
		for (final Class[] element : fmt)
			for(final Class c : element)
				str.append(c.getSimpleName()).append(" ");
		Log.errOut(ID(),str.toString());
		return false;
	}

	@Override
	public double actionsCost(final MOB mob, final List<String> cmds)
	{
		return CMProps.getCommandActionCost(ID(), 0.0);
	}
	@Override
	public double combatActionsCost(MOB mob, List<String> cmds)
	{
		return CMProps.getCommandCombatActionCost(ID(), 0.0);
	}
	@Override
	public double checkedActionsCost(final MOB mob, final List<String> cmds)
	{
		if(mob!=null)
			return mob.isInCombat() ? combatActionsCost(mob,cmds) : actionsCost(mob,cmds);
		return actionsCost(mob,cmds);
	}
	@Override public boolean canBeOrdered(){return true;}
	@Override public boolean securityCheck(MOB mob){return true;}
	@Override public CMObject newInstance(){return this;}
	@Override
	public CMObject copyOf()
	{
		try
		{
			final Object O=this.clone();
			return (CMObject)O;
		}
		catch(final CloneNotSupportedException e)
		{
			return this;
		}
	}

	protected final static Filterer<Environmental> noCoinFilter=new Filterer<Environmental>()
	{
		@Override
		public boolean passesFilter(Environmental obj)
		{
			return !(obj instanceof Coins);
		}
	};

	@Override public int compareTo(CMObject o){ return CMClass.classID(this).compareToIgnoreCase(CMClass.classID(o));}
}
