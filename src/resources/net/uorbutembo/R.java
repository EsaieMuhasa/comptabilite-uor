package resources.net.uorbutembo;

public class R {
	
	/**
	 * Renvoie le chemain vers l'icone dont le nom est en parametre
	 * nous syposont que l'incone doit avoir l'extension .png
	 * @param name
	 * @return
	 */
	public static final String getIcon (String name) {
		String icon = R.class.getResource("/resources/net/uorbutembo/icons/"+name+".png").getFile();
		return icon;
	}
	
	public static final String getIcon (String name, String ext) {
		String icon = R.class.getResource("/resources/net/uorbutembo/icons/"+name+"."+ext).getFile();
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
