package com.suscipio_solutions.consecro_mud.Items.BasicTech;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMProps;


public class GenAbsorbantShield extends GenPersonalShield
{
	@Override public String ID(){	return "GenAbsorbantShield";}

	public GenAbsorbantShield()
	{
		super();
		setName("an absorption shield generator");
		setDisplayText("an absorption shield generator sits here.");
		setDescription("The absorption shield generator is worn about the body and activated to use. It absorbs all manner of weapon types. ");
		setDescription("The integrity shield generator is worn about the body and activated to use. It protects against disruption and disintegration beams. ");
	}

	@Override
	protected String fieldOnStr(MOB viewerM)
	{
		return (owner() instanceof MOB)?
			"A sparkling field of energy surrounds <O-NAME>.":
			"A sparkling field of energy surrounds <T-NAME>.";
	}

	@Override
	protected String fieldDeadStr(MOB viewerM)
	{
		return (owner() instanceof MOB)?
			"The sparkling field around <O-NAME> flickers and dies out.":
			"The sparkling field around <T-NAME> flickers and dies out.";
	}

	@Override
	protected boolean doShield(MOB mob, CMMsg msg, double successFactor)
	{
		if(msg.value()<=0)
			return true;
		if((successFactor>=1.0)||((successFactor>0.0)&&(msg.value()==1)))
		{
			mob.location().show(mob,msg.source(),null,CMMsg.MSG_OK_VISUAL,L("The sparkling field around <S-NAME> completely absorbs the @x1 attack from <T-NAME>.",msg.tool().name()));
			msg.setValue(0);
		}
		else
		if(successFactor>=0.0)
		{
			msg.setValue((int)Math.round(successFactor*msg.value()));
			final String showDamage = CMProps.getVar(CMProps.Str.SHOWDAMAGE).equalsIgnoreCase("YES")?" ("+Math.round(successFactor*100.0)+")":"";
			if(successFactor>=0.75)
				msg.addTrailerMsg(CMClass.getMsg(mob,msg.source(),msg.tool(),CMMsg.MSG_OK_VISUAL,L("The sparkling field around <S-NAME> absorbs most@x1 of the <O-NAMENOART> damage.",showDamage)));
			else
			if(successFactor>=0.50)
				msg.addTrailerMsg(CMClass.getMsg(mob,msg.source(),msg.tool(),CMMsg.MSG_OK_VISUAL,L("The sparkling field around <S-NAME> absorbs much@x1 of the <O-NAMENOART> damage.",showDamage)));
			else
			if(successFactor>=0.25)
				msg.addTrailerMsg(CMClass.getMsg(mob,msg.source(),msg.tool(),CMMsg.MSG_OK_VISUAL,L("The sparkling field around <S-NAME> absorbs some@x1 of the <O-NAMENOART> damage.",showDamage)));
			else
				msg.addTrailerMsg(CMClass.getMsg(mob,msg.source(),msg.tool(),CMMsg.MSG_OK_VISUAL,L("The sparkling field around <S-NAME> absorbs a little@x1 of the <O-NAMENOART> damage.",showDamage)));
		}
		return true;
	}

	@Override
	protected boolean doesShield(MOB mob, CMMsg msg, double successFactor)
	{
		return activated();
	}
}
