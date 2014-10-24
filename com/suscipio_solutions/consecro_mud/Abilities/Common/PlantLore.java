package com.suscipio_solutions.consecro_mud.Abilities.Common;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Items.interfaces.RawMaterial;
import com.suscipio_solutions.consecro_mud.Libraries.interfaces.TrackingLibrary;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.Directions;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;
import com.suscipio_solutions.consecro_mud.core.interfaces.Tickable;



@SuppressWarnings({"unchecked","rawtypes"})
public class PlantLore extends CommonSkill
{
	@Override public String ID() { return "PlantLore"; }
	private final static String localizedName = CMLib.lang().L("Plant Lore");
	@Override public String name() { return localizedName; }
	private static final String[] triggerStrings =I(new String[] {"PLANTLORE","PSPECULATE"});
	@Override public String[] triggerStrings(){return triggerStrings;}
	@Override public int classificationCode() {   return Ability.ACODE_COMMON_SKILL|Ability.DOMAIN_NATURELORE; }

	protected boolean success=false;
	public PlantLore()
	{
		super();
		displayText=L("You are observing plant growth...");
		verb=L("observing plant growths");
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
					final StringBuffer str=new StringBuffer(L("Your growth observation attempt failed.\n\r"));
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
			if((affected!=null)&&(affected instanceof MOB)&&(!helping))
			{
				final MOB mob=(MOB)affected;
				final Room room=mob.location();
				if((success)&&(!aborted)&&(room!=null))
				{
					if((room.domainType()&Room.INDOORS)==0)
					{
						final StringBuffer str=new StringBuffer("");
						final Vector V=new Vector();
						TrackingLibrary.TrackingFlags flags;
						flags = new TrackingLibrary.TrackingFlags()
								.plus(TrackingLibrary.TrackingFlag.OPENONLY)
								.plus(TrackingLibrary.TrackingFlag.AREAONLY)
								.plus(TrackingLibrary.TrackingFlag.NOAIR);
						CMLib.tracking().getRadiantRooms(room,V,flags,null,2+(getXLEVELLevel(mob)/2),null);
						for(int v=0;v<V.size();v++)
						{
							final Room R=(Room)V.elementAt(v);
							final int material=R.myResource()&RawMaterial.MATERIAL_MASK;
							final int resource=R.myResource()&RawMaterial.RESOURCE_MASK;
							if(!RawMaterial.CODES.IS_VALID(resource))
								continue;
							if((material!=RawMaterial.MATERIAL_VEGETATION)
							&&(resource!=RawMaterial.RESOURCE_COTTON)
							&&(resource!=RawMaterial.RESOURCE_HEMP)
							&&(resource!=RawMaterial.RESOURCE_SAP)
							&&(material!=RawMaterial.MATERIAL_WOODEN))
								continue;
							final String resourceStr=RawMaterial.CODES.NAME(resource);
							if(R==room)
								str.append(L("You think this spot would be good for @x1.\n\r",resourceStr.toLowerCase()));
							else
							{
								int isAdjacent=-1;
								for(int d=Directions.NUM_DIRECTIONS()-1;d>=0;d--)
								{
									final Room room2=room.getRoomInDir(d);
									if(room2==R) isAdjacent=d;
								}
								if(isAdjacent>=0)
									str.append(L("There looks like @x1 @x2.\n\r",resourceStr.toLowerCase(),Directions.getInDirectionName(isAdjacent)));
								else
								{
									int d=CMLib.tracking().radiatesFromDir(R,V);
									if(d>=0)
									{
										d=Directions.getOpDirectionCode(d);
										str.append(L("There looks like @x1 far @x2.\n\r",resourceStr.toLowerCase(),Directions.getInDirectionName(d)));
									}
								}

							}
						}
						commonTell(mob,str.toString());
					}
					else
						commonTell(mob,L("You don't find any good plant life around here."));
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
		verb=L("observing plant growth");
		success=false;
		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;
		if(proficiencyCheck(mob,0,auto))
			success=true;
		final int duration=getDuration(45,mob,1,5);
		final CMMsg msg=CMClass.getMsg(mob,null,this,getActivityMessageType(),L("<S-NAME> start(s) observing the growth in this area."));
		if(mob.location().okMessage(mob,msg))
		{
			mob.location().send(mob,msg);
			beneficialAffect(mob,mob,asLevel,duration);
		}
		return true;
	}
}
