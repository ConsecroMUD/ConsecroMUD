package com.suscipio_solutions.consecro_mud.Abilities.Prayers;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Items.interfaces.DeadBody;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Item;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Wearable;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMProps;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;



@SuppressWarnings("rawtypes")
public class Prayer_Desecrate extends Prayer
{
	@Override public String ID() { return "Prayer_Desecrate"; }
	private final static String localizedName = CMLib.lang().L("Desecrate");
	@Override public String name() { return localizedName; }
	@Override public int classificationCode(){return Ability.ACODE_PRAYER|Ability.DOMAIN_DEATHLORE;}
	@Override public int abstractQuality(){ return Ability.QUALITY_INDIFFERENT;}
	@Override public long flags(){return Ability.FLAG_UNHOLY;}
	@Override protected int canTargetCode(){return Ability.CAN_ITEMS;}

	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		Item target=null;
		if((commands.size()==0)&&(!auto)&&(givenTarget==null))
			target=Prayer_Sacrifice.getBody(mob.location());
		if(target==null)
			target=getTarget(mob,mob.location(),givenTarget,commands,Wearable.FILTER_UNWORNONLY);
		if(target==null) return false;

		if((!(target instanceof DeadBody))
		   ||(target.rawSecretIdentity().toUpperCase().indexOf("FAKE")>=0))
		{
			mob.tell(L("You may only desecrate the dead."));
			return false;
		}
		if((((DeadBody)target).playerCorpse())
		&&(!((DeadBody)target).mobName().equals(mob.Name()))
		&&(((DeadBody)target).getContents().size()>0))
		{
			mob.tell(L("You are not allowed to desecrate a players corpse."));
			return false;
		}

		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;

		final boolean success=proficiencyCheck(mob,0,auto);

		if(success)
		{
			// it worked, so build a copy of this ability,
			// and add it to the affects list of the
			// affected MOB.  Then tell everyone else
			// what happened.
			final CMMsg msg=CMClass.getMsg(mob,target,this,verbalCastCode(mob,target,auto),auto?L("<T-NAME> feel(s) desecrated!"):L("^S<S-NAME> desecrate(s) <T-NAMESELF> before @x1.^?",hisHerDiety(mob)));
			if(mob.location().okMessage(mob,msg))
			{
				mob.location().send(mob,msg);
				if(CMLib.flags().isEvil(mob))
				{
					double exp=5.0;
					final int levelLimit=CMProps.getIntVar(CMProps.Int.EXPRATE);
					final int levelDiff=(mob.phyStats().level())-target.phyStats().level();
					if(levelDiff>levelLimit) exp=0.0;
					if(exp>0.0)
						CMLib.leveler().postExperience(mob,null,null,(int)Math.round(exp)+super.getXPCOSTLevel(mob),false);
				}
				target.destroy();
				mob.location().recoverRoomStats();
			}
		}
		else
			beneficialWordsFizzle(mob,target,L("<S-NAME> attempt(s) to desecrate <T-NAMESELF>, but fail(s)."));

		// return whether it worked
		return success;
	}
}
