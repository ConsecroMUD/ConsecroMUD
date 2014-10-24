package com.suscipio_solutions.consecro_mud.Items.Software;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;


public class GenShipProgram extends GenSoftware
{
	@Override public String ID(){	return "GenShipProgram";}

	protected String circuitKey="";
	protected String readableText="";

	public GenShipProgram()
	{
		super();
		setName("a software disk");
		setDisplayText("a small disk sits here.");
		setDescription("It appears to be a general software program.");
	}

	@Override
	public void setCircuitKey(String key)
	{
		circuitKey=(key==null)?"":key;
	}

	@Override public TechType getTechType() { return TechType.SHIP_SOFTWARE; }

	@Override public String getParentMenu() { return ""; }
	@Override public String getInternalName() { return "";}

	@Override
	public boolean isActivationString(String word)
	{
		return super.isActivationString(word);
	}

	@Override
	public boolean isDeActivationString(String word)
	{
		return super.isDeActivationString(word);
	}

	@Override
	public boolean isCommandString(String word, boolean isActive)
	{
		return super.isCommandString(word, isActive);
	}

	@Override
	public String getActivationMenu()
	{
		return super.getActivationMenu();
	}

	@Override
	public boolean checkActivate(MOB mob, String message)
	{
		return super.checkActivate(mob, message);
	}

	@Override
	public boolean checkDeactivate(MOB mob, String message)
	{
		return super.checkDeactivate(mob, message);
	}

	@Override
	public boolean checkTyping(MOB mob, String message)
	{
		return super.checkTyping(mob, message);
	}

	@Override
	public boolean checkPowerCurrent(int value)
	{
		return super.checkPowerCurrent(value);
	}

	@Override
	public void onActivate(MOB mob, String message)
	{
		super.onActivate(mob, message);
	}

	@Override
	public void onDeactivate(MOB mob, String message)
	{
		super.onDeactivate(mob, message);
	}

	@Override
	public void onTyping(MOB mob, String message)
	{
		super.onTyping(mob, message);
	}

	@Override
	public void onPowerCurrent(int value)
	{
		super.onPowerCurrent(value);
	}

}
