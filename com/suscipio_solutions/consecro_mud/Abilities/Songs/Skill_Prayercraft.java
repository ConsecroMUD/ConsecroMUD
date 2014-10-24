package com.suscipio_solutions.consecro_mud.Abilities.Songs;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.core.CMLib;


public class Skill_Prayercraft extends Skill_Songcraft
{
	@Override public String ID() { return "Skill_Prayercraft"; }
	private final static String localizedName = CMLib.lang().L("Prayercraft");
	@Override public String name() { return localizedName; }
	@Override public int craftType(){return Ability.ACODE_PRAYER;}
}
