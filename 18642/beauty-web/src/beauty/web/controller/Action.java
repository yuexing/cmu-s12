package beauty.web.controller;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import beauty.web.dataservice.DataService;
import beauty.web.exception.DataException;
import beauty.web.model.*;
import beauty.web.model.User.Type;
import beauty.web.action.service.msg.bean.*;

public abstract class Action {

	protected DataService ds;

	public static final boolean enableInvite = true;

	private static Logger log = Logger.getLogger(Action.class);

	// Returns the name of the action, used to match the request in the hash
	// table
	public abstract String getName();

	public Type[] getTypes() {
		return new Type[] { Type.admin, Type.manufacturer, Type.retail };
	}

	// Returns the name of the jsp used to render the output.
	public abstract String perform(HttpServletRequest request,
			HttpServletResponse response) throws ServletException;

	// used in delete, detail, etc.
	protected String getReferer(HttpServletRequest request) {
		String referer = request.getHeader("referer");
		referer = referer.substring(referer.lastIndexOf('/') + 1);
		if (referer.length() == 0) {
			referer = "index.jsp";
		} else {
			if (referer.indexOf('?') != -1)
				referer = referer.substring(0, referer.indexOf('?'));
		}

		return referer;
	}

	// used by services
	// the architecture needs changing... leave it now
	// helper functions
	// the input will never be null
	protected MBrand[] parse(Brand[] bs) {
		MBrand[] mbs = new MBrand[bs.length];
		for (int i = 0; i < bs.length; i++) {
			mbs[i] = new MBrand(bs[i]);
		}
		return mbs;
	}

	protected MBenefit[] parse(Benefit[] bes) {
		MBenefit[] mbes = new MBenefit[bes.length];
		for (int i = 0; i < bes.length; i++) {
			mbes[i] = new MBenefit(bes[i]);
		}
		return mbes;
	}

	protected MCategory[] parse(Category[] cates) {
		MCategory[] mcates = new MCategory[cates.length];
		for (int i = 0; i < cates.length; i++) {
			mcates[i] = new MCategory(cates[i]);
		}
		return mcates;
	}

	protected MTag[] parse(Tag[] tags) {
		MTag[] mtags = new MTag[tags.length];
		for (int i = 0; i < tags.length; i++) {
			mtags[i] = new MTag(tags[i]);
		}
		return mtags;
	}

	protected MDeal[] parse(Deal[] ds) throws DataException {
		MDeal[] mds = new MDeal[ds.length];

		for (int i = 0; i < ds.length; i++) {
				mds[i] = parse(ds[i]);
		}
		return mds;
	}

	protected MDeal parse(Deal d) throws DataException{
		MDeal md = new MDeal(d);
		Retail r = null;
		if((r = ds.getRetail(d.getRetailId())) == null){
				md.setRetail(null);
		}
		md.setRetail(new MRetail(r));
		return md;
	}

	protected MRetail[] parse(Retail[] retails) {
		MRetail[] mtags = new MRetail[retails.length];
		for (int i = 0; i < retails.length; i++) {
			mtags[i] = new MRetail(retails[i]);
		}
		return mtags;
	}

	protected MProduct[] parse(Product[] products) {
		if (products == null) {
			return null;
		}

		MProduct[] mpros = new MProduct[products.length];
		for (int i = 0; i < products.length; i++) {
			try {
				mpros[i] = parse(products[i]);
			} catch (DataException e) {
				e.printStackTrace();
			}
		}
		return mpros;
	}

	protected MComment[] parse(Comment[] cs) {
		MComment[] mcs = new MComment[cs.length];
		for (int i = 0; i < cs.length; i++) {
			try {
				mcs[i] = parse(cs[i]);
			} catch (DataException e) {
				e.printStackTrace();
			}
		}
		return mcs;
	}

	protected MComment parse(Comment c) throws DataException {
		MComment mc = new MComment(c);

		try {
			mc.setProductName(this.ds.getProduct(mc.getProductId()).getName());
		} catch (NullPointerException e) {
			log.debug("null pointer can happen in case of removal of product");
		}

		try {
			mc.setUserName(this.ds.getUser(mc.getUserId()).getEmail());
		} catch (NullPointerException e) {
			log.debug("null pointer should only happen in test");
		}

		if (c.isOrigin()) {
			// here will not be a recursive loop as its reply will not be origin
			mc.setComments(parse(this.ds.getCommentsByOrigin(mc.getId())));
		} else {
			mc.setComments(new MComment[0]);
		}
		return mc;
	}

	protected MProduct parse(Product p) throws DataException {
		Brand b = null;
		Category c = null;
		Benefit[] bes = null;
		Tag[] tags = null;

		MBrand mb = null;
		MCategory mc = null;
		MBenefit[] mbes = null;
		MTag[] mts = null;

		b = this.ds.getBrand(p.getBrandId());
		c = this.ds.getCategory(p.getCategoryId());
		bes = this.ds.getBenefitsByProduct(p.getId());
		tags = this.ds.getTagsByProduct(p.getId());

		mb = new MBrand(b);
		mc = new MCategory(c);
		mbes = this.parse(bes);
		mts = this.parse(tags);

		MProduct mp = new MProduct(p);
		mp.setBenefits(mbes);
		mp.setBrand(mb);
		mp.setCategory(mc);
		mp.setTags(mts);

		return mp;
	}
}
