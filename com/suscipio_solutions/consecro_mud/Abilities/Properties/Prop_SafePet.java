package com.suscipio_solutions.consecro_mud.Abilities.Properties;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Common.interfaces.PhyStats;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMParms;
import com.suscipio_solutions.consecro_mud.core.CMath;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;


public class Prop_SafePet extends Property
{
	@Override public String ID() { return "Prop_SafePet"; }
	@Override public String name(){ return "Unattackable Pets";}
	@Override protected int canAffectCode(){return Ability.CAN_MOBS;}
	protected boolean disabled=false;
	protected String displayMessage="Awww, leave <T-NAME> alone.";

	@Override
	public String accountForYourself()
	{ return "Unattackable";	}

	@Override
	public void affectPhyStats(Physical affected, PhyStats affectableStats)
	{
		super.affectPhyStats(affected, affectableStats);
		affectableStats.setDisposition(affectableStats.disposition()|PhyStats.IS_UNATTACKABLE);
	}

	@Override
	public void setMiscText(String newMiscText)
	{
		super.setMiscText(newMiscText);
		final String newDisplayMsg=CMParms.getParmStr(newMiscText, "MSG", "");
		if(newDisplayMsg.trim().length()>0)
		{
			displayMessage=newDisplayMsg.trim();
		}
	}

	@Override
	public boolean okMessage(final Environmental myHost, final CMMsg msg)
	{
		if(affected instanceof MOB)
		{
			if((msg.amISource((MOB)affected))&&(msg.sourceMinor()==CMMsg.TYP_DEATH)&&(!CMath.bset(msg.sourceMajor(), CMMsg.MASK_ALWAYS)))
			{
				msg.source().tell(L("You are safe from death."));
				return false;
			}
			else
			if(CMath.bset(msg.targetMajor(),CMMsg.MASK_MALICIOUS))
			{
				if((msg.amITarget(affected))
				&&(!disabled))
				{
					if(!CMath.bset(msg.sourceMajor(),CMMsg.MASK_ALWAYS))
						msg.source().tell(msg.source(),affected,null,displayMessage);
					((MOB)affected).makePeace();
					return false;
				}
				else
				if(msg.amISource((MOB)affected))
					disabled=true;
			}
			else
			if((msg.targetMinor()==CMMsg.TYP_DAMAGE) && msg.amITarget(affected))
				msg.setValue(0);
			else
			if(!((MOB)affected).isInCombat())
				disabled=false;
		}
		else
		if(CMath.bset(msg.targetMajor(),CMMsg.MASK_MALICIOUS) && msg.amITarget(affected))
		{
			if(!CMath.bset(msg.sourceMajor(),CMMsg.MASK_ALWAYS))
				msg.source().tell(msg.source(),affected,null,displayMessage);
			return false;
		}
		return super.okMessage(myHost,msg);
	}
}
