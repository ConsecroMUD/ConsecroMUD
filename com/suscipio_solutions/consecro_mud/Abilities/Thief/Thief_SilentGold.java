package com.suscipio_solutions.consecro_mud.Abilities.Thief;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Coins;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;
import com.suscipio_solutions.consecro_mud.core.interfaces.ItemPossessor;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;


@SuppressWarnings("rawtypes")
public class Thief_SilentGold extends ThiefSkill
{
	@Override public String ID() { return "Thief_SilentGold"; }
	@Override public String displayText() {return "(Silent AutoGold)";}
	private final static String localizedName = CMLib.lang().L("Silent AutoGold");
	@Override public String name() { return localizedName; }
	@Override protected int canAffectCode(){return CAN_MOBS;}
	@Override protected int canTargetCode(){return 0;}
	@Override public int classificationCode(){return Ability.ACODE_THIEF_SKILL|Ability.DOMAIN_STEALING;}
	@Override public int abstractQuality(){return Ability.QUALITY_OK_SELF;}
	private static final String[] triggerStrings =I(new String[] {"SILENTGOLD"});
	@Override public String[] triggerStrings(){return triggerStrings;}
	private CMMsg lastMsg=null;

	@Override
	public void executeMsg(final Environmental myHost, final CMMsg msg)
	{
		super.executeMsg(myHost,msg);
		if((affected!=null)&&(affected instanceof MOB))
		{
			if((msg.sourceMinor()==CMMsg.TYP_DEATH)
			&&(msg.source()!=affected)
			&&(CMLib.flags().canBeSeenBy(msg.source(),(MOB)affected))
			&&(msg!=lastMsg)
			&&(msg.source().location()==((MOB)affected).location()))
			{
				lastMsg=msg;
				final double money=CMLib.beanCounter().getTotalAbsoluteNativeValue(msg.source());
				final double exper=getXLEVELLevel((MOB)affected);
				final double gold=money/10.0*((2.0+exper)/2);
				if(gold>0.0)
				{
					final Coins C=CMLib.beanCounter().makeBestCurrency(msg.source(),gold);
					if((C!=null)&&(C.getNumberOfCoins()>0))
					{
						CMLib.beanCounter().subtractMoney(msg.source(),C.getTotalValue());
						final MOB mob=(MOB)affected;
						mob.location().addItem(C,ItemPossessor.Expire.Monster_EQ);
						mob.location().recoverRoomStats();
						final MOB victim=mob.getVictim();
						mob.setVictim(null);
						final CMMsg msg2=CMClass.getMsg(mob,C,this,CMMsg.MSG_THIEF_ACT,L("You silently loot <T-NAME> from the corpse of @x1",msg.source().name(mob)),CMMsg.MSG_THIEF_ACT,null,CMMsg.NO_EFFECT,null);
						if(mob.location().okMessage(mob,msg2))
						{
							mob.location().send(mob,msg2);
							CMLib.commands().postGet(mob,null,C,true);
						}
						if(victim!=null) mob.setVictim(victim);
					}
				}
			}
		}
	}

	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		if((mob.fetchEffect(ID())!=null))
		{
			mob.tell(L("You are no longer automatically looting gold from corpses silently."));
			mob.delEffect(mob.fetchEffect(ID()));
			return false;
		}
		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;

		final boolean success=proficiencyCheck(mob,0,auto);

		if(success)
		{
			mob.tell(L("You will now automatically loot gold from corpses silently."));
			beneficialAffect(mob,mob,asLevel,0);
			final Ability A=mob.fetchEffect(ID());
			if(A!=null) A.makeLongLasting();
		}
		else
			beneficialVisualFizzle(mob,null,L("<S-NAME> attempt(s) to start silently looting gold from corpses, but fail(s)."));
		return success;
	}

}
