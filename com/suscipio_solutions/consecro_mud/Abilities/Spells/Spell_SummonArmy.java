package com.suscipio_solutions.consecro_mud.Abilities.Spells;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Common.interfaces.PhyStats;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;
import com.suscipio_solutions.consecro_mud.core.interfaces.Tickable;


@SuppressWarnings("rawtypes")
public class Spell_SummonArmy extends Spell
{
	@Override public String ID() { return "Spell_SummonArmy"; }
	private final static String localizedName = CMLib.lang().L("Summon Army");
	@Override public String name() { return localizedName; }
	private final static String localizedStaticDisplay = CMLib.lang().L("(Monster Summoning)");
	@Override public String displayText() { return localizedStaticDisplay; }
	@Override public int abstractQuality(){return Ability.QUALITY_BENEFICIAL_SELF;}
	@Override public int classificationCode(){return Ability.ACODE_SPELL|Ability.DOMAIN_CONJURATION;}
	@Override public long flags(){return Ability.FLAG_SUMMONING;}
	@Override public int enchantQuality(){return Ability.QUALITY_INDIFFERENT;}
	public boolean hasFought=false;

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
		if((affected==null)
		||(!(affected instanceof MOB))
		||(((MOB)affected).amDead())
		||(((MOB)affected).amFollowing()==null)
		||((hasFought)&&(!((MOB)affected).isInCombat())))
		{
			unInvoke();
			return false;
		}
		else
		if(!hasFought)
			hasFought=((MOB)affected).isInCombat();
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
			final CMMsg msg=CMClass.getMsg(mob,null,this,verbalCastCode(mob,null,auto),auto?"":L("^S<S-NAME> summon(s) help from the Java Plane.^?"));
			if(mob.location().okMessage(mob,msg))
			{
				mob.location().send(mob,msg);
				final String[] choices={"Dog","Orc","Tiger","Troll","Chimp","BrownBear","Goblin","LargeBat","GiantScorpion","Rattlesnake","Ogre"};
				final int num=(mob.phyStats().level()+(getXLEVELLevel(mob)+(2*getX1Level(mob))))/3;
				for(int i=0;i<num;i++)
				{
					final MOB newMOB=CMClass.getMOB(choices[CMLib.dice().roll(1,choices.length,-1)]);
					newMOB.addNonUninvokableEffect(CMClass.getAbility("Prop_ModExperience"));
					newMOB.setLocation(mob.location());
					newMOB.basePhyStats().setRejuv(PhyStats.NO_REJUV);
					newMOB.recoverCharStats();
					newMOB.recoverPhyStats();
					newMOB.recoverMaxState();
					newMOB.resetToMaxState();
					newMOB.bringToLife(mob.location(),true);
					CMLib.beanCounter().clearZeroMoney(newMOB,null);
					newMOB.location().showOthers(newMOB,null,CMMsg.MSG_OK_ACTION,L("<S-NAME> appears!"));
					newMOB.setStartRoom(null); // keep before postFollow for Conquest
					newMOB.setVictim(mob.getVictim());
					CMLib.commands().postFollow(newMOB,mob,true);
					if(newMOB.amFollowing()!=mob)
						newMOB.setFollowing(mob);
					if(newMOB.getVictim()!=null)
						newMOB.getVictim().setVictim(newMOB);
					hasFought=false;
					beneficialAffect(mob,newMOB,asLevel,0);
				}
			}
		}
		else
			return beneficialWordsFizzle(mob,null,L("<S-NAME> call(s) for magical help, but chokes on the words."));

		// return whether it worked
		return success;
	}

}
