package com.suscipio_solutions.consecro_mud.Abilities.Properties;
import java.util.List;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CharStats;
import com.suscipio_solutions.consecro_mud.Common.interfaces.PhyStats;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMParms;
import com.suscipio_solutions.consecro_mud.core.CMSecurity;
import com.suscipio_solutions.consecro_mud.core.Directions;
import com.suscipio_solutions.consecro_mud.core.collections.XVector;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;
import com.suscipio_solutions.consecro_mud.core.interfaces.Tickable;


@SuppressWarnings({"unchecked","rawtypes"})
public class Prop_NarrowLedge extends Property
{
	@Override public String ID() { return "Prop_NarrowLedge"; }
	@Override public String name(){ return "The Narrow Ledge";}
	@Override protected int canAffectCode(){return Ability.CAN_ROOMS|Ability.CAN_EXITS;}

	protected int check=16;
	protected String name="the narrow ledge";
	protected List<MOB> mobsToKill=new Vector();

	@Override
	public String accountForYourself()
	{ return "Very narrow";	}

	@Override
	public void setMiscText(String newText)
	{
		mobsToKill=new Vector();
		super.setMiscText(newText);
		check=CMParms.getParmInt(newText,"check",16);
		name=CMParms.getParmStr(newText,"name","the narrow ledge");
	}

	@Override
	public boolean tick(Tickable ticking, int tickID)
	{
		if(tickID==Tickable.TICKID_SPELL_AFFECT)
		{
			synchronized(mobsToKill)
			{
				CMLib.threads().deleteTick(this,Tickable.TICKID_SPELL_AFFECT);
				final List<MOB> V=new XVector<MOB>(mobsToKill);
				mobsToKill.clear();
				for(int v=0;v<V.size();v++)
				{
					final MOB mob=V.get(v);
					if(mob.location()!=null)
					{
						if((affected instanceof Room)&&(mob.location()!=affected))
							continue;

						if((affected instanceof Room)
						&&((((Room)affected).domainType()==Room.DOMAIN_INDOORS_AIR)
						   ||(((Room)affected).domainType()==Room.DOMAIN_OUTDOORS_AIR))
						&&(((Room)affected).getRoomInDir(Directions.DOWN)!=null)
						&&(((Room)affected).getExitInDir(Directions.DOWN)!=null)
						&&(((Room)affected).getExitInDir(Directions.DOWN).isOpen()))
							mob.location().show(mob,null,CMMsg.MSG_OK_ACTION,L("<S-NAME> fall(s) off @x1!!",name));
						else
						{
							mob.location().show(mob,null,CMMsg.MSG_OK_ACTION,L("<S-NAME> fall(s) off @x1 to <S-HIS-HER> death!!",name));
							if(!CMSecurity.isAllowed(mob,mob.location(),CMSecurity.SecFlag.IMMORT))
								mob.location().show(mob,null,CMMsg.MSG_DEATH,null);
						}
					}
				}
			}
		}
		return true;
	}
	@Override
	public void executeMsg(final Environmental myHost, final CMMsg msg)
	{
		if((msg.targetMinor()==CMMsg.TYP_ENTER)
		&&((msg.amITarget(affected))||(msg.tool()==affected))
		&&(!CMLib.flags().isFalling(msg.source())))
		{
			final MOB mob=msg.source();
			if((!CMLib.flags().isInFlight(mob))
			&&(CMLib.dice().roll(1,check,-mob.charStats().getStat(CharStats.STAT_DEXTERITY))>0))
			{
				synchronized(mobsToKill)
				{
					if(!mobsToKill.contains(mob))
					{
						mobsToKill.add(mob);
						final Ability falling=CMClass.getAbility("Falling");
						falling.setProficiency(0);
						falling.setAffectedOne(affected);
						falling.invoke(null,null,mob,true,0);
						CMLib.threads().startTickDown(this,Tickable.TICKID_SPELL_AFFECT,1);
					}
				}
			}
		}
		super.executeMsg(myHost,msg);
	}
	@Override
	public void affectPhyStats(Physical affected, PhyStats affectableStats)
	{
		// always disable flying restrictions!
		affectableStats.setDisposition(affectableStats.disposition()|PhyStats.IS_SLEEPING);
	}
}
