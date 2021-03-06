package com.suscipio_solutions.consecro_mud.Abilities.Prayers;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Areas.interfaces.Area;
import com.suscipio_solutions.consecro_mud.Behaviors.interfaces.LegalBehavior;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Common.interfaces.Law;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;



@SuppressWarnings("rawtypes")
public class Prayer_LowerLaw extends Prayer
{
	@Override public String ID() { return "Prayer_LowerLaw"; }
	private final static String localizedName = CMLib.lang().L("Lower Law");
	@Override public String name() { return localizedName; }
	@Override public String displayText(){ return "";}
	@Override protected int canTargetCode(){return 0;}
	@Override public long flags(){return Ability.FLAG_HOLY;}
	@Override public int abstractQuality(){return Ability.QUALITY_INDIFFERENT;}
	@Override public int classificationCode(){return Ability.ACODE_PRAYER|Ability.DOMAIN_COMMUNING;}


	public void possiblyAddLaw(Law L, Vector<String> V, String code)
	{
		if(L.basicCrimes().containsKey(code))
		{
			final String name=L.basicCrimes().get(code)[Law.BIT_CRIMENAME];
			if(!V.contains(name)) V.add(name);
		}
	}

	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;

		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;

		final boolean success=proficiencyCheck(mob,0,auto);
		if(success)
		{
			final CMMsg msg=CMClass.getMsg(mob,null,this,verbalCastCode(mob,null,auto),auto?"":L("^S<S-NAME> @x1 for knowledge of the lower law here.^?",prayWord(mob)));
			if(mob.location().okMessage(mob,msg))
			{
				mob.location().send(mob,msg);
				final Area O=CMLib.law().getLegalObject(mob.location());
				final LegalBehavior B=CMLib.law().getLegalBehavior(mob.location());
				if((B==null)||(O==null))
					mob.tell(L("No lower law is established here."));
				else
				{
					final Law L=B.legalInfo(O);
					final Vector<String> crimes=new Vector<String>();
					possiblyAddLaw(L,crimes,"TRESPASSING");
					possiblyAddLaw(L,crimes,"ASSAULT");
					possiblyAddLaw(L,crimes,"MURDER");
					possiblyAddLaw(L,crimes,"NUDITY");
					possiblyAddLaw(L,crimes,"ARMED");
					possiblyAddLaw(L,crimes,"RESISTINGARREST");
					possiblyAddLaw(L,crimes,"PROPERTYROB");
					for(final String key : L.abilityCrimes().keySet())
						if(key.startsWith("$"))
							crimes.add(key.substring(1));
					if(L.taxLaws().containsKey("TAXEVASION"))
						crimes.add(((String[])L.taxLaws().get("TAXEVASION"))[Law.BIT_CRIMENAME]);
					for(int x=0;x<L.bannedSubstances().size();x++)
					{
						final String name=L.bannedBits().get(x)[Law.BIT_CRIMENAME];
						if(!crimes.contains(name)) crimes.add(name);
					}
					for(int x=0;x<L.otherCrimes().size();x++)
					{
						final String name=L.otherBits().get(x)[Law.BIT_CRIMENAME];
						if(!crimes.contains(name)) crimes.add(name);
					}
					mob.tell(L("The following lower crimes are divinely revealed to you: @x1.",CMLib.english().toEnglishStringList(crimes.toArray(new String[0]))));
				}
			}
		}
		else
			beneficialWordsFizzle(mob,null,L("<S-NAME> @x1, but nothing is revealed.",prayWord(mob)));

		return success;
	}
}
