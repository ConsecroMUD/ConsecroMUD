package com.suscipio_solutions.consecro_mud.Abilities.Prayers;
import java.util.List;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Behaviors.interfaces.LegalBehavior;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Common.interfaces.Law;
import com.suscipio_solutions.consecro_mud.Common.interfaces.LegalWarrant;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;



@SuppressWarnings({"unchecked","rawtypes"})
public class Prayer_Condemnation extends Prayer
{
	@Override public String ID() { return "Prayer_Condemnation"; }
	private final static String localizedName = CMLib.lang().L("Condemnation");
	@Override public String name() { return localizedName; }
	@Override public int classificationCode(){return Ability.ACODE_PRAYER|Ability.DOMAIN_EVANGELISM;}
	@Override protected int canTargetCode(){return Ability.CAN_MOBS;}
	@Override protected int canAffectCode(){return 0;}
	@Override public int abstractQuality(){ return Ability.QUALITY_OK_OTHERS;}
	@Override public long flags(){return Ability.FLAG_UNHOLY;}

	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		LegalBehavior B=null;
		if(mob.location()!=null) B=CMLib.law().getLegalBehavior(mob.location());

		final MOB target=getTarget(mob,commands,givenTarget);
		if(target==null) return false;
		List<LegalWarrant> warrants=new Vector();
		if(B!=null)
			warrants=B.getWarrantsOf(CMLib.law().getLegalObject(mob.location()),target);

		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;

		final boolean success=proficiencyCheck(mob,0,auto);

		if((success)&&(warrants.size()>0))
		{
			final CMMsg msg=CMClass.getMsg(mob,target,this,verbalCastCode(mob,target,auto),auto?"":L("^S<S-NAME> @x1 to condemn <T-NAMESELF>.^?",prayForWord(mob)));
			if(mob.location().okMessage(mob,msg))
			{
				mob.location().send(mob,msg);
				if(msg.value()<=0)
				for(int i=0;i<warrants.size();i++)
				{
					final LegalWarrant W=warrants.get(i);
					if(W.punishment()<Law.PUNISHMENT_HIGHEST)
						W.setPunishment(W.punishment()+1);
				}
			}

		}
		else
			beneficialWordsFizzle(mob,target,L("<S-NAME> @x1 to condemn <T-NAMESELF>, but nothing happens.",prayForWord(mob)));


		// return whether it worked
		return success;
	}
}
