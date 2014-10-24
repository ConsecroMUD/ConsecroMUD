package com.suscipio_solutions.consecro_mud.Abilities.Properties;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.TriggeredAffect;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CharStats;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Item;
import com.suscipio_solutions.consecro_mud.Items.interfaces.RawMaterial;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Weapon;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Wearable;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMath;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;


public class Prop_AbsorbDamage extends Property implements TriggeredAffect
{
	@Override public String ID() { return "Prop_AbsorbDamage"; }
	@Override public String name(){ return "Absorb Damage";}
	@Override protected int canAffectCode(){return Ability.CAN_MOBS|Ability.CAN_ITEMS;}

	@Override
	public String accountForYourself()
	{
		final String id="Absorbs damage of the following amount and types: "+text();
		return id;
	}

	@Override
	public int triggerMask()
	{
		return TriggeredAffect.TRIGGER_BEING_HIT;
	}

	@Override
	public boolean okMessage(final Environmental myHost, final CMMsg msg)
	{
		if(!super.okMessage(myHost,msg))
			return false;
		if((affected!=null)
		&&(msg.targetMinor()==CMMsg.TYP_DAMAGE)&&(msg.value()>0))
		{
			MOB M=null;
			if(affected instanceof MOB)
				M=(MOB)affected;
			else
			if((affected instanceof Item)
			&&(!((Item)affected).amWearingAt(Wearable.IN_INVENTORY))
			&&(((Item)affected).owner()!=null)
			&&(((Item)affected).owner() instanceof MOB))
				M=(MOB)((Item)affected).owner();
			if(M==null) return true;
			if(!msg.amITarget(M)) return true;

			String text=text().toUpperCase();

			int immune=text.indexOf("+ALL");
			int x=-1;
			for(final int i : CharStats.CODES.SAVING_THROWS())
				if((CharStats.CODES.CMMSGMAP(i)==msg.sourceMinor())
				&&((msg.tool()==null)||(i!=CharStats.STAT_SAVE_MAGIC)))
				{
					x=text.indexOf(CharStats.CODES.NAME(i));
					if(x>0)
					{
						if((text.charAt(x-1)=='-')&&(immune>=0))
							immune=-1;
						else
						if(text.charAt(x-1)!='-')
							immune=x;
					}
				}

			if((x<0)&&(msg.tool() instanceof Weapon))
			{
				final Weapon W=(Weapon)msg.tool();
				x=text.indexOf(Weapon.TYPE_DESCS[W.weaponType()]);
				if(x<0) x=(CMLib.flags().isABonusItems(W))?text.indexOf("MAGIC"):-1;
				if(x<0) x=text.indexOf(RawMaterial.CODES.NAME(W.material()));
				if(x>0)
				{
					if((text.charAt(x-1)=='-')&&(immune>=0))
						immune=-1;
					else
					if(text.charAt(x-1)!='-')
						immune=x;
				}
				else
				{
					x=text.indexOf("LEVEL");
					if(x>0)
					{
						String lvl=text.substring(x+5);
						if(lvl.indexOf(' ')>=0)
							lvl=lvl.substring(lvl.indexOf(' '));
						if((text.charAt(x-1)=='-')&&(immune>=0))
						{
							if(W.phyStats().level()>=CMath.s_int(lvl))
								immune=-1;
						}
						else
						if(text.charAt(x-1)!='-')
						{
							if(W.phyStats().level()<CMath.s_int(lvl))
								immune=x;
						}
					}
				}
			}

			if((x<0)&&(msg.tool() instanceof Ability))
			{
				final int classType=((Ability)msg.tool()).classificationCode()&Ability.ALL_ACODES;
				switch(classType)
				{
				case Ability.ACODE_SPELL:
				case Ability.ACODE_PRAYER:
				case Ability.ACODE_CHANT:
				case Ability.ACODE_SONG:
					{
						x=text.indexOf("MAGIC");
						if(x>0)
						{
							if((text.charAt(x-1)=='-')&&(immune>=0))
								immune=-1;
							else
							if(text.charAt(x-1)!='-')
								immune=x;
						}
					}
					break;
				default:
					break;
				}
			}
			if(immune>0)
			{
				int lastNumber=-1;
				x=0;
				while(x<immune)
				{
					if(Character.isDigit(text.charAt(x))&&((x==0)||(!Character.isDigit(text.charAt(x-1)))))
					   lastNumber=x;
					x++;
				}
				if(lastNumber>=0)
				{
					text=text.substring(lastNumber,immune).trim();
					x=text.indexOf(' ');
					if(x>0) text=text.substring(0,x).trim();
					if(text.endsWith("%"))
						msg.setValue(msg.value()-(int)Math.round(CMath.mul(msg.value(),CMath.div(CMath.s_int(text.substring(0,text.length()-1)),100.0))));
					else
						msg.setValue(msg.value()-CMath.s_int(text));
					if(msg.value()<0) msg.setValue(0);
				}
			}
		}
		return true;
	}
}
