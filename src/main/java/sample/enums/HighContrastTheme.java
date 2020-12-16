package sample.enums;

/**
 * enumeration of high contrast themes
 * @author mzl
 * @Description HighContrastTheme
 * @Data  2020/10/21 10:12
 */
public enum HighContrastTheme {
	//high contrast themes
	WHITEONBLACK("WHITEONBLACK"),
	BLACKONWHITE("BLACKONWHITE"),
	YELLOWONBLACK("YELLOWONBLACK");

	private String name;

	HighContrastTheme(String name){
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
