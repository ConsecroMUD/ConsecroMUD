package com.suscipio_solutions.consecro_mud.Abilities.Spells;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMath;
import com.suscipio_solutions.consecro_mud.core.collections.Pair;
import com.suscipio_solutions.consecro_mud.core.collections.SLinkedList;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;
import com.suscipio_solutions.consecro_mud.core.interfaces.Tickable;


@SuppressWarnings("rawtypes")
public class Spell_GroupStatus extends Spell
{
	@Override public String ID() { return "Spell_GroupStatus"; }
	private final static String localizedName = CMLib.lang().L("Group Status");
	@Override public String name() { return localizedName; }
	private final static String localizedStaticDisplay = CMLib.lang().L("(Group Status)");
	@Override public String displayText() { return localizedStaticDisplay; }
	@Override public int abstractQuality(){ return Ability.QUALITY_INDIFFERENT;}
	@Override protected int canAffectCode(){return CAN_MOBS;}
	@Override public int classificationCode(){ return Ability.ACODE_SPELL|Ability.DOMAIN_DIVINATION;}
	protected List<Pair<MOB,Ability>> groupMembers=null;

	protected HashSet<String> reporteds=new HashSet<String>();
	protected HashSet<String> affects=new HashSet<String>();

	@Override
	public boolean tick(Tickable ticking, int tickID)
	{
		if(!super.tick(ticking, tickID))
			return false;
		if(ticking instanceof MOB)
		{
			final MOB mob=(MOB)ticking;
			if(((MOB)ticking)==invoker())
			{
				if(groupMembers==null)
				{
					groupMembers=new SLinkedList<Pair<MOB,Ability>>();
					final Set<MOB> grp=mob.getGroupMembers(new TreeSet<MOB>());
					for(final MOB M : grp)
					{
						final Pair<MOB,Ability> P=new Pair<MOB,Ability>(M,null);
						groupMembers.add(P);
					}
				}
				for(final Pair<MOB,Ability> P : groupMembers)
				{
					if(P.second==null)
					{
						P.second=CMClass.getAbility(ID());
						if(P.second!=null)
						{
							P.second.setName(text());
							P.second.setStat("TICKDOWN", Integer.toString(tickDown));
							P.first.addEffect(P.second);
							P.second.setInvoker(mob);
						}
					}
				}
			}
			else
			if(invoker()!=null)
			{
				if((mob.curState().getHitPoints()<5)
				||(mob.curState().getHitPoints()<mob.getWimpHitPoint())
				||(CMath.div(mob.curState().getHitPoints(),mob.maxState().getHitPoints())<=0.15))
				{
					if(!reporteds.contains("LOWHITPOINTS"))
					{
						invoker().tell(L("@x1 is low on hit points.",mob.Name()));
						reporteds.add("LOWHITPOINTS");
					}
				}
				else
					reporteds.remove("LOWHITPOINTS");


				for(final Iterator<String> i=affects.iterator();i.hasNext();)
				{
					final String affectName=i.next();
					if(mob.fetchEffect(affectName)==null)
						i.remove();
				}
				for(final Enumeration<Ability> a=mob.effects();a.hasMoreElements();)
				{
					final Ability A=a.nextElement();
					if((A.abstractQuality() == Ability.QUALITY_MALICIOUS)
					&&(!affects.contains(A.ID())))
					{
						affects.add(A.ID());
						invoker().tell(L("@x1 is now affected by @x2.",mob.Name(),A.name()));
					}
				}
			}
		}
		return true;
	}

	@Override
	public boolean okMessage(final Environmental myHost, final CMMsg msg)
	{
		if((affected instanceof MOB)
		&&(msg.amISource((MOB)affected))
		&&(msg.sourceMinor()==CMMsg.TYP_DEATH)
		&&(affected != invoker()))
		{
			if(!reporteds.contains("DEATH"))
			{
				invoker().tell(L("@x1 is dying.",affected.Name()));
				reporteds.add("DEATH");
			}
		}
		return super.okMessage(myHost,msg);
	}

	@Override
	public void unInvoke()
	{
		if(!(affected instanceof MOB))
			return;
		final MOB mob=(MOB)affected;

		if(canBeUninvoked())
		{
			if(groupMembers!=null)
				for(final Pair<MOB,Ability> Gs : groupMembers)
				{
					final Ability A=Gs.second;
					if((A!=null)&&(A.invoker()==mob))
						A.unInvoke();
				}
			groupMembers=null;
		}
		super.unInvoke();
	}

	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		MOB target=mob;
		if((auto)&&(givenTarget!=null)&&(givenTarget instanceof MOB))
			target=(MOB)givenTarget;
		if(target.fetchEffect(this.ID())!=null)
		{
			mob.tell(target,null,null,L("<S-NAME> <S-IS-ARE> already knowledgable about <S-HIS-HER> group."));
			return false;
		}

		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;


		// now see if it worked
		final boolean success=proficiencyCheck(mob,0,auto);

		if(success)
		{
			final CMMsg msg=CMClass.getMsg(mob,target,this,verbalCastCode(mob,target,auto),auto?"":L("^S<S-NAME> point(s) at <S-HIS-HER> group members and knowingly cast(s) a spell.^?"));
			if(mob.location().okMessage(mob,msg))
			{
				mob.location().send(mob,msg);
				beneficialAffect(mob,target,asLevel,0);
			}
		}
		else
			return beneficialWordsFizzle(mob,target,L("<S-NAME> point(s) at <S-HIS-HER> group members speak(s) knowingly, but nothing more happens."));


		// return whether it worked
		return success;
	}
}
