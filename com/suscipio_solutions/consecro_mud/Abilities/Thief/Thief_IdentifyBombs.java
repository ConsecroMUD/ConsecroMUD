package com.suscipio_solutions.consecro_mud.Abilities.Thief;
import java.util.Iterator;
import java.util.List;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Trap;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Exits.interfaces.Exit;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Container;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Item;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.Directions;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;
import com.suscipio_solutions.consecro_mud.core.interfaces.ShopKeeper;



public class Thief_IdentifyBombs extends ThiefSkill
{
	@Override public String ID() { return "Thief_IdentifyBombs"; }
	private final static String localizedName = CMLib.lang().L("Identify Bombs");
	@Override public String name() { return localizedName; }
	@Override public String displayText(){ return "";}
	@Override protected int canAffectCode(){return CAN_MOBS;}
	@Override protected int canTargetCode(){return 0;}
	@Override public int abstractQuality(){return Ability.QUALITY_OK_SELF;}
	@Override public boolean isAutoInvoked(){return true;}
	@Override public boolean canBeUninvoked(){return false;}
	protected Room lastRoom=null;
	@Override public int classificationCode(){return Ability.ACODE_THIEF_SKILL|Ability.DOMAIN_DETRAP;}

	public String trapCheck(Physical P)
	{
		if(P!=null)
		{
			final Trap T=CMLib.utensils().fetchMyTrap(P);
			if((T!=null)&&(T.isABomb()))
			{
				if(CMLib.dice().rollPercentage()==1)
				{
					helpProficiency((MOB)affected, 0);
					affected.recoverPhyStats();
				}
				return P.name()+" is a bomb.\n\r";
			}
		}
		return "";
	}

	public String trapHere(MOB mob, Physical P)
	{
		final StringBuffer msg=new StringBuffer("");
		if(P==null) return msg.toString();
		if((P instanceof Room)&&(CMLib.flags().canBeSeenBy(P,mob)))
			msg.append(trapCheck(mob.location()));
		else
		if((P instanceof Container)&&(CMLib.flags().canBeSeenBy(P,mob)))
		{
			final Container C=(Container)P;
			final List<Item> V=C.getContents();
			for(int v=0;v<V.size();v++)
				if(trapCheck(V.get(v)).length()>0)
				{
					if(CMLib.dice().rollPercentage()==1)
					{
						helpProficiency((MOB)affected, 0);
						affected.recoverPhyStats();
					}
					msg.append(L("@x1 contains a bomb.",C.name()));
				}
		}
		else
		if((P instanceof Item)&&(CMLib.flags().canBeSeenBy(P,mob)))
			msg.append(trapCheck(P));
		else
		if((P instanceof Exit)&&(CMLib.flags().canBeSeenBy(P,mob)))
		{
			final Room room=mob.location();
			if(room!=null)
			for(int d=Directions.NUM_DIRECTIONS()-1;d>=0;d--)
			{
				if(room.getExitInDir(d)==P)
				{
					final Exit E2=room.getReverseExit(d);
					final Room R2=room.getRoomInDir(d);
					msg.append(trapCheck(P));
					msg.append(trapCheck(E2));
					msg.append(trapCheck(R2));
					break;
				}
			}
		}
		else
		if((P instanceof MOB)&&(CMLib.flags().canBeSeenBy(P,mob)))
		{
			for(int i=0;i<((MOB)P).numItems();i++)
			{
				final Item I=((MOB)P).getItem(i);
				if(trapCheck(I).length()>0)
				{
					if(CMLib.dice().rollPercentage()==1)
					{
						helpProficiency((MOB)affected, 0);
						affected.recoverPhyStats();
					}
					return P.name()+" is carrying a bomb.";
				}
			}
			final ShopKeeper SK=CMLib.coffeeShops().getShopKeeper(P);
			if(SK!=null)
			{
				for(final Iterator<Environmental> i=SK.getShop().getStoreInventory();i.hasNext();)
				{
					final Environmental E2=i.next();
					if(E2 instanceof Item)
						if(trapCheck((Item)E2).length()>0)
						{
							if(CMLib.dice().rollPercentage()==1)
							{
								helpProficiency((MOB)affected, 0);
								affected.recoverPhyStats();
							}
							return P.name()+" has a bomb in stock.";
						}
				}
			}
		}
		return msg.toString();
	}

	@Override
	public void executeMsg(final Environmental myHost, final CMMsg msg)
	{
		super.executeMsg(myHost,msg);
		if((affected!=null)
		&&(affected instanceof MOB)
		&&(msg.target() instanceof Physical)
		&&(msg.amISource((MOB)affected))
		&&((msg.sourceMinor()==CMMsg.TYP_LOOK)||(msg.sourceMinor()==CMMsg.TYP_EXAMINE)))
		{
			if((msg.tool()!=null)&&(msg.tool().ID().equals(ID())))
			{
				final String str=trapHere((MOB)affected,(Physical)msg.target());
				if(str.length()>0)
					((MOB)affected).tell(str);
			}
			else
			if((trapHere((MOB)affected,(Physical)msg.target()).length()>0)
			&&(msg.source()!=msg.target()))
			{
				final CMMsg msg2=CMClass.getMsg(msg.source(),msg.target(),this,CMMsg.MSG_LOOK,CMMsg.NO_EFFECT,CMMsg.NO_EFFECT,null);
				msg.addTrailerMsg(msg2);
			}
		}
	}
}
