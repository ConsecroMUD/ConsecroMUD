package com.suscipio_solutions.consecro_mud.Abilities.Properties;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Areas.interfaces.Area;
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
import com.suscipio_solutions.consecro_mud.core.collections.XHashSet;
import com.suscipio_solutions.consecro_mud.core.interfaces.Drink;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;
import com.suscipio_solutions.consecro_mud.core.interfaces.Tickable;


@SuppressWarnings("rawtypes")
public class Prop_InstantDeath extends Property
{
	@Override public String ID(){return "Prop_InstantDeath";}
	@Override public long flags() { return super.flags()|Ability.FLAG_POTENTIALLY_DEADLY; }
	@Override protected int canAffectCode(){return Ability.CAN_ROOMS|Ability.CAN_AREAS|Ability.CAN_ITEMS|Ability.CAN_MOBS;}
	protected CompiledZapperMask mask=null;
	protected volatile boolean[] killTrigger={false};


	public Prop_InstantDeath()
	{
		super();
	}

	@Override
	public void setMiscText(String newMiscText)
	{
		super.setMiscText(newMiscText);
		final String maskStr=CMParms.getParmStr(newMiscText,"mask","");
		mask=null;
		if((maskStr!=null)&&(maskStr.trim().length()>0))
			mask=CMLib.masking().getPreCompiledMask(maskStr);
	}

	@Override
	public String accountForYourself()
	{
		return "instant killing";
	}

	public Set<MOB> getEveryoneHere(MOB spareMe, Room R)
	{
		final Set<MOB> V=new HashSet<MOB>();
		if(R==null) return V;
		for(int i=0;i<R.numInhabitants();i++)
		{
			final MOB M=R.fetchInhabitant(i);
			if((spareMe!=null)&&(spareMe==M))
				continue;
			if((M!=null)
			&&(!CMSecurity.isAllowed(M,R,CMSecurity.SecFlag.IMMORT))
			&&((mask==null)||(CMLib.masking().maskCheck(mask, M, false))))
				V.add(M);
		}
		return V;
		//CMLib.combat().postDeath(null,M,null);
	}

	protected MOB getTickersMOB(Tickable ticking)
	{
		if(ticking==null) return null;

		if(ticking instanceof MOB)
			return (MOB)ticking;
		else
		if(ticking instanceof Item)
			if(((Item)ticking).owner() != null)
				if(((Item)ticking).owner() instanceof MOB)
					return (MOB)((Item)ticking).owner();

		return null;
	}

	protected Room getTickersRoom(Tickable ticking)
	{
		if(ticking==null) return null;

		if(ticking instanceof Room)
			return (Room)ticking;

		final MOB mob=getTickersMOB(ticking);
		if(mob!=null)
			return mob.location();

		if(ticking instanceof Item)
			if(((Item)ticking).owner() != null)
				if(((Item)ticking).owner() instanceof Room)
					return (Room)((Item)ticking).owner();

		return null;
	}

	public Set<MOB> getDeadMOBsFrom(Environmental whoE)
	{
		if(whoE instanceof MOB)
		{
			final MOB mob=(MOB)whoE;
			final Room room=mob.location();
			if(room!=null)
				return getEveryoneHere(mob,room);
		}
		else
		if(whoE instanceof Item)
		{
			final Item item=(Item)whoE;
			final Environmental E=item.owner();
			if(E!=null)
			{
				final Room room=getTickersRoom(whoE);
				if(room!=null)
				{
					if((E instanceof MOB)&&((mask==null)||(CMLib.masking().maskCheck(mask, E, false))))
						return new XHashSet<MOB>((MOB)E);
					else
					if(E instanceof Room)
						return getEveryoneHere(null,(Room)E);
					room.recoverRoomStats();
				}
			}
		}
		else
		if(whoE instanceof Room)
			return getEveryoneHere(null,(Room)whoE);
		else
		if(whoE instanceof Area)
		{
			final Set<MOB> allMobs=new HashSet<MOB>();
			for(final Enumeration r=((Area)whoE).getMetroMap();r.hasMoreElements();)
			{
				final Room R=(Room)r.nextElement();
				allMobs.addAll(getEveryoneHere(null,R));
			}
		}
		return new HashSet<MOB>();
	}

	@Override
	public boolean tick(Tickable ticking, int tickID)
	{
		if(tickID!=Tickable.TICKID_MISCELLANEOUS)
			return super.tick(ticking, tickID);
		while(killTrigger[0])
		{
			final LinkedList<MOB> killThese=new LinkedList<MOB>();
			synchronized(killTrigger)
			{
				killThese.addAll(getDeadMOBsFrom(affected));
				killTrigger[0]=false;
			}
			for(final MOB M : killThese)
			{
				CMLib.combat().postDeath(null, M, null);
			}
		}
		return false;
	}

	@Override
	public void executeMsg(Environmental affecting, CMMsg msg)
	{
		super.executeMsg(affecting,msg);
		if(msg.amITarget(affecting))
		{
			boolean activated=false;
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
			if(activated)
			{
				synchronized(killTrigger)
				{
					killTrigger[0]=true;
					if(!CMLib.threads().isTicking(this, Tickable.TICKID_MISCELLANEOUS))
						CMLib.threads().startTickDown(this, Tickable.TICKID_MISCELLANEOUS, 500,1);
				}
			}
		}
	}
}
