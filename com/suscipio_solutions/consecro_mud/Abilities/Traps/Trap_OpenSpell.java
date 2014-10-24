package com.suscipio_solutions.consecro_mud.Abilities.Traps;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.core.CMLib;


public class Trap_OpenSpell extends Trap_Open
{
	@Override public String ID() { return "Trap_OpenSpell"; }
	private final static String localizedName = CMLib.lang().L("Open Spell Trap");
	@Override public String name() { return localizedName; }
	@Override protected int canAffectCode(){return Ability.CAN_EXITS|Ability.CAN_ITEMS;}
	@Override protected int canTargetCode(){return 0;}
	@Override protected int trapType(){return TRAP_SPELL;}
	public Trap_OpenSpell()
	{
		super();
		setMiscText("Spell_Sleep");
	}

}
