package com.suscipio_solutions.consecro_mud.Abilities.Spells;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Common.interfaces.Clan;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Coins;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Item;
import com.suscipio_solutions.consecro_mud.Items.interfaces.RawMaterial;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Wearable;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.collections.Pair;
import com.suscipio_solutions.consecro_mud.core.interfaces.ItemPossessor;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;


@SuppressWarnings("rawtypes")
public class Spell_ClanDonate extends Spell
{
	@Override public String ID() { return "Spell_ClanDonate"; }
	private final static String localizedName = CMLib.lang().L("Clan Donate");
	@Override public String name() { return localizedName; }
	@Override protected int canTargetCode(){return Ability.CAN_ITEMS;}
	@Override public int abstractQuality(){ return Ability.QUALITY_INDIFFERENT;}
	@Override public int classificationCode(){return Ability.ACODE_SPELL|Ability.DOMAIN_CONJURATION;}
	@Override public long flags(){return super.flags()|Ability.FLAG_CLANMAGIC;}
	@Override protected int overrideMana(){return 5;}
	@Override protected boolean disregardsArmorCheck(MOB mob){return true;}

	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		final Item target=getTarget(mob,null,givenTarget,null,commands,Wearable.FILTER_UNWORNONLY);
		if(target==null) return false;
		if(!mob.isMine(target))
		{
			mob.tell(L("You aren't holding that!"));
			return false;
		}

		if(!mob.clans().iterator().hasNext())
		{
			mob.tell(L("You aren't even a member of a clan."));
			return false;
		}
		final Pair<Clan,Integer> clanPair=CMLib.clans().findPrivilegedClan(mob, Clan.Function.CLAN_BENEFITS);
		if(clanPair==null)
		{
			mob.tell(L("You are not authorized to draw from the power of your clan."));
			return false;
		}
		final Clan C=clanPair.first;
		Room clanDonateRoom=null;
		clanDonateRoom=CMLib.map().getRoom(C.getDonation());
		if(clanDonateRoom==null)
		{
			mob.tell(L("Your clan does not have a donation home."));
			return false;
		}
		if(!CMLib.flags().canAccess(mob,clanDonateRoom))
		{
			mob.tell(L("This magic can not be used to donate from here."));
			return false;
		}

		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;

		final boolean success=proficiencyCheck(mob,0,auto);

		if(success)
		{
			CMMsg msg=CMClass.getMsg(mob,target,this,verbalCastCode(mob,target,auto),L("^S<S-NAME> invoke(s) a donation spell upon <T-NAMESELF>.^?"));
			if(mob.location().okMessage(mob,msg))
			{
				final MOB victim=mob.getVictim();
				boolean proceed=(target instanceof Coins);
				if(!proceed)
				{
					final Room prevRoom=mob.location();
					clanDonateRoom.bringMobHere(mob,false);
					proceed=CMLib.commands().postDrop(mob,target,true,false,false);
					prevRoom.bringMobHere(mob,false);
				}
				if(proceed)
				{
					mob.location().send(mob,msg);
					msg=CMClass.getMsg(mob,target,this,CMMsg.MSG_OK_VISUAL,L("<T-NAME> appears!"));
					if(clanDonateRoom.okMessage(mob,msg))
					{
						mob.location().show(mob,target,this,CMMsg.MSG_OK_VISUAL,L("<T-NAME> vanishes!"));
						if(!clanDonateRoom.isContent(target))
							clanDonateRoom.moveItemTo(target,ItemPossessor.Expire.Player_Drop);
						if(!(target.amDestroyed()))
						{
							if(target instanceof Coins)
								((Coins)target).putCoinsBack();
							else
							if(target instanceof RawMaterial)
								((RawMaterial)target).rebundle();
						}
						clanDonateRoom.recoverRoomStats();
						clanDonateRoom.sendOthers(mob,msg);
					}
				}
				mob.setVictim(victim);
			}

		}
		else
			beneficialWordsFizzle(mob,target,L("<S-NAME> attempt(s) to invoke donation upon <T-NAMESELF>, but fizzle(s) the spell."));


		// return whether it worked
		return success;
	}
}
