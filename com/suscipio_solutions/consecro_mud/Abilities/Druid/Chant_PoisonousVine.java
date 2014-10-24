package com.suscipio_solutions.consecro_mud.Abilities.Druid;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CharStats;
import com.suscipio_solutions.consecro_mud.Common.interfaces.Faction;
import com.suscipio_solutions.consecro_mud.Common.interfaces.PhyStats;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;



public class Chant_PoisonousVine extends Chant_SummonVine
{
	@Override public String ID() { return "Chant_PoisonousVine"; }
	private final static String localizedName = CMLib.lang().L("Poisonous Vine");
	@Override public String name() { return localizedName; }
	private final static String localizedStaticDisplay = CMLib.lang().L("(Poisonous Vine)");
	@Override public String displayText() { return localizedStaticDisplay; }
	@Override public int classificationCode(){return Ability.ACODE_CHANT|Ability.DOMAIN_PLANTCONTROL;}
	@Override public int abstractQuality(){return Ability.QUALITY_BENEFICIAL_SELF;}
	@Override public int enchantQuality(){return Ability.QUALITY_INDIFFERENT;}
	@Override protected int canAffectCode(){return CAN_MOBS;}
	@Override protected int canTargetCode(){return 0;}

	@Override
	public MOB determineMonster(MOB caster, int material)
	{
		final MOB victim=caster.getVictim();
		final MOB newMOB=CMClass.getMOB("GenMOB");
		int level=adjustedLevel(caster,0);
		if(level<1) level=1;
		newMOB.basePhyStats().setLevel(level);
		newMOB.basePhyStats().setAbility(13);
		newMOB.baseCharStats().setMyRace(CMClass.getRace("Vine"));
		final String name="a poisonous vine";
		newMOB.setName(name);
		newMOB.setDisplayText(L("@x1 looks enraged!",name));
		newMOB.setDescription("");
		CMLib.factions().setAlignment(newMOB,Faction.Align.NEUTRAL);
		Ability A=CMClass.getAbility("Fighter_Rescue");
		A.setProficiency(100);
		newMOB.addAbility(A);
		A=null;
		final int classlevel=CMLib.ableMapper().qualifyingClassLevel(caster,this)-CMLib.ableMapper().qualifyingLevel(caster,this);
		switch(classlevel/5)
		{
		case 0:	A=CMClass.getAbility("Poison_Sting"); break;
		case 1:	A=CMClass.getAbility("Poison_Bloodboil"); break;
		case 2:	A=CMClass.getAbility("Poison_Venom"); break;
		default: 	A=CMClass.getAbility("Poison_Decreptifier"); break;
		}
		if(A!=null)
		{
			A.setProficiency(100);
			newMOB.addAbility(A);
		}
		newMOB.addBehavior(CMClass.getBehavior("CombatAbilities"));
		newMOB.setVictim(victim);
		newMOB.basePhyStats().setSensesMask(newMOB.basePhyStats().sensesMask()|PhyStats.CAN_SEE_DARK);
		newMOB.setLocation(caster.location());
		newMOB.basePhyStats().setRejuv(PhyStats.NO_REJUV);
		newMOB.basePhyStats().setDamage(6+(5*(level/5)));
		newMOB.basePhyStats().setAttackAdjustment(10);
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
