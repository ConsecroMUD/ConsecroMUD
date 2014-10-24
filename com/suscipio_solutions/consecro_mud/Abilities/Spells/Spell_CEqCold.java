package com.suscipio_solutions.consecro_mud.Abilities.Spells;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;


@SuppressWarnings("rawtypes")
public class Spell_CEqCold extends Spell_BaseClanEq {
@Override public String ID() { return "Spell_CEqCold"; }
private final static String localizedName = CMLib.lang().L("ClanEnchant Cold");
	@Override public String name() { return localizedName; }
@Override public int abstractQuality(){ return Ability.QUALITY_INDIFFERENT;}

  @Override
public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
  {
	type="Cold";
	// All the work is done by the base model
	if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
		return false;
	  return true;
  }
}
