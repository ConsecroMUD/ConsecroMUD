package com.suscipio_solutions.consecro_mud.Abilities.Thief;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Item;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Wearable;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;
import com.suscipio_solutions.consecro_mud.core.interfaces.ItemPossessor;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;


@SuppressWarnings("rawtypes")
public class Thief_SilentLoot extends ThiefSkill
{
	@Override public String ID() { return "Thief_SilentLoot"; }
	@Override public String displayText() {return "(Silent AutoLoot)";}
	private final static String localizedName = CMLib.lang().L("Silent AutoLoot");
	@Override public String name() { return localizedName; }
	@Override protected int canAffectCode(){return CAN_MOBS;}
	@Override protected int canTargetCode(){return 0;}
	@Override public int classificationCode(){return Ability.ACODE_THIEF_SKILL|Ability.DOMAIN_STEALING;}
	@Override public int abstractQuality(){return Ability.QUALITY_OK_SELF;}
	private static final String[] triggerStrings =I(new String[] {"SILENTLOOT"});
	@Override public String[] triggerStrings(){return triggerStrings;}

	@Override
	public void executeMsg(final Environmental myHost, final CMMsg msg)
	{
		super.executeMsg(myHost,msg);
		if((affected!=null)&&(affected instanceof MOB))
		{
			if((msg.sourceMinor()==CMMsg.TYP_DEATH)
			&&(msg.source()!=affected)
			&&(CMLib.flags().canBeSeenBy(msg.source(),(MOB)affected))
			&&(msg.source().location()==((MOB)affected).location())
			&&((msg.source().numItems())>0))
			{
				int max=1+getXLEVELLevel((MOB)affected);
				Item item=msg.source().fetchItem(null,Wearable.FILTER_UNWORNONLY,"all");
				if(item==null) item=msg.source().fetchItem(null,Wearable.FILTER_WORNONLY,"all");
				while(((--max)>=0)&&(item!=null)&&(msg.source().isMine(item)))
				{
					item.unWear();
					item.removeFromOwnerContainer();
					item.setContainer(null);
					final MOB mob=(MOB)affected;
					mob.location().addItem(item,ItemPossessor.Expire.Monster_EQ);
					final MOB victim=mob.getVictim();
					mob.setVictim(null);
					final CMMsg msg2=CMClass.getMsg(mob,item,this,CMMsg.MSG_THIEF_ACT,L("You silently autoloot <T-NAME> from the corpse of @x1",msg.source().name(mob)),CMMsg.MSG_THIEF_ACT,null,CMMsg.NO_EFFECT,null);
					if(mob.location().okMessage(mob,msg2))
					{
						mob.location().send(mob,msg2);
						CMLib.commands().postGet(mob,null,item,true);
					}
					if(victim!=null) mob.setVictim(victim);
					item=msg.source().fetchItem(null,Wearable.FILTER_UNWORNONLY,"all");
					if(item==null) item=msg.source().fetchItem(null,Wearable.FILTER_WORNONLY,"all");
				}
			}
		}
	}

	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		if((mob.fetchEffect(ID())!=null))
		{
			mob.tell(L("You are no longer automatically looting items from corpses silently."));
			mob.delEffect(mob.fetchEffect(ID()));
			return false;
		}
		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;

		final boolean success=proficiencyCheck(mob,0,auto);

		if(success)
		{
			mob.tell(L("You will now automatically loot items from corpses silently."));
			beneficialAffect(mob,mob,asLevel,0);
			final Ability A=mob.fetchEffect(ID());
			if(A!=null) A.makeLongLasting();
		}
		else
			beneficialVisualFizzle(mob,null,L("<S-NAME> attempt(s) to start silently looting items from corpses, but fail(s)."));
		return success;
	}

}
