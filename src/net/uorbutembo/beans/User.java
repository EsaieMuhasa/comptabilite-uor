/**
 * 
 */
package net.uorbutembo.beans;

/**
 * @author Esaie MUHASA
 *
 */
public abstract class User extends DBEntity {
	private static final long serialVersionUID = -6025563457537816603L;
	
	/**
	 * 
	 * @author Esaie MUHASA
	 * Le sexe d'un utilsiateur
	 */
	public static class Kind {
		private String shortName;
		private String fullName;
		
		/**
		 * @param shortName
		 * @param fullName
		 */
		public Kind(String shortName, String fullName) {
			super();
			this.shortName = shortName;
			this.fullName = fullName;
		}
		
		/**
		 * @return the shortName
		 */
		public String getShortName() {
			return shortName;
		}
		/**
		 * @param shortName the shortName to set
		 */
		public void setShortName(String shortName) {
			this.shortName = shortName;
		}
		/**
		 * @return the fullName
		 */
		public String getFullName() {
			return fullName;
		}
		/**
		 * @param fullName the fullName to set
		 */
		public void setFullName(String fullName) {
			this.fullName = fullName;
		}

		@Override
		public String toString() {
			return  "["+shortName+"] "+fullName;
		}
	}
	
	public static final Kind [] KINDS = new Kind [] {new Kind("M", "Masculin"), new Kind("F", "Feminin")}; 
	
	/**
	 * Le nom de l'utilisateur
	 */
	protected String name;
	
	/**
	 * Le post-nom de l'utilisateur
	 */
	protected String postName;
	
	/**
	 * Le prenom de l'utilsateur
	 */
	protected String firstName;
	
	/**
	 * l'adresse dela photo de l'utilsateur
	 */
	protected String picture;
	
	/**
	 * L'adresse e-mail de l'utilsateur
	 */
	protected String email;
	
	/**
	 * Le mot de passe de connexion d'un utilisateur
	 */
	protected String password;
	
	/**
	 * le numero de telephone d'un utilisateur
	 */
	protected String telephone;
	
	/**
	 * Le sexe d'un  utilisateur 
	 */
	protected Kind kind;

	/**
	 * 
	 */
	public User() {
		super();
	}

	/**
	 * @param id
	 */
	public User(Long id) {
		super(id);
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the postName
	 */
	public String getPostName() {
		return postName;
	}

	/**
	 * @param postName the postName to set
	 */
	public void setPostName(String postName) {
		this.postName = postName;
	}

	/**
	 * @return the firstName
	 */
	public String getFirstName() {
		return firstName;
	}

	/**
	 * @param firstName the firstName to set
	 */
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	/**
	 * @return the picture
	 */
	public String getPicture() {
		return picture;
	}

	/**
	 * @param picture the picture to set
	 */
	public void setPicture(String picture) {
		if(picture == null || picture.isEmpty()) 
			return;
		this.picture = picture;
	}

	/**
	 * @return the email
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * @param email the email to set
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * @param password the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * @return the telephone
	 */
	public String getTelephone() {
		return telephone;
	}

	/**
	 * @param telephone the telephone to set
	 */
	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}
	
	public String getFullName () {
		return this.name+" "+this.postName+" "+this.firstName;
	}
	
	/**
	 * @return the kind
	 */
	public Kind getKind() {
		return kind;
	}

	/**
	 * @param kind the kind to set
	 */
	public void setKind(String kind) {
		if(!KINDS[0].getShortName().equals(kind) && !KINDS[1].getShortName().equals(kind)) {
			throw new IllegalArgumentException("valeur invalide en parametre de la methode setKind() => "+kind);
		}
		this.kind = KINDS[0].getShortName().equals(kind)? KINDS[0] : KINDS[1];
	}

	@Override
	public String toString() {
		return this.getFullName();
	}

}
