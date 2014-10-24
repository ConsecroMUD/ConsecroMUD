package com.suscipio_solutions.consecro_mud.Abilities.Misc;
import java.util.List;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.StdAbility;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Behaviors.interfaces.Behavior;
import com.suscipio_solutions.consecro_mud.Commands.interfaces.Command;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CharState;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CharStats;
import com.suscipio_solutions.consecro_mud.Common.interfaces.PhyStats;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMParms;
import com.suscipio_solutions.consecro_mud.core.CMStrings;
import com.suscipio_solutions.consecro_mud.core.collections.SLinkedList;
import com.suscipio_solutions.consecro_mud.core.interfaces.CMObject;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;
import com.suscipio_solutions.consecro_mud.core.interfaces.MsgListener;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;
import com.suscipio_solutions.consecro_mud.core.interfaces.StatsAffecting;
import com.suscipio_solutions.consecro_mud.core.interfaces.Tickable;


@SuppressWarnings({"unchecked","rawtypes"})
public class PresenceReaction extends StdAbility
{
	@Override public String ID(){return "PresenceReaction";}
	@Override protected int canAffectCode(){return Ability.CAN_MOBS;}
	@Override protected int canTargetCode(){return Ability.CAN_MOBS;}
	protected MOB reactToM=null;
	protected boolean startedManaging=false;
	protected String previousMood = null;
	protected String reactToName=null;
	protected SLinkedList<Object[]> unmanagedYet=new SLinkedList<Object[]>();
	protected SLinkedList<CMObject> managed = new SLinkedList<CMObject>();

	public PresenceReaction()
	{
		super();
		super.makeLongLasting();
		super.savable=false;
		super.canBeUninvoked=false;
	}
	@Override
	protected void cloneFix(Ability E)
	{
		reactToM=null;
		previousMood=null;
		reactToName=null;
		reactToM=null;
		affected=null;
		invoker=null;
		unmanagedYet=new SLinkedList<Object[]>();
		managed = new SLinkedList<CMObject>();
	}

	public void addAffectOrBehavior(String substr)
	{
		final int x=substr.indexOf('=');
		if(x>=0)
		{
			final String nam=substr.substring(0,x);
			if(nam.trim().length()==0)
			{
				reactToName=substr.substring(1);
				return;
			}
			final Behavior B=CMClass.getBehavior(nam);
			if(B!=null)
			{
				B.setSavable(false);
				final Object[] SET=new Object[]{B,substr.substring(x+1)};
				unmanagedYet.add(SET);
				return;
			}
			final Ability A=CMClass.getAbility(nam);
			if(A!=null)
			{
				A.setSavable(false);
				A.makeNonUninvokable();
				final Object[] SET=new Object[]{A,substr.substring(x+1)};
				unmanagedYet.add(SET);
				return;
			}
			final Command C=CMClass.getCommand(nam);
			if(C!=null)
			{
				final Object[] SET=new Object[]{C,substr.substring(x+1)};
				unmanagedYet.add(SET);
			}
		}
	}

	@Override
	public void setMiscText(String parms)
	{
		if(parms.startsWith("+"))
			addAffectOrBehavior(parms.substring(1));
		else
		{
			final List<String> parsed=CMParms.parseAny(parms,"~~",true);
			for (final String string : parsed)
				addAffectOrBehavior(string);
		}
	}

	@Override
	public boolean okMessage(Environmental affecting, CMMsg msg)
	{
		for(final CMObject O : managed)
			if(O instanceof MsgListener)
				if(!((MsgListener)O).okMessage(affecting, msg))
					return false;
		return super.okMessage(affecting,msg);
	}

	@Override
	public void executeMsg(Environmental affecting, CMMsg msg)
	{
		for(final CMObject O : managed)
			if(O instanceof MsgListener)
				((MsgListener)O).executeMsg(affecting, msg);
		super.executeMsg(affecting,msg);
	}

	@Override
	public void affectPhyStats(Physical affected, PhyStats affectableStats)
	{
		for(final CMObject O : managed)
			if(O instanceof StatsAffecting)
				((StatsAffecting)O).affectPhyStats(affected, affectableStats);
	}
	@Override
	public void affectCharStats(MOB affectedMob, CharStats affectableStats)
	{
		for(final CMObject O : managed)
			if(O instanceof StatsAffecting)
				((StatsAffecting)O).affectCharStats(affectedMob, affectableStats);
	}
	@Override
	public void affectCharState(MOB affectedMob, CharState affectableMaxState)
	{
		for(final CMObject O : managed)
			if(O instanceof StatsAffecting)
				((StatsAffecting)O).affectCharState(affectedMob, affectableMaxState);
	}
	protected synchronized boolean shutdownPresence(MOB affected)
	{
		final Room R=affected.location();
		if(((R==null)||(reactToM==null)||(!R.isInhabitant(reactToM))))
		{
			final MOB M=(MOB)super.affected;
			for(final CMObject O : managed)
			{
				if((O!=null)&&(O.ID().equals("Mood")))
				{
					if(previousMood!=null)
					{
						try
						{
							final Command C=CMClass.getCommand("Mood");
							if(C!=null)
								C.execute(M,CMParms.parse("MOOD "+previousMood),0);
						}
						catch(final Exception e){}
					}
				}
				else
				if(O instanceof Environmental)
					((Environmental)O).destroy();
			}
			affected.delEffect(this);
		}
		unmanagedYet.clear();
		managed.clear();
		return false;
	}

	protected boolean initializeManagedObjects(MOB affected)
	{
		if(unmanagedYet.size()==0)
			return false;
		boolean didAnything=false;
		final SLinkedList<Object[]> commands = new SLinkedList<Object[]>();
		while(unmanagedYet.size()>0)
		{
			final Object[] thing=unmanagedYet.removeFirst();
			if(thing[0] instanceof Ability)
			{
				if(((Ability)thing[0]).ID().equalsIgnoreCase("Mood"))
				{
					previousMood="";
					final Ability A=affected.fetchEffect("Mood");
					if(A!=null) previousMood=A.text();
					if(previousMood.trim().length()==0)
						previousMood="NORMAL";
				}
				final Ability A=(Ability)thing[0];
				A.setAffectedOne(affected);
				A.setMiscText((String)thing[1]);
				managed.add(A);
				didAnything=true;
				continue;
			}
			if(thing[0] instanceof Behavior)
			{
				final Behavior B=(Behavior)thing[0];
				B.startBehavior(affected);
				B.setParms((String)thing[1]);
				managed.add(B);
				didAnything=true;
				continue;
			}
			if(thing[0] instanceof Command)
			{
				commands.add(thing);
				continue;
			}
		}
		unmanagedYet = commands;
		if(didAnything)
		{
			affected.recoverCharStats();
			affected.recoverPhyStats();
			affected.recoverMaxState();
		}
		return didAnything;
	}

	protected void initializeAllManaged(MOB affected)
	{
		if(unmanagedYet.size()==0) return;
		initializeManagedObjects(affected);
		while(unmanagedYet.size()>0)
		{
			final Object[] thing=unmanagedYet.removeFirst();
			if(thing[0] instanceof Command)
			{
				final Command C=(Command)thing[0];
				try
				{
					final String cmdparms=C.getAccessWords()[0]+" "+CMStrings.replaceAll((String)thing[1],"<TARGET>",reactToM.Name());
					affected.enqueCommand(CMParms.parse(cmdparms),Command.METAFLAG_FORCED, 0);
				}
				catch(final Exception e){}
				managed.add(C);
				continue;
			}
		}
	}

	@Override
	public boolean tick(Tickable ticking, int tickID)
	{
		super.tick(ticking,tickID);
		if(tickID!=Tickable.TICKID_MOB) return true;
		if(reactToM==null)
		{
			// dont combine this if with the above
			if((affected instanceof MOB)&&(reactToName!=null))
				reactToM=((MOB)affected).location().fetchInhabitant(reactToName);
			if(reactToM==null)
				return shutdownPresence((MOB)affected);
		}
		else
		if(this.affected instanceof MOB)
		{
			final MOB affected=(MOB)this.affected;
			if((affected.location()!=reactToM.location())
				||(affected.amDead())
				||(reactToM.amDead())
				||(affected.amDestroyed())
				||(reactToM.amDestroyed())
				||(!CMLib.flags().isInTheGame(affected, true))
				||(!CMLib.flags().isInTheGame(reactToM, true)))
					return shutdownPresence(affected);
			initializeAllManaged(affected);
			for(final CMObject O : managed)
				if(O instanceof Tickable)
					((Tickable)O).tick(ticking, tickID);
		}
		return true;
	}

	@Override
	public boolean invoke(MOB mob, Vector commands, Physical target, boolean auto, int asLevel)
	{
		if(target==null)
		{
			final PresenceReaction A=(PresenceReaction)mob.fetchEffect(ID());
			if(A!=null)
				A.shutdownPresence(mob);
			if(affected==mob)
				shutdownPresence(mob);
			return A!=null;
		}
		if(!(target instanceof MOB)) return false;
		final PresenceReaction A=(PresenceReaction)this.copyOf();
		A.reactToM=(MOB)target;
		for(final Object O : commands)
			A.addAffectOrBehavior((String)O);
		commands.clear();
		commands.addElement(A);
		if(auto)
		{
			synchronized(mob)
			{
				if(mob.fetchEffect(ID())==null)
				{
					mob.addNonUninvokableEffect(A);
					A.initializeManagedObjects(mob);
				}
				return true;
			}
		}
		else
		{
			A.makeLongLasting();
			A.makeNonUninvokable();
			A.setAffectedOne(mob);
			A.initializeManagedObjects(mob);
 			return true;
		}

	}
}
