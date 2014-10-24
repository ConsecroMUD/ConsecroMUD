package com.suscipio_solutions.consecro_mud.Abilities.Spells;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Common.interfaces.PhyStats;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.Deity;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMProps;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;


@SuppressWarnings("rawtypes")
public class Spell_SummonEnemy extends Spell
{
	@Override public String ID() { return "Spell_SummonEnemy"; }
	private final static String localizedName = CMLib.lang().L("Summon Enemy");
	@Override public String name() { return localizedName; }
	private final static String localizedStaticDisplay = CMLib.lang().L("(Enemy Summoning)");
	@Override public String displayText() { return localizedStaticDisplay; }
	@Override protected int canTargetCode(){return 0;}
	@Override public int classificationCode(){return Ability.ACODE_SPELL|Ability.DOMAIN_CONJURATION;}
	@Override public long flags(){return Ability.FLAG_TRANSPORTING|Ability.FLAG_SUMMONING;}
	@Override protected int overrideMana(){return Ability.COST_PCT+50;}
	@Override public int enchantQuality(){return Ability.QUALITY_INDIFFERENT;}
	@Override public int abstractQuality(){ return Ability.QUALITY_INDIFFERENT;}

	@Override
	public void unInvoke()
	{
		final MOB mob=(MOB)affected;
		super.unInvoke();
		if((canBeUninvoked())&&(mob!=null))
		{
			if(mob.amDead()) mob.setLocation(null);
			mob.destroy();
		}
	}

	@Override
	public void executeMsg(final Environmental myHost, final CMMsg msg)
	{
		super.executeMsg(myHost,msg);
		if((affected!=null)
		&&(affected instanceof MOB)
		&&(msg.amISource((MOB)affected)||msg.amISource(((MOB)affected).amFollowing()))
		&&(msg.sourceMinor()==CMMsg.TYP_QUIT))
		{
			unInvoke();
			if(msg.source().playerStats()!=null) msg.source().playerStats().setLastUpdated(0);
		}
	}

	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;

		final boolean success=proficiencyCheck(mob,0,auto);

		if(success)
		{
			invoker=mob;
			final CMMsg msg=CMClass.getMsg(mob,null,this,verbalCastCode(mob,null,auto),auto?"":L("^S<S-NAME> conjur(s) the dark shadow of a living creature...^?"));
			if(mob.location().okMessage(mob,msg))
			{
				mob.location().send(mob,msg);
				final MOB target = determineMonster(mob, mob.phyStats().level());
				if(target!=null)
				{
					final CMMsg msg2=CMClass.getMsg(mob,target,this,verbalCastCode(mob,null,auto)|CMMsg.MASK_MALICIOUS,null);
					if(mob.location().okMessage(mob, msg2))
					{
						mob.location().send(mob, msg2);
						beneficialAffect(mob,target,asLevel,0);
						target.setVictim(mob);
					}
					else
						CMLib.tracking().wanderAway(target, false, true);
				}
				else
					mob.tell(L("Your equal could not be summoned."));
			}
		}
		else
			return beneficialWordsFizzle(mob,null,L("<S-NAME> conjur(s), but nothing happens."));

		// return whether it worked
		return success;
	}
	public MOB determineMonster(MOB caster, int level)
	{
		if(caster==null) return null;
		if(caster.location()==null) return null;
		if(caster.location().getArea()==null) return null;
		MOB monster=null;
		int tries=10000;
		while((monster==null)&&((--tries)>0))
		{
			final Room room=CMLib.map().getRandomRoom();
			if((room!=null)&&CMLib.flags().canAccess(caster,room)&&(room.numInhabitants()>0))
			{
				final MOB mob=room.fetchRandomInhabitant();
				if((mob!=null)
				&&(!(mob instanceof Deity))
				&&(mob.phyStats().level()>=level-(CMProps.getIntVar(CMProps.Int.EXPRATE)/2))
				&&(mob.phyStats().level()<=(level+(CMProps.getIntVar(CMProps.Int.EXPRATE)/2)))
				&&(mob.charStats()!=null)
				&&(mob.charStats().getMyRace()!=null)
				&&(CMProps.isTheme(mob.charStats().getMyRace().availabilityCode()))
				&&((CMLib.flags().isGood(caster)&&CMLib.flags().isEvil(mob))
					|| (CMLib.flags().isNeutral(mob))
					|| (CMLib.flags().isEvil(caster)&&CMLib.flags().isGood(mob)))
				&&(caster.mayIFight(mob))
				)
					monster=mob;
			}
		}
		if(monster==null) return null;
		monster=(MOB)monster.copyOf();
		monster.basePhyStats().setRejuv(PhyStats.NO_REJUV);
		monster.recoverCharStats();
		monster.recoverPhyStats();
		monster.recoverMaxState();
		monster.resetToMaxState();
		monster.text();
		monster.bringToLife(caster.location(),true);
		CMLib.beanCounter().clearZeroMoney(monster,null);
		monster.location().showOthers(monster,null,CMMsg.MSG_OK_ACTION,L("<S-NAME> appears!"));
		caster.location().recoverRoomStats();
		monster.setStartRoom(null);
		return(monster);
	}
}
