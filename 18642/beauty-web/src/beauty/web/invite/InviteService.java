package beauty.web.invite;

import beauty.web.model.User.Type;

/**
 * The inviteService is to limite the 
 * registration.
 * 
 * @author amixyue
 */
public interface InviteService {

	String genInviteCode(Type type);
	
	boolean isInvited(String code, Type type);
	
	void consumeCode(String code);
}
