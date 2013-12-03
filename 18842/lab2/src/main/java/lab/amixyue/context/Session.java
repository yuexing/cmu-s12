package lab.amixyue.context;

public interface Session {
	void setAttribute(String name, Object value);
	Object getAttribute(String name);
	Context getContext();
}
