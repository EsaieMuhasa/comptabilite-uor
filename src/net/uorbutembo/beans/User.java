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
	
	@Override
	public String toString() {
		return this.getFullName();
	}

}
