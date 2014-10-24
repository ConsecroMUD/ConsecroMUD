package com.suscipio_solutions.consecro_mud.Behaviors;
import java.util.LinkedList;

import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Armor;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Coins;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Container;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Item;
import com.suscipio_solutions.consecro_mud.Items.interfaces.RawMaterial;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Weapon;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMParms;
import com.suscipio_solutions.consecro_mud.core.CMSecurity;
import com.suscipio_solutions.consecro_mud.core.CMath;
import com.suscipio_solutions.consecro_mud.core.Log;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;


public class ItemIdentifier extends StdBehavior
{
	@Override public String ID(){return "ItemIdentifier";}

	private LinkedList<CMath.CompiledOperation> costFormula = null;

	@Override
	public String accountForYourself()
	{
		return "item identifying for a price";
	}

	protected double cost(Item item)
	{
		if(costFormula != null)
		{
			final double[] vars = {item.phyStats().level(), item.value(), item.usesRemaining(), CMLib.flags().isABonusItems(item)?1.0:0.0,item.basePhyStats().level(), item.baseGoldValue(),0,0,0,0,0};
			return CMath.parseMathExpression(costFormula, vars, 0.0);
		}
		else
			return 500+(item.phyStats().level()*20);
	}

	@Override
	public void setParms(String parms)
	{
		super.setParms(parms);
		final String formulaString = CMParms.getParmStr(parms,"COST","500 + (@x1 * 20)");
		costFormula = null;
		if(formulaString.trim().length()>0)
		{
			try
			{
				costFormula = CMath.compileMathExpression(formulaString);
			}
			catch(final Exception e)
			{
				Log.errOut(ID(),"Error compiling formula: " + formulaString);
			}
		}
	}

	@Override
	public boolean okMessage(Environmental affecting, CMMsg msg)
	{
		if(!super.okMessage(affecting,msg))
			return false;
		final MOB source=msg.source();
		if(!canFreelyBehaveNormal(affecting))
			return true;
		final MOB observer=(MOB)affecting;
		if((source!=observer)
		&&(msg.amITarget(observer))
		&&(msg.targetMinor()==CMMsg.TYP_GIVE)
		&&(!CMSecurity.isAllowed(source,source.location(),CMSecurity.SecFlag.CMDROOMS))
		&&(!(msg.tool() instanceof Coins))
		&&(msg.tool()!=null)
		&&(msg.tool() instanceof Item))
		{
			final double cost=cost((Item)msg.tool());
			if(CMLib.beanCounter().getTotalAbsoluteShopKeepersValue(msg.source(),observer)<(cost))
			{
				final String costStr=CMLib.beanCounter().nameCurrencyShort(observer,cost);
				CMLib.commands().postSay(observer,source,L("You'll need @x1 for me to identify that.",costStr),true,false);
				return false;
			}
			return true;
		}
		return true;
	}

	@Override
	public void executeMsg(Environmental affecting, CMMsg msg)
	{
		super.executeMsg(affecting,msg);
		if(!canFreelyBehaveNormal(affecting))
			return;
		final MOB observer=(MOB)affecting;
		final MOB source=msg.source();

		if((source!=observer)
		&&(msg.amITarget(observer))
		&&(msg.targetMinor()==CMMsg.TYP_GIVE)
		&&(!CMSecurity.isAllowed(source,source.location(),CMSecurity.SecFlag.CMDROOMS))
		&&(!(msg.tool() instanceof Coins))
		&&(msg.tool()!=null)
		&&(msg.tool() instanceof Item))
		{
			final Item I = (Item)msg.tool();
			final double cost=cost(I);
			CMLib.beanCounter().subtractMoney(source,CMLib.beanCounter().getCurrency(observer),cost);
			final String costStr=CMLib.beanCounter().nameCurrencyLong(observer,cost);
			source.recoverPhyStats();
			CMMsg newMsg=CMClass.getMsg(msg.source(),observer,null,CMMsg.MSG_OK_ACTION,L("<S-NAME> give(s) @x1 to <T-NAMESELF>.",costStr));
			msg.addTrailerMsg(newMsg);
			newMsg=CMClass.getMsg(observer,I,null,CMMsg.MSG_EXAMINE,L("<S-NAME> examine(s) <T-NAME> very closely."));
			msg.addTrailerMsg(newMsg);
			final StringBuffer up=new StringBuffer(I.name(observer)+" is made of "+RawMaterial.CODES.NAME(I.material()).toLowerCase()+".\n\r");
			if((I instanceof Armor)&&(((Armor)I).phyStats().height()>0))
				up.append("It is a size "+((Armor)I).phyStats().height()+".\n\r");
			final int weight=I.phyStats().weight();
			if((weight!=I.basePhyStats().weight())&&(I instanceof Container))
				up.append("It weighs "+I.basePhyStats().weight()+" pounds empty and "+weight+" pounds right now.\n\r");
			else
				up.append("It weighs "+weight+" pounds.\n\r");
			if(I instanceof Weapon)
			{
				final Weapon w=(Weapon)I;
				up.append("It is a "+Weapon.CLASS_DESCS[w.weaponClassification()].toLowerCase()+" weapon.\n\r");
				up.append("It does "+Weapon.TYPE_DESCS[w.weaponType()].toLowerCase()+" damage.\n\r");
			}
			up.append(I.secretIdentity());
			newMsg=CMClass.getMsg(observer,null,null,CMMsg.MSG_SPEAK,L("^T<S-NAME> say(s) '@x1'^?.",up.toString()));
			msg.addTrailerMsg(newMsg);
			newMsg=CMClass.getMsg(observer,source,I,CMMsg.MSG_GIVE,L("<S-NAME> give(s) <O-NAME> to <T-NAMESELF>."));
			msg.addTrailerMsg(newMsg);
			newMsg=CMClass.getMsg(observer,I,null,CMMsg.MSG_DROP,null);
			msg.addTrailerMsg(newMsg);
		}
	}
}
