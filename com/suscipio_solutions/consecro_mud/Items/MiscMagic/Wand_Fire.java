package com.suscipio_solutions.consecro_mud.Items.MiscMagic;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Items.interfaces.RawMaterial;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Wearable;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;


public class Wand_Fire extends StdWand
{
	@Override public String ID(){	return "Wand_Fire";}
	public Wand_Fire()
	{
		super();

		setName("a gold wand");
		setDisplayText("a golden wand is here.");
		setDescription("A wand made out of gold, with a deep red ruby at the tip");
		secretIdentity="The wand of fire.  Responds to 'Blaze' and 'Burn'";
		this.setUsesRemaining(50);
		baseGoldValue=20000;
		basePhyStats().setLevel(12);
		material=RawMaterial.RESOURCE_OAK;
		recoverPhyStats();
		secretWord="BLAZE, BURN";
	}


	@Override
	public void setSpell(Ability theSpell)
	{
		super.setSpell(theSpell);
		secretWord="BLAZE, BURN";
	}
	@Override
	public void setMiscText(String newText)
	{
		super.setMiscText(newText);
		secretWord="BLAZE, BURN";
	}


	@Override
	public void executeMsg(final Environmental myHost, final CMMsg msg)
	{
		final MOB mob=msg.source();
		switch(msg.sourceMinor())
		{
		case CMMsg.TYP_WAND_USE:
			if((mob.isMine(this))
			   &&(!amWearingAt(Wearable.IN_INVENTORY))
			   &&(msg.target() instanceof MOB)
			   &&(mob.location().isInhabitant((MOB)msg.target())))
			{
				final MOB target=(MOB)msg.target();
				int x=msg.targetMessage().toUpperCase().indexOf("BLAZE");
				if(x>=0)
				{
					final Ability spell = CMClass.getAbility("Spell_BurningHands");
					if((usesRemaining()>0)&&(spell!=null)&&(useTheWand(spell,mob,0)))
					{
						this.setUsesRemaining(this.usesRemaining()-1);
						spell.invoke(mob, target, true,phyStats().level());
						return;
					}
				}
				x=msg.targetMessage().toUpperCase().indexOf("BURN");
				if(x>=0)
				{
					final Ability spell = CMClass.getAbility("Spell_Fireball");
					if((usesRemaining()>4)&&(spell!=null)&&(useTheWand(spell,mob,0)))
					{
						this.setUsesRemaining(this.usesRemaining()-5);
						spell.invoke(mob, target, true,phyStats().level());
						return;
					}
				}
			}
			return;
		default:
			break;
		}
		super.executeMsg(myHost,msg);
	}
}
