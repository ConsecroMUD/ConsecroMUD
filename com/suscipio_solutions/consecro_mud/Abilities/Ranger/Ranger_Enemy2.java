package com.suscipio_solutions.consecro_mud.Abilities.Ranger;
import com.suscipio_solutions.consecro_mud.core.CMLib;


public class Ranger_Enemy2 extends Ranger_Enemy1
{
	@Override public String ID() { return "Ranger_Enemy2"; }
	private final static String localizedName = CMLib.lang().L("Favored Enemy 2");
	@Override public String name() { return localizedName; }

}
