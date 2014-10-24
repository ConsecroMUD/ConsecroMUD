package com.suscipio_solutions.consecro_mud.Abilities.Skills;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CharStats;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Item;
import com.suscipio_solutions.consecro_mud.Items.interfaces.RawMaterial;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Scroll;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Wearable;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMParms;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;


@SuppressWarnings("rawtypes")
public class Skill_Write extends StdSkill
{
	@Override public String ID() { return "Skill_Write"; }
	private final static String localizedName = CMLib.lang().L("Write");
	@Override public String name() { return localizedName; }
	@Override protected int canAffectCode(){return 0;}
	@Override protected int canTargetCode(){return Ability.CAN_ITEMS;}
	@Override public int abstractQuality(){return Ability.QUALITY_INDIFFERENT;}
	private static final String[] triggerStrings =I(new String[] {"WRITE","WR"});
	@Override public String[] triggerStrings(){return triggerStrings;}
	@Override public int classificationCode(){return Ability.ACODE_SKILL|Ability.DOMAIN_CALLIGRAPHY;}
	@Override public int overrideMana(){return 0;}

	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		if(mob.charStats().getStat(CharStats.STAT_INTELLIGENCE)<5)
		{
			mob.tell(L("You are too stupid to actually write anything."));
			return false;
		}
		if(commands.size()<1)
		{
			mob.tell(L("What would you like to write on?"));
			return false;
		}
		Item target=mob.fetchItem(null,Wearable.FILTER_UNWORNONLY,(String)commands.elementAt(0));
		if(target==null)
		{
			target=mob.location().findItem(null,(String)commands.elementAt(0));
			if((target!=null)&&(CMLib.flags().isGettable(target)))
			{
				mob.tell(L("You don't have that."));
				return false;
			}
		}
		if((target==null)||(!CMLib.flags().canBeSeenBy(target,mob)))
		{
			mob.tell(L("You don't see '@x1' here.",((String)commands.elementAt(0))));
			return false;
		}

		final Item item=target;
		if(((item.material()!=RawMaterial.RESOURCE_PAPER)
		   &&(item.material()!=RawMaterial.RESOURCE_SILK)
		   &&(item.material()!=RawMaterial.RESOURCE_HIDE)
		   &&(item.material()!=RawMaterial.RESOURCE_HEMP))
		||(!item.isReadable()))
		{
			mob.tell(L("You can't write on that."));
			return false;
		}

		if(item instanceof Scroll)
		{
			mob.tell(L("You can't write on a scroll."));
			return false;
		}

		if(CMParms.combine(commands,1).toUpperCase().startsWith("FILE="))
		{
			mob.tell(L("You can't write that."));
			return false;
		}

		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;

		final boolean success=proficiencyCheck(mob,0,auto);

		if(success)
		{
			final CMMsg msg=CMClass.getMsg(mob,target,this,CMMsg.MSG_WRITE,L("<S-NAME> write(s) on <T-NAMESELF>."),CMMsg.MSG_WRITE,CMParms.combine(commands,1),CMMsg.MSG_WRITE,L("<S-NAME> write(s) on <T-NAMESELF>."));
			if(mob.location().okMessage(mob,msg))
				mob.location().send(mob,msg);
		}
		else
			mob.location().show(mob,target,CMMsg.MSG_OK_VISUAL,L("<S-NAME> attempt(s) to write on <T-NAMESELF>, but mess(es) up."));
		return success;
	}

}
