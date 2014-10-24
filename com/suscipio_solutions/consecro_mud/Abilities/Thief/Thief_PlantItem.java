package com.suscipio_solutions.consecro_mud.Abilities.Thief;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Common.interfaces.PhyStats;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Item;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Wearable;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;


@SuppressWarnings("rawtypes")
public class Thief_PlantItem extends ThiefSkill
{
	@Override public String ID() { return "Thief_PlantItem"; }
	private final static String localizedName = CMLib.lang().L("Plant Item");
	@Override public String name() { return localizedName; }
	@Override protected int canAffectCode(){return 0;}
	@Override protected int canTargetCode(){return Ability.CAN_MOBS;}
	@Override public int abstractQuality(){return Ability.QUALITY_INDIFFERENT;}
	@Override public int classificationCode(){return Ability.ACODE_THIEF_SKILL|Ability.DOMAIN_STEALING;}
	private static final String[] triggerStrings =I(new String[] {"PLANTITEM"});
	@Override public String[] triggerStrings(){return triggerStrings;}
	@Override public int usageType(){return USAGE_MOVEMENT|USAGE_MANA;}
	public int code=0;

	@Override public int abilityCode(){return code;}
	@Override public void setAbilityCode(int newCode){code=newCode;}

	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		if(commands.size()<2)
		{
			mob.tell(L("What would you like to plant on whom?"));
			return false;
		}
		final MOB target=mob.location().fetchInhabitant((String)commands.lastElement());
		if((target==null)||(!CMLib.flags().canBeSeenBy(target,mob)))
		{
			mob.tell(L("You don't see '@x1' here.",(String)commands.lastElement()));
			return false;
		}
		if(target==mob)
		{
			mob.tell(L("You cannot plant anything on yourself!"));
			return false;
		}
		commands.removeElement(commands.lastElement());

		final Item item=super.getTarget(mob,null,givenTarget,commands,Wearable.FILTER_UNWORNONLY);
		if(item==null) return false;

		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;

		int levelDiff=target.phyStats().level()-(mob.phyStats().level()+abilityCode()+(getXLEVELLevel(mob)*2));
		if(levelDiff<0) levelDiff=0;
		levelDiff*=5;
		final boolean success=proficiencyCheck(mob,-levelDiff,auto);

		if(success)
		{
			final CMMsg msg=CMClass.getMsg(mob,target,item,CMMsg.MSG_GIVE,L("<S-NAME> plant(s) <O-NAME> on <T-NAMESELF>."),CMMsg.MASK_ALWAYS|CMMsg.MSG_GIVE,null,CMMsg.MASK_ALWAYS|CMMsg.MSG_GIVE,null);
			if(mob.location().okMessage(mob,msg))
			{
				mob.location().send(mob,msg);
				if(target.isMine(item))
				{
					item.basePhyStats().setDisposition(item.basePhyStats().disposition()|PhyStats.IS_HIDDEN);
					item.recoverPhyStats();
				}
			}
		}
		else
			beneficialVisualFizzle(mob,target,L("<S-NAME> attempt(s) to plant @x1 on <T-NAMESELF>, but fail(s).",item.name()));
		return success;
	}
}
