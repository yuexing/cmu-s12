package hw2.amixyue.dao.annotation;

public @interface DateFormat {
	//format: "yyyy-MM-DD", "yyyy-MM-DD HH:MM:SS"
	String formate()default "yyyy-MM-DD";
}
