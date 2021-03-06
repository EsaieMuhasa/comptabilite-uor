/**
 * 
 */
package net.uorbutembo.beans;

/**
 * @author Esaie MUHASA
 * classe d'etude (L1, L2,...).
 * Dans la logique de fonction de l'universite, une classe d'etude n'est pas une orentation
 * mais dans le sens programmatique celle-ci l'est car elle a les meme comportement qu'une orientation
 */
public class StudyClass extends Orientation {
	private static final long serialVersionUID = -6721949263252407143L;

	/**
	 * 
	 */
	public StudyClass() {
		super();
	}

	/**
	 * @param id
	 */
	public StudyClass(long id) {
		super(id);
	}

	@Override
	public boolean equals (Object obj) {
		if (obj instanceof StudyClass) {
			StudyClass s = (StudyClass) obj;
			return s.id == id;
		}
		return super.equals(obj);
	}

}
