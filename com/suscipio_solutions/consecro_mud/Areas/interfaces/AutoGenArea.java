package com.suscipio_solutions.consecro_mud.Areas.interfaces;

import java.util.Map;

public interface AutoGenArea extends Area
{
	/**
	 * Get the path to the xml file to use to generate this areas rooms
	 * @return the path
	 */
	public String getGeneratorXmlPath();

	/**
	 * Set the path to the xml file to use to generate this areas rooms
	 * @param path the resource path
	 */
	public void setGeneratorXmlPath(String path);

	/**
	 * Get a miscellaneous, xml-specific set of other vars to set
	 * when generating a new area
	 * @return the variable mappings
	 */
	public Map<String,String> getAutoGenVariables();

	/**
	 * Set a miscellaneous, xml-specific set of other vars to set
	 * when generating a new area
	 * @param vars the variable mappings
	 */
	public void setAutoGenVariables(Map<String,String> vars);

	/**
	 * Set a miscellaneous, xml-specific set of other vars to set
	 * when generating a new area. Format is VAR=VALUE VAR2="VALUE"
	 * @param vars the variable mappings
	 */
	public void setAutoGenVariables(String vars);
}
