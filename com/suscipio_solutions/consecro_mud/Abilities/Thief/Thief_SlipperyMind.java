package com.suscipio_solutions.consecro_mud.Abilities.Thief;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Common.interfaces.Faction;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.collections.Pair;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;
import com.suscipio_solutions.consecro_mud.core.interfaces.Tickable;


@SuppressWarnings("rawtypes")
public class Thief_SlipperyMind extends ThiefSkill
{
	@Override public String ID() { return "Thief_SlipperyMind"; }
	private final static String localizedName = CMLib.lang().L("Slippery Mind");
	@Override public String name() { return localizedName; }
	private final static String localizedStaticDisplay = CMLib.lang().L("(Slippery Mind)");
	@Override public String displayText() { return localizedStaticDisplay; }
	@Override protected int canAffectCode(){return CAN_MOBS;}
	@Override protected int canTargetCode(){return 0;}
	@Override public int abstractQuality(){return Ability.QUALITY_INDIFFERENT;}
	private static final String[] triggerStrings =I(new String[] {"SLIPPERYMIND"});
	@Override public String[] triggerStrings(){return triggerStrings;}
	protected volatile LinkedList<Pair<Faction,Integer>> oldFactions=null;

	@Override
	public boolean tick(Tickable ticking, int tickID)
	{
		if(unInvoked) return false;
		if((affected!=null)&&(affected instanceof MOB)&&(ticking instanceof MOB))
		{
			if(!super.tick(ticking,tickID))
				return false;
			final MOB mob=(MOB)affected;
			Faction F=null;
			if(oldFactions==null)
			{
				oldFactions=new LinkedList<Pair<Faction,Integer>>();
				for(final Enumeration e=mob.fetchFactions();e.hasMoreElements();)
				{
					F=CMLib.factions().getFaction((String)e.nextElement());
					if(F!=null)
					{
						oldFactions.add(new Pair<Faction,Integer>(F,Integer.valueOf(mob.fetchFaction(F.factionID()))));
						mob.addFaction(F.factionID(),F.middle());
					}
				}
			}
			else
			for(final Pair<Faction,Integer> p : oldFactions)
			{
				F=p.first;
				if(mob.fetchFaction(F.factionID())!=F.middle())
					mob.addFaction(F.factionID(),F.middle());
			}
		}
		return true;
	}

	@Override
	public void executeMsg(Environmental host, CMMsg msg)
	{
		super.executeMsg(host,msg);
		if(super.canBeUninvoked())
		{
			if((affected!=null)
			&&(affected instanceof MOB)
			&&(msg.amISource((MOB)affected))
			&&(msg.sourceMinor()==CMMsg.TYP_QUIT))
				unInvoke();
			else
			if(msg.sourceMinor()==CMMsg.TYP_SHUTDOWN)
				unInvoke();
		}
	}

	@Override
	public boolean okMessage(Environmental host, CMMsg msg)
	{
		if(((msg.sourceMinor()==CMMsg.TYP_QUIT)
			||(msg.sourceMinor()==CMMsg.TYP_SHUTDOWN)
			||(msg.sourceMinor()==CMMsg.TYP_DEATH) // yes, intentional
			||(msg.sourceMinor()==CMMsg.TYP_ROOMRESET)))
		{
			unInvoke();
		}
		return super.okMessage(host,msg);
	}

	@Override
	public void unInvoke()
	{
		final Environmental E=affected;
		super.unInvoke();
		if((E instanceof MOB)&&(oldFactions!=null))
		{
			if(!((MOB)E).amDead())
				((MOB)E).tell(L("You've lost your slippery mind concentration."));
			for(final Pair<Faction,Integer> p : oldFactions)
				((MOB)E).addFaction(p.first.factionID(),p.second.intValue());
			oldFactions=null;
		}
	}

	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		MOB target=mob;
		if((auto)&&(givenTarget!=null)&&(givenTarget instanceof MOB))
			target=(MOB)givenTarget;

		if(target.fetchEffect(this.ID())!=null)
		{
			mob.tell(target,null,null,L("<S-NAME> already <S-HAS-HAVE> a slippery mind."));
			return false;
		}

		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;

		final boolean success=proficiencyCheck(mob,0,auto);

		final CMMsg msg=CMClass.getMsg(mob,target,this,auto?CMMsg.MASK_ALWAYS:CMMsg.MSG_DELICATE_SMALL_HANDS_ACT,CMMsg.MSG_OK_VISUAL,CMMsg.MSG_OK_VISUAL,auto?L("<T-NAME> gain(s) a slippery mind."):L("<S-NAME> wink(s) and nod(s)."));
		if(!success)
			return beneficialVisualFizzle(mob,null,auto?"":L("<S-NAME> wink(s) and nod(s), but <S-IS-ARE>n't fooling anyone."));
		else
		if(mob.location().okMessage(mob,msg))
		{
			mob.location().send(mob,msg);
			oldFactions=null;
			beneficialAffect(mob,target,asLevel,0);
			final Ability A=target.fetchEffect(ID());
			if(A!=null)
				A.tick(target,Tickable.TICKID_MOB);
		}
		return success;
	}
}
