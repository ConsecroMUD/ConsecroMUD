package com.suscipio_solutions.consecro_mud.Behaviors;
import java.util.Enumeration;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Areas.interfaces.Area;
import com.suscipio_solutions.consecro_mud.Behaviors.interfaces.Behavior;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Armor;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Food;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Item;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Weapon;
import com.suscipio_solutions.consecro_mud.Libraries.interfaces.MaskingLibrary.CompiledZapperMask;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMParms;
import com.suscipio_solutions.consecro_mud.core.CMSecurity;
import com.suscipio_solutions.consecro_mud.core.interfaces.Drink;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;
import com.suscipio_solutions.consecro_mud.core.interfaces.Tickable;


@SuppressWarnings({"unchecked","rawtypes"})
public class InstantDeath extends ActiveTicker
{
	@Override public String ID(){return "InstantDeath";}
	@Override public long flags() { return super.flags()|Behavior.FLAG_POTENTIALLYAUTODEATHING; }
	protected CompiledZapperMask mask=null;

	public InstantDeath()
	{
		super();
		minTicks=1;maxTicks=1;chance=100;
		tickReset();
	}

	@Override
	public void setParms(String parms)
	{
		super.setParms(parms);
		final String maskStr=CMParms.getParmStr(parms,"mask","");
		mask=null;
		if((maskStr!=null)&&(maskStr.trim().length()>0))
			mask=CMLib.masking().getPreCompiledMask(maskStr);
	}

	boolean activated=false;

	@Override
	public String accountForYourself()
	{
		return "instant killing";
	}

	public void killEveryoneHere(MOB spareMe, Room R)
	{
		if(R==null) return;
		final Vector V=new Vector();
		for(int i=0;i<R.numInhabitants();i++)
		{
			final MOB M=R.fetchInhabitant(i);
			if((spareMe!=null)&&(spareMe==M))
				continue;
			if((M!=null)
			&&(!CMSecurity.isAllowed(M,R,CMSecurity.SecFlag.IMMORT))
			&&((mask==null)||(CMLib.masking().maskCheck(mask, M, false))))
				V.addElement(M);
		}
		for(int v=0;v<V.size();v++)
		{
			final MOB M=(MOB)V.elementAt(v);
			CMLib.combat().postDeath(null,M,null);
		}
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
					killEveryoneHere(mob,room);
			}
			else
			if(ticking instanceof Item)
			{
				final Item item=(Item)ticking;
				final Environmental E=item.owner();
				if(E==null) return true;
				final Room room=getBehaversRoom(ticking);
				if(room==null) return true;
				if((E instanceof MOB)&&((mask==null)||(CMLib.masking().maskCheck(mask, E, false))))
					CMLib.combat().postDeath(null,(MOB)E,null);
				else
				if(E instanceof Room)
					killEveryoneHere(null,(Room)E);
				room.recoverRoomStats();
			}
			else
			if(ticking instanceof Room)
				killEveryoneHere(null,(Room)ticking);
			else
			if(ticking instanceof Area)
			{
				for(final Enumeration r=((Area)ticking).getMetroMap();r.hasMoreElements();)
				{
					final Room R=(Room)r.nextElement();
					killEveryoneHere(null,R);
				}
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
			if(affecting instanceof MOB)
			{
				if((msg.targetMajor(CMMsg.MASK_MALICIOUS))
				&&(!msg.source().isMonster()))
					activated=true;
			}
			else
			if((affecting instanceof Food)
			||(affecting instanceof Drink))
			{
				if((msg.targetMinor()==CMMsg.TYP_EAT)
				||(msg.targetMinor()==CMMsg.TYP_DRINK))
					activated=true;
			}
			else
			if((affecting instanceof Armor)
			||(affecting instanceof Weapon))
			{
				if((msg.targetMinor()==CMMsg.TYP_WEAR)
				||(msg.targetMinor()==CMMsg.TYP_HOLD)
				||(msg.targetMinor()==CMMsg.TYP_WIELD))
					activated=true;
			}
			else
			if(affecting instanceof Item)
			{
				if((msg.targetMinor()==CMMsg.TYP_GET)||(msg.targetMinor()==CMMsg.TYP_PUSH)||(msg.targetMinor()==CMMsg.TYP_PULL))
					activated=true;
			}
			else
				activated=true;
		}
	}
}
