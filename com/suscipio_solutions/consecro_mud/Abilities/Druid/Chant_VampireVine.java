package com.suscipio_solutions.consecro_mud.Abilities.Druid;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CharStats;
import com.suscipio_solutions.consecro_mud.Common.interfaces.Faction;
import com.suscipio_solutions.consecro_mud.Common.interfaces.PhyStats;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;



public class Chant_VampireVine extends Chant_SummonVine
{
	@Override public String ID() { return "Chant_VampireVine"; }
	private final static String localizedName = CMLib.lang().L("Vampire Vine");
	@Override public String name() { return localizedName; }
	private final static String localizedStaticDisplay = CMLib.lang().L("(Vampire Vine)");
	@Override public String displayText() { return localizedStaticDisplay; }
	@Override public int classificationCode(){return Ability.ACODE_CHANT|Ability.DOMAIN_PLANTCONTROL;}
	@Override public long flags(){return Ability.FLAG_SUMMONING;}
	@Override public int abstractQuality(){return Ability.QUALITY_BENEFICIAL_SELF;}
	@Override public int enchantQuality(){return Ability.QUALITY_INDIFFERENT;}

	@Override
	public boolean okMessage(final Environmental myHost, final CMMsg msg)
	{
		if(!super.okMessage(myHost,msg)) return false;
		if(affected instanceof MOB)
		{
			final MOB mob=(MOB)affected;
			if(msg.amISource(mob)&&(msg.targetMinor()==CMMsg.TYP_DAMAGE))
			{
				int amount=msg.value();
				if(amount>3)
				{
					amount=amount/4;
					CMLib.combat().postHealing(((MOB)affected),((MOB)affected),this,CMMsg.MASK_ALWAYS|CMMsg.TYP_CAST_SPELL,amount,null);
					if(invoker!=null)
						CMLib.combat().postHealing(invoker,invoker,this,CMMsg.MASK_ALWAYS|CMMsg.TYP_CAST_SPELL,amount,null);
				}
			}
		}

		return true;
	}

	@Override
	public MOB determineMonster(MOB caster, int material)
	{
		final MOB victim=caster.getVictim();
		final MOB newMOB=CMClass.getMOB("GenMOB");
		int level=adjustedLevel(caster,0);
		if(level<1) level=1;
		newMOB.basePhyStats().setLevel(level);
		newMOB.basePhyStats().setAbility(19);
		newMOB.baseCharStats().setMyRace(CMClass.getRace("Vine"));
		final String name="a vampire vine";
		newMOB.setName(name);
		newMOB.setDisplayText(L("@x1 looks enraged!",name));
		newMOB.setDescription("");
		CMLib.factions().setAlignment(newMOB,Faction.Align.NEUTRAL);
		final Ability A=CMClass.getAbility("Fighter_Rescue");
		A.setProficiency(100);
		newMOB.addAbility(A);
		newMOB.setVictim(victim);
		newMOB.basePhyStats().setAbility(newMOB.basePhyStats().ability()*2);
		newMOB.basePhyStats().setSensesMask(newMOB.basePhyStats().sensesMask()|PhyStats.CAN_SEE_DARK);
		newMOB.setLocation(caster.location());
		newMOB.basePhyStats().setRejuv(PhyStats.NO_REJUV);
		newMOB.basePhyStats().setDamage(30+(9*(level/5)));
		newMOB.basePhyStats().setAttackAdjustment(10+(level));
		newMOB.basePhyStats().setArmor(100-(30+(level/2)));
		newMOB.baseCharStats().setStat(CharStats.STAT_GENDER,'N');
		newMOB.addNonUninvokableEffect(CMClass.getAbility("Prop_ModExperience"));
		newMOB.setMiscText(newMOB.text());
		newMOB.recoverCharStats();
		newMOB.recoverPhyStats();
		newMOB.recoverMaxState();
		newMOB.resetToMaxState();
		newMOB.bringToLife(caster.location(),true);
		CMLib.beanCounter().clearZeroMoney(newMOB,null);
		newMOB.setStartRoom(null); // keep before postFollow for Conquest
		CMLib.commands().postFollow(newMOB,caster,true);
		if(newMOB.amFollowing()!=caster)
			caster.tell(L("@x1 seems unwilling to follow you.",newMOB.name()));
		else
		{
			if(newMOB.getVictim()!=victim) newMOB.setVictim(victim);
			newMOB.location().showOthers(newMOB,victim,CMMsg.MSG_OK_ACTION,L("<S-NAME> start(s) attacking <T-NAMESELF>!"));
		}
		return(newMOB);
	}
}
