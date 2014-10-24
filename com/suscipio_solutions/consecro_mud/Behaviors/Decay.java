package com.suscipio_solutions.consecro_mud.Behaviors;
import com.suscipio_solutions.consecro_mud.Behaviors.interfaces.Behavior;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Armor;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Item;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Weapon;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMParms;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;
import com.suscipio_solutions.consecro_mud.core.interfaces.Rideable;
import com.suscipio_solutions.consecro_mud.core.interfaces.Tickable;


public class Decay extends ActiveTicker
{
	@Override public String ID(){return "Decay";}
	@Override protected int canImproveCode(){return Behavior.CAN_ITEMS|Behavior.CAN_MOBS;}
	public Decay()
	{
		super();
		minTicks=50;maxTicks=50;chance=100;
		tickReset();
	}

	boolean activated=false;
	protected String answer=" vanishes!";

	@Override
	public String accountForYourself()
	{
		return "decaying over time";
	}

	@Override
	public void setParms(String newParms)
	{
		super.setParms(newParms);
		activated=false;
		tickDown=CMParms.getParmInt(parms,"remain",tickDown);
		answer=CMParms.getParmStr(parms,"answer"," vanishes!");
		if(newParms.toUpperCase().indexOf("NOTRIGGER")>=0)
			activated=true;
	}

	@Override
	public String getParms()
	{
		final String s=parms;
		final int x=s.toUpperCase().indexOf("REMAIN=");
		if(x<0) return "remain="+tickDown+" "+s;
		int y=s.indexOf(' ',x+1);
		if(y<0) y=s.length();
		return ("remain="+tickDown+" "+s.substring(0,x)+s.substring(y).trim()).trim();
	}

	@Override
	public boolean tick(Tickable ticking, int tickID)
	{
		super.tick(ticking,tickID);
		if(!activated) return true;
		if(canAct(ticking,tickID))
		{
			if(ticking instanceof MOB)
			{
				final MOB mob=(MOB)ticking;
				final Room room=mob.location();
				if(room!=null)
				{
					if(mob.amDead()) mob.setLocation(null);
					mob.destroy();
					room.recoverRoomStats();
					if(answer.trim().length()>0)
						room.showHappens(CMMsg.MSG_OK_VISUAL,mob.name()+" "+answer.trim());
				}
			}
			else
			if(ticking instanceof Item)
			{
				final Item item=(Item)ticking;
				final Environmental E=item.owner();
				if(E==null) return true;
				final Room room=getBehaversRoom(ticking);
				if(room==null) return true;
				if(answer.trim().length()>0)
				{
					if(E instanceof MOB)
					{
						((MOB)E).tell(item.name()+" "+answer.trim());
						((MOB)E).recoverPhyStats();
						((MOB)E).recoverCharStats();
						((MOB)E).recoverMaxState();
					}
					else
					if(E instanceof Room)
						((Room)E).showHappens(CMMsg.MSG_OK_VISUAL,item.name()+" "+answer.trim());
				}
				item.destroy();
				room.recoverRoomStats();
			}
		}
		return true;
	}

	@Override
	public void executeMsg(Environmental affecting, CMMsg msg)
	{
		super.executeMsg(affecting,msg);
		if(activated) return;
		if(msg.amITarget(affecting))
		{
			if(affecting instanceof Rideable)
			{
				if(((msg.targetMinor()==CMMsg.TYP_SLEEP)
					||(msg.targetMinor()==CMMsg.TYP_SIT)
					||(msg.targetMinor()==CMMsg.TYP_MOUNT)
					||(msg.targetMinor()==CMMsg.TYP_ENTER))
				&&(!msg.source().isMonster())
				&&(CMLib.masking().maskCheck(getParms(),msg.source(),true)))
					activated=true;
			}
			else
			if(affecting instanceof MOB)
			{
				if((msg.targetMajor(CMMsg.MASK_MALICIOUS))
				&&(!msg.source().isMonster())
				&&(CMLib.masking().maskCheck(getParms(),msg.source(),true)))
					activated=true;
			}
			else
			if((affecting instanceof Armor)
			||(affecting instanceof Weapon))
			{
				if(((msg.targetMinor()==CMMsg.TYP_WEAR)
					||(msg.targetMinor()==CMMsg.TYP_HOLD)
					||(msg.targetMinor()==CMMsg.TYP_WIELD))
				&&(CMLib.masking().maskCheck(getParms(),msg.source(),true)))
					activated=true;
			}
			else
			if(affecting instanceof Item)
			{
				if(((msg.targetMinor()==CMMsg.TYP_GET)||(msg.targetMinor()==CMMsg.TYP_PUSH)||(msg.targetMinor()==CMMsg.TYP_PULL))
				&&(CMLib.masking().maskCheck(getParms(),msg.source(),true)))
				{
					activated=true;
				}
			}
		}
	}
}
