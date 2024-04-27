package net.pie.enums;

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

	PIE_CERTIFICATE ("pie_certificate", "pie_certificat", "pie_certificato", "pie_certificado"),

	ENCRYPTION_ERROR ("Encryption Error", "Erreur de cryptage", "Errore di crittografia", "Error de cifrado"),

	NO_SOURCE ("No Source","Aucune source", "Nessuna fonte", "Sin fuentes"),

	ENCODING_STRING_REQUIRED ("Encoding String is required", "La chaîne d'encodage est requise",
			 "La stringa di codifica è obbligatoria", "Se requiere cadena de codificación"),

	INVALID_FILE ("Invalid File", "Fichier non valide", "File non valido", "Archivo inválido"),

	NO_DECODE_OBJECT ("No Decode Object Found", "Aucun objet de décodage trouvé", "Nessun oggetto di decodifica trovato",
			"No se encontró ningún objeto de decodificación"),

	UNABLE_TO_DECODE ("Unable to decode object", "Impossible de décoder l'objet", "Impossibile decodificare l'oggetto",
			"No se puede decodificar el objeto"),

	INVALID_DECODING_DESTINATION ("Invalid Decoding destination", "Destination de décodage invalide",
			"Destinazione di decodifica non valida", "Destino de decodificación no válido"),

	DOWNLOAD_FAILED ("Download Failed", "Échec du téléchargement", "Scaricamento fallito", "Descarga fracasó"),

	MISSING_FILE_NAME ("Missing file name for download", "Nom de fichier manquant à télécharger",
			"Nome file mancante per il download", "Falta el nombre del archivo para descargar"),

	NO_SOURCE_SIZE ("No Source Size Available", "Aucune taille source disponible", "Nessuna dimensione sorgente disponibile",
			"No hay tamaño de fuente disponible"),

	LOADING_FILE ("Loading File", "Chargement du fichier", "Caricamento file", "Cargando archivo"),

	FILE ("File", "Déposer", "File", "Archivo"),

	MISSING_FILE_ADDON ("is missing. Unable to continue.", "est manquant. Impossible de continuer.", "manca. Impossibile continuare.",
		"Está perdido. No se puede continuar."),

	UNABLE_TO_READ_FILE ("Unable to read file", "Impossible de lire le fichier", "Impossibile leggere il file",
			"Imposible leer el archivo"),

	DOWNLOADING_FILE ("Downloading File", "Téléchargement du fichier", "Download del file", "Descargando archivo"),

	UNABLE_TO_OPEN_STREAM ("Unable to open stream", "Impossible d'ouvrir le flux", "Impossibile aprire lo streaming",
			"No se puede abrir la transmisión"),

	USING_INPUTSTREAM ("Using Input-stream", "Utilisation du flux d'entrée", "Utilizzo del flusso di input", "Usando flujo de entrada"),

	UNABLE_TO_CREATE_ZIP_ADDITIONAL ("Unable to create zip flie for additional files", "Impossible de créer un fichier zip pour des fichiers supplémentaires",
			"Impossibile creare un file zip per file aggiuntivi", "No se puede crear un archivo zip para archivos adicionales"),

	ENCODED_FILE_EXISTS ("Encoded file already exists : New encoded file", "Le fichier encodé existe déjà : Nouveau fichier encodé",
			"Il file codificato esiste già: nuovo file codificato", "El archivo codificado ya existe: nuevo archivo codificado"),

	OVERRIDE_FILE_REQUIRED ("Was not created, Set config to overwrite file is required", "N'a pas été créé, définir la configuration pour écraser le fichier est requis",
		"Non è stato creato, è necessario impostare la configurazione per sovrascrivere il file", "No se creó, se requiere configurar la configuración para sobrescribir el archivo"),

	UNABLE_TO_WRITE_ENCODED_IMAGE ("Unable to write encoded image", "Impossible d'écrire l'image codée", "Impossibile scrivere l'immagine codificata",
			"No se puede escribir la imagen codificada"),

	FILE_EXISTS ("File Exists", "Le fichier existe", "Il file esiste", "El archivo existe"),

	OVERWRITING_File("Overwriting File", "Fichier d'écrasement", "Sovrascrittura del file", "Sobrescribir archivo"),

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
