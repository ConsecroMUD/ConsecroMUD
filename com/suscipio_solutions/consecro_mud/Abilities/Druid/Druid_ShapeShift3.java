package com.suscipio_solutions.consecro_mud.Abilities.Druid;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.core.CMLib;



public class Druid_ShapeShift3 extends Druid_ShapeShift
{
	@Override public String ID() { return "Druid_ShapeShift3"; }
	private final static String localizedName = CMLib.lang().L("Third Totem");
	@Override public String name() { return localizedName; }
	@Override public int abstractQuality(){return Ability.QUALITY_OK_SELF;}
	@Override public String[] triggerStrings(){return empty;}


}
