package com.suscipio_solutions.consecro_mud.Abilities.Thief;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Commands.interfaces.Command;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CharStats;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMParms;
import com.suscipio_solutions.consecro_mud.core.CMath;
import com.suscipio_solutions.consecro_mud.core.interfaces.CMObject;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;


@SuppressWarnings({"unchecked","rawtypes"})
public class Thief_Bribe extends ThiefSkill
{
	@Override public String ID() { return "Thief_Bribe"; }
	private final static String localizedName = CMLib.lang().L("Bribe");
	@Override public String name() { return localizedName; }
	@Override protected int canAffectCode(){return 0;}
	@Override protected int canTargetCode(){return CAN_MOBS;}
	@Override public int abstractQuality(){return Ability.QUALITY_OK_OTHERS;}
	private static final String[] triggerStrings =I(new String[] {"BRIBE"});
	@Override public String[] triggerStrings(){return triggerStrings;}
	@Override protected boolean disregardsArmorCheck(MOB mob){return true;}
	protected MOB lastChecked=null;
	@Override public int classificationCode() {   return Ability.ACODE_SKILL|Ability.DOMAIN_INFLUENTIAL; }

	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		if(commands.size()<1)
		{
			mob.tell(L("Bribe whom?"));
			return false;
		}
		final Vector V=new Vector();
		V.addElement(commands.elementAt(0));
		final MOB target=this.getTarget(mob,V,givenTarget);
		if(target==null) return false;

		commands.removeElementAt(0);

		if((!target.mayIFight(mob))
		||(target.charStats().getStat(CharStats.STAT_INTELLIGENCE)<3)
		||(!target.isMonster()))
		{
			mob.tell(L("You can't bribe @x1.",target.name(mob)));
			return false;
		}

		if(commands.size()<1)
		{
			mob.tell(L("Bribe @x1 to do what?",target.charStats().himher()));
			return false;
		}

		CMObject O=CMLib.english().findCommand(target,commands);
		if(O instanceof Command)
		{
			if((!((Command)O).canBeOrdered())||(!((Command)O).securityCheck(mob)))
			{
				mob.tell(L("You can't bribe someone into doing that."));
				return false;
			}
		}
		else
		{
			if(O instanceof Ability)
				O=CMLib.english().getToEvoke(target,(Vector)commands.clone());
			if(O instanceof Ability)
			{
				if(CMath.bset(((Ability)O).flags(),Ability.FLAG_NOORDERING))
				{
					mob.tell(L("You can't bribe @x1 to do that.",target.name(mob)));
					return false;
				}
			}
		}

		if(((String)commands.elementAt(0)).toUpperCase().startsWith("FOL"))
		{
			mob.tell(L("You can't bribe someone to following you."));
			return false;
		}

		final int oldProficiency=proficiency();

		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;


		final double amountRequired=CMLib.beanCounter().getTotalAbsoluteNativeValue(target)
						+((double)((100l-((mob.charStats().getStat(CharStats.STAT_CHARISMA)+(2l*getXLEVELLevel(mob)))*2)))*target.phyStats().level());

		final String currency=CMLib.beanCounter().getCurrency(target);
		boolean success=proficiencyCheck(mob,0,auto);

		if((!success)||(CMLib.beanCounter().getTotalAbsoluteValue(mob,currency)<amountRequired))
		{
			final CMMsg msg=CMClass.getMsg(mob,target,this,CMMsg.MSG_SPEAK,L("^T<S-NAME> attempt(s) to bribe <T-NAMESELF> to '@x1', but no deal is reached.^?",CMParms.combine(commands,0)));
			if(mob.location().okMessage(mob,msg))
				mob.location().send(mob,msg);
			if(CMLib.beanCounter().getTotalAbsoluteValue(mob,currency)<amountRequired)
			{
				final String costWords=CMLib.beanCounter().nameCurrencyShort(currency,amountRequired);
				mob.tell(L("@x1 requires @x2 to do this.",target.charStats().HeShe(),costWords));
			}
			success=false;
		}
		else
		{
			final String costWords=CMLib.beanCounter().nameCurrencyShort(target,amountRequired);
			final CMMsg msg=CMClass.getMsg(mob,target,this,CMMsg.MSG_SPEAK,L("^T<S-NAME> bribe(s) <T-NAMESELF> to '@x1' for @x2.^?",CMParms.combine(commands,0),costWords));
			CMLib.beanCounter().subtractMoney(mob,currency,amountRequired);
			mob.recoverPhyStats();
			final CMMsg omsg=CMClass.getMsg(mob,target,null,CMMsg.MSG_ORDER,null);
			if((mob.location().okMessage(mob,msg))
			&&(mob.location().okMessage(mob,omsg)))
			{
				mob.location().send(mob,msg);
				mob.location().send(mob,omsg);
				if(omsg.sourceMinor()==CMMsg.TYP_ORDER)
					target.doCommand(commands,Command.METAFLAG_FORCED|Command.METAFLAG_ORDER);
			}
			CMLib.beanCounter().addMoney(mob,currency,amountRequired);
			target.recoverPhyStats();
		}
		if(target==lastChecked)
			setProficiency(oldProficiency);
		lastChecked=target;
		return success;
	}

}
