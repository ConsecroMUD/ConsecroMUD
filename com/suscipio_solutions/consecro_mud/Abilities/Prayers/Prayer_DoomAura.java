package com.suscipio_solutions.consecro_mud.Abilities.Prayers;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Weapon;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMLib;



public class Prayer_DoomAura extends Prayer_BladeBarrier
{
	@Override public String ID() { return "Prayer_DoomAura"; }
	private final static String localizedName = CMLib.lang().L("Doom Aura");
	@Override public String name() { return localizedName; }
	private final static String localizedStaticDisplay = CMLib.lang().L("(Doom Aura)");
	@Override public String displayText() { return localizedStaticDisplay; }
	@Override public int classificationCode(){return Ability.ACODE_PRAYER|Ability.DOMAIN_CORRUPTION;}
	@Override public long flags(){return Ability.FLAG_UNHOLY;}

	@Override protected String startStr() { return "An aura of doom appears around <T-NAME>!^?"; }

	@Override
	protected void doDamage(MOB srcM, MOB targetM, int damage)
	{
		CMLib.combat().postDamage(srcM, targetM,this,damage,CMMsg.TYP_UNDEAD|CMMsg.MASK_MALICIOUS|CMMsg.MASK_ALWAYS,Weapon.TYPE_BURNING,"The aura of doom around <S-NAME> <DAMAGE> <T-NAME>.");
	}
}
