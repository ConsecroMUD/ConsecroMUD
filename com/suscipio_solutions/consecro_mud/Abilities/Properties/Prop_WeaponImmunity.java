package com.suscipio_solutions.consecro_mud.Abilities.Properties;
import java.util.Hashtable;
import java.util.Vector;

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
import com.suscipio_solutions.consecro_mud.core.CMParms;
import com.suscipio_solutions.consecro_mud.core.CMath;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;


@SuppressWarnings({"unchecked","rawtypes"})
public class Prop_WeaponImmunity extends Property implements TriggeredAffect
{
	@Override public String ID() { return "Prop_WeaponImmunity"; }
	@Override public String name(){ return "Weapon Immunity";}
	@Override protected int canAffectCode(){return Ability.CAN_MOBS|Ability.CAN_ITEMS;}
	public Hashtable flags=new Hashtable();

	@Override
	public int triggerMask()
	{
		return TriggeredAffect.TRIGGER_BEING_HIT;
	}


	@Override public long flags(){return Ability.FLAG_IMMUNER;}

	@Override
	public String accountForYourself()
	{
		final String id="Weapon Immunities for the wearer: "+text();
		return id;
	}
	@Override
	public void setMiscText(String newValue)
	{
		super.setMiscText(newValue);
		flags=new Hashtable();
		final Vector<String> V=CMParms.parse(newValue.toUpperCase());
		Object c=null;
		String s=null;
		for(int v=0;v<V.size();v++)
		{
			s=V.elementAt(v);
			c=new Character(s.charAt(0));
			if((s.charAt(0)=='-')||(s.charAt(0)=='+'))
				s=s.substring(1);
			else
				c=new Character('+');
			if((s!=null)&&(s.startsWith("LEVEL")))
			{
				c=((Character)c).charValue()+" "+s.substring(5).trim();
				s=s.substring(5).trim();
			}
			flags.put(s,c);
		}
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
			if(msg.tool()==null) return true;
			if(flags.size()==0) return true;

			boolean immune=flags.containsKey("ALL")&&(((Character)flags.get("ALL")).charValue()=='+');
			Character foundPlusMinus=null;
			for(final int i : CharStats.CODES.SAVING_THROWS())
				if((CharStats.CODES.CMMSGMAP(i)==msg.sourceMinor())
				&&(i!=CharStats.STAT_SAVE_MAGIC))
				{
					foundPlusMinus=(Character)flags.get(CharStats.CODES.NAME(i));
					if(foundPlusMinus!=null)
					{
						if((foundPlusMinus.charValue()=='-')&&(immune))
							immune=false;
						else
						if(foundPlusMinus.charValue()!='-')
							immune=true;
						break;
					}
				}

			if((foundPlusMinus==null)&&(msg.tool() instanceof Weapon))
			{
				final Weapon W=(Weapon)msg.tool();
				foundPlusMinus=(Character)flags.get(Weapon.TYPE_DESCS[W.weaponType()]);
				foundPlusMinus=(Character)flags.get(Weapon.CLASS_DESCS[W.weaponClassification()]);
				foundPlusMinus=(Character)flags.get((CMLib.flags().isABonusItems(W))?"MAGIC":"NONMAGIC");
				foundPlusMinus=(Character)flags.get(RawMaterial.CODES.NAME((W).material()));
				if(foundPlusMinus!=null)
				{
					if((foundPlusMinus.charValue()=='-')&&(immune))
						immune=false;
					else
					if(foundPlusMinus.charValue()!='-')
						immune=true;
				}
				else
				{
					final Object O=flags.get("LEVEL");
					if((O!=null)&&(O instanceof String)&&(((String)O).length()>3))
					{
						String lvl=(String)O;
						foundPlusMinus=new Character(lvl.charAt(0));
						lvl=lvl.substring(2).trim();
						if((foundPlusMinus.charValue()=='-')&&(immune))
						{
							if(W.phyStats().level()>=CMath.s_int(lvl))
								immune=false;
						}
						else
						if(foundPlusMinus.charValue()!='-')
						{
							if(W.phyStats().level()<CMath.s_int(lvl))
								immune=true;
						}
					}
				}
			}

			if((foundPlusMinus==null)&&(msg.tool() instanceof Ability))
			{
				final int classType=((Ability)msg.tool()).classificationCode()&Ability.ALL_ACODES;
				switch(classType)
				{
				case Ability.ACODE_SPELL:
				case Ability.ACODE_PRAYER:
				case Ability.ACODE_CHANT:
				case Ability.ACODE_SONG:
					{
						foundPlusMinus=(Character)flags.get("MAGICSKILLS");
						if(foundPlusMinus==null) foundPlusMinus=(Character)flags.get("MAGIC");
						if(foundPlusMinus!=null)
						{
							if((foundPlusMinus.charValue()=='-')&&(immune))
								immune=false;
							else
							if(foundPlusMinus.charValue()!='-')
								immune=true;
						}
					}
					break;
				default:
					break;
				}
			}
			if(immune) msg.setValue(0);
		}
		return true;
	}
}
