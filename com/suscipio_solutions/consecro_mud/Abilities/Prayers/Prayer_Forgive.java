package com.suscipio_solutions.consecro_mud.Abilities.Prayers;
import java.util.List;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Behaviors.interfaces.LegalBehavior;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Common.interfaces.LegalWarrant;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMParms;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;



@SuppressWarnings({"unchecked","rawtypes"})
public class Prayer_Forgive extends Prayer
{
	@Override public String ID() { return "Prayer_Forgive"; }
	private final static String localizedName = CMLib.lang().L("Forgive");
	@Override public String name() { return localizedName; }
	@Override public int classificationCode(){return Ability.ACODE_PRAYER|Ability.DOMAIN_EVANGELISM;}
	@Override protected int canTargetCode(){return Ability.CAN_MOBS;}
	@Override protected int canAffectCode(){return 0;}
	@Override public int abstractQuality(){ return Ability.QUALITY_OK_OTHERS;}
	@Override public long flags(){return Ability.FLAG_HOLY;}

	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		LegalBehavior B=null;
		if(mob.location()!=null) B=CMLib.law().getLegalBehavior(mob.location());

		String name=CMParms.combine(commands,0);
		if(name.startsWith("$")) name=name.substring(1);
		if(name.endsWith("$")) name=name.substring(0,name.length()-1);
		if((name.trim().length()==0)&&(givenTarget!=null)) name=givenTarget.Name();
		if(name.trim().length()==0)
		{
			mob.tell(L("Forgive whom?"));
			return false;
		}
		List<LegalWarrant> warrants=new Vector();
		List<MOB> criminals=new Vector();
		if(B!=null)
		{
			criminals=B.getCriminals(CMLib.law().getLegalObject(mob.location()),name);
			if(criminals.size()>0)
				warrants=B.getWarrantsOf(CMLib.law().getLegalObject(mob.location()),criminals.get(0));
		}

		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;

		final boolean success=proficiencyCheck(mob,0,auto);

		if(success)
		{
			if(warrants.size()==0)
				beneficialWordsFizzle(mob,null,L("<S-NAME> @x1 to forgive @x2 for no reason at all.",prayForWord(mob),name));
			else
			{
				final CMMsg msg=CMClass.getMsg(mob,mob.location(),this,verbalCastCode(mob,mob.location(),auto),auto?"":L("^S<S-NAME> @x1 to forgive @x2.^?",prayForWord(mob),name));
				if(mob.location().okMessage(mob,msg))
				{
					mob.location().send(mob,msg);
					for(int i=0;i<warrants.size();i++)
					{
						final LegalWarrant W=warrants.get(i);
						W.setCrime("pardoned");
						W.setOffenses(0);
					}
				}
			}
		}
		else
			beneficialWordsFizzle(mob,null,L("<S-NAME> @x1 to forgive @x2, but nothing happens.",prayForWord(mob),name));


		// return whether it worked
		return success;
	}
}
