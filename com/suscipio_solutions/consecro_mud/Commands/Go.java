package com.suscipio_solutions.consecro_mud.Commands;
import java.util.List;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Commands.interfaces.Command;
import com.suscipio_solutions.consecro_mud.Exits.interfaces.Exit;
import com.suscipio_solutions.consecro_mud.Items.interfaces.SpaceShip;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMParms;
import com.suscipio_solutions.consecro_mud.core.CMProps;
import com.suscipio_solutions.consecro_mud.core.CMStrings;
import com.suscipio_solutions.consecro_mud.core.CMath;
import com.suscipio_solutions.consecro_mud.core.Directions;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;
import com.suscipio_solutions.consecro_mud.core.interfaces.Rideable;


@SuppressWarnings({"unchecked","rawtypes"})
public class Go extends StdCommand
{
	public Go(){}

	private final String[] access=I(new String[]{"GO","WALK"});
	@Override public String[] getAccessWords(){return access;}

	protected Command stander=null;
	protected Vector ifneccvec=null;
	public void standIfNecessary(MOB mob, int metaFlags)
		throws java.io.IOException
	{
		if((ifneccvec==null)||(ifneccvec.size()!=2))
		{
			ifneccvec=new Vector();
			ifneccvec.addElement("STAND");
			ifneccvec.addElement("IFNECESSARY");
		}
		if(stander==null) stander=CMClass.getCommand("Stand");
		if((stander!=null)&&(ifneccvec!=null))
			stander.execute(mob,ifneccvec,metaFlags);
	}

	@Override
	public boolean execute(MOB mob, Vector commands, int metaFlags)
		throws java.io.IOException
	{
		standIfNecessary(mob,metaFlags);
		if((commands.size()>3)
		&&(commands.firstElement() instanceof Integer))
		{
			return CMLib.tracking().walk(mob,
						((Integer)commands.elementAt(0)).intValue(),
						((Boolean)commands.elementAt(1)).booleanValue(),
						((Boolean)commands.elementAt(2)).booleanValue(),
						((Boolean)commands.elementAt(3)).booleanValue(),false);

		}
		final String whereStr=CMParms.combine(commands,1);
		final Room R=mob.location();
		if(R==null) return false;

		final boolean inAShip =(R instanceof SpaceShip)||(R.getArea() instanceof SpaceShip);
		final String validDirs = inAShip?Directions.SHIP_NAMES_LIST() : Directions.NAMES_LIST();

		int direction=-1;
		if(whereStr.equalsIgnoreCase("OUT"))
		{
			if(!CMath.bset(R.domainType(),Room.INDOORS))
			{
				mob.tell(L("You aren't indoors."));
				return false;
			}

			for(int d=Directions.NUM_DIRECTIONS()-1;d>=0;d--)
			{
				if((R.getExitInDir(d)!=null)
				&&(R.getRoomInDir(d)!=null)
				&&(!CMath.bset(R.getRoomInDir(d).domainType(),Room.INDOORS)))
				{
					if(direction>=0)
					{
						mob.tell(L("Which way out?  Try @x1.",validDirs));
						return false;
					}
					direction=d;
				}
			}
			if(direction<0)
			{
				mob.tell(L("There is no direct way out of this place.  Try a direction."));
				return false;
			}
		}
		if(direction<0)
		{
			if(mob.isMonster())
				direction=Directions.getGoodDirectionCode(whereStr);
			else
				direction=(inAShip)?Directions.getGoodShipDirectionCode(whereStr):Directions.getGoodCompassDirectionCode(whereStr);
		}
		if(direction<0)
		{
			final Environmental E=R.fetchFromRoomFavorItems(null,whereStr);
			if(E instanceof Rideable)
			{
				final Command C=CMClass.getCommand("Enter");
				return C.execute(mob,commands,metaFlags);
			}
			if(E instanceof Exit)
			{
				for(int d=Directions.NUM_DIRECTIONS()-1;d>=0;d--)
					if(R.getExitInDir(d)==E)
					{ direction=d; break;}
			}
		}
		final String doing=(String)commands.elementAt(0);
		if(direction>=0)
			CMLib.tracking().walk(mob,direction,false,false,false,false);
		else
		{
			boolean doneAnything=false;
			for(int v=1;v<commands.size();v++)
			{
				int num=1;
				String s=(String)commands.elementAt(v);
				if(CMath.s_int(s)>0)
				{
					num=CMath.s_int(s);
					v++;
					if(v<commands.size())
						s=(String)commands.elementAt(v);
				}
				else
				if((s.length()>0) && (Character.isDigit(s.charAt(0))))
				{
					int x=1;
					while((x<s.length()-1)&&(Character.isDigit(s.charAt(x))))
						x++;
					num=CMath.s_int(s.substring(0,x));
					s=s.substring(x);
				}

				if(mob.isMonster())
					direction=Directions.getGoodDirectionCode(s);
				else
					direction=(inAShip)?Directions.getGoodShipDirectionCode(s):Directions.getGoodCompassDirectionCode(s);
				if(direction>=0)
				{
					doneAnything=true;
					for(int i=0;i<num;i++)
					{
						if(mob.isMonster())
						{
							if(!CMLib.tracking().walk(mob,direction,false,false,false,false))
								return false;
						}
						else
						{
							final Vector V=new Vector();
							V.addElement(doing);
							V.addElement(inAShip?Directions.getShipDirectionName(direction):Directions.getDirectionName(direction));
							mob.enqueCommand(V,metaFlags,0);
						}
					}
				}
				else
					break;
			}
			if(!doneAnything)
				mob.tell(L("@x1 which direction?\n\rTry @x2.",CMStrings.capitalizeAndLower(doing),validDirs.toLowerCase()));
		}
		return false;
	}

	@Override
	public double actionsCost(final MOB mob, final List<String> cmds)
	{
		double cost=CMath.div(CMProps.getIntVar(CMProps.Int.DEFCMDTIME),100.0);
		if((mob!=null)&&(mob.isAttribute(MOB.Attrib.AUTORUN)))
			cost /= 4.0;
		return CMProps.getCommandActionCost(ID(), cost);
	}

	@Override public boolean canBeOrdered(){return true;}
}
