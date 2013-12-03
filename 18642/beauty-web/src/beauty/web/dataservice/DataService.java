package beauty.web.dataservice;

import java.util.*;

import org.genericdao.MatchArg;
import org.genericdao.RollbackException;
import org.genericdao.Transaction;
import org.mybeans.form.FileProperty;

import beauty.web.dao.*;
import beauty.web.exception.DataException;
import beauty.web.model.*;
import beauty.web.util.LCS;
import beauty.web.util.StringUtil;
import beauty.web.util.bean.ParsedProduct;

public class DataService {

	private BenefitDao benefitDao;
	private BrandDao brandDao;
	private DealDao dealDao;
	private CategoryDao cateDao;
	private CommentDao commentDao;
	private PhotoDao photoDao;
	private ProductDao productDao;
	private ProductTagDao productTagDao;
	private ProductBenefitDao productBenefitDao;
	private TagDao tagDao;
	private RetailDao retailDao;
	private ProductRetailDao productRetailDao;
	private UserDao userDao;
	private InviteCodeDao inviteDao;

	public DataService(Model model) {
		this.benefitDao = model.getBenefitDao();
		this.brandDao = model.getBrandDao();
		this.commentDao = model.getCommentDao();
		this.cateDao = model.getCategoryDao();
		this.photoDao = model.getPhotoDao();
		this.productDao = model.getProductDao();
		this.productTagDao = model.getProductTagDao();
		this.productBenefitDao = model.getProductBenefitDao();
		this.tagDao = model.getTagDao();
		this.dealDao = model.getDealDao();
		this.retailDao = model.getRetailDao();
		this.productRetailDao = model.getProductRetailDao();
		this.userDao = model.getUserDao();
		this.inviteDao = model.getInviteDao();
	}

	public int savePhoto(Photo p) throws DataException {
		try {
			photoDao.createAutoIncrement(p);
		} catch (RollbackException e) {
			throw new DataException(e);
		}
		return p.getId(); // will set here?
	}

	public Photo readPhoto(int id) throws DataException {
		Photo p = null;
		try {
			p = this.photoDao.read(id);
		} catch (RollbackException e) {
			throw new DataException(e);
		}
		return p;
	}

	/*
	 * ------------------------------------------ brands
	 * ----------------------------------------------
	 */
	private Brand[] getBrandsWithSimilarName(String stdForm)
			throws DataException {
		Brand[] brands = null;
		try {
			brands = brandDao.match(MatchArg.or(MatchArg.contains("stdForm",
					stdForm)));
		} catch (RollbackException e) {
			throw new DataException(e);
		}
		if (brands.length == 0) {
			return null;
		}
		return brands;
	}

	public synchronized int saveBrandForce(Brand b, FileProperty file)
			throws DataException {
		try {
			Transaction.begin();
			if (file != null) {
				Photo photo = new Photo();
				photo.setBytes(file.getBytes());
				photo.setContentType(file.getContentType());
				this.photoDao.createAutoIncrement(photo);
				b.setAttachmentId(photo.getId());
			}
			brandDao.createAutoIncrement(b);
			Transaction.commit();
		} catch (RollbackException e) {
			throw new DataException(e);
		} finally {
			if (Transaction.isActive()) {
				Transaction.rollback();
			}
		}
		return b.getId();
	}

	public synchronized int saveBrand(Brand b, FileProperty file)
			throws DataException {
		Brand[] suspectBrands;
		String[] suspects;
		try {
			Transaction.begin();
			if ((suspectBrands = getBrandsWithSimilarName(b.getStdForm())) != null) {
				throw new DataException("suspects: "
						+ arrToString(parseBrands(suspectBrands)));
			} else if ((suspects = LCS.getSuspects(b.getStdForm(),
					getBrandNames())) != null) {
				throw new DataException("suspects: " + arrToString(suspects));
			}

			if (file != null) {
				Photo photo = new Photo();
				photo.setBytes(file.getBytes());
				photo.setContentType(file.getContentType());
				this.photoDao.createAutoIncrement(photo);
				b.setAttachmentId(photo.getId());
			}

			brandDao.createAutoIncrement(b);
			Transaction.commit();
		} catch (RollbackException e) {
			throw new DataException(e);
		} finally {
			if (Transaction.isActive()) {
				Transaction.rollback();
			}
		}
		return b.getId();
	}

	private ArrayList<String> getBrandNames() throws DataException {
		ArrayList<String> brandNames = new ArrayList<String>();
		Brand[] brands = null;
		try {
			brands = brandDao.match();
		} catch (RollbackException e) {
			throw new DataException(e);
		}
		for (Brand brand : brands) {
			brandNames.add(brand.getName());
		}
		return brandNames;
	}

	public Brand[] getBrands() throws DataException {
		Brand[] brands = null;
		try {
			brands = brandDao.match();
		} catch (RollbackException e) {
			throw new DataException(e);
		}
		if (brands.length == 0) {
			return null;
		}
		return brands;
	}

	@SuppressWarnings("unused")
	private String[] parseContents(Deal[] deals) throws DataException {
		ArrayList<String> dealNames = new ArrayList<String>();
		for (Deal d : deals) {
			dealNames.add(d.getContent());
		}
		if (dealNames.size() == 0) {
			return null;
		} else {
			String[] tmp = new String[dealNames.size()];
			return dealNames.toArray(tmp);
		}
	}

	@SuppressWarnings("unused")
	private ArrayList<String> getDealContents() throws DataException {
		ArrayList<String> dealNames = new ArrayList<String>();
		Deal[] deals = null;
		try {
			deals = dealDao.match();
		} catch (RollbackException e) {
			throw new DataException(e);
		}
		for (Deal d : deals) {
			dealNames.add(d.getContent());
		}
		return dealNames;
	}

	public int saveDeal(Deal d) throws DataException {

		try {
			Transaction.begin();

			dealDao.createAutoIncrement(d);
			Transaction.commit();
		} catch (RollbackException e) {
			throw new DataException(e);
		} finally {
			if (Transaction.isActive())
				Transaction.rollback();
		}

		return d.getId();
	}

	public Deal[] getDeals() throws DataException {
		Deal[] deals = null;

		try {
			deals = dealDao.match();
		} catch (RollbackException e) {
			throw new DataException(e);
		}
		if (deals.length == 0) {
			return null;
		}
		return deals;
	}

	private String[] parseBrands(Brand[] brands) {
		ArrayList<String> brandNames = new ArrayList<String>();
		for (Brand b : brands) {
			brandNames.add(b.getName());
		}
		if (brandNames.size() == 0) {
			return null;
		} else {
			String[] tmp = new String[brandNames.size()];
			return brandNames.toArray(tmp);
		}
	}

	/*
	 * ------------------------------------------ categories
	 * ----------------------------------------------
	 */
	public Category[] getCategories() throws DataException {
		Category[] categories = null;
		try {
			categories = cateDao.match();
		} catch (RollbackException e) {
			throw new DataException(e);
		}
		if (categories.length == 0) {
			return null;
		}
		return categories;
	}

	private Category[] getCategoriesWithSimilarName(String stdForm)
			throws DataException {
		Category[] categories = null;
		try {
			categories = cateDao.match(MatchArg.or(MatchArg.contains("stdForm",
					stdForm)));
		} catch (RollbackException e) {
			throw new DataException(e);
		}
		if (categories.length == 0) {
			return null;
		}
		return categories;
	}

	private String[] parseCategories(Category[] categories)
			throws DataException {
		ArrayList<String> categoryNames = new ArrayList<String>();
		for (Category c : categories) {
			categoryNames.add(c.getName());
		}
		if (categoryNames.size() == 0) {
			return null;
		} else {
			String[] tmp = new String[categoryNames.size()];
			return categoryNames.toArray(tmp);
		}
	}

	private ArrayList<String> getCategoryNames() throws DataException {
		ArrayList<String> categoryNames = new ArrayList<String>();
		Category[] categories = null;
		try {
			categories = cateDao.match();
		} catch (RollbackException e) {
			throw new DataException(e);
		}
		for (Category category : categories) {
			categoryNames.add(category.getName());
		}
		return categoryNames;
	}

	/**
	 * save a category into database
	 * 
	 * @param c
	 * @return
	 */
	public synchronized int saveCategoryForce(Category c) throws DataException {

		try {
			cateDao.createAutoIncrement(c);
		} catch (RollbackException e) {
			throw new DataException(e);
		} finally {
			if (Transaction.isActive()) {
				Transaction.rollback();
			}
		}
		return c.getId();

	}

	public synchronized int saveCategory(Category c) throws DataException {
		Category[] suspectCategories;
		String[] suspects;

		try {
			Transaction.begin();
			if ((suspectCategories = getCategoriesWithSimilarName(c
					.getStdForm())) != null) {
				throw new DataException("suspects: "
						+ arrToString(parseCategories(suspectCategories)));
			} else if ((suspects = LCS.getSuspects(c.getStdForm(),
					getCategoryNames())) != null) {
				throw new DataException("suspects: " + arrToString(suspects));
			}

			cateDao.createAutoIncrement(c);
			Transaction.commit();
		} catch (RollbackException e) {
			throw new DataException(e);
		} finally {
			if (Transaction.isActive()) {
				Transaction.rollback();
			}
		}
		return c.getId();
	}

	/*
	 * ------------------------------------------ benefits
	 * ----------------------------------------------
	 */
	public Benefit[] getBenefits() throws DataException {
		Benefit[] benefits = null;
		try {
			benefits = benefitDao.match();
		} catch (RollbackException e) {
			throw new DataException(e);
		}
		if (benefits.length == 0) {
			return null;
		}
		return benefits;
	}

	private String[] parseBenes(Benefit[] benes) throws DataException {
		ArrayList<String> beneNames = new ArrayList<String>();
		for (Benefit be : benes) {
			beneNames.add(be.getName());
		}
		if (beneNames.size() == 0) {
			return null;
		} else {
			String[] tmp = new String[beneNames.size()];
			return beneNames.toArray(tmp);
		}
	}

	private Benefit[] getBenefitsWithSimilarName(String stdForm)
			throws DataException {
		Benefit[] benes = null;
		try {
			benes = benefitDao.match(MatchArg.or(MatchArg.contains("stdForm",
					stdForm)));
		} catch (RollbackException e) {
			throw new DataException(e);
		}
		if (benes.length == 0) {
			return null;
		}
		return benes;

	}

	private ArrayList<String> getBenefitNames() throws DataException {
		ArrayList<String> beneNames = new ArrayList<String>();
		Benefit[] benes = null;
		try {
			benes = benefitDao.match();
		} catch (RollbackException e) {
			throw new DataException(e);
		}
		for (Benefit be : benes) {
			beneNames.add(be.getName());
		}
		return beneNames;
	}

	public synchronized int saveBenefitForce(Benefit be) throws DataException {

		try {
			benefitDao.createAutoIncrement(be);
		} catch (RollbackException e) {
			throw new DataException(e);
		}
		return be.getId();
	}

	public synchronized int saveBenefit(Benefit be) throws DataException {
		Benefit[] suspectBenes;
		String[] suspects;

		try {
			Transaction.begin();
			if ((suspectBenes = this
					.getBenefitsWithSimilarName(be.getStdForm())) != null) {
				throw new DataException("suspects: "
						+ arrToString(parseBenes(suspectBenes)));
			} else if ((suspects = LCS.getSuspects(be.getStdForm(),
					getBenefitNames())) != null) {
				throw new DataException("suspects: " + arrToString(suspects));
			}

			benefitDao.createAutoIncrement(be);
			Transaction.commit();
		} catch (RollbackException e) {
			throw new DataException(e);
		} finally {
			if (Transaction.isActive())
				Transaction.rollback();
		}

		return be.getId();
	}

	public Comment[] getComments() throws DataException {
		Comment[] comments = null;
		try {
			comments = commentDao.match();
		} catch (RollbackException e) {
			throw new DataException(e);
		}
		if (comments.length == 0) {
			return null;
		}
		return comments;
	}

	public Product[] getProducts() throws DataException {
		Product[] products = null;
		try {
			products = productDao.match();
		} catch (RollbackException e) {
			throw new DataException(e);
		}
		if (products.length == 0) {
			return null;
		}
		return products;
	}

	public Product getProduct(int id) throws DataException {
		Product p = null;
		try {
			p = productDao.read(id);
		} catch (RollbackException e) {
			throw new DataException(e);
		}
		return p;
	}

	private Product[] getProductsWithSimilarName(String stdForm)
			throws DataException {
		if (stdForm.length() == 0) {
			return null;
		}
		Product[] suspectProducts = null;
		try {
			suspectProducts = productDao.match(MatchArg.or(MatchArg.contains(
					"stdForm", stdForm)));
		} catch (RollbackException e) {
			throw new DataException(e);
		}
		if (suspectProducts.length == 0) {
			return null;
		}
		return suspectProducts;
	}

	private String[] parseProducts(Product[] suspectProducts)
			throws DataException {
		ArrayList<String> productNames = new ArrayList<String>();
		for (Product p : suspectProducts) {
			productNames.add(p.getName());
		}
		if (productNames.size() == 0) {
			return null;
		} else {
			String[] tmp = new String[productNames.size()];
			return productNames.toArray(tmp);
		}
	}

	// for furthur finding out suspected products
	private ArrayList<String> getProductNames() throws DataException {
		ArrayList<String> productNames = new ArrayList<String>();
		Product[] products = null;
		try {
			products = productDao.match();
		} catch (RollbackException e) {
			throw new DataException(e);
		}
		for (Product p : products) {
			productNames.add(p.getName());
		}
		return productNames;
	}

	public synchronized int saveParsedProduct(ParsedProduct p, int owner)
			throws DataException {
		String name = p.getName(), brand = p.getBrand().getName(), category = p
				.getCategory(), stdForm = null;
		Set<String> benefits = p.getBenefits(), tags = p.getTags();

		try {
			Transaction.begin();
			// test product
			stdForm = StringUtil.getStdFrom(name);
			Product pro = null;
			Product[] pros = null;
			if ((pros = this.productDao.match(MatchArg.equals("stdForm",
					stdForm))) != null && pros.length > 0) {
				pro = pros[0];
				return pro.getId();
			} else {
				pro = new Product(); // waiting to be saved
				pro.setName(name);
				pro.setStdForm(stdForm);
				pro.setOwner(owner);
				pro.setIntroduction(p.getIntroduction());
				pro.setPrice(p.getPrice());
			}

			// save brand
			stdForm = StringUtil.getStdFrom(brand);
			Brand b = null;
			Brand[] bs = null;
			if ((bs = this.brandDao.match(MatchArg.equals("stdForm", stdForm))) == null
					|| bs.length == 0) {
				b = new Brand();
				b.setOwner(owner);

				// create photo for brand
				Photo photo = new Photo();
				photo.setBytes(p.getBrand().getPhoto());
				this.photoDao.createAutoIncrement(photo);
				b.setAttachmentId(photo.getId());

				b.setName(brand);
				b.setStdForm(stdForm);
				b.setProductCount(1);
				brandDao.createAutoIncrement(b);
			} else {
				b = bs[0];
				b.setProductCount(b.getProductCount() + 1);
				this.brandDao.update(b);
			}

			// save category
			stdForm = StringUtil.getStdFrom(category);
			Category c = null;
			Category[] cs = null;
			if ((cs = this.cateDao.match(MatchArg.equals("stdForm", stdForm))) == null
					|| cs.length == 0) {
				c = new Category();
				c.setOwner(owner);
				c.setName(category);
				c.setStdForm(stdForm);
				c.setProductCount(1);
				cateDao.createAutoIncrement(c);
			} else {
				c = cs[0];
				c.setProductCount(c.getProductCount() + 1);
				this.cateDao.update(c);
			}
			// save product
			Photo photo = new Photo();
			photo.setBytes(p.getPhoto());
			this.photoDao.createAutoIncrement(photo);
			pro.setAttachmentId(photo.getId());
			pro.setBrandId(b.getId());
			pro.setCategoryId(c.getId());
			productDao.createAutoIncrement(pro);

			// save tag
			for (String t : tags) {
				stdForm = StringUtil.getStdFrom(t);
				Tag tag = null;
				Tag[] tagArr = null;
				if ((tagArr = this.tagDao.match(MatchArg.equals("stdForm",
						stdForm))) == null || tagArr.length == 0) {
					tag = new Tag();
					tag.setOwner(owner);
					tag.setName(t);
					tag.setStdForm(stdForm);
					tag.setProductCount(1);
					this.tagDao.createAutoIncrement(tag);
				} else {
					tag = tagArr[0];
					tag.setProductCount(tag.getProductCount() + 1);
					this.tagDao.update(tag);
				}
				ProductTag pt = new ProductTag();
				pt.setTagId(tag.getId());
				pt.setProductId(pro.getId());
				this.productTagDao.createAutoIncrement(pt);
			}

			// save benefit
			for (String be : benefits) {
				stdForm = StringUtil.getStdFrom(be);
				Benefit benefit = null;
				Benefit[] beArr = null;
				if ((beArr = this.benefitDao.match(MatchArg.equals("stdForm",
						stdForm))) == null || beArr.length == 0) {
					benefit = new Benefit();
					benefit.setOwner(owner);
					benefit.setName(be);
					benefit.setStdForm(stdForm);
					benefit.setProductCount(1);
					this.benefitDao.createAutoIncrement(benefit);
				} else {
					benefit = beArr[0];
					benefit.setProductCount(benefit.getProductCount() + 1);
					this.benefitDao.update(benefit);
				}
				ProductBenefit pb = new ProductBenefit();
				pb.setBenefitId(benefit.getId());
				pb.setProductId(pro.getId());
				this.productBenefitDao.createAutoIncrement(pb);
			}
			Transaction.commit();
		} catch (RollbackException e) {
			throw new DataException(e);
		} finally {
			if (Transaction.isActive()) {
				Transaction.rollback();
			}
		}
		return -1;
	}

	public synchronized int saveProduct(Product p, FileProperty file,
			String benefits, String tags) throws DataException {

		Product[] suspectProducts;
		String[] suspects;

		try {
			Transaction.begin();

			if ((suspectProducts = getProductsWithSimilarName(p.getStdForm())) != null) {
				throw new DataException("suspects: "
						+ arrToString(parseProducts(suspectProducts)));
			} else if ((suspects = LCS.getSuspects(p.getStdForm(),
					getProductNames())) != null) {
				throw new DataException("suspects: " + arrToString(suspects));
			}

			if (file != null && file.getBytes().length != 0) {
				Photo photo = new Photo();
				photo.setBytes(file.getBytes());
				photo.setContentType(file.getContentType());
				this.photoDao.createAutoIncrement(photo);
				p.setAttachmentId(photo.getId());
			}

			productDao.createAutoIncrement(p);

			// relationship with brand
			Brand b = getBrand(p.getBrandId());
			b.setProductCount(b.getProductCount() + 1);
			this.brandDao.update(b);

			// relationship with category
			Category c = getCategory(p.getCategoryId());
			c.setProductCount(c.getProductCount() + 1);
			this.cateDao.update(c);

			// relationship with benefit
			Benefit be = null;
			int bid = 0;
			String[] idss = null;
			if (benefits != null) {
				ProductBenefit pb = null;
				idss = benefits.split("\\W");
				for (String id : idss) {
					id = id.trim();
					if (id.length() > 0) {
						bid = Integer.parseInt(id);

						be = getBenefit(bid);
						be.setProductCount(be.getProductCount() + 1);
						updateBenefit(be);

						pb = new ProductBenefit();
						pb.setBenefitId(bid);
						pb.setProductId(p.getId());
						this.productBenefitDao.createAutoIncrement(pb);
					}
				}
			}

			// relationship with tag
			Tag tag = null;
			int tid = 0;
			if (tags != null) {
				ProductTag pt = null;
				idss = tags.split("\\W");
				for (String id : idss) {
					id = id.trim();
					if (id.length() > 0) {
						tid = Integer.parseInt(id);

						tag = getTag(tid);
						tag.setProductCount(tag.getProductCount() + 1);
						updateTag(tag);

						pt = new ProductTag();
						pt.setTagId(tid);
						pt.setProductId(p.getId());
						this.productTagDao.createAutoIncrement(pt);
					}
				}
			}

			Transaction.commit();
		} catch (RollbackException e) {
			throw new DataException(e);
		} finally {
			if (Transaction.isActive()) {
				Transaction.rollback();
			}
		}
		return p.getId();
	}

	public synchronized int saveProductForce(Product p, FileProperty file,
			String benefits, String tags) throws DataException {

		try {
			Transaction.begin();

			if (file != null && file.getBytes().length != 0) {
				Photo photo = new Photo();
				photo.setBytes(file.getBytes());
				photo.setContentType(file.getContentType());
				this.photoDao.createAutoIncrement(photo);
				p.setAttachmentId(photo.getId());
			}

			productDao.createAutoIncrement(p);

			// relationship with brand
			Brand b = getBrand(p.getBrandId());
			b.setProductCount(b.getProductCount() + 1);
			this.brandDao.update(b);

			// relationship with category
			Category c = getCategory(p.getCategoryId());
			c.setProductCount(c.getProductCount() + 1);
			this.cateDao.update(c);

			// relationship with benefit
			Benefit be = null;
			int bid = 0;
			String[] idss = null;
			if (benefits != null) {
				ProductBenefit pb = null;
				idss = benefits.split("\\W");
				for (String id : idss) {
					id = id.trim();
					if (id.length() > 0) {
						bid = Integer.parseInt(id);

						be = getBenefit(bid);
						be.setProductCount(be.getProductCount() + 1);
						updateBenefit(be);

						pb = new ProductBenefit();
						pb.setBenefitId(bid);
						pb.setProductId(p.getId());
						this.productBenefitDao.createAutoIncrement(pb);
					}
				}
			}

			// relationship with tag
			Tag tag = null;
			int tid = 0;
			if (tags != null) {
				ProductTag pt = null;
				idss = tags.split("\\W");
				for (String id : idss) {
					id = id.trim();
					if (id.length() > 0) {
						tid = Integer.parseInt(id);

						tag = getTag(tid);
						tag.setProductCount(tag.getProductCount() + 1);
						updateTag(tag);

						pt = new ProductTag();
						pt.setTagId(tid);
						pt.setProductId(p.getId());
						this.productTagDao.createAutoIncrement(pt);
					}
				}
			}

			Transaction.commit();
		} catch (RollbackException e) {
			throw new DataException(e);
		} finally {
			if (Transaction.isActive()) {
				Transaction.rollback();
			}
		}
		return p.getId();
	}

	public synchronized int updateProduct(Product p, FileProperty file,
			String benefits, String tags) throws DataException {

		try {
			Transaction.begin();

			if (file != null && file.getBytes().length != 0) {
				this.photoDao.delete(p.getAttachmentId());
				// new photo
				Photo photo = new Photo();
				photo.setBytes(file.getBytes());
				photo.setContentType(file.getContentType());
				this.photoDao.createAutoIncrement(photo);

				p.setAttachmentId(photo.getId());
			}

			Product oldp = this.productDao.read(p.getId());

			// do not allow to change brand

			// clear category
			Category cate = this.cateDao.read(oldp.getCategoryId());
			cate.setProductCount(cate.getProductCount() - 1);
			this.cateDao.update(cate);

			// relationship with category
			cate = getCategory(p.getCategoryId());
			cate.setProductCount(cate.getProductCount() + 1);
			this.cateDao.update(cate);

			// clear product and tag
			ProductTag[] pts = this.productTagDao.match(MatchArg.equals(
					"productId", p.getId()));
			for (ProductTag pt : pts) {
				Tag t = this.tagDao.read(pt.getTagId());
				t.setProductCount(t.getProductCount() - 1);
				this.tagDao.update(t);

				this.productTagDao.delete(pt.getId());
			}

			// clear product and benefit
			ProductBenefit[] pbs = this.productBenefitDao.match(MatchArg
					.equals("productId", p.getId()));
			for (ProductBenefit pb : pbs) {
				Benefit be = this.benefitDao.read(pb.getBenefitId());
				be.setProductCount(be.getProductCount() - 1);
				this.benefitDao.update(be);

				this.productBenefitDao.delete(pb.getId());
			}

			// relationship with benefit
			Benefit be = null;
			int bid = 0;
			String[] idss = null;
			if (benefits != null) {
				ProductBenefit pb = null;
				idss = benefits.split("\\W");
				for (String id : idss) {
					id = id.trim();
					if (id.length() > 0) {
						bid = Integer.parseInt(id);

						be = getBenefit(bid);
						be.setProductCount(be.getProductCount() + 1);
						updateBenefit(be);

						pb = new ProductBenefit();
						pb.setBenefitId(bid);
						pb.setProductId(p.getId());
						this.productBenefitDao.createAutoIncrement(pb);
					}
				}
			}

			// relationship with tag
			Tag tag = null;
			int tid = 0;
			if (tags != null) {
				ProductTag pt = null;
				idss = tags.split("\\W");
				for (String id : idss) {
					id = id.trim();
					if (id.length() > 0) {
						tid = Integer.parseInt(id);

						tag = getTag(tid);
						tag.setProductCount(tag.getProductCount() + 1);
						updateTag(tag);

						pt = new ProductTag();
						pt.setTagId(tid);
						pt.setProductId(p.getId());
						this.productTagDao.createAutoIncrement(pt);
					}
				}
			}
			this.productDao.update(p);

			Transaction.commit();
		} catch (RollbackException e) {
			throw new DataException(e);
		} finally {
			if (Transaction.isActive()) {
				Transaction.rollback();
			}
		}
		return p.getId();
	}

	public void updateProduct(Product p) throws DataException {
		try {
			productDao.update(p);
		} catch (RollbackException e) {
			throw new DataException(e);
		}
	}

	public int saveProductBenefit(ProductBenefit pb) throws DataException {
		try {
			productBenefitDao.createAutoIncrement(pb);
		} catch (RollbackException e) {
			throw new DataException(e);
		}
		return pb.getId();

	}

	public int saveProductTag(ProductTag pt) throws DataException {
		try {
			productTagDao.createAutoIncrement(pt);
		} catch (RollbackException e) {
			throw new DataException(e);
		}
		return pt.getId();

	}

	public String arrToString(String[] arr) throws DataException {
		StringBuilder strb = new StringBuilder();
		for (String str : arr) {
			strb.append(str).append(", ");
		}
		return strb.toString();
	}

	private Tag[] getTagsWithSimilarName(String stdForm) throws DataException {
		Tag[] tags = null;
		try {
			tags = tagDao.match(MatchArg.or(MatchArg.contains("stdForm",
					stdForm)));
		} catch (RollbackException e) {
			throw new DataException(e);
		}
		if (tags.length == 0) {
			return null;
		}
		return tags;

	}

	private String[] parseTags(Tag[] tags) throws DataException {
		ArrayList<String> tagNames = new ArrayList<String>();
		for (Tag t : tags) {
			tagNames.add(t.getName());
		}
		if (tagNames.size() == 0) {
			return null;
		} else {
			String[] tmp = new String[tagNames.size()];
			return tagNames.toArray(tmp);
		}
	}

	private ArrayList<String> getTagNames() throws DataException {
		ArrayList<String> tagNames = new ArrayList<String>();
		Tag[] tags = null;
		try {
			tags = tagDao.match();
		} catch (RollbackException e) {
			throw new DataException(e);
		}
		for (Tag tag : tags) {
			tagNames.add(tag.getName());
		}
		return tagNames;
	}

	public synchronized int saveTag(Tag t) throws DataException {
		Tag[] suspectTags;
		String[] suspects;

		try {
			Transaction.begin();
			if ((suspectTags = getTagsWithSimilarName(t.getStdForm())) != null) {
				throw new DataException("suspects: "
						+ arrToString(parseTags(suspectTags)));
			} else if ((suspects = LCS.getSuspects(t.getStdForm(),
					getTagNames())) != null) {
				throw new DataException("suspects: " + arrToString(suspects));
			}
			tagDao.createAutoIncrement(t);
			Transaction.commit();
		} catch (RollbackException e) {
			throw new DataException(e);
		} finally {
			if (Transaction.isActive())
				Transaction.rollback();
		}
		return t.getId();

	}

	public synchronized int saveTagForce(Tag t) throws DataException {

		try {
			tagDao.createAutoIncrement(t);
		} catch (RollbackException e) {
			throw new DataException(e);
		}
		return t.getId();

	}

	public Comment getComment(int id) throws DataException {
		Comment c = null;
		try {
			c = commentDao.read(id);
		} catch (RollbackException e) {
			throw new DataException(e);
		}
		return c;
	}

	public void updateComment(Comment co) throws DataException {
		try {
			commentDao.update(co);
		} catch (RollbackException e) {
			throw new DataException(e);
		}
	}

	public int saveComment(Comment co, FileProperty file) throws DataException {
		try {
			Transaction.begin();

			Product p = getProduct(co.getProductId());
			p.setCommentNum(p.getCommentNum() + 1);
			updateProduct(p);

			if (!co.isOrigin()) {
				Comment c = getComment(co.getReplyId());
				c.setReplyCount(c.getReplyCount() + 1);
				this.commentDao.update(c);
			}

			if (file != null && file.getBytes().length != 0) {
				Photo photo = new Photo();
				photo.setBytes(file.getBytes());
				photo.setContentType(file.getContentType());
				this.photoDao.createAutoIncrement(photo);
				co.setAttachmentId(photo.getId());
			}

			commentDao.createAutoIncrement(co);

			Transaction.commit();
		} catch (RollbackException e) {
			throw new DataException(e);
		} finally {
			if (Transaction.isActive())
				Transaction.rollback();
		}
		return co.getId();
	}

	public void updateComment(Comment co, FileProperty file)
			throws DataException {
		try {
			Transaction.begin();

			if (file != null && file.getBytes().length != 0) {
				if (co.getAttachmentId() != -1)
					this.photoDao.delete(co.getAttachmentId());

				// new photo
				Photo photo = new Photo();
				photo.setBytes(file.getBytes());
				photo.setContentType(file.getContentType());
				this.photoDao.createAutoIncrement(photo);
				co.setAttachmentId(photo.getId());
			}

			commentDao.update(co);

			Transaction.commit();
		} catch (RollbackException e) {
			throw new DataException(e);
		} finally {
			if (Transaction.isActive())
				Transaction.rollback();
		}
	}

	public Brand getBrand(int brandId) throws DataException {
		Brand b = null;

		try {
			b = brandDao.read(brandId);
		} catch (RollbackException e) {
			throw new DataException(e);
		}
		return b;
	}

	public void updateBrand(Brand b, FileProperty file) throws DataException {
		try {
			if (file != null && file.getBytes().length != 0) {
				this.photoDao.delete(b.getAttachmentId());
				// new photo
				Photo p = new Photo();
				p.setBytes(file.getBytes());
				p.setContentType(file.getContentType());
				this.photoDao.createAutoIncrement(p);
				b.setAttachmentId(p.getId());
			}
			brandDao.update(b);
		} catch (RollbackException e) {
			throw new DataException(e);
		}
	}

	public void updateBrand(Brand b) throws DataException {
		try {
			brandDao.update(b);
		} catch (RollbackException e) {
			throw new DataException(e);
		}
	}

	public Category getCategory(int categoryId) throws DataException {
		Category b = null;

		try {
			b = cateDao.read(categoryId);
		} catch (RollbackException e) {
			throw new DataException(e);
		}
		return b;
	}

	public void updateCategory(Category c) throws DataException {
		try {
			this.cateDao.update(c);
		} catch (RollbackException e) {
			throw new DataException(e);
		}
	}

	public Benefit getBenefit(int bid) throws DataException {
		Benefit be = null;
		try {
			be = this.benefitDao.read(bid);
		} catch (RollbackException e) {
			throw new DataException(e);
		}
		return be;
	}

	public void updateBenefit(Benefit be) throws DataException {
		try {
			this.benefitDao.update(be);
		} catch (RollbackException e) {
			throw new DataException(e);
		}
	}

	public Tag getTag(int tid) throws DataException {
		Tag tag = null;
		try {
			tag = this.tagDao.read(tid);
		} catch (RollbackException e) {
			throw new DataException(e);
		}
		return tag;
	}

	public Tag[] getTags() throws DataException {
		Tag[] tags = null;
		try {
			tags = tagDao.match();
		} catch (RollbackException e) {
			throw new DataException(e);
		}
		if (tags.length == 0) {
			return null;
		}
		return tags;
	}

	public void updateTag(Tag tag) throws DataException {
		try {
			this.tagDao.update(tag);
		} catch (RollbackException e) {
			throw new DataException(e);
		}
	}

	public Tag[] getTagsByProduct(int id) throws DataException {
		ArrayList<Tag> tags = new ArrayList<Tag>();
		ProductTag[] pbs;
		try {
			pbs = this.productTagDao.match(MatchArg.equals("productId", id));
			for (ProductTag pb : pbs) {
				tags.add(this.tagDao.read(pb.getTagId()));
			}
		} catch (RollbackException e) {
			throw new DataException(e);
		}

		if (tags.size() == 0) {
			return null;
		}
		Tag[] tmp = new Tag[tags.size()];
		return tags.toArray(tmp);
	}

	public Benefit[] getBenefitsByProduct(int id) throws DataException {
		ArrayList<Benefit> benefits = new ArrayList<Benefit>();
		ProductBenefit[] pbs;
		try {
			pbs = this.productBenefitDao
					.match(MatchArg.equals("productId", id));
			for (ProductBenefit pb : pbs) {
				benefits.add(this.benefitDao.read(pb.getBenefitId()));
			}
		} catch (RollbackException e) {
			throw new DataException(e);
		}

		if (benefits.size() == 0) {
			return null;
		}
		Benefit[] tmp = new Benefit[benefits.size()];
		return benefits.toArray(tmp);
	}

	// all the comments posted by the user
	public Comment[] getCommentsByUser(int id) throws DataException {
		Comment[] comments = null;
		try {
			comments = this.commentDao.match(MatchArg.equals("userId", id));
		} catch (RollbackException e) {
			throw new DataException(e);
		}
		return comments;
	}

	// the origin comment about the product
	public Comment[] getCommentsByProduct(int id) throws DataException {
		Comment[] comments = null;
		try {

			comments = this.commentDao.match(MatchArg.and(
					MatchArg.equals("productId", id),
					MatchArg.equals("replyId", -1)));
		} catch (RollbackException e) {
			throw new DataException(e);
		}
		return comments;
	}

	public Comment[] getCommentsByOrigin(int id) throws DataException {
		Comment[] comments = null;
		try {
			comments = this.commentDao.match(MatchArg.equals("replyId", id));
		} catch (RollbackException e) {
			throw new DataException(e);
		}
		return comments;
	}

	public Product[] getProductsByBrand(int id) throws DataException {
		Product[] products = null;
		try {
			products = this.productDao.match(MatchArg.equals("brandId", id));
		} catch (RollbackException e) {
			throw new DataException(e);
		}
		return products;
	}

	public Product[] getProductsByCategory(int id) throws DataException {
		Product[] products = null;
		try {
			products = this.productDao.match(MatchArg.equals("categoryId", id));
		} catch (RollbackException e) {
			throw new DataException(e);
		}
		return products;
	}

	public Product[] getProductsByTag(int id) throws DataException {
		ArrayList<Product> products = new ArrayList<Product>();

		ProductTag[] pts = null;
		try {
			pts = this.productTagDao.match(MatchArg.equals("tagId", id));
			for (ProductTag pt : pts) {
				products.add(this.productDao.read(pt.getProductId()));
			}
		} catch (RollbackException e) {
			throw new DataException(e);
		}
		if (products.size() == 0) {
			return null;
		}
		Product[] tmp = new Product[products.size()];
		return products.toArray(tmp);
	}

	public Product[] getProductsByBenefit(int id) throws DataException {
		ArrayList<Product> products = new ArrayList<Product>();

		ProductBenefit[] pbs = null;
		try {
			pbs = this.productBenefitDao
					.match(MatchArg.equals("benefitId", id));
			for (ProductBenefit pb : pbs) {
				products.add(this.productDao.read(pb.getProductId()));
			}
		} catch (RollbackException e) {
			throw new DataException(e);
		}
		if (products.size() == 0) {
			return null;
		}
		Product[] tmp = new Product[products.size()];
		return products.toArray(tmp);
	}

	public Product[] getProductsByRetail(int id) throws DataException {
		ArrayList<Product> products = new ArrayList<Product>();

		ProductRetail[] prs = null;
		try {
			prs = this.productRetailDao.match(MatchArg.equals("retailId", id));
			for (ProductRetail pr : prs) {
				products.add(this.productDao.read(pr.getProductId()));
			}
		} catch (RollbackException e) {
			throw new DataException(e);
		}
		if (products.size() == 0) {
			return null;
		}
		Product[] tmp = new Product[products.size()];
		return products.toArray(tmp);
	}

	public void deleteProduct(int id) throws DataException {
		try {
			Transaction.begin();
			Comment[] comments = null;
			ProductTag[] pts = null;
			ProductBenefit[] pbs = null;

			Product p = this.productDao.read(id);

			// brand
			Brand b = this.brandDao.read(p.getBrandId());
			b.setProductCount(b.getProductCount() - 1);
			this.brandDao.update(b);

			// category
			Category cate = this.cateDao.read(p.getCategoryId());
			cate.setProductCount(cate.getProductCount() - 1);
			this.cateDao.update(cate);

			// product and tag
			pts = this.productTagDao.match(MatchArg.equals("productId", id));
			for (ProductTag pt : pts) {
				Tag t = this.tagDao.read(pt.getTagId());
				t.setProductCount(t.getProductCount() - 1);
				this.tagDao.update(t);

				this.productTagDao.delete(pt.getId());
			}

			// product and benefit
			pbs = this.productBenefitDao
					.match(MatchArg.equals("productId", id));
			for (ProductBenefit pb : pbs) {
				Benefit be = this.benefitDao.read(pb.getBenefitId());
				be.setProductCount(be.getProductCount() - 1);
				this.benefitDao.update(be);

				this.productBenefitDao.delete(pb.getId());
			}

			// product and retail
			ProductRetail[] prs = this.productRetailDao.match(MatchArg.equals(
					"productId", id));
			for (ProductRetail pr : prs) {
				this.productRetailDao.delete(pr.getId());
			}

			// comment
			comments = this.commentDao.match(MatchArg.equals("productId", id));
			for (Comment c : comments) {
				this.commentDao.delete(c.getId());
			}

			this.productDao.delete(id);

			Transaction.commit();
		} catch (RollbackException e) {
			throw new DataException(e);
		} finally {
			if (Transaction.isActive())
				Transaction.rollback();
		}
	}

	public void deleteComment(int id) throws DataException {
		try {
			Transaction.begin();

			Comment[] comments = null;
			Comment co = this.commentDao.read(id);
			Product p = this.productDao.read(co.getProductId());

			comments = this.commentDao.match(MatchArg.equals("replyId", id));
			int count = 1;
			for (Comment c : comments) {
				this.commentDao.delete(c.getId());
				count++;
			}

			p.setCommentNum(p.getCommentNum() - count);
			this.productDao.update(p);

			this.commentDao.delete(id);

			Transaction.commit();
		} catch (RollbackException e) {
			throw new DataException(e);
		} finally {
			if (Transaction.isActive())
				Transaction.rollback();
		}
	}

	public void deleteBrand(int id) throws DataException {
		try {
			Transaction.begin();

			Product[] products = null;

			products = this.productDao.match(MatchArg.equals("brandId", id));
			for (Product p : products) {
				Comment[] comments = null;
				ProductTag[] pts = null;
				ProductBenefit[] pbs = null;
				int pid = p.getId();
				// no need to deal with brand, as it will be deleted
				// category
				Category cate = this.cateDao.read(p.getCategoryId());
				cate.setProductCount(cate.getProductCount() - 1);
				this.cateDao.update(cate);

				// product and retail
				ProductRetail[] prs = this.productRetailDao.match(MatchArg
						.equals("productId", id));
				for (ProductRetail pr : prs) {
					this.productRetailDao.delete(pr.getId());
				}

				// product and tag
				pts = this.productTagDao.match(MatchArg
						.equals("productId", pid));
				for (ProductTag pt : pts) {
					Tag t = this.tagDao.read(pt.getTagId());
					t.setProductCount(t.getProductCount() - 1);
					this.tagDao.update(t);

					this.productTagDao.delete(pt.getId());
				}

				// product and benefit
				pbs = this.productBenefitDao.match(MatchArg.equals("productId",
						pid));
				for (ProductBenefit pb : pbs) {
					Benefit be = this.benefitDao.read(pb.getBenefitId());
					be.setProductCount(be.getProductCount() - 1);
					this.benefitDao.update(be);

					this.productBenefitDao.delete(pb.getId());
				}

				// comment
				comments = this.commentDao.match(MatchArg.equals("productId",
						pid));
				for (Comment c : comments) {
					this.commentDao.delete(c.getId());
				}

				this.productDao.delete(pid);
			}

			this.brandDao.delete(id);
			Transaction.commit();
		} catch (RollbackException e) {
			throw new DataException(e);
		} finally {
			if (Transaction.isActive())
				Transaction.rollback();
		}
	}

	public void deleteCategory(int id) throws DataException {
		try {
			Transaction.begin();

			Product[] products = null;

			products = this.productDao.match(MatchArg.equals("categoryId", id));
			for (Product p : products) {
				p.setCategoryId(-1);
				this.productDao.update(p);
			}
			this.cateDao.delete(id);

			Transaction.commit();
		} catch (RollbackException e) {
			throw new DataException(e);
		} finally {
			if (Transaction.isActive())
				Transaction.rollback();
		}
	}

	public void deleteTag(int id) throws DataException {
		try {
			Transaction.begin();

			ProductTag[] pts = null;
			try {
				pts = this.productTagDao.match(MatchArg.equals("tagId", id));
				for (ProductTag pt : pts) {
					this.productTagDao.delete(pt.getId());
				}
				this.tagDao.delete(id);
			} catch (RollbackException e1) {
				e1.printStackTrace();
			}

			Transaction.commit();
		} catch (RollbackException e) {
			throw new DataException(e);
		} finally {
			if (Transaction.isActive())
				Transaction.rollback();
		}
	}

	public void deleteBenefit(int id) throws DataException {
		try {
			Transaction.begin();

			ProductBenefit[] pbs = null;
			try {
				pbs = this.productBenefitDao.match(MatchArg.equals("benefitId",
						id));
				for (ProductBenefit pb : pbs) {
					this.productBenefitDao.delete(pb.getId());
				}
				this.benefitDao.delete(id);
			} catch (RollbackException e1) {
				e1.printStackTrace();
			}

			Transaction.commit();
		} catch (RollbackException e) {
			throw new DataException(e);
		}
	}

	public void deleteDeal(int id) throws DataException {
		try {
			this.dealDao.delete(id);
		} catch (RollbackException e) {
			throw new DataException(e);
		}
	}

	public void deleteRetail(int id) throws DataException {
		try {
			Transaction.begin();

			this.retailDao.delete(id);

			// delelte deals
			Deal[] deals = this.dealDao.match(MatchArg.equals("retailId", id));
			for (Deal d : deals) {
				this.dealDao.delete(d.getId());
			}
			// delete product and retail
			ProductRetail[] prs = this.productRetailDao.match(MatchArg.equals(
					"retailId", id));
			for (ProductRetail pr : prs) {
				this.productRetailDao.delete(pr.getId());
			}
			Transaction.commit();
		} catch (RollbackException e) {
			throw new DataException(e);
		} finally {
			if (Transaction.isActive())
				Transaction.rollback();
		}
	}

	public Retail[] getRetails() throws DataException {
		Retail[] retails = null;
		try {
			retails = retailDao.match();
		} catch (RollbackException e) {
			throw new DataException(e);
		}
		if (retails.length == 0) {
			return null;
		}
		return retails;
	}

	public int saveRetail(Retail r) throws DataException {
		try {
			Transaction.begin();
			retailDao.createAutoIncrement(r);
			Transaction.commit();
		} catch (RollbackException e) {
			throw new DataException(e);
		} finally {
			if (Transaction.isActive())
				Transaction.rollback();
		}

		return r.getId();
	}

	@SuppressWarnings("unused")
	private ArrayList<String> getRetailNames() throws DataException {
		ArrayList<String> retailNames = new ArrayList<String>();
		Retail[] retails = null;
		try {
			retails = retailDao.match();
		} catch (RollbackException e) {
			throw new DataException(e);
		}
		for (Retail r : retails) {
			retailNames.add(r.getName());
		}
		return retailNames;
	}

	public Retail getRetail(int id) throws DataException {
		Retail retail = null;
		try {
			retail = retailDao.read(id);
		} catch (RollbackException e) {
			throw new DataException(e);
		}
		return retail;
	}

	public Retail[] getRetailsByProduct(int id) throws DataException {
		ArrayList<Retail> retails = new ArrayList<Retail>();
		ProductRetail[] prs;
		try {
			Transaction.begin();
			prs = this.productRetailDao.match(MatchArg.equals("productId", id));
			for (ProductRetail pr : prs) {
				retails.add(this.retailDao.read(pr.getRetailId()));
			}
			Transaction.commit();
			if (retails.size() == 0)
				return null;
			else {
				Retail[] retailArr = new Retail[retails.size()];
				return retails.toArray(retailArr);
			}
		} catch (RollbackException e) {
			throw new DataException(e);
		} finally {
			if (Transaction.isActive())
				Transaction.rollback();
		}
	}

	public int saveProductRetail(ProductRetail pr) throws DataException {
		try {
			this.productRetailDao.createAutoIncrement(pr);
		} catch (RollbackException e) {
			throw new DataException(e);
		}
		return pr.getId();
	}

	public boolean existPR(int pid, int rid) {
		ProductRetail[] prs;
		try {
			prs = this.productRetailDao.match(MatchArg.and(
					MatchArg.equals("productId", pid),
					MatchArg.equals("retailId", rid)));
			if (prs.length > 0) {
				return true;
			}
		} catch (RollbackException e) {
			e.printStackTrace();
		}
		return false;
	}

	public void updateRetail(Retail r) throws DataException {
		try {
			this.retailDao.update(r);
		} catch (RollbackException e) {
			throw new DataException(e);
		}

	}

	public Deal getDeal(int did) throws DataException {
		Deal d = null;
		try {
			d = this.dealDao.read(did);
		} catch (RollbackException e) {
			throw new DataException(e);
		}
		return d;
	}

	public void updateDeal(Deal d) throws DataException {
		try {
			this.dealDao.update(d);
		} catch (RollbackException e) {
			throw new DataException(e);
		}
	}

	public void deletePR(int pid, int rid) throws DataException {
		ProductRetail[] prs;
		try {
			Transaction.begin();
			prs = this.productRetailDao.match(MatchArg.and(
					MatchArg.equals("productId", pid),
					MatchArg.equals("retailId", rid)));

			for (ProductRetail pr : prs) {
				this.productRetailDao.delete(pr.getId());
			}
			Transaction.commit();
		} catch (RollbackException e) {
			throw new DataException(e);
		} finally {
			if (Transaction.isActive())
				Transaction.rollback();
		}
	}

	public int saveUser(User u) throws DataException {
		try {
			Transaction.begin();
			userDao.createAutoIncrement(u);
			Transaction.commit();
		} catch (RollbackException e) {
			throw new DataException(e);
		} finally {
			if (Transaction.isActive())
				Transaction.rollback();
		}

		return u.getId();
	}

	// not automatically assign id
	public int saveUserN(User u) throws DataException {
		try {
			Transaction.begin();
			userDao.create(u);
			Transaction.commit();
		} catch (RollbackException e) {
			throw new DataException(e);
		} finally {
			if (Transaction.isActive())
				Transaction.rollback();
		}

		return u.getId();
	}

	public User getUserByEmail(String ema) throws DataException {
		User[] users = null;
		try {
			users = this.userDao.match(MatchArg.equals("email", ema));
			if (users.length == 0) {
				return null;
			} else {
				return users[0];
			}
		} catch (RollbackException e) {
			throw new DataException(e);
		}
	}

	public User getUser(int userId) throws DataException {
		try {
			return this.userDao.read(userId);
		} catch (RollbackException e) {
			throw new DataException(e);
		}
	}

	public void updateUser(User u) throws DataException {
		try {
			this.userDao.update(u);
		} catch (RollbackException e) {
			throw new DataException(e);
		}
	}

	public void saveInviteCode(String code, User.Type type)
			throws DataException {
		try {
			InviteCode ic = new InviteCode();
			ic.setCode(code);
			ic.setType(type);
			ic.setTimestamp(new Date().getTime());
			this.inviteDao.create(ic);
		} catch (RollbackException e) {
			throw new DataException(e);
		}
	}

	public void deleteInviteCode(String code) throws DataException {
		try {
			this.inviteDao.delete(code);
		} catch (RollbackException e) {
			throw new DataException(e);
		}
	}

	public boolean checkInviteCode(String code, User.Type type)
			throws DataException {
		try {
			InviteCode[] codes = this.inviteDao.match(MatchArg.and(
					MatchArg.equals("code", code),
					MatchArg.equals("type", type)));
			if (codes != null && codes.length > 0) {
				return true;
			} else {
				return false;
			}
		} catch (RollbackException e) {
			throw new DataException(e);
		}
	}
}
