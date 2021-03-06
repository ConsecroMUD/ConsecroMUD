package com.suscipio_solutions.consecro_mud.Abilities.Prayers;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.DiseaseAffect;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CharStats;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;
import com.suscipio_solutions.consecro_mud.core.interfaces.Tickable;



@SuppressWarnings("rawtypes")
public class Prayer_Doomspout extends Prayer implements DiseaseAffect
{
	@Override public String ID() { return "Prayer_Doomspout"; }
	private final static String localizedName = CMLib.lang().L("Doomspout");
	@Override public String name() { return localizedName; }
	@Override public int classificationCode(){return Ability.ACODE_PRAYER|Ability.DOMAIN_EVANGELISM;}
	@Override public int abstractQuality(){ return Ability.QUALITY_MALICIOUS;}
	@Override public long flags(){return Ability.FLAG_UNHOLY;}
	private final static String localizedStaticDisplay = CMLib.lang().L("(Doomspout)");
	@Override public String displayText() { return localizedStaticDisplay; }
	@Override protected int canAffectCode(){return Ability.CAN_MOBS;}
	@Override protected int canTargetCode(){return Ability.CAN_MOBS;}
	@Override public int difficultyLevel(){return 7;}
	int plagueDown=4;
	String godName="The Demon";
	protected boolean ispoke=false;

	@Override public int spreadBitmap(){return DiseaseAffect.SPREAD_PROXIMITY;}

	@Override
	public String getHealthConditionDesc()
	{
		return "Profound mental compulsion disorder.";
	}

	@Override
	public boolean tick(Tickable ticking, int tickID)
	{
		if(!(affected instanceof MOB))
			return super.tick(ticking,tickID);

		if(!super.tick(ticking,tickID))
			return false;
		if((--plagueDown)<=0)
		{
			final MOB mob=(MOB)affected;
			plagueDown=4;
			if(invoker==null) invoker=mob;
			if(mob.location()==null) return false;
			ispoke=false;
			switch(CMLib.dice().roll(1,12,0))
			{
			case 1:	CMLib.commands().postSay(mob,null,L("Repent, or @x1 will consume your soul!",godName),false,false); break;
			case 2:	CMLib.commands().postSay(mob,null,L("We are all damned! Hope is forgotten!"),false,false); break;
			case 3:	CMLib.commands().postSay(mob,null,L("@x1 has damned us all!",godName),false,false); break;
			case 4:	CMLib.commands().postSay(mob,null,L("Death is the only way out for us now!"),false,false); break;
			case 5:	CMLib.commands().postSay(mob,null,L("The finger of @x1 will destroy all!",godName),false,false); break;
			case 6:	CMLib.commands().postSay(mob,null,L("The waters will dry! The air will turn cold! Our bodies will fail! We are Lost!"),false,false); break;
			case 7:	CMLib.commands().postSay(mob,null,L("Nothing can save you! Throw yourself on the mercy of @x1!",godName),false,false); break;
			case 8:	CMLib.commands().postSay(mob,null,L("@x1 will show us no mercy!",godName),false,false); break;
			case 9:	CMLib.commands().postSay(mob,null,L("@x1 has spoken! We will all be destroyed!",godName),false,false);
					break;
			case 10:
			case 11:
			case 12:
					CMLib.commands().postSay(mob,null,L("Our doom is upon us! The end is near!"),false,false);
					break;
			}
			if((CMLib.flags().canSpeak(mob))&&(ispoke))
			{
				final MOB target=mob.location().fetchRandomInhabitant();
				if((target!=null)
				&&(CMLib.flags().canBeHeardSpeakingBy(mob,target))
				&&(target!=invoker)
				&&(target!=mob)
				&&(target.fetchEffect(ID())==null))
					if(CMLib.dice().rollPercentage()>target.charStats().getSave(CharStats.STAT_SAVE_DISEASE))
					{
						mob.location().show(target,null,CMMsg.MSG_OK_VISUAL,L("<S-NAME> look(s) seriously ill!"));
						maliciousAffect(invoker,target,0,0,-1);
					}
					else
						spreadImmunity(target);
			}
		}
		return true;
	}

	@Override
	public void affectCharStats(MOB affected, CharStats affectableStats)
	{
		super.affectCharStats(affected,affectableStats);
		if(affected==null) return;
		if(affectableStats.getStat(CharStats.STAT_INTELLIGENCE)>3)
			affectableStats.setStat(CharStats.STAT_INTELLIGENCE,3);
	}

	@Override
	public void executeMsg(final Environmental myHost, final CMMsg msg)
	{
		super.executeMsg(myHost,msg);
		if((affected!=null)
		&&(msg.source()==affected)
		&&(msg.sourceMinor()==CMMsg.TYP_SPEAK))
			ispoke=true;
	}

	@Override
	public void unInvoke()
	{
		// undo the affects of this spell
		if(!(affected instanceof MOB))
			return;
		final MOB mob=(MOB)affected;

		super.unInvoke();

		if(canBeUninvoked())
			if((mob.location()!=null)&&(!mob.amDead()))
			{
				spreadImmunity(mob);
				mob.location().show(mob,null,CMMsg.MSG_OK_VISUAL,L("<S-YOUPOSS> doomspout disease clear up."));
			}
	}

	@Override
	public int castingQuality(MOB mob, Physical target)
	{
		if(mob!=null)
		{
			if(mob.isInCombat())
				return Ability.QUALITY_INDIFFERENT;
		}
		return super.castingQuality(mob,target);
	}

	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		final MOB target=getTarget(mob,commands,givenTarget);
		if(target==null) return false;

		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;

		final boolean success=proficiencyCheck(mob,0,auto);
		if(success)
		{
			// it worked, so build a copy of this ability,
			// and add it to the affects list of the
			// affected MOB.  Then tell everyone else
			// what happened.
			final CMMsg msg=CMClass.getMsg(mob,target,this,verbalCastCode(mob,target,auto)|CMMsg.MASK_MALICIOUS,auto?"":L("^S<S-NAME> inflict(s) an unholy disease upon <T-NAMESELF>.^?"));
			final CMMsg msg2=CMClass.getMsg(mob,target,this,CMMsg.MSK_CAST_MALICIOUS_VERBAL|CMMsg.TYP_DISEASE|(auto?CMMsg.MASK_ALWAYS:0),null);
			final CMMsg msg3=CMClass.getMsg(mob,target,this,CMMsg.MSK_CAST_MALICIOUS_VERBAL|CMMsg.TYP_MIND|(auto?CMMsg.MASK_ALWAYS:0),null);
			if((mob.location().okMessage(mob,msg))
			&&(mob.location().okMessage(mob,msg2))
			&&(mob.location().okMessage(mob,msg3)))
			{
				mob.location().send(mob,msg);
				mob.location().send(mob,msg2);
				mob.location().send(mob,msg3);
				if((msg.value()<=0)&&(msg2.value()<=0)&&(msg3.value()<=0))
				{
					invoker=mob;
					if(mob.getWorshipCharID().length()>0)
						godName=mob.getWorshipCharID();
					maliciousAffect(mob,target,asLevel,0,-1);
					mob.location().show(target,null,CMMsg.MSG_OK_VISUAL,L("<S-NAME> look(s) seriously ill!"));
				}
				else
					spreadImmunity(target);
			}
		}
		else
			return maliciousFizzle(mob,target,L("<S-NAME> attempt(s) to inflict a disease upon <T-NAMESELF>, but flub(s) it."));


		// return whether it worked
		return success;
	}
}
