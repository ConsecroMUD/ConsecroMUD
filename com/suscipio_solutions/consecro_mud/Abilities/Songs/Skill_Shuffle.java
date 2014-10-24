package com.suscipio_solutions.consecro_mud.Abilities.Songs;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;


@SuppressWarnings({"unchecked","rawtypes"})
public class Skill_Shuffle extends BardSkill
{
	@Override public String ID() { return "Skill_Shuffle"; }
	private final static String localizedName = CMLib.lang().L("Shuffle");
	@Override public String name() { return localizedName; }
	@Override protected int canAffectCode(){return 0;}
	@Override protected int canTargetCode(){return CAN_MOBS;}
	@Override public int abstractQuality(){return Ability.QUALITY_INDIFFERENT;}
	private static final String[] triggerStrings =I(new String[] {"SHUFFLE"});
	@Override public String[] triggerStrings(){return triggerStrings;}
	@Override public int classificationCode(){return Ability.ACODE_SKILL|Ability.DOMAIN_FOOLISHNESS;}
	@Override public int usageType(){return USAGE_MOVEMENT;}

	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		if((CMLib.flags().isSitting(mob)||CMLib.flags().isSleeping(mob)))
		{
			mob.tell(L("You must stand up first!"));
			return false;
		}

		if(mob.isInCombat())
		{
			mob.tell(L("Not while you are fighting!"));
			return false;
		}
		if(mob.location().numInhabitants()==1)
		{
			mob.tell(L("You are the only one here!"));
			return false;
		}

		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;

		final boolean success=proficiencyCheck(mob,0,auto);

		if(success)
		{
			final CMMsg msg=CMClass.getMsg(mob,null,this,CMMsg.MSG_NOISYMOVEMENT|(auto?CMMsg.MASK_ALWAYS:0),L("<S-NAME> shuffle(s) around, bumping into everyone."));
			final CMMsg msg2=CMClass.getMsg(mob,null,this,CMMsg.MSG_DELICATE_HANDS_ACT|(auto?CMMsg.MASK_ALWAYS:0),null);
			if((mob.location().okMessage(mob,msg))&&(mob.location().okMessage(mob,msg2)))
			{
				mob.location().send(mob,msg);
				mob.location().send(mob,msg2);
				final Vector V=new Vector();
				final Room R=mob.location();
				for(int i=0;i<R.numInhabitants();i++)
				{
					final MOB M=R.fetchInhabitant(i);
					V.addElement(M);
				}
				while(R.numInhabitants()>0)
				{
					final MOB M=R.fetchInhabitant(0);
					R.delInhabitant(M);
				}
				while(V.size()>0)
				{
					final MOB M=(MOB)V.elementAt(CMLib.dice().roll(1,V.size(),-1));
					if(M.location()==R) R.addInhabitant(M);
					V.removeElement(M);
				}
			}
		}
		else
			return beneficialVisualFizzle(mob,null,L("<S-NAME> shuffle(s) around, confusing <S-HIM-HERSELF>."));

		return success;
	}

}
