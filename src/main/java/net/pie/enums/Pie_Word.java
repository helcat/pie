package net.pie.enums;
/** **********************************************<br>
 * PIE Pixel Image Encode<br>
 * pixel.image.encode@gmail.com
 */

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/** ***************************************************<br>
 * Poor Mans translator : There are better ways of doing this but it works.
 */

public enum Pie_Word {
	NO_OPTIONS (
			"Error no configuration options", "Erreur aucune option de configuration",
			"Errore nessuna opzione di configurazione", "Error sin opciones de configuración"),

	CONFIGURATION_ERROR ("Configuration Error","Erreur de configuration","Errore di configurazione","Error de configuración"),
	ENTER_CERTIFICATE ("Enter Certificate", "Entrez le certificat", "Inserisci il certificato", "Ingresar certificado"),
	ENTER_DIRECTORY("Enter Directory", "Entrer dans le répertoire", "Entra nella Rubrica", "Ingresar al directorio"),
	ENTER_SOURCE_FILE ("Enter Source File", "Entrez le fichier source", "Inserisci il file di origine", "Ingrese el archivo fuente"),
	ENTER_SOURCE_TEXT("Enter Source Text","Saisir le texte source","Inserisci il testo sorgente","Ingrese el texto fuente"),
	ENTER_FILE_NAME("Enter A File Name","Entrez un nom de fichier","Inserisci un nome file","Ingrese un nombre de archivo"),

	CERTIFICATE_NOT_CREATED ("Was unable to create a certificate file", "Impossible de créer un fichier de certificat",
					"Impossibile creare un file di certificato", "No se pudo crear un archivo de certificado"),

	CERTIFICATE_CREATED ("Certificate created", "Certificat créé", "Certificato creato", "Certificado creado"),

	ENCRYPTION_ERROR_NO_KEY ("Encryption Error - Cannot create key", "Erreur de cryptage - Impossible de créer la clé",
			"Errore di crittografia: impossibile creare la chiave", "Error de cifrado: no se puede crear la clave"),

	ENCRYPTION_PASS_SIZE_ERROR ("Encrypted password size less than 6 long", "Taille du mot de passe crypté inférieure à 6",
			"La dimensione della password crittografata è inferiore a 6", "Tamaño de contraseña cifrada inferior a 6"),

	ENCRYPTION_FILE_INVALID ("Encryption Certificate is not a file", "Le certificat de cryptage n'est pas un fichier",
			"Il certificato di crittografia non è un file","El certificado de cifrado no es un archivo"),

	ENCRYPTION ("Encryption", "Chiffrement", "Crittografia", "Cifrado"),

	ENCRYPTION_PHRASE ("Encryption Phrase", "Phrase de cryptage", "Frase di crittografia", "Frase de cifrado"),

	ENCRYPTION_ERROR ("Encryption Error", "Erreur de cryptage", "Errore di crittografia", "Error de cifrado"),

	CERTIFICATE ("Certificate", "Certificat", "Certificato", "Certificado"),

	MAKE_CERTIFICATE ("Make_Certificate", "Faire_un_certificat", "Crea_certificato", "Hacer_certificado"),

	VERIFY_CERTIFICATE ("Verify_Certificate", "Vérifier_le_certificat", "Verifica_certificato",
		"Verificar_certificado"),

	CERTIFICATE_VERIFIED ("Certificate Verified","Certificat vérifié", "Certificato verificato",
			"Certificado verificado"),

	NO_SOURCE ("No Source","Aucune source", "Nessuna fonte", "Sin fuentes"),

	OUTPUT_REQUIRED ("Output Required", "Sortie requise","Uscita richiesta", "Salida requerida"),

	ENCODING_STRING_REQUIRED ("Encoding String is required", "La chaîne d'encodage est requise",
			 "La stringa di codifica è obbligatoria", "Se requiere cadena de codificación"),

	NO_DECODE_OBJECT ("No Decode Object Found", "Aucun objet de décodage trouvé", "Nessun oggetto di decodifica trovato",
			"No se encontró ningún objeto de decodificación"),

	UNABLE_TO_DECODE ("Unable to decode object", "Impossible de décoder l'objet", "Impossibile decodificare l'oggetto",
			"No se puede decodificar el objeto"),

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

	UNKNOWN ("Unknown", "Inconnue", "Sconosciuto", "Desconocido"),

	URL_ERROR ("URL Error", "Erreur d'URL", "Errore nell'URL", "Error de URL"),

	NO_BROWSER ("Could not find web browser", "Impossible de trouver le navigateur Web", "Impossibile trovare il browser web",
			"No se pudo encontrar el navegador web"),

	DECODING_FAILED_SOURCE ("Decoding Failed : Source required", "Échec du décodage : source requise",
			"Decodifica non riuscita: sorgente richiesta", "Error de decodificación: fuente requerida"),

	WRITING_TO_STREAM_ERROR ("Writing to stream error", "Erreur d'écriture dans le flux", "Errore di scrittura nello streaming",
			"Error al escribir en la secuencia"),

	DECODING_COMPLETE ("Decoding Complete", "Décodage terminé", "Decodifica completata", "Decodificación completa"),

	DECRYPTION_REQUIRED ("Decryption Required", "Décryptage requis", "Decrittazione richiesta", "Se requiere descifrado"),

	DECRYPTION_FAILED ("Decryption Failed", "Échec du décryptage", "Decrittografia non riuscita","Error de descifrado"),

	INVALID_ENCODED_IMAGE ("Invalid Encoded Image", "Image codée invalide", "Immagine codificata non valida", "Imagen codificada no válida"),

	INVALID_ENCODED_FILE ("Invalid Encoded File", "Fichier codé invalide", "File codificato non valido", "Archivo codificado no válido"),
	INVALID_FILE ("Invalid File", "Fichier invalide", "File non valido", "Archivo no válido"),

	DECODING_ERROR ("Decoding Error", "Erreur de décodage","Errore di decodifica", "Error de decodificación"),
	DECODING_OUTPUT_REQUIRED ("Decoding Output Required", "Sortie de décodage requise", "Uscita di decodifica richiesta", "Salida de decodificación requerida"),

	DECODING_COLLECTING_FILE ("Decoding : Collecting file", "Décodage : Collecte de fichier", "Decodifica: raccolta di file",
			"Decodificación: recopilación de archivos"),

	ERROR ("Error","Erreur", "Errore", "Error"),
	OUTPUT ("output", "sortir", "produzione", "producción"),
	ALREADY_EXISTS ("Already Exists", "Existe déjà", "Esiste già", "Ya existe"),

	CREATING_STREAM_ERROR ("Creating stream Error", "Erreur de création de flux", "Errore nella creazione del flusso",
			"Error al crear secuencia"),

	MAX_FILES_EXCEEDED ("System protection. Max files exceeded", "Proteccion del sistema. Se superó el número máximo de archivos",
		"Protezione del sistema. È stato superato il numero massimo di file", "Proteccion del sistema. Se superó el número máximo de archivos"),

	ENCODING_FAILED ("Encoding Failed", "Échec de l'encodage", "Codifica non riuscita", "Error de codificación"),
	ENCODING_OUTPUT_REQUIRED("Encoding Failed Output Required", "Échec de l'encodage Sortie requise", "Codifica output non riuscita obbligatoria",
			"Error de codificación Salida requerida"),
	ENCODING_INVALID_OUTPUT ("Encoding Invalid Output", "Encodage d'une sortie invalide", "Codifica output non valida", "Codificación de salida no válida"),

	ENCODING_COMPLETE ("Encoding Complete", "Encodage terminé", "Codifica completata", "Codificación completa"),

	UNABLE_To_READ_FILE ("Unable to read file", "Impossible de lire le fichier", "Impossibile leggere il file",
			"Imposible leer el archivo"),

	ENCODED_IMAGE_WAS_NOT_SAVED ("Encoded image was not saved", "L'image codée n'a pas été enregistrée",
			"L'immagine codificata non è stata salvata", "La imagen codificada no se guardó"),

	UNEXPRECTED_FILE_COUNT ("Unexpected file count.", "Nombre de fichiers inattendu", "Conteggio file imprevisto",
			"Recuento de archivos inesperado"),

	IMAGE_SIZE_WOULD_BE ("Image Size Would be", "La taille de l'image serait", "La dimensione dell'immagine sarebbe",
			"El tamaño de la imagen sería"),

	MAX_SIZE_IS ("Maximum Size Is", "La taille maximale est", "La dimensione massima è","El tamaño máximo es"),

	INCREASE_MEMORY ("Increase Memory and / or Maximum Image Size. Encode mode", "Augmentez la mémoire et/ou la taille maximale de l'image. Mode d'encodage",
			"Aumenta la memoria e/o la dimensione massima dell'immagine. Modalità di codifica",
			"Aumentar la Memoria y/o el Tamaño Máximo de Imagen. Modo de codificación"),

	FAILED ("Failed", "Échoué", "Fallito", "Fallido"),

	MAX_IMAGE_SIZE_NOT_SET ("Maximum Image Size Is Not Set", "La taille maximale de l'image n'est pas définie",
		"La dimensione massima dell'immagine non è impostata", "El tamaño máximo de imagen no está establecido"),

	SHAPE("Shape", "Forme", "Forma", "Forma"),
	RECTANGLE ("Rectangle", "Rectangle", "Rettangolo", "Rectángulo"),
	SQUARE ("Square", "Carré", "Piazza", "Cuadrado"),

	ONE ("One", "Un", "Uno", "Uno"),
	TWO ("Two", "Deux", "Due", "Dos"),

	ENCODE ("Encode", "Encoder", "Codificare", "Codificar"),
	DECODE ("Decode", "Décoder", "Decodificare", "Descodificar"),

	BASE64_ENCODE("base64_encode", "encodage_bBase64", "codifica_base64", "codificación_base64"),
	BASE64_DECODE("base64_decode", "décodage_base64", "decodifica_base64", "decodificación_base64"),

	DIRECTORY ("Directory", "Annuaire", "Direttorio", "Directorio"),

	MODE ("Mode", "Mode", "Modalità", "Modo" ),
	Max_MB ("MaxMB", "MaxMB", "MaxMB", "MáxMB"),

	LOG ("Log", "Enregistrer", "Tronco_d'albero", "Registro"),
	INFORMATION ("Information", "Information", "Informazione", "Información"),
	OFF ("Off", "Désactivé", "Spento", "Apagado"),
	SEVERE ("Severe", "Grave", "Acuto", "Severo"),

	OVERWRITE ("Overwrite", "Écraser", "Sovrascrivi", "Sobrescribir"),

	DEFAULT ("Default", "Défaut", "Predefinito", "Por defecto"),

	LEAVE_BLANK("Leave blank if not required","Laisser vide si ce n'est pas nécessaire",
			"Lasciare vuoto se non richiesto", "Dejar en blanco si no es necesario"),

	NAME ("Name","Nom","Nome","Nombre"),

	CONSOLE ("Console", "Console", "Consolle", "Consola"),

	PREFIX ("Prefix", "Préfixe", "Prefisso", "Prefijo"),

	BASE64_FILE("base64_file", "fichier_base64", "file_base64", "archivo_base64"),

	BASE64 ("base64", "base64", "base64", "base64"),

	HELP ("help" , "aide", "aiuto", "ayuda"),

	PIE_BLOG ("https://virtuware.blogspot.com/" , "https://virtuware.blogspot.com/",
			"https://virtuware.blogspot.com/", "https://virtuware.blogspot.com/")
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

	/** *****************************************************************<br>
	 * Find out if the user has entered one of the given translations of a given Pie_Word
	 * @param word Pie_Word
	 * @param wording String
	 * @return boolean
	 */
	public static boolean is_in_Translation(Pie_Word word, String wording) {
		return (word.getEn().equalsIgnoreCase(wording.toLowerCase()) ||
				word.getFr().equalsIgnoreCase(wording.toLowerCase()) ||
				word.getIt().equalsIgnoreCase(wording.toLowerCase()) ||
				word.getEs().equalsIgnoreCase(wording.toLowerCase()));
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

	public static List<String> available_languages() {
		return Arrays.asList("en", "fr", "it", "es");
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
