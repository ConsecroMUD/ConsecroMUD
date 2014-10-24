package com.suscipio_solutions.consecro_mud.Items.interfaces;
import java.util.List;

import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;


public interface Perfume
{
	public List<String> getSmellEmotes(Perfume me);
	public String getSmellList();
	public void setSmellList(String list);
	public void wearIfAble(MOB mob, Perfume me);
}
