package com.suscipio_solutions.consecro_mud.Abilities.Paladin;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;



public class Paladin_Purity extends PaladinSkill
{
	@Override public String ID() { return "Paladin_Purity"; }
	private final static String localizedName = CMLib.lang().L("Paladin`s Purity");
	@Override public String name() { return localizedName; }
	@Override public int classificationCode(){return Ability.ACODE_SKILL|Ability.DOMAIN_HOLYPROTECTION;}
	public Paladin_Purity()
	{
		super();
	}

	@Override
	public boolean okMessage(final Environmental myHost, final CMMsg msg)
	{
		if(!super.okMessage(myHost,msg))
			return false;
		if((affected==null)||(!(CMLib.flags().isGood(affected))))
			return true;
		if(!(affected instanceof MOB)) return true;

		if((msg.sourceMinor()==CMMsg.TYP_FACTIONCHANGE)
		&&(msg.source()==affected)
		&&(msg.tool()!=null)
		&&(!msg.source().isMine(msg.tool()))
		&&(msg.value()<0)
		&&(msg.othersMessage()!=null)
		&&(msg.othersMessage().equalsIgnoreCase(CMLib.factions().AlignID())))
		{
			msg.source().location().show(msg.source(),null,CMMsg.MSG_OK_VISUAL,L("<S-YOUPOSS> purity protects <S-HIM-HER> from the evil influence."));
			return false;
		}
		return true;
	}
}
