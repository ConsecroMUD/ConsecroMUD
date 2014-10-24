package com.suscipio_solutions.consecro_mud.Items.interfaces;


public interface Recipe extends Item
{
	public String getCommonSkillID();
	public void setCommonSkillID(String ID);
	public int getTotalRecipePages();
	public void setTotalRecipePages(int numRemaining);
	public String[] getRecipeCodeLines();
	public void setRecipeCodeLines(String[] lines);
}

