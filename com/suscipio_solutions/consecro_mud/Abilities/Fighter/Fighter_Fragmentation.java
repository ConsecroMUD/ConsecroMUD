package com.suscipio_solutions.consecro_mud.Abilities.Fighter;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Weapon;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMath;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;



public class Fighter_Fragmentation extends FighterSkill
{
	@Override public String ID() { return "Fighter_Fragmentation"; }
	private final static String localizedName = CMLib.lang().L("Fragmentation");
	@Override public String name() { return localizedName; }
	@Override public String displayText(){ return "";}
	@Override protected int canAffectCode(){return CAN_MOBS;}
	@Override protected int canTargetCode(){return 0;}
	@Override public int abstractQuality(){return Ability.QUALITY_BENEFICIAL_SELF;}
	@Override public int classificationCode(){return Ability.ACODE_SKILL|Ability.DOMAIN_WEAPON_USE;}
	@Override public boolean isAutoInvoked(){return true;}
	@Override public boolean canBeUninvoked(){return false;}

	@Override
	public void executeMsg(final Environmental myHost, final CMMsg msg)
	{
		if((msg.target() instanceof Weapon)
		&&(msg.tool()==this))
			((Weapon)msg.target()).destroy();
		super.executeMsg(myHost,msg);
	}

	@Override
	public boolean okMessage(final Environmental myHost, final CMMsg msg)
	{
		if((affected instanceof MOB)
		&&(msg.amISource((MOB)affected))
		&&(msg.targetMinor()==CMMsg.TYP_DAMAGE)
		&&(msg.tool() instanceof Weapon)
		&&(msg.value()>0)
		&&(msg.target() instanceof MOB)
		&&(((Weapon)msg.tool()).weaponClassification()==Weapon.CLASS_THROWN))
		{
			if(CMLib.dice().rollPercentage()<25) helpProficiency((MOB)affected, 0);
			final CMMsg msg2=CMClass.getMsg((MOB)msg.target(),msg.tool(),this,CMMsg.MSG_OK_VISUAL,L("^F^<FIGHT^><T-NAME> fragment(s) in <S-NAME>!^</FIGHT^>^?"));
			CMLib.color().fixSourceFightColor(msg2);
			msg.addTrailerMsg(msg2);
			msg.setValue(msg.value()+(int)Math.round(CMath.mul(3.0 * msg.value(),CMath.div(proficiency(),100.0-(10.0*getXLEVELLevel(invoker()))))));
		}

		return super.okMessage(myHost,msg);
	}

}
