package com.suscipio_solutions.consecro_mud.Abilities.Songs;
import java.util.Set;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Common.interfaces.PhyStats;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMProps;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;
import com.suscipio_solutions.consecro_mud.core.interfaces.Tickable;



@SuppressWarnings("rawtypes")
public class Skill_CenterOfAttention extends BardSkill
{
	@Override public String ID() { return "Skill_CenterOfAttention"; }
	private final static String localizedName = CMLib.lang().L("Center of Attention");
	@Override public String name() { return localizedName; }
	@Override public String displayText() { return L("(Watching "+(invoker()==null?"a crazy bard":invoker().name())+")"); }
	@Override protected int canAffectCode(){return CAN_MOBS;}
	@Override protected int canTargetCode(){return CAN_MOBS;}
	@Override public int abstractQuality(){return Ability.QUALITY_MALICIOUS;}
	private static final String[] triggerStrings =I(new String[] {"CENTEROFATTENTION"});
	@Override public String[] triggerStrings(){return triggerStrings;}
	@Override public int classificationCode(){ return Ability.ACODE_SKILL|Ability.DOMAIN_FOOLISHNESS;}
	@Override public int usageType(){return USAGE_MOVEMENT|USAGE_MANA;}
	@Override protected int getTicksBetweenCasts() { return (int)(CMProps.getMillisPerMudHour() / CMProps.getTickMillis() / 2); }

	@Override
	public void affectPhyStats(Physical affected, PhyStats affectableStats)
	{
		super.affectPhyStats(affected,affectableStats);
		if(affected instanceof MOB)
		{
			if(CMLib.flags().canBeSeenBy(invoker(), (MOB)affected))
				affectableStats.setSensesMask(affectableStats.sensesMask()|PhyStats.CAN_NOT_MOVE);
		}
	}

	@Override
	public boolean tick(Tickable ticking, int tickID)
	{
		if(!super.tick(ticking, tickID))
			return false;
		if(affected instanceof MOB)
		{
			final MOB mob=(MOB)affected;
			if((!CMLib.flags().canBeSeenBy(invoker(), mob))
			||((invoker()!=null)&&(mob.location()!=invoker().location())))
				unInvoke();
			String verbStr;
			String targetStr;
			switch(CMLib.dice().roll(1, 10, 0))
			{
			case 1: verbStr=L("<S-IS-ARE> entranced by"); break;
			case 2: verbStr=L("remain(s) captivated by"); break;
			case 3: verbStr=L("<S-IS-ARE> captivated by"); break;
			case 4: verbStr=L("remain(s) entranced by"); break;
			case 5: verbStr=L("can't stop watching"); break;
			case 6: verbStr=L("stare(s) amazed at"); break;
			case 7: verbStr=L("<S-IS-ARE> hypnotized by"); break;
			case 8: verbStr=L("remain(s) enthralled by"); break;
			case 9: verbStr=L("<S-IS-ARE> delighted by"); break;
			default: verbStr=L("remain(s) enchanted by"); break;
			}
			switch(CMLib.dice().roll(1, 10, 0))
			{
			case 1: targetStr=L("<T-YOUPOSS> performance"); break;
			case 2: targetStr=L("<T-YOUPOSS> antics"); break;
			case 3: targetStr=L("<T-YOUPOSS> flailing about"); break;
			case 4: targetStr=L("<T-YOUPOSS> drama"); break;
			case 5: targetStr=L("<T-YOUPOSS> show"); break;
			case 6: targetStr=L("the ongoing spectacle"); break;
			case 7: targetStr=L("<T-YOUPOSS> comedy"); break;
			case 8: targetStr=L("<T-YOUPOSS> tomfoolery"); break;
			case 9: targetStr=L("<T-YOUPOSS> escapades"); break;
			default: targetStr=L("<T-YOUPOSS> stunts"); break;
			}
			mob.location().show(mob, invoker(), CMMsg.MSG_OK_VISUAL, L("<S-NAME> @x1 @x2.",verbStr,targetStr));
		}

		return true;
	}

	@Override
	public int castingQuality(MOB mob, Physical target)
	{
		if((mob!=null)&&(target!=null))
		{
			if(CMLib.flags().isSitting(mob))
				return Ability.QUALITY_INDIFFERENT;
			if(!CMLib.flags().aliveAwakeMobileUnbound(mob,false))
				return Ability.QUALITY_INDIFFERENT;
			if(target.fetchEffect(ID())!=null)
				return Ability.QUALITY_INDIFFERENT;
		}
		return super.castingQuality(mob,target);
	}

	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		if(CMLib.flags().isSitting(mob))
		{
			mob.tell(L("You need to stand up!"));
			return false;
		}
		if(!CMLib.flags().aliveAwakeMobileUnbound(mob,false))
			return false;

		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;

		final Set<MOB> h=properTargets(mob,givenTarget,auto);
		if(h==null)
		{
			mob.tell(L("There doesn't appear to be anyone here worth performing for."));
			return false;
		}

		final boolean success=proficiencyCheck(mob,0,auto);
		if(success)
		{
			final CMMsg msg=CMClass.getMsg(mob,null,this,CMMsg.MSK_MALICIOUS_MOVE|CMMsg.TYP_JUSTICE|(auto?CMMsg.MASK_ALWAYS:0),
										auto?"":L("<S-NAME> begin(s) flailing about while making loud silly noises."));
			if(mob.location().okMessage(mob,msg))
			{
				mob.location().send(mob,msg);
				for (final Object element : h)
				{
					final MOB target=(MOB)element;
					if(CMLib.flags().canBeSeenBy(mob, target))
					{
						int levelDiff=target.phyStats().level()-(((2*getXLEVELLevel(mob))+mob.phyStats().level()));
						if(levelDiff>0)
							levelDiff=levelDiff*5;
						else
							levelDiff=0;
						final CMMsg msg2=CMClass.getMsg(mob,target,this,CMMsg.MSK_MALICIOUS_MOVE|CMMsg.TYP_MIND|(auto?CMMsg.MASK_ALWAYS:0),null);
						if(mob.location().okMessage(mob,msg2))
						{
							mob.location().send(mob,msg2);
							if((msg.value()<=0)&&(msg2.value()<=0))
							{
								maliciousAffect(mob,target,asLevel,3,-1);
								target.location().show(target,mob,CMMsg.MSG_OK_ACTION,L("<S-NAME> begin(s) watching <T-NAME> with an amused expression."));
							}
						}
					}
				}
			}
			setTimeOfNextCast(mob);
		}
		else
			return maliciousFizzle(mob,null,L("<S-NAME> attempt(s) to become the center of attention, but fail(s)."));
		return success;
	}
}
