package com.suscipio_solutions.consecro_mud.Abilities.Druid;
import java.util.HashSet;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Common.interfaces.PhyStats;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;



@SuppressWarnings("rawtypes")
public class Chant_BestowName extends Chant
{
	@Override public String ID() { return "Chant_BestowName"; }
	private final static String localizedName = CMLib.lang().L("Bestow Name");
	@Override public String name() { return localizedName; }
	@Override public int classificationCode(){return Ability.ACODE_CHANT|Ability.DOMAIN_BREEDING;}
	@Override public int abstractQuality(){ return Ability.QUALITY_OK_OTHERS;}
	@Override public String displayText(){return "";}
	@Override protected int canAffectCode(){return Ability.CAN_MOBS;}
	@Override protected int canTargetCode(){return Ability.CAN_MOBS;}

	@Override
	public void affectPhyStats(Physical affected, PhyStats affectedStats)
	{
		super.affectPhyStats(affected,affectedStats);
		if((affected instanceof MOB)
		&&(((MOB)affected).amFollowing()==null)
		&&(CMLib.flags().isInTheGame((MOB)affected,true)))
		{
			affected.delEffect(affected.fetchEffect(ID()));
			affectedStats.setName(null);
		}
		else
		if((text().length()>0))
			affectedStats.setName(text());
	}

	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		if(commands.size()<2)
		{
			mob.tell(L("You must specify the animal, and a name to give him."));
			return false;
		}
		String myName=((String)commands.lastElement()).trim();
		commands.removeElementAt(commands.size()-1);
		if(myName.length()==0)
		{
			mob.tell(L("You must specify a name."));
			return false;
		}
		if(myName.indexOf(' ')>=0)
		{
			mob.tell(L("Your name may not contain a space."));
			return false;
		}

		final MOB target=this.getTarget(mob,commands,givenTarget);
		if(target==null) return false;
		if((!CMLib.flags().isAnimalIntelligence(target))||(!target.isMonster())||(!mob.getGroupMembers(new HashSet<MOB>()).contains(target)))
		{
			mob.tell(L("This chant only works on non-player animals in your group."));
			return false;
		}

		if((target.name().toUpperCase().startsWith("A "))
		||(target.name().toUpperCase().startsWith("AN "))
		||(target.name().toUpperCase().startsWith("SOME ")))
			myName=target.name()+" named "+myName;
		else
		if(target.name().indexOf(' ')>=0)
			myName=myName+", "+target.name();


		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;

		final boolean success=proficiencyCheck(mob,0,auto);

		if(success)
		{
			// it worked, so build a copy of this ability,
			// and add it to the affects list of the
			// affected MOB.  Then tell everyone else
			// what happened.
			final CMMsg msg=CMClass.getMsg(mob,target,this,verbalCastCode(mob,target,auto),auto?"":L("^S<S-NAME> chant(s) to <T-NAMESELF>, bestowing the name '@x1'.^?",myName));
			if(mob.location().okMessage(mob,msg))
			{
				mob.location().send(mob,msg);
				final Chant_BestowName A=(Chant_BestowName)copyOf();
				A.setMiscText(myName);
				target.addNonUninvokableEffect(A);
				mob.location().recoverRoomStats();
			}
		}
		else
			return beneficialWordsFizzle(mob,target,L("<S-NAME> chant(s) to <T-NAMESELF>, but nothing happens."));


		// return whether it worked
		return success;
	}
}
