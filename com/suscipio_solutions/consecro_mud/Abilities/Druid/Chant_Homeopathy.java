package com.suscipio_solutions.consecro_mud.Abilities.Druid;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.DiseaseAffect;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;
import com.suscipio_solutions.consecro_mud.core.interfaces.Tickable;




@SuppressWarnings("rawtypes")
public class Chant_Homeopathy extends Chant
{
	@Override public String ID() { return "Chant_Homeopathy"; }
	private final static String localizedName = CMLib.lang().L("Homeopathy");
	@Override public String name() { return localizedName; }
	private final static String localizedStaticDisplay = CMLib.lang().L("(Homeopathy)");
	@Override public String displayText() { return localizedStaticDisplay; }
	@Override protected int canAffectCode(){return 0;}
	@Override protected int canTargetCode(){return CAN_MOBS;}
	@Override public int classificationCode(){return Ability.ACODE_CHANT|Ability.DOMAIN_PRESERVING;}
	@Override public int abstractQuality(){return Ability.QUALITY_OK_OTHERS;}

	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		final MOB target=getTarget(mob,commands,givenTarget);
		if(target==null) return false;
		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;

		final boolean success=proficiencyCheck(mob,0,auto);
		if(success)
		{
			final CMMsg msg=CMClass.getMsg(mob,target,this,verbalCastCode(mob,target,auto),auto?L("Something is happening to <T-NAME>!"):L("^S<S-NAME> chant(s) homeopathically to <T-NAME>^?"));
			if(mob.location().okMessage(mob,msg))
			{
				mob.location().send(mob,msg);
				Ability D=null;
				for(int t=0;t<target.numEffects();t++) // personal effects
				{
					final Ability A=target.fetchEffect(t);
					if((A!=null)&&(A instanceof DiseaseAffect))
						D=A;
				}
				final int roll=CMLib.dice().rollPercentage();
				if((roll>66)||(D==null))
					mob.location().show(target,null,CMMsg.MSG_OK_VISUAL,L("<S-YOUPOSS> condition is unchanged."));
				else
				if(roll>33)
				{
					mob.location().show(target,null,CMMsg.MSG_OK_VISUAL,L("<S-NAME> glow(s) a bit."));
					D.unInvoke();
				}
				else
				{
					mob.location().show(target,null,CMMsg.MSG_OK_VISUAL,L("Something is definitely happening to <S-NAME>!"));
					for(int i=0;i<1000;i++)
						if(!D.tick(target,Tickable.TICKID_MOB))
							break;
				}
			}
		}
		else
			beneficialVisualFizzle(mob,null,L("<S-NAME> chant(s) to <T-NAMESELF>, but nothing happens."));

		return success;
	}
}
