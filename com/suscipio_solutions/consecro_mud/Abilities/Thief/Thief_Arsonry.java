package com.suscipio_solutions.consecro_mud.Abilities.Thief;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Items.interfaces.ClanItem;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Item;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Wearable;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMParms;
import com.suscipio_solutions.consecro_mud.core.Directions;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;


@SuppressWarnings({"unchecked","rawtypes"})
public class Thief_Arsonry extends ThiefSkill
{
	@Override public String ID() { return "Thief_Arsonry"; }
	private final static String localizedName = CMLib.lang().L("Arsonry");
	@Override public String name() { return localizedName; }
	@Override protected int canAffectCode(){return 0;}
	@Override protected int canTargetCode(){return Ability.CAN_ITEMS;}
	@Override public int abstractQuality(){return Ability.QUALITY_INDIFFERENT;}
	private static final String[] triggerStrings =I(new String[] {"ARSON","ARSONRY"});
	@Override public int classificationCode() {   return Ability.ACODE_SKILL|Ability.DOMAIN_CRIMINAL; }
	@Override public String[] triggerStrings(){return triggerStrings;}

	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		if(commands.size()<1)
		{
			mob.tell(L("What or which direction is that which would you like to set on fire?"));
			return false;
		}
		final String str=CMParms.combine(commands,0);
		final int dir=Directions.getGoodDirectionCode(str);
		Room targetRoom=null;
		Physical target=null;
		if(dir>=0)
		{
			final Room room=mob.location().getRoomInDir(dir);
			if((room==null)||(mob.location().getExitInDir(dir)==null))
			{
				mob.tell(L("But there's nothing that way!"));
				return false;
			}
			if(!mob.location().getExitInDir(dir).isOpen())
			{
				mob.tell(L("That way isn't open!"));
				return false;
			}
			final Vector choices=new Vector();
			for(int i=0;i<room.numItems();i++)
			{
				final Item I=room.getItem(i);
				if((I!=null)
				&&(I.container()==null)
				&&(I.displayText().length()==0)
				&&(CMLib.flags().isGettable(I))
				&&(!(I instanceof ClanItem))
				&&(CMLib.flags().burnStatus(I)>0))
					choices.addElement(I);
			}
			if(choices.size()==0)
			{
				mob.tell(L("There's nothing that way you can burn!"));
				return false;
			}
			target=(Item)choices.elementAt(CMLib.dice().roll(1,choices.size(),-1));
			targetRoom=room;
		}
		else
		{
			final Item item=getTarget(mob,mob.location(),givenTarget,null,commands,Wearable.FILTER_UNWORNONLY);
			if(item==null) return false;
			target=item;
			targetRoom=mob.location();
		}
		boolean proceed=false;
		for(int i=0;i<mob.numItems();i++)
		{
			final Item I=mob.getItem(i);
			if((I!=null)&&(CMLib.flags().isOnFire(I))&&(CMLib.flags().canBeSeenBy(I,mob)))
			{ proceed=true; break;}
		}
		if(!proceed)
		{
			mob.tell(L("You need to have something in your inventory on fire, like a torch, to use this skill."));
			return false;
		}
		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;

		int levelDiff=target.phyStats().level()-(mob.phyStats().level()+abilityCode()+(2*super.getXLEVELLevel(mob)));
		if(levelDiff<0) levelDiff=0;
		levelDiff*=5;
		final boolean success=proficiencyCheck(mob,-levelDiff,auto);
		if(success)
		{
			final CMMsg msg=CMClass.getMsg(mob,target,this,CMMsg.MSG_DELICATE_SMALL_HANDS_ACT,L("<S-NAME> commit(s) arsonry against <T-NAME>."));
			if((mob.location().okMessage(mob,msg))
			&&((targetRoom==mob.location())||(targetRoom.okMessage(mob,msg))))
			{
				mob.location().send(mob,msg);
				if(targetRoom!=mob.location()) targetRoom.sendOthers(mob,msg);
				final Ability B=CMClass.getAbility("Burning");
				if(B!=null)
					B.invoke(mob,target,true,CMLib.flags().burnStatus(target));
			}
		}
		else
			beneficialVisualFizzle(mob,target,L("<S-NAME> attempt(s) arsonry against <T-NAME>, but fails."));
		return success;
	}

}
