package com.suscipio_solutions.consecro_mud.Abilities.Druid;
import java.util.HashSet;
import java.util.List;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Trap;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CharStats;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Item;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.collections.XVector;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;


@SuppressWarnings({"unchecked","rawtypes"})
public class Chant_PlantTrap extends Chant implements Trap
{
	@Override public String ID() { return "Chant_PlantTrap"; }
	private final static String localizedName = CMLib.lang().L("Plant Trap");
	@Override public String name() { return localizedName; }
	@Override public int classificationCode(){return Ability.ACODE_CHANT|Ability.DOMAIN_PLANTCONTROL;}
	@Override public int abstractQuality(){ return Ability.QUALITY_MALICIOUS;}
	@Override protected int canAffectCode(){return CAN_ROOMS;}
	@Override protected int canTargetCode(){return 0;}
	@Override protected int overrideMana(){return 100;}

	@Override public boolean isABomb(){return false;}
	@Override public void activateBomb(){}
	@Override public void setReset(int Reset){}
	@Override public int getReset(){return 0;}
	@Override public boolean maySetTrap(MOB mob, int asLevel){return false;}
	@Override public List<Item> getTrapComponents() { return new Vector(); }
	@Override public boolean canSetTrapOn(MOB mob, Physical P){return false;}
	@Override public String requiresToSet(){return "";}
	@Override
	public Trap setTrap(MOB mob, Physical P, int trapBonus, int qualifyingClassLevel, boolean permanent)
	{beneficialAffect(mob,P,qualifyingClassLevel+trapBonus,0); return (Trap)P.fetchEffect(ID());}

	@Override public boolean disabled(){return false;}
	@Override public boolean sprung(){return false;}
	@Override public void disable(){unInvoke();}
	@Override
	public void spring(MOB M)
	{
		doMyThing(M);
	}

	public static final String[] choices={"Chant_PlantChoke","Chant_PlantConstriction"};
	public void doMyThing(MOB target)
	{
		if((target!=invoker())&&(target.location()!=null))
		{
			if((!invoker().mayIFight(target))
			||(invoker().getGroupMembers(new HashSet<MOB>()).contains(target))
			||(CMLib.dice().rollPercentage()<=target.charStats().getSave(CharStats.STAT_SAVE_TRAPS)))
				target.location().show(target,null,null,CMMsg.MASK_ALWAYS|CMMsg.MSG_NOISE,L("<S-NAME> avoid(s) some aggressive plants!"));
			else
			if(target.location().show(target,target,this,CMMsg.MASK_ALWAYS|CMMsg.MSG_NOISE,L("<S-NAME> <S-IS-ARE> assaulted by the plants!")))
			{
				final Vector them=new XVector<String>(choices);
				if(invoker()!=null)
					for (final String choice : choices)
						if(invoker().fetchAbility(choice)==null)
							them.removeElement(choice);
				if(them.size()>0)
				{
					final String s=(String)them.elementAt(CMLib.dice().roll(1,them.size(),-1));
					final Ability A=CMClass.getAbility(s);
					A.invoke(target,target,true,0);
				}
			}
		}
	}

	public boolean helpfulAbilityFound(MOB mob)
	{
		for (final String choice : choices)
			if(mob.fetchAbility(choice)!=null)
			 return true;
		return false;
	}

	@Override
	public void executeMsg(final Environmental myHost, final CMMsg msg)
	{
		if(msg.amITarget(affected)&&(msg.targetMinor()==CMMsg.TYP_ENTER)
		&&(!msg.amISource(invoker))
		&&(msg.source().amFollowing()!=invoker))
			spring(msg.source());
		super.executeMsg(myHost,msg);
	}

	@Override
	public int castingQuality(MOB mob, Physical target)
	{
		if(mob!=null)
		{
			if(!helpfulAbilityFound(mob))
				return Ability.QUALITY_INDIFFERENT;
			final Room R=mob.location();
			if(R!=null)
			{
				if(((R.domainType()&Room.INDOORS)>0))
					return Ability.QUALITY_INDIFFERENT;
				if((R.domainType()==Room.DOMAIN_OUTDOORS_CITY)
				   ||(R.domainType()==Room.DOMAIN_OUTDOORS_SPACEPORT)
				   ||(R.domainType()==Room.DOMAIN_OUTDOORS_UNDERWATER)
				   ||(R.domainType()==Room.DOMAIN_OUTDOORS_AIR)
				   ||(R.domainType()==Room.DOMAIN_OUTDOORS_WATERSURFACE))
					return Ability.QUALITY_INDIFFERENT;
			}
		}
		return super.castingQuality(mob,target);
	}

	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		final Room target=mob.location();
		if(target==null) return false;
		if(target.fetchEffect(ID())!=null)
		{
			mob.tell(L("This place is already trapped."));
			return false;
		}
		if(!helpfulAbilityFound(mob))
		{
			mob.tell(L("You must know plant choke or plant constriction for this chant to work."));
			return false;
		}

		if(((mob.location().domainType()&Room.INDOORS)>0)&&(!auto))
		{
			mob.tell(L("You must be outdoors for this chant to work."));
			return false;
		}
		if(((mob.location().domainType()==Room.DOMAIN_OUTDOORS_CITY)
		   ||(mob.location().domainType()==Room.DOMAIN_OUTDOORS_SPACEPORT)
		   ||(mob.location().domainType()==Room.DOMAIN_OUTDOORS_UNDERWATER)
		   ||(mob.location().domainType()==Room.DOMAIN_OUTDOORS_AIR)
		   ||(mob.location().domainType()==Room.DOMAIN_OUTDOORS_WATERSURFACE))
		&&(!auto))
		{
			mob.tell(L("This chant does not work here."));
			return false;
		}
		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;

		final boolean success=proficiencyCheck(mob,0,auto);

		if(success)
		{
			// it worked, so build a copy of this ability,
			// and add it to the affects list of the
			// affected MOB.  Then tell everyone else
			// what happened.
			final CMMsg msg=CMClass.getMsg(mob,target,this,verbalCastCode(mob,target,auto),auto?L("This area seems to writh with malicious plants."):L("^S<S-NAME> chant(s), stirring the plant life into maliciousness.^?"));
			if(mob.location().okMessage(mob,msg))
			{
				mob.location().send(mob,msg);
				beneficialAffect(mob,target,asLevel,0);
			}
		}
		else
			beneficialWordsFizzle(mob,target,L("<S-NAME> chant(s), but the magic fades."));

		// return whether it worked
		return success;
	}
}
