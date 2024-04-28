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

	OVERRIDE_FILE_REQUIRED ("Was not created, Set config to overwrite file is required",
			"N'a pas été créé, définir la configuration pour écraser le fichier est requis",
		"Non è stato creato, è necessario impostare la configurazione per sovrascrivere il file",
			"No se creó, se requiere configurar la configuración para sobrescribir el archivo"),

	UNABLE_TO_WRITE_ENCODED_IMAGE ("Unable to write encoded image", "Impossible d'écrire l'image codée", "Impossibile scrivere l'immagine codificata",
			"No se puede escribir la imagen codificada"),

	FILE_EXISTS ("File Exists", "Le fichier existe", "Il file esiste", "El archivo existe"),

	OVERWRITING_File("Overwriting File", "Fichier d'écrasement", "Sovrascrittura del file", "Sobrescribir archivo"),

	TEXT ("text", "texte", "testo", "texto"),

	URL_ERROR ("URL Error", "Erreur d'URL", "Errore nell'URL", "Error de URL"),

	NO_BROWSER ("Could not find web browser", "Impossible de trouver le navigateur Web", "Impossibile trovare il browser web",
			"No se pudo encontrar el navegador web"),

	DECODING_FAILED_SOURCE ("Decoding Failed : Source required", "Échec du décodage : source requise",
			"Decodifica non riuscita: sorgente richiesta", "Error de decodificación: fuente requerida"),

	DECODING_FAILED_DEST_SOURCE ("Decoding Failed : Source destination required", "Échec du décodage : source destination requise",
			"Decodifica non riuscita: destinazione di origine richiesta", "Error de decodificación: se requiere destino de origen"),

	DEST_FILE_DELETED ("Destination File Deleted", "Fichier de destination supprimé",
			"Archivo de destino eliminado", "Decodificado a variable - Disponible usando"),

	DECODED_TO_VALUE_USING ("Decoded To Variable - Available using", "Décodé en variable - Disponible en utilisant",
	"Decodificato in variabile: disponibile utilizzando","Decodificado a variable - Disponible usando" ),

	WRITING_TO_STREAM_ERROR ("Writing to stream error", "Erreur d'écriture dans le flux", "Errore di scrittura nello streaming",
			"Error al escribir en la secuencia"),

	DECODING_COMPLETE ("Decoding Complete", "Décodage terminé", "Decodifica completata", "Decodificación completa"),

	DECRYPTION_REQUIRED ("Decryption Required", "Décryptage requis", "Decrittazione richiesta", "Se requiere descifrado"),

	BASE_ENCODING_ERROR ("Base Encoding Error", "Erreur d'encodage de base", "Errore di codifica di base", "Error de codificación base"),

	INVALID_ENCODED_IMAGE ("Invalid Encoded Image", "Image codée invalide", "Immagine codificata non valida", "Imagen codificada no válida"),

	INVALID_ENCODED_FILE ("Invalid Encoded File", "Fichier codé invalide", "File codificato non valido", "Archivo codificado no válido"),

	DECODING_ERROR ("Decoding Error", "Erreur de décodage","Errore di decodifica", "Error de decodificación"),

	DECODING_COLLECTING_FILE ("Decoding : Collecting file", "Décodage : Collecte de fichier", "Decodifica: raccolta di file",
			"Decodificación: recopilación de archivos"),

	CANNOT_BE_USED_WITH ("cannot be used with", "ne peut pas être utilisé avec", "non può essere utilizzato con", "no se puede utilizar con"),

	ERROR ("Error","Erreur", "Errore", "Error"),

	ALREADY_EXISTS ("Already Exists", "Existe déjà", "Esiste già", "Ya existe"),

	CREATING_STREAM_ERROR ("Creating stream Error", "Erreur de création de flux", "Errore nella creazione del flusso",
			"Error al crear secuencia"),

	MAX_FILES_EXCEEDED ("System protection. Max files exceeded", "Proteccion del sistema. Se superó el número máximo de archivos",
		"Protezione del sistema. È stato superato il numero massimo di file", "Proteccion del sistema. Se superó el número máximo de archivos"),

	ENCODING_FAILED ("Encoding Failed", "Échec de l'encodage", "Codifica non riuscita", "Error de codificación"),

	ENCODING_COMPLETE ("Encoding Complete", "Encodage terminé", "Codifica completata", "Codificación completa"),

	COMPRESSION_FAILED ("Compression Failed", "Échec de la compression", "Compressione non riuscita", "Fallo de compresión"),

	UNABLE_To_READ_FILE ("Unable to read file", "Impossible de lire le fichier", "Impossibile leggere il file",
			"Imposible leer el archivo"),

	ENCODED_IMAGE_WAS_NOT_SAVED ("Encoded image was not saved", "L'image codée n'a pas été enregistrée",
			"L'immagine codificata non è stata salvata", "La imagen codificada no se guardó"),

	UNEXPRECTED_FILE_COUNT ("Unexpected file count.", "Nombre de fichiers inattendu", "Conteggio file imprevisto",
			"Recuento de archivos inesperado"),

	GENERATING_IMAGE_SIZE ("Generating Image Size", "Génération de la taille de l'image", "Generazione della dimensione dell'immagine",
			"Generando tamaño de imagen"),

	IMAGE_SIZE_WOULD_BE ("Image Size Would be", "La taille de l'image serait", "La dimensione dell'immagine sarebbe",
			"El tamaño de la imagen sería"),

	MAX_SIZE_IS ("Maximum Size Is", "La taille maximale est", "La dimensione massima è","El tamaño máximo es"),

	INCREASE_MEMORY ("Increase Memory and / or Maximum Image Size. Encode mode", "Augmentez la mémoire et/ou la taille maximale de l'image. Mode d'encodage",
			"Aumenta la memoria e/o la dimensione massima dell'immagine. Modalità di codifica",
			"Aumentar la Memoria y/o el Tamaño Máximo de Imagen. Modo de codificación"),

	FAILED ("Failed", "Échoué", "Fallito", "Fallido"),

	MAX_IMAGE_SIZE_NOT_SET ("Maximum Image Size Is Not Set", "La taille maximale de l'image n'est pas définie",
		"La dimensione massima dell'immagine non è impostata", "El tamaño máximo de imagen no está establecido")
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

	public static String translate(Pie_Word word) {
		return translate(word, Locale.getDefault().getLanguage().toLowerCase());
	}

	public static String translate(Pie_Word word, String langauge) {
		if (langauge == null)
			langauge = Locale.getDefault().getLanguage().toLowerCase();

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
