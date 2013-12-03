package beauty.web.invite;

import java.util.Date;

import org.apache.log4j.Logger;

import beauty.web.dataservice.DataService;
import beauty.web.exception.DataException;
import beauty.web.model.User.Type;
import beauty.web.util.StringUtil;

// stub
public class InviteServiceImpl implements InviteService{

	private static final Logger log = Logger.getLogger(InviteServiceImpl.class);
	private DataService ds;
	
	public InviteServiceImpl(DataService ds){
		this.ds = ds;
	}
	
	@Override
	public String genInviteCode(Type type) {
		String code = StringUtil.hash(String.valueOf(new Date().getTime()));
		try {
			ds.saveInviteCode(code, type);
		} catch (DataException e) {
			log.warn(e.getMessage());
		}
		return code;
	}

	@Override
	public boolean isInvited(String code, Type type) {
		try {
			return ds.checkInviteCode(code, type);
		} catch (DataException e) {
			log.warn(e.getMessage());
			return false;
		}
	}

	@Override
	public void consumeCode(String code) {
		try {
			ds.deleteInviteCode(code);
		} catch (DataException e) {
			log.warn(e.getMessage());
		}
	}
}
