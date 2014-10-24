package com.suscipio_solutions.consecro_mud.Abilities.Skills;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Commands.interfaces.Command;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CharStats;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMStrings;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;


@SuppressWarnings({"unchecked","rawtypes"})
public class Skill_Haggle extends StdSkill
{
	@Override public String ID() { return "Skill_Haggle"; }
	private final static String localizedName = CMLib.lang().L("Haggle");
	@Override public String name() { return localizedName; }
	@Override protected int canAffectCode(){return CAN_MOBS;}
	@Override protected int canTargetCode(){return CAN_MOBS;}
	@Override public int abstractQuality(){return Ability.QUALITY_INDIFFERENT;}
	private static final String[] triggerStrings =I(new String[] {"HAGGLE"});
	@Override public String[] triggerStrings(){return triggerStrings;}
	@Override public int classificationCode(){return Ability.ACODE_SKILL|Ability.DOMAIN_INFLUENTIAL;}

	@Override
	public void affectCharStats(MOB affectedMOB, CharStats affectableStats)
	{
		super.affectCharStats(affectedMOB,affectableStats);
		affectableStats.setStat(CharStats.STAT_CHARISMA,affectableStats.getStat(CharStats.STAT_CHARISMA)+10+getXLEVELLevel(invoker()));
	}


	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		String cmd="";
		if(commands.size()>0)
			cmd=((String)commands.firstElement()).toUpperCase();

		if((commands.size()<2)||((!cmd.equals("BUY")&&(!cmd.equals("SELL")))))
		{
			mob.tell(L("You must specify BUY, SELL, an item, and possibly a ShopKeeper (unless it is implied)."));
			return false;
		}

		final Environmental shopkeeper=CMLib.english().parseShopkeeper(mob,commands,CMStrings.capitalizeAndLower(cmd)+" what to whom?");
		if(shopkeeper==null) return false;
		if(commands.size()==0)
		{
			mob.tell(L("@x1 what?",CMStrings.capitalizeAndLower(cmd)));
			return false;
		}

		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;

		final boolean success=proficiencyCheck(mob,0,auto);
		if(success)
		{
			final CMMsg msg=CMClass.getMsg(mob,shopkeeper,this,CMMsg.MSG_SPEAK,auto?"":L("<S-NAME> haggle(s) with <T-NAMESELF>."));
			if(mob.location().okMessage(mob,msg))
			{
				mob.location().send(mob,msg);
				invoker=mob;
				mob.addEffect(this);
				mob.recoverCharStats();
				commands.insertElementAt(CMStrings.capitalizeAndLower(cmd),0);
				mob.doCommand(commands,Command.METAFLAG_FORCED);
				commands.addElement(shopkeeper.name());
				mob.delEffect(this);
				mob.recoverCharStats();
			}
		}
		else
			beneficialWordsFizzle(mob,shopkeeper,L("<S-NAME> haggle(s) with <T-NAMESELF>, but <S-IS-ARE> unconvincing."));

		// return whether it worked
		return success;
	}
}
