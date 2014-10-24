package com.suscipio_solutions.consecro_mud.Commands;
import java.util.Set;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Exits.interfaces.Exit;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMParms;
import com.suscipio_solutions.consecro_mud.core.CMProps;
import com.suscipio_solutions.consecro_mud.core.CMath;
import com.suscipio_solutions.consecro_mud.core.Directions;


@SuppressWarnings({"unchecked","rawtypes"})
public class Flee extends Go
{
	public Flee(){}

	private final String[] access=I(new String[]{"FLEE"});
	@Override public String[] getAccessWords(){return access;}
	@Override
	public boolean execute(MOB mob, Vector commands, int metaFlags)
		throws java.io.IOException
	{
		String direction="";
		if(commands.size()>1) direction=CMParms.combine(commands,1);
		if(mob==null) return false;
		final Room R=mob.location();
		if(R==null) return false;
		if((!mob.isMonster())||(mob.amFollowing()!=null))
		{
			if(!mob.isInCombat())
			{
				mob.tell(L("You can only flee while in combat."));
				return false;
			}
		}

		boolean XPloss=true;
		final MOB fighting=mob.getVictim();
		if(fighting!=null)
		{
			final Set<MOB> H=CMLib.combat().allCombatants(mob);
			for (final Object element : H)
			{
				final MOB M=(MOB)element;
				if(CMLib.flags().aliveAwakeMobileUnbound(M,true))
				{
					XPloss=true;
					break;
				}
				XPloss=false;
			}
		}

		if((!XPloss)&&(direction.length()==0))
		{
			mob.tell(L("You stop fighting."));
			direction="NOWHERE";
		}

		int directionCode=-1;
		if(!direction.equals("NOWHERE"))
		{
			if(direction.length()==0)
			{
				final Vector directions=new Vector();
				for(int d=Directions.NUM_DIRECTIONS()-1;d>=0;d--)
				{
					final Exit thisExit=R.getExitInDir(d);
					final Room thisRoom=R.getRoomInDir(d);
					if((thisRoom!=null)&&(thisExit!=null)&&(thisExit.isOpen()))
						directions.addElement(Integer.valueOf(d));
				}
				// up is last resort
				if(directions.size()>1)
					directions.removeElement(Integer.valueOf(Directions.UP));
				if(directions.size()>0)
				{
					directionCode=((Integer)directions.elementAt(CMLib.dice().roll(1,directions.size(),-1))).intValue();
					direction=Directions.getDirectionName(directionCode);
				}
			}
			else
				directionCode=Directions.getGoodDirectionCode(direction);
			if(directionCode<0)
			{
				mob.tell(L("Flee where?!"));
				return false;
			}
		}
		if((direction.equals("NOWHERE"))||((directionCode>=0)&&(CMLib.tracking().walk(mob,directionCode,true,false,false))))
		{
			mob.makePeace();
			if(XPloss&&(fighting!=null))
			{
				final String whatToDo=CMProps.getVar(CMProps.Str.PLAYERFLEE);
				if(whatToDo==null) return false;
				final int[] expLost={10+((mob.phyStats().level()-fighting.phyStats().level()))*5};
				if(expLost[0]<10) expLost[0]=10;
				final String[] cmds=CMParms.toStringArray(CMParms.parseCommas(whatToDo,true));
				CMLib.combat().handleConsequences(mob,fighting,cmds,expLost,"You lose @x1 experience points for withdrawing.");
				final double pctHPremaining=CMath.div(mob.curState().getHitPoints(),mob.maxState().getHitPoints());
				if(expLost[0]>0)
				{
					final int gainedExperience=(int)Math.round(CMath.mul(expLost[0],1.0-pctHPremaining))/4;
					if((fighting!=mob)
					&&(gainedExperience>0)
					&&((mob.session()==null)
					   ||(fighting.session()==null)
					   ||(!mob.session().getAddress().equals(fighting.session().getAddress()))))
							CMLib.leveler().postExperience(fighting,null,null,gainedExperience,false);
				}
			}
		}
		return false;
	}

	@Override public boolean canBeOrdered(){return true;}


}
