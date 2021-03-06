package com.suscipio_solutions.consecro_mud.Abilities.Prayers;
import java.util.List;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMParms;
import com.suscipio_solutions.consecro_mud.core.CMath;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;


@SuppressWarnings("rawtypes")
public class Prayer_Sanctum extends Prayer
{
	@Override public String ID() { return "Prayer_Sanctum"; }
	private final static String localizedName = CMLib.lang().L("Sanctum");
	@Override public String name() { return localizedName; }
	private final static String localizedStaticDisplay = CMLib.lang().L("(Sanctum)");
	@Override public String displayText() { return localizedStaticDisplay; }
	@Override public int classificationCode(){return Ability.ACODE_PRAYER|Ability.DOMAIN_WARDING;}
	@Override public int abstractQuality(){ return Ability.QUALITY_OK_OTHERS;}
	@Override protected int canAffectCode(){return CAN_ROOMS;}
	@Override public long flags(){return Ability.FLAG_HOLY|Ability.FLAG_UNHOLY;}

	protected boolean inRoom(MOB mob, Room R)
	{
		if(!CMLib.law().doesAnyoneHavePrivilegesHere(mob, text(), R))
		{
			mob.tell(L("You feel your muscles unwilling to cooperate."));
			return false;
		}
		return true;
	}

	@Override
	public boolean okMessage(final Environmental myHost, final CMMsg msg)
	{
		if(affected==null)
			return super.okMessage(myHost,msg);

		final Room R=(Room)affected;
		if((msg.targetMinor()==CMMsg.TYP_ENTER)
		&&(msg.target()==R)
		&&(!msg.source().Name().equals(text()))
		&&(msg.source().getClanRole(text())==null)
		&&((msg.source().amFollowing()==null)
			||((!msg.source().amFollowing().Name().equals(text()))
				&&(msg.source().amFollowing().getClanRole(text())==null)))
		&&(!CMLib.law().doesHavePriviledgesHere(msg.source(),R)))
		{
			msg.source().tell(L("You feel your muscles unwilling to cooperate."));
			return false;
		}
		if((CMath.bset(msg.sourceMajor(),CMMsg.MASK_MALICIOUS))
		||(CMath.bset(msg.targetMajor(),CMMsg.MASK_MALICIOUS))
		||(CMath.bset(msg.othersMajor(),CMMsg.MASK_MALICIOUS)))
		{
			if((msg.source()!=null)
			&&(msg.target()!=null)
			&&(msg.source()!=affected)
			&&(msg.source()!=msg.target()))
			{
				if(affected instanceof MOB)
				{
					final MOB mob=(MOB)affected;
					if((CMLib.flags().aliveAwakeMobile(mob,true))
					&&(!mob.isInCombat()))
					{
						String t="No fighting!";
						if(text().indexOf(';')>0)
						{
							final List<String> V=CMParms.parseSemicolons(text(),true);
							t=V.get(CMLib.dice().roll(1,V.size(),-1));
						}
						CMLib.commands().postSay(mob,msg.source(),t,false,false);
					}
					else
						return super.okMessage(myHost,msg);
				}
				else
				{
					String t="You feel too peaceful here.";
					if(text().indexOf(';')>0)
					{
						final List<String> V=CMParms.parseSemicolons(text(),true);
						t=V.get(CMLib.dice().roll(1,V.size(),-1));
					}
					msg.source().tell(t);
				}
				final MOB victim=msg.source().getVictim();
				if(victim!=null) victim.makePeace();
				msg.source().makePeace();
				msg.modify(msg.source(),msg.target(),msg.tool(),CMMsg.NO_EFFECT,"",CMMsg.NO_EFFECT,"",CMMsg.NO_EFFECT,"");
				return false;
			}
		}
		return super.okMessage(myHost,msg);
	}


	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		final Physical target=mob.location();
		if(target==null) return false;
		if(target.fetchEffect(ID())!=null)
		{
			mob.tell(L("This place is already a sanctum."));
			return false;
		}

		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;

		final boolean success=proficiencyCheck(mob,0,auto);
		if(success)
		{
			final CMMsg msg=CMClass.getMsg(mob,target,this,verbalCastCode(mob,target,auto),auto?"":L("^S<S-NAME> @x1 to make this place a sanctum.^?",prayForWord(mob)));
			if(mob.location().okMessage(mob,msg))
			{
				mob.location().send(mob,msg);
				setMiscText(mob.Name());

				if((target instanceof Room)
				&&(CMLib.law().doesOwnThisProperty(mob,((Room)target))))
				{
					final String landOwnerName=CMLib.law().getLandOwnerName((Room)target);
					if(CMLib.clans().getClan(landOwnerName)!=null)
					{
						setMiscText(landOwnerName);
						beneficialAffect(mob,target,asLevel,0);
					}
					else
					{
						target.addNonUninvokableEffect((Ability)this.copyOf());
						CMLib.database().DBUpdateRoom((Room)target);
					}
				}
				else
					beneficialAffect(mob,target,asLevel,0);
			}
		}
		else
			beneficialWordsFizzle(mob,target,L("<S-NAME> @x1 to make this place a sanctum, but <S-IS-ARE> not answered.",prayForWord(mob)));

		return success;
	}
}
