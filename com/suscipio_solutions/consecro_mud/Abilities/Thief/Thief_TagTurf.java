package com.suscipio_solutions.consecro_mud.Abilities.Thief;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Common.interfaces.Clan;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMParms;
import com.suscipio_solutions.consecro_mud.core.CMProps;
import com.suscipio_solutions.consecro_mud.core.collections.Pair;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;


@SuppressWarnings("rawtypes")
public class Thief_TagTurf extends ThiefSkill
{
	@Override public String ID() { return "Thief_TagTurf"; }
	private final static String localizedName = CMLib.lang().L("Tag Turf");
	@Override public String name() { return localizedName; }
	private final static String localizedStaticDisplay = CMLib.lang().L("(Tagged)");
	@Override public String displayText() { return localizedStaticDisplay; }
	@Override protected int canAffectCode(){return CAN_ROOMS;}
	@Override protected int canTargetCode(){return 0;}
	@Override public int abstractQuality(){return Ability.QUALITY_INDIFFERENT;}
	private static final String[] triggerStrings =I(new String[] {"TAGTURF","TURFTAG"});
	@Override public String[] triggerStrings(){return triggerStrings;}
	@Override public int usageType(){return USAGE_MOVEMENT|USAGE_MANA;}
	@Override public int classificationCode(){return Ability.ACODE_THIEF_SKILL|Ability.DOMAIN_STREETSMARTS;}

	@Override
	public boolean okMessage(Environmental host, CMMsg msg)
	{
		if(!super.okMessage(host,msg))
			return false;

		if((msg.tool() instanceof Ability)
		&&(!msg.source().Name().equals(text()))
		&&(msg.source().getClanRole(text())==null)
		&&(!msg.tool().ID().equals("Thief_TurfWar"))
		&&((((Ability)msg.tool()).classificationCode()&Ability.ALL_ACODES)==Ability.ACODE_THIEF_SKILL))
		{
			msg.source().tell(L("You definitely aren't allowed to do that on @x1's turf.",text()));
			return false;
		}
		return true;
	}

	@Override
	public void executeMsg(Environmental host, CMMsg msg)
	{
		super.executeMsg(host,msg);
		if((msg.target()==affected)
		&&(affected instanceof Room)
		&&((msg.targetMinor()==CMMsg.TYP_LOOK)||(msg.targetMinor()==CMMsg.TYP_EXAMINE))
		&&((CMLib.flags().canSeeHidden(msg.source()))||(msg.source().Name().equals(text()))))
		{
			if((msg.source().Name().equals(text()))
			||((msg.source().getClanRole(text())!=null) && CMLib.clans().checkClanPrivilege(msg.source(), Clan.Function.CLAN_BENEFITS)))
				msg.addTrailerMsg(CMClass.getMsg(msg.source(),msg.target(),null,
										CMMsg.MSG_OK_VISUAL,L("This is your turf."),
										CMMsg.NO_EFFECT,null,
										CMMsg.NO_EFFECT,null));
			else
				msg.addTrailerMsg(CMClass.getMsg(msg.source(),msg.target(),null,
										CMMsg.MSG_OK_VISUAL,L("This turf has been claimed by @x1.",text()),
										CMMsg.NO_EFFECT,null,
										CMMsg.NO_EFFECT,null));
		}
	}

	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;

		Room target=mob.location();
		if((auto)&&(givenTarget!=null)&&(givenTarget instanceof Room))
			target=(Room)givenTarget;
		final Ability A=target.fetchEffect(ID());
		if(A!=null)
		{
			final Pair<Clan,Integer> clanRole=mob.getClanRole(A.text());
			if((A.text().equals(mob.Name())
				||((clanRole!=null)&&(clanRole.second.intValue()>=clanRole.first.getGovernment().getAcceptPos())))
			&&(CMParms.combine(commands,0).equalsIgnoreCase("UNTAG")))
			{
				A.unInvoke();
				target.delEffect(A);
				mob.tell(L("This place has been untagged."));
				return true;
			}
			mob.tell(L("This place has already been tagged by @x1.",A.text()));
			return false;
		}
		if((mob.location().domainType()!=Room.DOMAIN_OUTDOORS_CITY)
		   &&(mob.location().domainType()!=Room.DOMAIN_INDOORS_WOOD)
		   &&(mob.location().domainType()!=Room.DOMAIN_INDOORS_STONE))
		{
			mob.tell(L("A place like this can't get your turf."));
			return false;
		}
		if((!CMLib.law().doesOwnThisProperty(mob,mob.location()))
		&&(CMLib.law().getLandTitle(mob.location())!=null)
		&&(CMLib.law().getLandTitle(mob.location()).getOwnerName().length()>0))
		{
			mob.tell(L("You can't tag anothers property!"));
			return false;
		}



		final boolean success=proficiencyCheck(mob,0,auto);

		final CMMsg msg=CMClass.getMsg(mob,null,this,auto?CMMsg.MASK_ALWAYS:CMMsg.MSG_DELICATE_HANDS_ACT,CMMsg.MSG_OK_VISUAL,CMMsg.MSG_OK_VISUAL,auto?"":L("<S-NAME> tag(s) this place as <S-HIS-HER> turf."));
		if(!success)
			return beneficialVisualFizzle(mob,null,auto?"":L("<S-NAME> attempt(s) to tag this place, but can't get into it."));
		else
		if(mob.location().okMessage(mob,msg))
		{
			mob.location().send(mob,msg);
			final Clan C=CMLib.clans().findRivalrousClan(mob);
			if(C!=null)
				setMiscText(C.clanID());
			else
				setMiscText(mob.Name());
			beneficialAffect(mob,target,asLevel,(CMProps.getIntVar(CMProps.Int.TICKSPERMUDMONTH)));
		}
		return success;
	}
}
