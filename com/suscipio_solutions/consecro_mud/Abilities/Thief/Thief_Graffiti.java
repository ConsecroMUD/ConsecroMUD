package com.suscipio_solutions.consecro_mud.Abilities.Thief;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Item;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMParms;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;


@SuppressWarnings("rawtypes")
public class Thief_Graffiti extends ThiefSkill
{
	@Override public String ID() { return "Thief_Graffiti"; }
	private final static String localizedName = CMLib.lang().L("Graffiti");
	@Override public String name() { return localizedName; }
	@Override protected int canAffectCode(){return 0;}
	@Override protected int canTargetCode(){return 0;}
	@Override public int abstractQuality(){return Ability.QUALITY_INDIFFERENT;}
	private static final String[] triggerStrings =I(new String[] {"GRAFFITI"});
	@Override public String[] triggerStrings(){return triggerStrings;}
	@Override public int classificationCode(){return Ability.ACODE_THIEF_SKILL|Ability.DOMAIN_STREETSMARTS;}

	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		final String str=CMParms.combine(commands,0);
		if(str.length()==0)
		{
			mob.tell(L("What would you like to write here?"));
			return false;
		}
		Room target=mob.location();
		if((auto)&&(givenTarget!=null)&&(givenTarget instanceof Room))
			target=(Room)givenTarget;

		if((mob.location().domainType()!=Room.DOMAIN_OUTDOORS_CITY)
		   &&(mob.location().domainType()!=Room.DOMAIN_INDOORS_WOOD)
		   &&(mob.location().domainType()!=Room.DOMAIN_INDOORS_STONE))
		{
			mob.tell(L("You can't put graffiti here."));
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
			final CMMsg msg=CMClass.getMsg(mob,target,this,CMMsg.MSG_DELICATE_SMALL_HANDS_ACT,L("<S-NAME> write(s) graffiti here."));
			if(mob.location().okMessage(mob,msg))
			{
				mob.location().send(mob,msg);
				final Item I=CMClass.getItem("GenWallpaper");
				I.setName(L("Graffiti"));
				CMLib.flags().setReadable(I,true);
				I.recoverPhyStats();
				I.setReadableText(str);
				switch(CMLib.dice().roll(1,6,0))
				{
				case 1:
					I.setDescription(L("Someone has scribbed some graffiti here.  Try reading it."));
					break;
				case 2:
					I.setDescription(L("A cryptic message has been written on the walls.  Try reading it."));
					break;
				case 3:
					I.setDescription(L("Someone wrote a message here to read."));
					break;
				case 4:
					I.setDescription(L("A strange message is written here.  Read it."));
					break;
				case 5:
					I.setDescription(L("This graffiti looks like it is in @x1 handwriting.  Read it!",mob.name()));
					break;
				case 6:
					I.setDescription(L("The wall is covered in graffiti.  You might want to read it."));
					break;
				}
				mob.location().addItem(I);
				I.recoverPhyStats();
				mob.location().recoverRoomStats();
			}
		}
		else
			beneficialVisualFizzle(mob,target,L("<S-NAME> attempt(s) to write graffiti here, but fails."));
		return success;
	}
}
