package com.suscipio_solutions.consecro_mud.Abilities.Immortal;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Commands.interfaces.Command;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Item;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Wearable;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.Log;
import com.suscipio_solutions.consecro_mud.core.collections.XVector;
import com.suscipio_solutions.consecro_mud.core.interfaces.ItemPossessor;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;

@SuppressWarnings({"unchecked","rawtypes"})
public class Immortal_Wrath extends Immortal_Skill
{
	boolean doneTicking=false;
	@Override public String ID() { return "Immortal_Wrath"; }
	private final static String localizedName = CMLib.lang().L("Wrath");
	@Override public String name() { return localizedName; }
	@Override protected int canAffectCode(){return 0;}
	@Override protected int canTargetCode(){return CAN_MOBS;}
	@Override public int abstractQuality(){return Ability.QUALITY_MALICIOUS;}
	private static final String[] triggerStrings =I(new String[] {"WRATH"});
	@Override public String[] triggerStrings(){return triggerStrings;}
	@Override public int classificationCode(){return Ability.ACODE_SKILL|Ability.DOMAIN_IMMORTAL;}
	@Override public int maxRange(){return adjustedMaxInvokerRange(1);}
	@Override public int usageType(){return USAGE_MOVEMENT;}

	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		boolean announce=false;
		if(((String)commands.lastElement()).equals("!"))
		{
			commands.removeElementAt(commands.size()-1);
			announce=true;
		}
		final MOB target=getTargetAnywhere(mob,commands,givenTarget,true);
		if(target==null) return false;

		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;

		final boolean success=proficiencyCheck(mob,0,auto);

		if(success)
		{
			final CMMsg msg=CMClass.getMsg(mob,target,this,CMMsg.MASK_MOVE|CMMsg.TYP_JUSTICE|(auto?CMMsg.MASK_ALWAYS:0),
									auto?L("<T-NAME> <T-IS-ARE> knocked out of <T-HIS-HER> shoes!!!"):
										 L("^F**<S-NAME> BLAST(S) <T-NAMESELF>**, knocking <T-HIM-HER> out of <T-HIS-HER> shoes!!^?"));
			CMLib.color().fixSourceFightColor(msg);
			if(target.location().okMessage(mob,msg))
			{
				target.location().send(mob,msg);
				if(target.curState().getHitPoints()>2)
					target.curState().setHitPoints(target.curState().getHitPoints()/2);
				if(target.curState().getMana()>2)
					target.curState().setMana(target.curState().getMana()/2);
				if(target.curState().getMovement()>2)
					target.curState().setMovement(target.curState().getMovement()/2);
				final Item I=target.fetchFirstWornItem(Wearable.WORN_FEET);
				if(I!=null)
				{
					I.unWear();
					I.removeFromOwnerContainer();
					target.location().addItem(I,ItemPossessor.Expire.Player_Drop);
				}
				Log.sysOut("Banish",mob.Name()+" wrathed "+target.name()+".");
				if(announce)
				{
					final Command C=CMClass.getCommand("Announce");
					try
					{
						C.execute(mob,new XVector("ANNOUNCE",target.name()+" is knocked out of "+target.charStats().hisher()+" shoes!!!"),Command.METAFLAG_FORCED);
					}catch(final Exception e){}
				}
			}
		}
		else
			return beneficialVisualFizzle(mob,target,L("<S-NAME> attempt(s) to inflict <S-HIS-HER> wrath upon <T-NAMESELF>, but fail(s)."));
		return success;
	}
}
