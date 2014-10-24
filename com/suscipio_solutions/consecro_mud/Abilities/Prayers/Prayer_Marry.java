package com.suscipio_solutions.consecro_mud.Abilities.Prayers;
import java.util.List;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CharStats;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMTableRow;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Item;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Wearable;
import com.suscipio_solutions.consecro_mud.Libraries.interfaces.ChannelsLibrary;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMParms;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;



@SuppressWarnings("rawtypes")
public class Prayer_Marry extends Prayer
{
	@Override public String ID() { return "Prayer_Marry"; }
	private final static String localizedName = CMLib.lang().L("Marry");
	@Override public String name() { return localizedName; }
	@Override public long flags(){return Ability.FLAG_HOLY;}
	@Override public int abstractQuality(){return Ability.QUALITY_OK_OTHERS;}
	@Override public int classificationCode(){return Ability.ACODE_PRAYER|Ability.DOMAIN_BLESSING;}

	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		if(commands.size()<2)
		{
			mob.tell(L("Whom to whom?"));
			return false;
		}
		final String name1=(String)commands.lastElement();
		final String name2=CMParms.combine(commands,0,commands.size()-1);
		MOB husband=mob.location().fetchInhabitant(name1);
		if((husband==null)||(!CMLib.flags().canBeSeenBy(mob,husband)))
		{
			mob.tell(L("You don't see @x1 here!",name1));
			return false;
		}
		MOB wife=mob.location().fetchInhabitant(name2);
		if((wife==null)||(!CMLib.flags().canBeSeenBy(mob,wife)))
		{
			mob.tell(L("You don't see @x1 here!",name2));
			return false;
		}
		if(wife.charStats().getStat(CharStats.STAT_GENDER)=='M')
		{
			final MOB M=wife;
			wife=husband;
			husband=M;
		}
		if(wife.isMarriedToLiege())
		{
			mob.tell(L("@x1 is already married!!",wife.name()));
			return false;
		}
		if(husband.isMarriedToLiege())
		{
			mob.tell(L("@x1 is already married!!",husband.name()));
			return false;
		}
		if(wife.getLiegeID().length()>0)
		{
			mob.tell(L("@x1 is lieged to @x2, and cannot marry.",wife.name(),wife.getLiegeID()));
			return false;
		}
		if(husband.getLiegeID().length()>0)
		{
			mob.tell(L("@x1 is lieged to @x2, and cannot marry.",husband.name(),husband.getLiegeID()));
			return false;
		}
		if((wife.isMonster())||(wife.playerStats()==null))
		{
			mob.tell(L("@x1 must be a player to marry.",wife.name()));
			return false;
		}
		if((husband.isMonster())||(husband.playerStats()==null))
		{
			mob.tell(L("@x1 must be a player to marry.",wife.name()));
			return false;
		}
		CMLib.coffeeTables().bump(husband,CMTableRow.STAT_BIRTHS);
		Item I=husband.fetchItem(null,Wearable.FILTER_WORNONLY,"wedding band");
		if(I==null)
		{
			mob.tell(L("@x1 isn't wearing a wedding band!",husband.name()));
			return false;
		}
		I=wife.fetchItem(null,Wearable.FILTER_WORNONLY,"wedding band");
		if(I==null)
		{
			mob.tell(L("@x1 isn't wearing a wedding band!",wife.name()));
			return false;
		}
		MOB witness=null;
		for(int i=0;i<mob.location().numInhabitants();i++)
		{
			final MOB M=mob.location().fetchInhabitant(i);
			if((M!=null)
			&&(M!=mob)
			&&(M!=husband)
			&&(M!=wife))
				witness=M;
		}
		if(witness==null)
		{
			mob.tell(L("You need a witness present."));
			return false;
		}

		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;

		final boolean success=proficiencyCheck(mob,0,auto);
		if(success)
		{
			final CMMsg msg=CMClass.getMsg(mob,null,this,verbalCastCode(mob,null,auto),auto?"":L("^S<S-NAME> @x1 to bless the holy union between @x2 and @x3.^?",prayForWord(mob),husband.name(),wife.name()));
			if(mob.location().okMessage(mob,msg))
			{
				mob.location().send(mob,msg);
				husband.setLiegeID(wife.Name());
				wife.setLiegeID(husband.Name());
				CMLib.coffeeTables().bump(husband,CMTableRow.STAT_MARRIAGES);
				CMLib.commands().postSay(mob,husband,L("You may kiss your bride!"),false,false);
				final List<String> channels=CMLib.channels().getFlaggedChannelNames(ChannelsLibrary.ChannelFlag.MARRIAGES);
				for(int i=0;i<channels.size();i++)
					CMLib.commands().postChannel(channels.get(i),husband.clans(),husband.name()+" and "+wife.name()+" were just joined in holy matrimony!",true);
			 }
		}
		else
			beneficialWordsFizzle(mob,null,L("<S-NAME> start(s) 'Dearly beloved', and then clear(s) <S-HIS-HER> throat."));

		return success;
	}
}
