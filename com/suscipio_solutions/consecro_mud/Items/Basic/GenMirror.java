package com.suscipio_solutions.consecro_mud.Items.Basic;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Items.interfaces.RawMaterial;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Wearable;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;


public class GenMirror extends GenItem
{
	@Override public String ID(){	return "GenMirror";}
	protected boolean oncePerRound=false;
	public GenMirror()
	{
		super();
		setName("a generic mirror");
		basePhyStats.setWeight(2);
		setDisplayText("a generic mirror sits here.");
		setDescription("You see yourself in it!");
		baseGoldValue=5;
		basePhyStats().setLevel(1);
		recoverPhyStats();
		setMaterial(RawMaterial.RESOURCE_GLASS);
	}
	@Override
	public String description()
	{
		return "You see yourself in it!";
	}

	@Override
	public boolean okMessage(final Environmental myHost, final CMMsg msg)
	{
		if((owner==null)||(!(owner instanceof MOB))||(amWearingAt(Wearable.IN_INVENTORY)))
			return super.okMessage(myHost,msg);

		final MOB mob=(MOB)owner;
		if((msg.amITarget(mob))
		&&(!oncePerRound)
		&&(msg.tool() instanceof Ability)
		&&((msg.tool().ID().equals("Spell_FleshStone"))
			||(msg.tool().ID().equals("Prayer_FleshRock")))
		&&(!mob.amDead())
		&&(mob!=msg.source()))
		{
			oncePerRound=true;
			mob.location().show(mob,null,CMMsg.MSG_OK_VISUAL,L("@x1 reflects the vicious magic!",name()));
			final Ability A=(Ability)msg.tool();
			A.invoke(mob,msg.source(),true,phyStats().level());
			return false;
		}
		oncePerRound=false;
		return super.okMessage(myHost,msg);
	}

}
