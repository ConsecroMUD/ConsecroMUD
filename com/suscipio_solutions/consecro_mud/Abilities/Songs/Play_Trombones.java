package com.suscipio_solutions.consecro_mud.Abilities.Songs;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Items.interfaces.MusicalInstrument;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;


public class Play_Trombones extends Play_Instrument
{
	@Override public String ID() { return "Play_Trombones"; }
	private final static String localizedName = CMLib.lang().L("Trombones");
	@Override public String name() { return localizedName; }
	@Override protected int requiredInstrumentType(){return MusicalInstrument.TYPE_TROMBONES;}
	@Override public String mimicSpell(){return "Spell_Frenzy";}
	private static Ability theSpell=null;
	@Override
	protected Ability getSpell()
	{
		if(theSpell!=null) return theSpell;
		if(mimicSpell().length()==0) return null;
		theSpell=CMClass.getAbility(mimicSpell());
		return theSpell;
	}

}
