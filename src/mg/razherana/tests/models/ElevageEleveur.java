// Generated Model using mg.razherana.generator
// Happy Codingg!

package mg.razherana.tests.models;

import mg.razherana.lorm.annot.columns.Column;
import mg.razherana.lorm.annot.general.Table;
import mg.razherana.lorm.Lorm;

@Table("elevage_eleveur")
public class ElevageEleveur extends Lorm<ElevageEleveur> { 
	@Column(value = "eleveur_email", getter = "getEleveuremail", setter = "setEleveuremail")
	private String eleveurEmail;

	@Column(value = "eleveur_password", getter = "getEleveurpassword", setter = "setEleveurpassword")
	private String eleveurPassword;

	@Column(value = "eleveur_id", primaryKey = true, getter = "getEleveurid", setter = "setEleveurid")
	private int eleveurId;

	@Column(value = "eleveur_prenom", getter = "getEleveurprenom", setter = "setEleveurprenom")
	private String eleveurPrenom;

	@Column(value = "eleveur_nom", getter = "getEleveurnom", setter = "setEleveurnom")
	private String eleveurNom;

	public String getEleveuremail() { return eleveurEmail; }

	public void setEleveuremail(String eleveurEmail) { this.eleveurEmail = eleveurEmail; }

	public String getEleveurpassword() { return eleveurPassword; }

	public void setEleveurpassword(String eleveurPassword) { this.eleveurPassword = eleveurPassword; }

	public int getEleveurid() { return eleveurId; }

	public void setEleveurid(int eleveurId) { this.eleveurId = eleveurId; }

	public String getEleveurprenom() { return eleveurPrenom; }

	public void setEleveurprenom(String eleveurPrenom) { this.eleveurPrenom = eleveurPrenom; }

	public String getEleveurnom() { return eleveurNom; }

	public void setEleveurnom(String eleveurNom) { this.eleveurNom = eleveurNom; }
}