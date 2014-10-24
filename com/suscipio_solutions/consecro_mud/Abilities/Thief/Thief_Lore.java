package com.suscipio_solutions.consecro_mud.Abilities.Thief;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Item;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Wearable;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;


@SuppressWarnings("rawtypes")
public class Thief_Lore extends ThiefSkill
{
	@Override public String ID() { return "Thief_Lore"; }
	private final static String localizedName = CMLib.lang().L("Lore");
	@Override public String name() { return localizedName; }
	@Override protected int canAffectCode(){return 0;}
	@Override protected int canTargetCode(){return Ability.CAN_ITEMS;}
	@Override public int abstractQuality(){return Ability.QUALITY_INDIFFERENT;}
	private static final String[] triggerStrings =I(new String[] {"LORE"});
	@Override public String[] triggerStrings(){return triggerStrings;}
	@Override protected boolean disregardsArmorCheck(MOB mob){return true;}
	@Override public int classificationCode(){return Ability.ACODE_THIEF_SKILL|Ability.DOMAIN_ARCANELORE;}

	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		final Item target=getTarget(mob,mob.location(),givenTarget,commands,Wearable.FILTER_ANY);
		if(target==null) return false;

		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;

		int levelDiff=target.phyStats().level()-(mob.phyStats().level()+abilityCode()+(2*super.getXLEVELLevel(mob)));
		if(levelDiff<0) levelDiff=0;
		levelDiff*=5;
		final boolean success=proficiencyCheck(mob,-levelDiff,auto);

		if(success)
		{
			final CMMsg msg=CMClass.getMsg(mob,target,this,CMMsg.MSG_DELICATE_HANDS_ACT,auto?"":L("<S-NAME> stud(ys) <T-NAMESELF> and consider(s) for a moment."));
			if(mob.location().okMessage(mob,msg))
			{
				mob.location().send(mob,msg);
				final String identity=target.secretIdentity();
				mob.tell(identity);

			}
		}
		else
			beneficialVisualFizzle(mob,target,L("<S-NAME> stud(ys) <T-NAMESELF>, but can't remember a thing."));
		return success;
	}
}
