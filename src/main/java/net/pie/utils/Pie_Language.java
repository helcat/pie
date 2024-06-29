package net.pie.utils;
/** **********************************************<br>
 * PIE Pixel Image Encode
 * @author terry clarke
 * @since 1.0
 * @version 1.3
 * Copyright Terry Clarke 2024
 */

import net.pie.enums.Pie_Word;
import java.util.Locale;

public class Pie_Language {
	private String code;

	public Pie_Language(String code) {
		if (Pie_Word.available_languages().contains(code))
			setCode(code);
		else
			setCode(Pie_Word.available_languages().contains(Locale.getDefault().getLanguage().toLowerCase()) ?
					Locale.getDefault().getLanguage().toLowerCase() : "en");
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}
}