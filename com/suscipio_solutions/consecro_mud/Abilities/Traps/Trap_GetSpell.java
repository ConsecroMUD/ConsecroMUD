package com.suscipio_solutions.consecro_mud.Abilities.Traps;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.core.CMLib;


public class Trap_GetSpell extends Trap_Get
{
	@Override public String ID() { return "Trap_GetSpell"; }
	private final static String localizedName = CMLib.lang().L("Get Spell Trap");
	@Override public String name() { return localizedName; }
	@Override protected int canAffectCode(){return Ability.CAN_ITEMS;}
	@Override protected int canTargetCode(){return 0;}
	@Override protected int trapType(){return TRAP_SPELL;}
	public Trap_GetSpell()
	{
		super();
		setMiscText("Spell_Sleep");
	}

}
