package com.suscipio_solutions.consecro_mud.Abilities.Common;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Light;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Wearable;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;
import com.suscipio_solutions.consecro_mud.core.interfaces.Tickable;



public class SmokeRings extends CommonSkill
{
	@Override public String ID() { return "SmokeRings"; }
	private final static String localizedName = CMLib.lang().L("Smoke Rings");
	@Override public String name() { return localizedName; }
	@Override public boolean isAutoInvoked(){return true;}
	@Override public boolean canBeUninvoked(){return false;}
	@Override public int classificationCode() {   return Ability.ACODE_COMMON_SKILL|Ability.DOMAIN_ARTISTIC; }

	public SmokeRings()
	{
		super();
		displayText="";
		canBeUninvoked=false;
	}

	@Override
	public void executeMsg(Environmental affected, CMMsg msg)
	{
		if(((affected instanceof MOB)
		&&(msg.amISource((MOB)affected)))
		&&(msg.targetMinor()==CMMsg.TYP_HANDS)
		&&(msg.target() instanceof Light)
		&&(msg.tool() instanceof Light)
		&&(msg.target()==msg.tool())
		&&(((Light)msg.target()).amWearingAt(Wearable.WORN_MOUTH))
		&&(((Light)msg.target()).isLit())
		&&(proficiencyCheck(null,(10*getXLEVELLevel((MOB)affected)),false)))
		{
			if(CMLib.dice().rollPercentage()==1) helpProficiency((MOB)affected,0);
			String str=L("<S-NAME> blow(s) out a perfect smoke ring.");
			switch(CMLib.dice().roll(1,10,0))
			{
			case 1:
				str=L("<S-NAME> blow(s) out a perfect smoke ring.");
				break;
			case 2:
				str=L("<S-NAME> blow(s) out a swirling string of smoke.");
				break;
			case 3:
				str=L("<S-NAME> blow(s) out a huge smoke ring.");
				break;
			case 4:
				str=L("<S-NAME> blow(s) out a train of tiny smoke rings.");
				break;
			case 5:
				str=L("<S-NAME> blow(s) out a couple of tiny smoke rings.");
				break;
			case 6:
				str=L("<S-NAME> blow(s) out a nice round smoke ring.");
				break;
			case 7:
				str=L("<S-NAME> blow(s) out three big smoke rings.");
				break;
			case 8:
				str=L("<S-NAME> blow(s) out an ENORMOUS smoke ring.");
				break;
			case 9:
				str=L("<S-NAME> blow(s) out a swirl of tiny smoke rings.");
				break;
			case 10:
				str=L("<S-NAME> blow(s) out a smoke ring shaped like a galley.");
				break;
			}
			msg.addTrailerMsg(CMClass.getMsg(msg.source(),null,msg.tool(),CMMsg.MSG_OK_VISUAL,str));
		}
		super.executeMsg(affected,msg);
	}
	@Override
	public boolean tick(Tickable ticking, int tickID)
	{
		return true;
	}
}
