package com.suscipio_solutions.consecro_mud.Abilities.Prayers;
import java.util.Enumeration;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CharStats;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMTableRow;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Coins;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Item;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Wearable;
import com.suscipio_solutions.consecro_mud.Libraries.interfaces.ChannelsLibrary;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.Banker;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.interfaces.LandTitle;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;




@SuppressWarnings("rawtypes")
public class Prayer_Divorce extends Prayer
{
	@Override public String ID() { return "Prayer_Divorce"; }
	private final static String localizedName = CMLib.lang().L("Divorce");
	@Override public String name() { return localizedName; }
	@Override public int classificationCode(){return Ability.ACODE_PRAYER|Ability.DOMAIN_CORRUPTION;}
	@Override public long flags(){return Ability.FLAG_UNHOLY;}
	@Override public int abstractQuality(){return Ability.QUALITY_OK_OTHERS;}

	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		final MOB target=getTarget(mob,commands,givenTarget);
		if(target==null) return false;
		if(!target.isMarriedToLiege())
		{
			mob.tell(L("@x1 is not married!",target.name(mob)));
			return false;
		}
		if(target.fetchItem(null,Wearable.FILTER_WORNONLY,"wedding band")!=null)
		{
			mob.tell(L("@x1 must remove the wedding band first.",target.name(mob)));
			return false;
		}

		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;

		final boolean success=proficiencyCheck(mob,0,auto);
		if(success)
		{
			final CMMsg msg=CMClass.getMsg(mob,target,this,verbalCastCode(mob,target,auto),auto?"":L("^S<S-NAME> divorce(s) <T-NAMESELF> from @x1.^?",target.getLiegeID()));
			if(mob.location().okMessage(mob,msg))
			{
				if((!target.isMonster())&&(target.soulMate()==null))
					CMLib.coffeeTables().bump(target,CMTableRow.STAT_DIVORCES);
				mob.location().send(mob,msg);
				String maleName=target.Name();
				String femaleName=target.getLiegeID();
				if(target.charStats().getStat(CharStats.STAT_GENDER)=='F')
				{
					femaleName=target.Name();
					maleName=target.getLiegeID();
				}
				final List<String> channels=CMLib.channels().getFlaggedChannelNames(ChannelsLibrary.ChannelFlag.DIVORCES);
				for(int i=0;i<channels.size();i++)
					CMLib.commands().postChannel(channels.get(i),mob.clans(),maleName+" and "+femaleName+" are now divorced.",true);
				final MOB M=CMLib.players().getPlayer(target.getLiegeID());
				if(M!=null) M.setLiegeID("");
				target.setLiegeID("");
				try
				{
					for(final Enumeration e=CMLib.map().rooms();e.hasMoreElements();)
					{
						final Room R=(Room)e.nextElement();
						final LandTitle T=CMLib.law().getLandTitle(R);
						if((T!=null)&&(T.getOwnerName().equals(maleName)))
						{
							T.setOwnerName(femaleName);
							CMLib.database().DBUpdateRoom(R);
						}
						for(int i=0;i<R.numInhabitants();i++)
						{
							final MOB M2=R.fetchInhabitant(i);
							if((M2!=null)&&(M2 instanceof Banker))
							{
								final Banker B=(Banker)M2;
								final List<Item> V=B.getDepositedItems(maleName);
								Item coins=B.findDepositInventory(femaleName,""+Integer.MAX_VALUE);
								for(int v=0;v<V.size();v++)
								{
									final Item I=V.get(v);
									if(I==null) break;
									B.delDepositInventory(maleName,I);
									if(I instanceof Coins)
									{
										if(coins!=null)
											B.delDepositInventory(femaleName,coins);
										else
										{
											coins=CMClass.getItem("StdCoins");
											((Coins)coins).setNumberOfCoins(0);
										}
										B.addDepositInventory(femaleName,coins);
									}
									else
										B.addDepositInventory(femaleName,I);
								}
							}
						}
					}
				}catch(final NoSuchElementException e){}
			}
		}
		else
			beneficialWordsFizzle(mob,target,L("<S-NAME> clear(s) <S-HIS-HER> throat."));

		return success;
	}
}
