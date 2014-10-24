package com.suscipio_solutions.consecro_mud.Abilities.Skills;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Items.interfaces.CagedAnimal;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Container;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Item;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMSecurity;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;


@SuppressWarnings("rawtypes")
public class Skill_Cage extends StdSkill
{
	@Override public String ID() { return "Skill_Cage"; }
	private final static String localizedName = CMLib.lang().L("Cage");
	@Override public String name() { return localizedName; }
	@Override public String displayText(){ return "";}
	@Override protected int canAffectCode(){return 0;}
	@Override protected int canTargetCode(){return CAN_MOBS;}
	@Override public int abstractQuality(){return Ability.QUALITY_MALICIOUS;}
	private static final String[] triggerStrings =I(new String[] {"CAGE"});
	@Override public String[] triggerStrings(){return triggerStrings;}
	@Override public int classificationCode(){ return Ability.ACODE_SKILL|Ability.DOMAIN_ANIMALAFFINITY;}
	@Override public int usageType(){return USAGE_MOVEMENT|USAGE_MANA;}

	@Override
	public int castingQuality(MOB mob, Physical target)
	{
		if((mob!=null)&&(target!=null)&&(mob.isInCombat()))
			return Ability.QUALITY_INDIFFERENT;
		return super.castingQuality(mob,target);
	}

	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		Item cage=null;
		if(mob.location()!=null)
		{
			for(int i=0;i<mob.location().numItems();i++)
			{
				final Item I=mob.location().getItem(i);
				if((I!=null)
				&&(I instanceof Container)
				&&((((Container)I).containTypes()&Container.CONTAIN_CAGED)==Container.CONTAIN_CAGED))
				{ cage=I; break;}
			}
			if(commands.size()>0)
			{
				final String last=(String)commands.lastElement();
				final Item I=mob.location().findItem(null,last);
				if((I!=null)
				&&(I instanceof Container)
				&&((((Container)I).containTypes()&Container.CONTAIN_CAGED)==Container.CONTAIN_CAGED))
				{
					cage=I;
					commands.removeElement(last);
				}
			}
		}

		final MOB target=getTarget(mob,commands,givenTarget);
		if(target==null) return false;
		if((!auto)&&(!(CMSecurity.isAllowed(mob,mob.location(),CMSecurity.SecFlag.ORDER)||target.willFollowOrdersOf(mob))))
		{
			boolean ok=false;
			if((target.isMonster())
			&&(CMLib.flags().isAnimalIntelligence(target)))
			{
				if(CMLib.flags().isSleeping(target)
				||(!CMLib.flags().canMove(target))
				||((target.amFollowing()==mob))
				||(CMLib.flags().isBoundOrHeld(target)))
					ok=true;
			}
			if(!ok)
			{
				mob.tell(L("@x1 won't seem to let you.",target.name(mob)));
				return false;
			}

			if(cage==null)
			{
				mob.tell(L("Cage @x1 where?",target.name(mob)));
				return false;
			}

			if((mob.isInCombat())&&(mob.getVictim()!=target))
			{
				mob.tell(L("Not while you are fighting!"));
				return false;
			}
		}

		// the invoke method for spells receives as
		// parameters the invoker, and the REMAINING
		// command line parameters, divided into words,
		// and added as String objects to a vector.
		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;

		final boolean success=proficiencyCheck(mob,0,auto);

		final CagedAnimal caged=(CagedAnimal)CMClass.getItem("GenCaged");
		if((success)&&(caged.cageMe(target)))
		{
			final CMMsg msg=CMClass.getMsg(mob,target,this,CMMsg.MSK_MALICIOUS_MOVE|CMMsg.TYP_JUSTICE,null);
			if(mob.location().okMessage(mob,msg))
			{
				mob.location().send(mob,msg);
				if((cage!=null)&&(cage.owner()!=null))
				{
					if(cage.owner() instanceof MOB)
						((MOB)cage.owner()).addItem(caged);
					else
					if(cage.owner() instanceof Room)
						((Room)cage.owner()).addItem(caged);
				}
				else
					mob.addItem(caged);
				final CMMsg putMsg=CMClass.getMsg(mob,cage,caged,CMMsg.MSG_PUT,L("<S-NAME> cage(s) <O-NAME> in <T-NAME>."));
				if(mob.location().okMessage(mob,putMsg))
				{
					mob.location().send(mob,putMsg);
					target.killMeDead(false);
				}
				else
					((Item)caged).destroy();
				mob.location().recoverRoomStats();
			}
		}
		else
			return maliciousFizzle(mob,target,L("<S-NAME> attempt(s) to cage <T-NAME> and fail(s)."));


		// return whether it worked
		return success;
	}
}
