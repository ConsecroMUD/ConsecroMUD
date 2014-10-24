package com.suscipio_solutions.consecro_mud.Items.Weapons;
import com.suscipio_solutions.consecro_mud.Items.interfaces.RawMaterial;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Weapon;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Wearable;
import com.suscipio_solutions.consecro_mud.Libraries.interfaces.GenericBuilder;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMProps;
import com.suscipio_solutions.consecro_mud.core.CMath;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;


public class GenWeapon extends StdWeapon
{
	@Override public String ID(){	return "GenWeapon";}
	protected String	readableText="";
	public GenWeapon()
	{
		super();

		setName("a generic weapon");
		basePhyStats.setWeight(2);
		setDisplayText("a generic weapon sits here.");
		setDescription("");
		baseGoldValue=5;
		properWornBitmap=Wearable.WORN_WIELD|Wearable.WORN_HELD;
		wornLogicalAnd=false;
		weaponType=Weapon.TYPE_BASHING;
		material=RawMaterial.RESOURCE_STEEL;
		basePhyStats().setAttackAdjustment(0);
		basePhyStats().setDamage(5);
		basePhyStats().setLevel(5);
		recoverPhyStats();
	}

	@Override public boolean isGeneric(){return true;}


	@Override
	public String text()
	{
		return CMLib.coffeeMaker().getPropertiesStr(this,false);
	}
	@Override public String readableText(){return readableText;}
	@Override public void setReadableText(String text){readableText=text;}

	@Override
	public void setMiscText(String newText)
	{
		miscText="";
		CMLib.coffeeMaker().setPropertiesStr(this,newText,false);
		recoverPhyStats();
	}
	private final static String[] MYCODES={"MINRANGE","MAXRANGE","WEAPONTYPE","WEAPONCLASS",
							  			   "AMMOTYPE","AMMOCAPACITY"};
	@Override
	public String getStat(String code)
	{
		if(CMLib.coffeeMaker().getGenItemCodeNum(code)>=0)
			return CMLib.coffeeMaker().getGenItemStat(this,code);
		switch(getCodeNum(code))
		{
		case 0: return ""+minRange();
		case 1: return ""+maxRange();
		case 2: return ""+weaponType();
		case 3: return ""+weaponClassification();
		case 4: return ammunitionType();
		case 5: return ""+ammunitionCapacity();
		default:
			return CMProps.getStatCodeExtensionValue(getStatCodes(), xtraValues, code);
		}
	}
	@Override
	public void setStat(String code, String val)
	{
		if(CMLib.coffeeMaker().getGenItemCodeNum(code)>=0)
			CMLib.coffeeMaker().setGenItemStat(this,code,val);
		else
		switch(getCodeNum(code))
		{
		case 0: setRanges(CMath.s_parseIntExpression(val),maxRange()); break;
		case 1: setRanges(minRange(),CMath.s_parseIntExpression(val)); break;
		case 2: setWeaponType(CMath.s_parseListIntExpression(Weapon.TYPE_DESCS,val)); break;
		case 3: setWeaponClassification(CMath.s_parseListIntExpression(Weapon.CLASS_DESCS, val)); break;
		case 4: setAmmunitionType(val); break;
		case 5: setAmmoCapacity(CMath.s_parseIntExpression(val)); break;
		default:
			CMProps.setStatCodeExtensionValue(getStatCodes(), xtraValues, code, val);
			break;
		}
	}
	@Override
	protected int getCodeNum(String code)
	{
		for(int i=0;i<MYCODES.length;i++)
			if(code.equalsIgnoreCase(MYCODES[i])) return i;
		return -1;
	}
	private static String[] codes=null;
	@Override
	public String[] getStatCodes()
	{
		if(codes!=null) return codes;
		final String[] MYCODES=CMProps.getStatCodesList(GenWeapon.MYCODES,this);
		final String[] superCodes=GenericBuilder.GENITEMCODES;
		codes=new String[superCodes.length+MYCODES.length];
		int i=0;
		for(;i<superCodes.length;i++)
			codes[i]=superCodes[i];
		for(int x=0;x<MYCODES.length;i++,x++)
			codes[i]=MYCODES[x];
		return codes;
	}
	@Override
	public boolean sameAs(Environmental E)
	{
		if(!(E instanceof GenWeapon)) return false;
		final String[] codes=getStatCodes();
		for(int i=0;i<codes.length;i++)
			if(!E.getStat(codes[i]).equals(getStat(codes[i])))
				return false;
		return true;
	}
}

