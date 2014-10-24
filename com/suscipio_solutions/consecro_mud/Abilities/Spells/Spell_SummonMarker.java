package com.suscipio_solutions.consecro_mud.Abilities.Spells;
import java.util.Enumeration;
import java.util.NoSuchElementException;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;


@SuppressWarnings("rawtypes")
public class Spell_SummonMarker extends Spell
{
	@Override public String ID() { return "Spell_SummonMarker"; }
	private final static String localizedName = CMLib.lang().L("Summon Marker");
	@Override public String name() { return localizedName; }
	@Override protected int canTargetCode(){return 0;}
	@Override protected int canAffectCode(){return Ability.CAN_ROOMS;}
	@Override public int classificationCode(){return Ability.ACODE_SPELL|Ability.DOMAIN_CONJURATION;}
	@Override public int abstractQuality(){ return Ability.QUALITY_INDIFFERENT;}

	@Override
	public void unInvoke()
	{

		if((canBeUninvoked())&&(invoker()!=null)&&(affected!=null)&&(affected instanceof Room))
			invoker().tell(L("Your marker in '@x1' dissipates.",((Room)affected).displayText()));
		super.unInvoke();
	}

	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		try
		{
			for(final Enumeration r=CMLib.map().rooms();r.hasMoreElements();)
			{
				final Room R=(Room)r.nextElement();
				if(CMLib.flags().canAccess(mob,R))
					for(final Enumeration<Ability> a=R.effects();a.hasMoreElements();)
					{
						final Ability A=a.nextElement();
						if((A!=null)
						   &&(A.ID().equals(ID()))
						   &&(A.invoker()==mob))
						{
							A.unInvoke();
							break;
						}
					}
			}
		}catch(final NoSuchElementException nse){}
		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;

		final boolean success=proficiencyCheck(mob,0,auto);

		if(success)
		{
			final CMMsg msg=CMClass.getMsg(mob,mob.location(),this,verbalCastCode(mob,null,auto),auto?"":L("^S<S-NAME> summon(s) <S-HIS-HER> marker energy to this place!^?"));
			if(mob.location().okMessage(mob,msg))
			{
				mob.location().send(mob,msg);
				mob.location().show(mob,mob.location(),CMMsg.MSG_OK_VISUAL,L("The spot <S-NAME> pointed to glows for brief moment."));
				beneficialAffect(mob,mob.location(),0,(adjustedLevel(mob,asLevel)*240)+450);
			}

		}
		else
			beneficialWordsFizzle(mob,null,L("<S-NAME> attempt(s) to summon <S-HIS-HER> marker energy, but fail(s)."));


		// return whether it worked
		return success;
	}
}
