package com.suscipio_solutions.consecro_mud.Abilities.Common;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Items.interfaces.RawMaterial;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.Directions;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;
import com.suscipio_solutions.consecro_mud.core.interfaces.Tickable;



@SuppressWarnings("rawtypes")
public class Speculate extends CommonSkill
{
	@Override public String ID() { return "Speculate"; }
	private final static String localizedName = CMLib.lang().L("Speculating");
	@Override public String name() { return localizedName; }
	private static final String[] triggerStrings =I(new String[] {"SPECULATE","SPECULATING"});
	@Override public String[] triggerStrings(){return triggerStrings;}
	@Override public int classificationCode() {   return Ability.ACODE_COMMON_SKILL|Ability.DOMAIN_NATURELORE; }

	protected boolean success=false;
	public Speculate()
	{
		super();
		displayText=L("You are speculating...");
		verb=L("speculating");
	}

	@Override
	public boolean tick(Tickable ticking, int tickID)
	{
		if((affected!=null)&&(affected instanceof MOB)&&(tickID==Tickable.TICKID_MOB))
		{
			final MOB mob=(MOB)affected;
			if(tickUp==6)
			{
				if(success==false)
				{
					final StringBuffer str=new StringBuffer(L("Your speculate attempt failed.\n\r"));
					commonTell(mob,str.toString());
					unInvoke();
				}

			}
		}
		return super.tick(ticking,tickID);
	}

	@Override
	public void unInvoke()
	{
		if(canBeUninvoked())
		{
			if((affected!=null)&&(affected instanceof MOB))
			{
				final MOB mob=(MOB)affected;
				final Room room=mob.location();
				if((success)&&(!aborted)&&(room!=null))
				{
					int resource=room.myResource()&RawMaterial.RESOURCE_MASK;
					if(RawMaterial.CODES.IS_VALID(resource))
					{
						final StringBuffer str=new StringBuffer("");
						String resourceStr=RawMaterial.CODES.NAME(resource);
						str.append(L("You think this spot would be good for @x1.\n\r",resourceStr.toLowerCase()));
						for(int d=Directions.NUM_DIRECTIONS()-1;d>=0;d--)
						{
							final Room room2=room.getRoomInDir(d);
							if((room2!=null)
							&&(room.getExitInDir(d)!=null)
							&&(room.getExitInDir(d).isOpen()))
							{
								resource=room2.myResource()&RawMaterial.RESOURCE_MASK;
								if(RawMaterial.CODES.IS_VALID(resource))
								{
									resourceStr=RawMaterial.CODES.NAME(resource);
									str.append(L("There looks like @x1 @x2.\n\r",resourceStr.toLowerCase(),Directions.getInDirectionName(d)));
								}
							}
						}
						commonTell(mob,str.toString());
					}
					else
						commonTell(mob,L("You don't find any good resources around here."));
				}
			}
		}
		super.unInvoke();
	}


	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		if(super.checkStop(mob, commands))
			return true;
		verb=L("speculating");
		success=false;
		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;
		if(proficiencyCheck(mob,0,auto))
			success=true;
		final int duration=getDuration(45,mob,1,10);
		final CMMsg msg=CMClass.getMsg(mob,null,this,getActivityMessageType(),L("<S-NAME> start(s) speculating on this area."));
		if(mob.location().okMessage(mob,msg))
		{
			mob.location().send(mob,msg);
			beneficialAffect(mob,mob,asLevel,duration);
		}
		return true;
	}
}
