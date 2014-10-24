package com.suscipio_solutions.consecro_mud.Abilities.Druid;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMProps;
import com.suscipio_solutions.consecro_mud.core.CMath;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;



@SuppressWarnings("rawtypes")
public class Chant_SpeedBirth extends Chant
{
	@Override public String ID() { return "Chant_SpeedBirth"; }
	private final static String localizedName = CMLib.lang().L("Speed Birth");
	@Override public String name() { return localizedName; }
	@Override protected int canAffectCode(){return 0;}
	@Override public int classificationCode(){return Ability.ACODE_CHANT|Ability.DOMAIN_BREEDING;}
	@Override public int abstractQuality(){return Ability.QUALITY_OK_OTHERS;}
	@Override protected int overrideMana(){return Ability.COST_ALL;}

	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		final MOB target=this.getTarget(mob,commands,givenTarget);
		if(target==null) return false;

		Ability A=target.fetchEffect("Pregnancy");
		long start=0;
		long end=0;
		long days=0;
		long remain=0;
		String rest=null;
		if(A!=null)
		{
			final int x=A.text().indexOf('/');
			if(x>0)
			{
				final int y=A.text().indexOf('/',x+1);
				if(y>x)
				{
					start=CMath.s_long(A.text().substring(0,x));
					end=CMath.s_long(A.text().substring(x+1,y));
					remain=end-System.currentTimeMillis();
					final long divisor=CMProps.getTickMillis()*CMProps.getIntVar(CMProps.Int.TICKSPERMUDDAY);
					days=remain/divisor; // down to days;
					rest=A.text().substring(y);
				}
				else
					A=null;
			}
			else
				A=null;
		}

		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;

		final boolean success=proficiencyCheck(mob,0,auto);
		if((success)&&(A!=null)&&(remain>0))
		{
			// it worked, so build a copy of this ability,
			// and add it to the affects list of the
			// affected MOB.  Then tell everyone else
			// what happened.
			final CMMsg msg=CMClass.getMsg(mob,target,this,verbalCastCode(mob,target,auto),auto?"":L("^S<S-NAME> chant(s) to <T-NAMESELF>.^?"));
			if(mob.location().okMessage(mob,msg))
			{
				mob.location().send(mob,msg);
				if(remain<=20000)
				{
					mob.tell(L("Birth is imminent!"));
					return true;
				}
				else
				if(days<1)
				{
					if(end > System.currentTimeMillis())
						remain=(end-System.currentTimeMillis())+19999;
				}
				else
					remain=remain/2;
				A.setMiscText((start-remain)+"/"+(end-remain)+rest);
				target.location().show(target,null,CMMsg.MSG_OK_VISUAL,L("<S-NAME> appear(s) even MORE pregnant!"));
			}
		}
		else
			return beneficialWordsFizzle(mob,target,L("<S-NAME> chant(s) to <T-NAMESELF>, but the magic fades."));


		// return whether it worked
		return success;
	}
}
