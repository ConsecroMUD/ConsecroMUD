package com.suscipio_solutions.consecro_mud.Abilities.Spells;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMParms;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;


@SuppressWarnings({"unchecked","rawtypes"})
public class Spell_DiviningEye extends Spell
{
	@Override public String ID() { return "Spell_DiviningEye"; }
	private final static String localizedName = CMLib.lang().L("Divining Eye");
	@Override public String name() { return localizedName; }
	@Override public int abstractQuality(){ return Ability.QUALITY_INDIFFERENT;}
	@Override protected int canAffectCode(){return 0;}
	@Override protected int canTargetCode(){return 0;}
	@Override public int classificationCode(){ return Ability.ACODE_SPELL|Ability.DOMAIN_DIVINATION;}

	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		if(commands.size()==0)
		{
			mob.tell(L("You must specify a divining spell and any parameters for it."));
			return false;
		}

		final Ability pryingEyeA=mob.fetchEffect("Spell_PryingEye");
		if(pryingEyeA==null)
		{
			mob.tell(L("This spell requires an active prying eye."));
			return false;
		}

		final String commandStr=CMParms.combine(commands);
		commands.insertElementAt("CAST",0);
		final Ability A=CMLib.english().getToEvoke(mob, commands);
		if(A==null)
		{
			mob.tell(L("'@x1' does not refer to any diviner spell you know.",commandStr));
			return false;
		}
		if(((A.classificationCode() & Ability.ALL_ACODES)!=Ability.ACODE_SPELL)
		||((A.classificationCode() & Ability.ALL_DOMAINS)!=Ability.DOMAIN_DIVINATION))
		{
			mob.tell(L("'@x1' is not a diviner spell you know.",A.name()));
			return false;
		}

		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;

		final boolean success=proficiencyCheck(mob,0,auto);

		if(success)
		{
			final CMMsg msg=CMClass.getMsg(mob,null,this,somanticCastCode(mob,null,auto),auto?"":L("^S<S-NAME> invoke(s) a remote divination!^?"));
			final Room room=mob.location();
			if(room.okMessage(mob,msg))
			{
				final MOB eyeM=(MOB)pryingEyeA.affecting();
				room.send(mob,msg);
				try
				{
					final Room eyeRoom=eyeM.location();
					if(eyeRoom!=null)
					{
						eyeM.addAbility(A);
						A.invoke(eyeM, commands, null, false, 0);
					}
				}
				finally
				{
				}
			}
		}
		else
			return beneficialVisualFizzle(mob,null,L("<S-NAME> attempt(s) to invoke something, but fail(s)."));

		// return whether it worked
		return success;
	}
}
