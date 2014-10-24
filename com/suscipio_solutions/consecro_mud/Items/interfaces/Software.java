package com.suscipio_solutions.consecro_mud.Items.interfaces;


public interface Software extends Item, Technical
{
	public String getParentMenu();
	public String getInternalName();
	public boolean isActivationString(String word);
	public boolean isDeActivationString(String word);
	public boolean isCommandString(String word, boolean isActive);
	public String getActivationMenu();
	public void addScreenMessage(String msg);
	public String getScreenMessage();
	public String getCurrentScreenDisplay();
	public void setCircuitKey(String key);
}

