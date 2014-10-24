package com.suscipio_solutions.consecro_mud.Abilities.Ranger;
import com.suscipio_solutions.consecro_mud.core.CMLib;


public class Ranger_Enemy4 extends Ranger_Enemy1
{
	@Override public String ID() { return "Ranger_Enemy4"; }
	private final static String localizedName = CMLib.lang().L("Favored Enemy 4");
	@Override public String name() { return localizedName; }
}
