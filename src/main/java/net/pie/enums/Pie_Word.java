package net.pie.enums;

import java.util.Locale;

public enum Pie_Word {
	NO_OPTIONS (
			"Error no configuration options", "Erreur aucune option de configuration",
			"Errore nessuna opzione di configurazione", "Error sin opciones de configuración"),

	INVALID_FOLDER ("Invalid directory" ,"Répertoire invalide" , "Cartella non valida", "Directorio invalido"),

	CERTIFICATE_NOT_CREATED ("Was unable to create a certificate file", "Impossible de créer un fichier de certificat",
					"Impossibile creare un file di certificato", "No se pudo crear un archivo de certificado"),

	CERTIFICATE_CREATED ("Certificate created", "Certificat créé", "Certificato creato", "Certificado creado"),

	ENCRYPTION_ERROR_NO_KEY ("Encryption Error - Cannot create key", "Erreur de cryptage - Impossible de créer la clé",
			"Errore di crittografia: impossibile creare la chiave", "Error de cifrado: no se puede crear la clave"),

	ENCRYPTION_PASS_ERROR ("Encrypted password is invalid", "Le mot de passe crypté n'est pas valide",
			"La password crittografata non è valida", "La contraseña cifrada no es válida"),

	ENCRYPTION_FILE_INVALID ("Encryption Certificate is not a file", "Le certificat de cryptage n'est pas un fichier",
			"Il certificato di crittografia non è un file","El certificado de cifrado no es un archivo"),

	PIE_CERTIFICATE ("pie_certificate", "pie_certificat", "pie_certificato", "pie_certificado")
	;

	private String en;
	private String fr;
	private String it;
	private String es;

	private Pie_Word(String en, String fr, String it, String es) {
		setEn(en);
		setFr(fr);
		setIt(it);
		setEs(es);
	}

	public static String translate(Pie_Word word, String langauge) {
		if (langauge == null)
			langauge = "en";

		switch (langauge.toLowerCase()) {
			case "en":
				return word.getEn();
			case "fr":
				return word.getFr();
			case "it":
				return word.getIt();
			case "es":
				return word.getEs();
			default:
				return word.getEn();
		}
	}

	public String getEn() {
		return en;
	}
	public void setEn(String en) {
		this.en = en;
	}
	public String getFr() {
		return fr;
	}
	public void setFr(String fr) {
		this.fr = fr;
	}
	public String getIt() {	return it; }
	public void setIt(String it) { this.it = it; }
	public String getEs() {	return es; }
	public void setEs(String es) { this.es = es; }
}
