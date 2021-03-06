package com.suscipio_solutions.consecro_mud.Abilities.Druid;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Common.interfaces.Faction;
import com.suscipio_solutions.consecro_mud.Common.interfaces.PhyStats;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;
import com.suscipio_solutions.consecro_mud.core.interfaces.Tickable;



@SuppressWarnings("rawtypes")
public class Chant_SummonElemental extends Chant
{
	@Override public String ID() { return "Chant_SummonElemental"; }
	private final static String localizedName = CMLib.lang().L("Summon Elemental");
	@Override public String name() { return localizedName; }
	private final static String localizedStaticDisplay = CMLib.lang().L("(Summon Elemental)");
	@Override public String displayText() { return localizedStaticDisplay; }
	@Override public int classificationCode(){return Ability.ACODE_CHANT|Ability.DOMAIN_DEEPMAGIC;}
	@Override public int abstractQuality(){return Ability.QUALITY_BENEFICIAL_SELF;}
	@Override public int enchantQuality(){return Ability.QUALITY_INDIFFERENT;}
	@Override protected int canAffectCode(){return CAN_MOBS;}
	@Override protected int canTargetCode(){return 0;}
	@Override public long flags(){return Ability.FLAG_SUMMONING;}

	@Override
	public boolean tick(Tickable ticking, int tickID)
	{
		if(tickID==Tickable.TICKID_MOB)
		{
			if((affected!=null)&&(affected instanceof MOB)&&(invoker!=null))
			{
				final MOB mob=(MOB)affected;
				if(((mob.amFollowing()==null)
				||(mob.amDead())
				||(mob.location()!=invoker.location())))
				{
					mob.delEffect(this);
					if(mob.amDead()) mob.setLocation(null);
					mob.destroy();
				}
			}
		}
		return super.tick(ticking,tickID);
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
	public int castingQuality(MOB mob, Physical target)
	{
		if(mob!=null)
		{
			final Room R=mob.location();
			if(R!=null)
			{
				if(CMLib.flags().hasAControlledFollower(mob,this))
					return Ability.QUALITY_INDIFFERENT;
			}
		}
		return super.castingQuality(mob,target);
	}

	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		if(CMLib.flags().hasAControlledFollower(mob, this))
		{
			mob.tell(L("You can only control one elemental."));
			return false;
		}
		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;

		final boolean success=proficiencyCheck(mob,0,auto);

		if(success)
		{
			invoker=mob;
			final CMMsg msg=CMClass.getMsg(mob,null,this,verbalCastCode(mob,null,auto),auto?"":L("^S<S-NAME> chant(s) and summon(s) help from another Plain.^?"));
			if(mob.location().okMessage(mob,msg))
			{
				mob.location().send(mob,msg);
				final MOB target = determineMonster(mob, mob.phyStats().level()+(2*super.getXLEVELLevel(mob)));
				target.addNonUninvokableEffect((Ability)this.copyOf());
				if(target.isInCombat()) target.makePeace();
				CMLib.commands().postFollow(target,mob,true);
				if(target.amFollowing()!=mob)
					mob.tell(L("@x1 seems unwilling to follow you.",target.name(mob)));
			}
		}
		else
			return beneficialWordsFizzle(mob,null,L("<S-NAME> chant(s), but nothing happens."));

		// return whether it worked
		return success;
	}
	public MOB determineMonster(MOB caster, int level)
	{
		final MOB newMOB=CMClass.getMOB("GenMOB");
		newMOB.basePhyStats().setLevel(adjustedLevel(caster,0));
		switch(CMLib.dice().roll(1,4,0))
		{
		case 1:
			newMOB.setName(L("a fire elemental"));
			newMOB.setDisplayText(L("a fire elemental is flaming nearby."));
			newMOB.setDescription(L("A large beast, wreathed in flame, with sparkling eyes and a hot temper."));
			newMOB.basePhyStats().setDisposition(newMOB.basePhyStats().disposition()|PhyStats.IS_LIGHTSOURCE);
			CMLib.factions().setAlignment(newMOB,Faction.Align.EVIL);
			newMOB.baseCharStats().setMyRace(CMClass.getRace("FireElemental"));
			newMOB.addAbility(CMClass.getAbility("Firebreath"));
			break;
		case 2:
			newMOB.setName(L("an ice elemental"));
			newMOB.setDisplayText(L("an ice elemental is chilling out here."));
			newMOB.setDescription(L("A large beast, made of ice, with crytaline eyes and a cold disposition."));
			CMLib.factions().setAlignment(newMOB,Faction.Align.GOOD);
			newMOB.baseCharStats().setMyRace(CMClass.getRace("WaterElemental"));
			newMOB.addAbility(CMClass.getAbility("Frostbreath"));
			break;
		case 3:
			newMOB.setName(L("an earth elemental"));
			newMOB.setDisplayText(L("an earth elemental looks right at home."));
			newMOB.setDescription(L("A large beast, made of rock and dirt, with a hard stare."));
			CMLib.factions().setAlignment(newMOB,Faction.Align.NEUTRAL);
			newMOB.baseCharStats().setMyRace(CMClass.getRace("EarthElemental"));
			newMOB.addAbility(CMClass.getAbility("Gasbreath"));
			break;
		case 4:
			newMOB.setName(L("an air elemental"));
			newMOB.setDisplayText(L("an air elemental blows right by."));
			newMOB.setDescription(L("A large beast, made of swirling clouds and air."));
			CMLib.factions().setAlignment(newMOB,Faction.Align.GOOD);
			newMOB.baseCharStats().setMyRace(CMClass.getRace("AirElemental"));
			newMOB.addAbility(CMClass.getAbility("Lighteningbreath"));
			break;
		}
		newMOB.recoverPhyStats();
		newMOB.recoverCharStats();
		newMOB.basePhyStats().setAbility(newMOB.basePhyStats().ability()*2);
		newMOB.basePhyStats().setArmor(CMLib.leveler().getLevelMOBArmor(newMOB));
		newMOB.basePhyStats().setAttackAdjustment(CMLib.leveler().getLevelAttack(newMOB));
		newMOB.basePhyStats().setSpeed(CMLib.leveler().getLevelMOBSpeed(newMOB));
		newMOB.basePhyStats().setDamage(CMLib.leveler().getLevelMOBDamage(newMOB));
		newMOB.basePhyStats().setSensesMask(newMOB.basePhyStats().sensesMask()|PhyStats.CAN_SEE_DARK);
		newMOB.addNonUninvokableEffect(CMClass.getAbility("Prop_ModExperience"));
		newMOB.addBehavior(CMClass.getBehavior("CombatAbilities"));
		newMOB.setLocation(caster.location());
		newMOB.basePhyStats().setRejuv(PhyStats.NO_REJUV);
		newMOB.setMiscText(newMOB.text());
		newMOB.recoverCharStats();
		newMOB.recoverPhyStats();
		newMOB.recoverMaxState();
		newMOB.resetToMaxState();
		newMOB.bringToLife(caster.location(),true);
		CMLib.beanCounter().clearZeroMoney(newMOB,null);
		newMOB.location().showOthers(newMOB,null,CMMsg.MSG_OK_ACTION,L("<S-NAME> appears!"));
		newMOB.setStartRoom(null);
		newMOB.addNonUninvokableEffect(this);
		return(newMOB);
	}
}
