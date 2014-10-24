package com.suscipio_solutions.consecro_mud.Abilities.Druid;
import java.util.HashSet;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Item;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Wearable;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;




@SuppressWarnings({"unchecked","rawtypes"})
public class Chant_EndureRust extends Chant
{
	@Override public String ID() { return "Chant_EndureRust"; }
	private final static String localizedName = CMLib.lang().L("Endure Rust");
	@Override public String name() { return localizedName; }
	private final static String localizedStaticDisplay = CMLib.lang().L("(Endure Rust)");
	@Override public String displayText() { return localizedStaticDisplay; }
	@Override protected int canAffectCode(){return CAN_MOBS|CAN_ITEMS;}
	@Override protected int canTargetCode(){return CAN_MOBS|CAN_ITEMS;}
	@Override public int abstractQuality(){return Ability.QUALITY_BENEFICIAL_OTHERS;}
	@Override public int classificationCode(){return Ability.ACODE_CHANT|Ability.DOMAIN_PRESERVING;}
	protected HashSet dontbother=new HashSet();

	@Override
	public void unInvoke()
	{
		if((affected instanceof MOB)&&(canBeUninvoked()))
			((MOB)affected).tell(L("Your rust endurance fades."));
		super.unInvoke();
	}

	@Override
	public boolean okMessage(Environmental host, CMMsg msg)
	{
		if((((msg.target()==affected)&&(affected instanceof Item))
			||(msg.target() instanceof Item)&&(affected instanceof MOB)&&(((MOB)affected).isMine(msg.target())))
		&&(msg.targetMinor()==CMMsg.TYP_WATER))
		{
			if(!dontbother.contains(msg.target()))
			{
				final Room R=CMLib.map().roomLocation(affected);
				dontbother.add(msg.target());
				if(R!=null)
					R.show(msg.source(),affected,CMMsg.MSG_OK_VISUAL,L("<T-NAME> resist(s) the oxidizing affects."));
			}
			return false;
		}
		return super.okMessage(host,msg);
	}

	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		final Physical target=this.getAnyTarget(mob,commands,givenTarget,Wearable.FILTER_ANY);
		if(target==null) return false;
		if(target instanceof Item)
		{
		}
		else
		if(target instanceof MOB)
		{
		}
		else
		{
			mob.tell(L("This chant won't affect @x1.",target.name(mob)));
			return false;
		}

		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;


		final boolean success=proficiencyCheck(mob,0,auto);

		if(!success)
		{
			return beneficialWordsFizzle(mob,target,L("<S-NAME> chant(s) to <T-NAMESELF>, but fail(s)."));
		}
		final CMMsg msg=CMClass.getMsg(mob,target,this,verbalCastCode(mob,target,auto),auto?"":L("^S<S-NAME> chant(s) to <T-NAMESELF>, causing a rust proof film to envelope <T-HIM-HER>!^?"));
		if(mob.location().okMessage(mob,msg))
		{
			dontbother.clear();
			mob.location().send(mob,msg);
			beneficialAffect(mob,target,asLevel,0);
		}
		return success;
	}
}
