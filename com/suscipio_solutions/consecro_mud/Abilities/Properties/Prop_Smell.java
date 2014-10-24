package com.suscipio_solutions.consecro_mud.Abilities.Properties;
import java.util.List;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Exits.interfaces.Exit;
import com.suscipio_solutions.consecro_mud.Items.interfaces.SpaceShip;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMParms;
import com.suscipio_solutions.consecro_mud.core.CMath;
import com.suscipio_solutions.consecro_mud.core.Directions;
import com.suscipio_solutions.consecro_mud.core.collections.DVector;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;
import com.suscipio_solutions.consecro_mud.core.interfaces.Tickable;


@SuppressWarnings("rawtypes")
public class Prop_Smell extends Property
{
	@Override public String ID() { return "Prop_Smell"; }
	@Override public String name(){ return "A Smell";}
	@Override protected int canAffectCode(){return Ability.CAN_ROOMS|Ability.CAN_AREAS|Ability.CAN_EXITS|Ability.CAN_ITEMS;}
	protected DVector smells=null;
	protected final static int FLAG_EMOTE=512;
	protected final static int FLAG_BROADCAST=1024;
	protected boolean lastWasBroadcast=false;

	@Override public String accountForYourself(){ return "";	}
	@Override
	public void setMiscText(String newStr)
	{
		if(newStr.startsWith("+"))
		{
			if(text().indexOf(newStr.substring(1).trim())>=0)
				return;
			super.setMiscText(text()+";"+newStr.substring(1).trim());
			smells=null;
		}
		else
		if(newStr.startsWith("-"))
		{
			final int x=text().indexOf(newStr.substring(1).trim());
			if(x>=0)
			{
				final int len=newStr.substring(1).trim().length();
				super.setMiscText(text().substring(0,x)+text().substring(x+len));
				smells=null;
			}
			else
				return;
		}
		else
		{
			super.setMiscText(newStr);
			smells=null;
		}
	}
	public DVector getSmells()
	{
		if(smells!=null) return smells;
		final List<String> allsmells=CMParms.parseSemicolons(text(),true);
		smells=new DVector(3);
		for(int i=0;i<allsmells.size();i++)
		{
			final String smell=allsmells.get(i);
			if(smell.length()>0)
			{
				int pct=100;
				int ticks=-1;
				final Vector parsedSmell=CMParms.parse(smell);
				for(int ii=parsedSmell.size()-1;ii>=0;ii--)
				{
					final String s=((String)parsedSmell.elementAt(ii)).toUpperCase();
					if(s.startsWith("TICKS="))
					{
						ticks=CMath.s_int(s.substring(6).trim());
						parsedSmell.removeElementAt(ii);
					}
					if(s.startsWith("CHANCE="))
					{
						pct=(pct&(FLAG_BROADCAST+FLAG_EMOTE))+CMath.s_int(s.substring(5).trim());
						parsedSmell.removeElementAt(ii);
					}
					if(s.equals("EMOTE"))
					{
						pct=pct&FLAG_EMOTE;
						parsedSmell.removeElementAt(ii);
					}
					if(s.equals("BROADCAST"))
					{
						pct=pct&FLAG_EMOTE;
						parsedSmell.removeElementAt(ii);
					}
				}
				final String finalSmell=CMParms.combine(parsedSmell,0).trim();
				if(finalSmell.length()>0)
					smells.addElement(finalSmell,Integer.valueOf(pct),Integer.valueOf(ticks));
			}
		}
		return smells;
	}

	public String selectSmell(boolean emoteOnly)
	{
		lastWasBroadcast=false;
		getSmells();
		if((smells!=null)&&(smells.size()>0))
		{
			int total=0;
			for(int i=0;i<smells.size();i++)
			{
				final int pct=((Integer)smells.elementAt(i,2)).intValue();
				if((!emoteOnly)||(CMath.bset(pct,FLAG_EMOTE)))
					total+=pct&511;
			}
			if(total==0) return "";
			int draw=CMLib.dice().roll(1,total,0);
			for(int i=0;i<smells.size();i++)
			{
				final int pct=((Integer)smells.elementAt(i,2)).intValue();
				if((!emoteOnly)||(CMath.bset(pct,FLAG_EMOTE)))
				{
					draw-=pct&511;
					if(draw<=0)
					{
						lastWasBroadcast=CMath.bset(pct,FLAG_BROADCAST);
						return (String)smells.elementAt(i,1);
					}
				}
			}
		}
		return "";
	}

	@Override
	public void executeMsg(final Environmental myHost, final CMMsg msg)
	{
		super.executeMsg(myHost,msg);
		if((msg.amITarget(affected))
		&&(msg.targetMinor()==CMMsg.TYP_SNIFF)
		&&(CMLib.flags().canSmell(msg.source())))
			msg.source().tell(msg.source(),affected,null,selectSmell(false));
	}

	public void emoteHere(Room room, MOB emoter, String str)
	{
		final CMMsg msg=CMClass.getMsg(emoter,null,CMMsg.MSG_EMOTE,str);
		if(room.okMessage(emoter,msg))
		for(int i=0;i<room.numInhabitants();i++)
		{
			final MOB M=room.fetchInhabitant(i);
			if((M!=null)&&(!M.isMonster())&&(CMLib.flags().canSmell(M)))
				M.executeMsg(M,msg);
		}
	}

	@Override
	public boolean tick(Tickable ticking, int tickID)
	{
		if((affected instanceof MOB)&&(CMLib.dice().rollPercentage()<=20))
		{
			final String emote=selectSmell(true);
			if((emote!=null)&&(emote.length()>0))
			{
				final Room room=CMLib.map().roomLocation(affected);
				if(room!=null)
				{
					emoteHere(room,(MOB)affected,emote);
					if(lastWasBroadcast)
					{
						final MOB emoter=CMClass.getFactoryMOB();
						for(int d=Directions.NUM_DIRECTIONS()-1;d>=0;d--)
						{
							final Room R=room.getRoomInDir(d);
							final Exit E=room.getExitInDir(d);
							if((R!=null)&&(E!=null)&&(E.isOpen()))
							{
								emoter.setLocation(R);
								final String inDir=((R instanceof SpaceShip)||(R.getArea() instanceof SpaceShip))?
										Directions.getShipInDirectionName(Directions.getOpDirectionCode(d)):
											Directions.getInDirectionName(Directions.getOpDirectionCode(d));
								emoter.setName(L("something @x1",inDir));
								emoteHere(R,emoter,emote);
							}
						}
						emoter.destroy();
					}
				}
			}
			final DVector sm=getSmells();
			boolean redo=false;
			for(int i=sm.size()-1;i>=0;i--)
			{
				if(((Integer)sm.elementAt(i,3)).intValue()>0)
				{
					final Integer I=Integer.valueOf(((Integer)smells.elementAt(i,3)).intValue()-1);
					if(I.intValue()>0)
					{
						final String smell=(String)sm.elementAt(i,1);
						final Integer pct=(Integer)sm.elementAt(i,2);
						sm.addElement(smell,pct,I);
					}
					sm.removeElementAt(i);
					if(I.intValue()<=0) redo=true;
				}
			}
			if(redo)
			{
				final StringBuffer newText=new StringBuffer("");
				for(int i=0;i<sm.size();i++)
				{
					final String smell=(String)sm.elementAt(i,1);
					final Integer pct=(Integer)sm.elementAt(i,2);
					final Integer ticks=(Integer)sm.elementAt(i,3);
					if(ticks.intValue()>0)
						newText.append("TICKS="+ticks+" ");
					if(CMath.bset(pct.intValue(),FLAG_EMOTE))
						newText.append("EMOTE ");
					if(CMath.bset(pct.intValue(),FLAG_BROADCAST))
						newText.append("BROADCAST ");
					if((pct.intValue()&511)!=100)
						newText.append("CHANCE="+(pct.intValue()&511)+" ");
					newText.append(smell+";");
				}
				if(newText.length()==0)
					affected.delEffect(this);
				else
					setMiscText(newText.toString());
			}
		}
		return super.tick(ticking,tickID);
	}
}

