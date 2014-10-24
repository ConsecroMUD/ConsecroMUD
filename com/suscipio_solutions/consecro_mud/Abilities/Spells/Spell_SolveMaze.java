package com.suscipio_solutions.consecro_mud.Abilities.Spells;
import java.util.List;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.GridLocale;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.Directions;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;


@SuppressWarnings("rawtypes")
public class Spell_SolveMaze extends Spell
{
	@Override public String ID() { return "Spell_SolveMaze"; }
	private final static String localizedName = CMLib.lang().L("Solve Maze");
	@Override public String name() { return localizedName; }
	@Override public int abstractQuality(){ return Ability.QUALITY_INDIFFERENT;}
	@Override protected int canTargetCode(){return 0;}
	@Override protected int canAffectCode(){return 0;}
	@Override public int classificationCode(){ return Ability.ACODE_SPELL|Ability.DOMAIN_DIVINATION;}

	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		final Room targetR=mob.location();
		if((targetR==null) || (targetR.getGridParent()==null))
		{
			mob.tell(L("This spell only works when you are in a maze"));
			return false;
		}

		final GridLocale grid = targetR.getGridParent();

		int direction=-1;
		Room outRoom=null;
		if((commands.size()>0)
		&&(commands.firstElement() instanceof String)
		&&(((String)commands.firstElement()).toLowerCase().startsWith("ma")))
			commands.remove(0);
		if(commands.size()==0)
		{
			final List<Integer> list=new Vector<Integer>();
			for(int d=0;d<Directions.NUM_DIRECTIONS();d++)
			{
				final Room R=grid.getRoomInDir(d);
				if((R!=null)&&(R.roomID().length()>0))
					list.add(Integer.valueOf(d));
			}
			if(list.size()==0)
			{
				for(int d=0;d<Directions.NUM_DIRECTIONS();d++)
				{
					final Room R=grid.getRoomInDir(d);
					if(R!=null)
						list.add(Integer.valueOf(d));
				}
			}
			if(list.size()>0)
			{
				direction=list.get(CMLib.dice().roll(1, list.size(), -1)).intValue();
				outRoom=grid.getRoomInDir(direction);
			}
		}
		else
		{
			direction=Directions.getDirectionCode(commands.firstElement().toString());
			if(direction>=0)
				outRoom=grid.getRoomInDir(direction);
		}

		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;

		final boolean success=proficiencyCheck(mob,0,auto);
		if(success && (outRoom !=null) )
		{
			final CMMsg msg=CMClass.getMsg(mob,targetR,this,somanticCastCode(mob,targetR,auto),auto?"":L("^S<S-NAME> wave(s) <S-HIS-HER> hands around, pointing in different directions.^?"));
			if(mob.location().okMessage(mob,msg))
			{
				mob.location().send(mob,msg);
				if(outRoom instanceof GridLocale)
					outRoom=((GridLocale)outRoom).prepareGridLocale(targetR,outRoom, direction);
				final int radius = (grid.xGridSize()*grid.yGridSize())+2;
				mob.tell(L("The directions are taking shape in your mind: \n\r@x1",CMLib.tracking().getTrailToDescription(targetR, new Vector<Room>(), CMLib.map().getExtendedRoomID(outRoom), false, false, radius, null,1)));
			}
		}
		else
			beneficialVisualFizzle(mob,targetR,L("<S-NAME> wave(s) <S-HIS-HER> hands around, looking more frustrated every second."));


		// return whether it worked
		return success;
	}
}
