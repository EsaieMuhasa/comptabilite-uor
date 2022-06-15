package net.uorbutembo.tools;

public class R {
	
	/**
	 * Renvoie le chemain vers l'icone dont le nom est en parametre
	 * nous syposont que l'incone doit avoir l'extension .png
	 * @param name
	 * @return
	 */
	public static final String getIcon (String name) {
		String icon = "icon/"+name+".png";
		return icon;
	}
	
	public static final String getIcon (String name, String ext) {
		String icon = "icon/"+name+"."+ext;
		return icon;
	}
	
	/**
	 * Recuperation de la configiration de 
	 * @return
	 */
	public static final Config getConfig () {
		return Config.getInstance();
	}

}
