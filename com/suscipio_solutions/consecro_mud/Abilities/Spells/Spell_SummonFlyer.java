package com.suscipio_solutions.consecro_mud.Abilities.Spells;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CharStats;
import com.suscipio_solutions.consecro_mud.Common.interfaces.Faction;
import com.suscipio_solutions.consecro_mud.Common.interfaces.PhyStats;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;
import com.suscipio_solutions.consecro_mud.core.interfaces.Rideable;
import com.suscipio_solutions.consecro_mud.core.interfaces.Tickable;


@SuppressWarnings("rawtypes")
public class Spell_SummonFlyer extends Spell
{
	@Override public String ID() { return "Spell_SummonFlyer"; }
	private final static String localizedName = CMLib.lang().L("Summon Flyer");
	@Override public String name() { return localizedName; }
	private final static String localizedStaticDisplay = CMLib.lang().L("(Summon Flyer)");
	@Override public String displayText() { return localizedStaticDisplay; }
	@Override public int abstractQuality(){ return Ability.QUALITY_INDIFFERENT;}
	@Override protected int canAffectCode(){return CAN_MOBS;}
	@Override protected int canTargetCode(){return 0;}
	@Override public int classificationCode(){return Ability.ACODE_SPELL|Ability.DOMAIN_CONJURATION;}
	@Override public long flags(){return Ability.FLAG_SUMMONING;}
	@Override protected int overrideMana(){return 50;}
	@Override public int enchantQuality(){return Ability.QUALITY_INDIFFERENT;}

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
	public boolean tick(Tickable ticking, int tickID)
	{
		if(tickID==Tickable.TICKID_MOB)
		{
			if((affected!=null)&&(affected instanceof MOB))
			{
				final MOB mob=(MOB)affected;
				if(!mob.isInCombat())
				if(((mob.amFollowing()==null)
				||(mob.location()==null)
				||(mob.amDead())
				||(invoker==null)
				||(invoker.location()==null)
				||((invoker!=null)&&(mob.location()!=invoker.location())&&(invoker.riding()!=affected))))
				{
					mob.delEffect(this);
					if(mob.amDead()) mob.setLocation(null);
					mob.destroy();
				}
				else
				if((mob.amFollowing()==null)&&(mob.curState().getHitPoints()<((mob.maxState().getHitPoints()/10)*3)))
				{
					mob.location().show(mob,null,CMMsg.MSG_OK_VISUAL,L("<S-NAME> flees."));
					mob.delEffect(this);
					if(mob.amDead()) mob.setLocation(null);
					mob.destroy();
				}
			}
		}
		return super.tick(ticking,tickID);
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
			final CMMsg msg=CMClass.getMsg(mob,null,this,verbalCastCode(mob,null,auto),auto?"":L("^S<S-NAME> magically call(s) for a loyal steed.^?"));
			if(mob.location().okMessage(mob,msg))
			{
				mob.location().send(mob,msg);
				final MOB target = determineMonster(mob, mob.phyStats().level()+((getX1Level(mob)+getXLEVELLevel(mob))/2));
				final MOB squabble = checkPack(target, mob);
				target.addNonUninvokableEffect( (Ability) copyOf());
				if(squabble==null)
				{
					if (target.isInCombat()) target.makePeace();
					CMLib.commands().postFollow(target,mob,true);
					invoker=mob;
					if (target.amFollowing() != mob)
						mob.tell(L("@x1 seems unwilling to follow you.",target.name(mob)));
				}
				else
				{
					squabble.location().showOthers(squabble,target,CMMsg.MSG_OK_ACTION,L("^F^<FIGHT^><S-NAME> bares its teeth at <T-NAME> and begins to attack!^</FIGHT^>^?"));
					target.setVictim(squabble);
				}
			}
		}
		else
			return beneficialWordsFizzle(mob,null,L("<S-NAME> call(s) for a steed, but choke(s) on the words."));

		// return whether it worked
		return success;
	}
	public MOB determineMonster(MOB caster, int level)
	{

		final MOB newMOB=CMClass.getMOB("GenRideable");
		final Rideable ride=(Rideable)newMOB;
		newMOB.basePhyStats().setAbility(11);
		newMOB.basePhyStats().setDisposition(newMOB.basePhyStats().disposition()|PhyStats.IS_FLYING);
		newMOB.basePhyStats().setLevel(level);
		newMOB.basePhyStats().setWeight(500);
		newMOB.basePhyStats().setRejuv(PhyStats.NO_REJUV);
		newMOB.baseCharStats().setMyRace(CMClass.getRace("Horse"));
		newMOB.baseCharStats().setStat(CharStats.STAT_GENDER,'M');
		newMOB.baseCharStats().getMyRace().startRacing(newMOB,false);
		newMOB.recoverPhyStats();
		newMOB.recoverCharStats();
		newMOB.basePhyStats().setArmor(CMLib.leveler().getLevelMOBArmor(newMOB));
		newMOB.basePhyStats().setAttackAdjustment(CMLib.leveler().getLevelAttack(newMOB));
		newMOB.basePhyStats().setDamage(CMLib.leveler().getLevelMOBDamage(newMOB));
		newMOB.basePhyStats().setSpeed(CMLib.leveler().getLevelMOBSpeed(newMOB));
		newMOB.setName(L("a flying warhorse"));
		newMOB.setDisplayText(L("a warhorse with broad powerful wings stands here"));
		newMOB.setDescription(L("A ferocious, fleet of foot, flying friend."));
		ride.setRideBasis(Rideable.RIDEABLE_AIR);
		ride.setRiderCapacity(2);
		newMOB.addNonUninvokableEffect(CMClass.getAbility("Prop_ModExperience"));
		newMOB.recoverCharStats();
		newMOB.recoverPhyStats();
		newMOB.recoverMaxState();
		CMLib.factions().setAlignment(newMOB,Faction.Align.NEUTRAL);
		newMOB.resetToMaxState();
		newMOB.text();
		newMOB.bringToLife(caster.location(),true);
		CMLib.beanCounter().clearZeroMoney(newMOB,null);
		newMOB.location().showOthers(newMOB,null,CMMsg.MSG_OK_ACTION,L("<S-NAME> appears!"));
		caster.location().recoverRoomStats();
		newMOB.setStartRoom(null);
		return(newMOB);
	}

	public MOB checkPack(MOB newPackmate, MOB mob)
	{
		for(int i=0;i<mob.numFollowers();i++)
		{
			final MOB possibleBitch = mob.fetchFollower(i);
			if(newPackmate.Name().equalsIgnoreCase(possibleBitch.Name())
			&&(possibleBitch.location()==newPackmate.location())
			&& (CMLib.dice().rollPercentage()-mob.charStats().getStat(CharStats.STAT_CHARISMA)+newPackmate.phyStats().level() > 75))
				return possibleBitch;
		}
		return null;
	}
}
