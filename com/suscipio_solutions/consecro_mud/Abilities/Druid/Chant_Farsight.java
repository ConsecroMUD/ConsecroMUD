package com.suscipio_solutions.consecro_mud.Abilities.Druid;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Exits.interfaces.Exit;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMStrings;
import com.suscipio_solutions.consecro_mud.core.Directions;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;



@SuppressWarnings("rawtypes")
public class Chant_Farsight extends Chant
{
	@Override public String ID() { return "Chant_Farsight"; }
	private final static String localizedName = CMLib.lang().L("Eaglesight");
	@Override public String name() { return localizedName; }
	@Override public int classificationCode(){return Ability.ACODE_CHANT|Ability.DOMAIN_ENDURING;}
	@Override public int abstractQuality(){return Ability.QUALITY_INDIFFERENT;}
	@Override protected int canAffectCode(){return 0;}
	@Override protected int canTargetCode(){return 0;}

	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		if((mob.location().domainType()&Room.INDOORS)>0)
		{
			mob.tell(L("You must be outdoors for this chant to work."));
			return false;
		}
		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;


		boolean success=proficiencyCheck(mob,0,auto);

		if(!success)
			this.beneficialVisualFizzle(mob,null,L("<S-NAME> chant(s) for a far off vision, but the magic fades."));
		else
		{
			final CMMsg msg=CMClass.getMsg(mob,null,this,verbalCastCode(mob,null,auto),L("^S<S-NAME> chant(s) for a far off vision.^?"));
			if(mob.location().okMessage(mob,msg))
			{
				mob.location().send(mob,msg);
				Room thatRoom=mob.location();
				int limit=(mob.phyStats().level()+(2*super.getXLEVELLevel(mob)))/3;
				if(limit<0) limit=1;
				if(commands.size()==0)
				{
					for(int d=Directions.NUM_DIRECTIONS()-1;d>=0;d--)
					{
						Exit exit=thatRoom.getExitInDir(d);
						Room room=thatRoom.getRoomInDir(d);

						if((exit!=null)&&(room!=null)&&(CMLib.flags().canBeSeenBy(exit,mob)&&(exit.isOpen())))
						{
							mob.tell("^D" + CMStrings.padRight(Directions.getDirectionName(d),5)+":^.^N ^d"+exit.viewableText(mob, room)+"^N");
							exit=room.getExitInDir(d);
							room=room.getRoomInDir(d);
							if((exit!=null)&&(room!=null)&&(CMLib.flags().canBeSeenBy(exit,mob)&&(exit.isOpen())))
							{
								mob.tell(CMStrings.padRight("",5)+":^N ^d"+exit.viewableText(mob, room)+"^N");
								exit=room.getExitInDir(d);
								room=room.getRoomInDir(d);
								if((exit!=null)&&(room!=null)&&(CMLib.flags().canBeSeenBy(exit,mob)&&(exit.isOpen())))
								{
									mob.tell(CMStrings.padRight("",5)+":^N ^d"+exit.viewableText(mob, room)+"^N");
								}
							}
						}
					}
				}
				else
				while(commands.size()>0)
				{
					final String whatToOpen=(String)commands.elementAt(0);
					final int dirCode=Directions.getGoodDirectionCode(whatToOpen);
					if(limit<=0)
					{
						mob.tell(L("Your sight has reached its limit."));
						success=true;
						break;
					}
					else
					if(dirCode<0)
					{
						mob.tell(L("\n\r'@x1' is not a valid direction.",whatToOpen));
						commands.removeAllElements();
						success=false;
					}
					else
					{
						final Exit exit=thatRoom.getExitInDir(dirCode);
						final Room room=thatRoom.getRoomInDir(dirCode);

						if((exit==null)||(room==null)||(!CMLib.flags().canBeSeenBy(exit,mob))||(!exit.isOpen()))
						{
							mob.tell(L("\n\rSomething has obstructed your vision."));
							success=false;
							commands.removeAllElements();
						}
						else
						{
							commands.removeElementAt(0);
							thatRoom=room;
							limit--;
							mob.tell(L("\n\r"));
							final CMMsg msg2=CMClass.getMsg(mob,thatRoom,CMMsg.MSG_LOOK,null);
							thatRoom.executeMsg(mob,msg2);
						}
					}
				}
			}
		}

		return success;
	}
}
