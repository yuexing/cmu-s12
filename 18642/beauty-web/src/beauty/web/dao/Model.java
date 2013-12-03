package beauty.web.dao;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

import org.genericdao.ConnectionPool;
import org.genericdao.DAOException;

public class Model {

	private BenefitDao benefitDao;
	private BrandDao brandDao;
	private CategoryDao categoryDao;
	private CommentDao commentDao;
	private PhotoDao photoDao;
	private ProductDao productDao;
	private ProductTagDao productTagDao;
	private ProductBenefitDao productBenefitDao;
	private TagDao tagDao;
	private DealDao dealDao;
	private RetailDao retailDao;
	private ProductRetailDao productRetailDao;
	private UserDao userDao;
	private InviteCodeDao inviteDao;

	public Model(ServletConfig config) throws ServletException {
		try {
			String jdbcDriver = config.getInitParameter("jdbcDriverName");
			String jdbcURL = config.getInitParameter("jdbcURL");

			ConnectionPool pool = new ConnectionPool(jdbcDriver, jdbcURL);

			benefitDao = new BenefitDao("beauty_benefit", pool);
			brandDao = new BrandDao("beauty_brand", pool);
			categoryDao = new CategoryDao("beauty_category", pool);
			commentDao = new CommentDao("beauty_comment", pool);
			photoDao = new PhotoDao("beauty_photo", pool);
			productDao = new ProductDao("beauty_product", pool);
			productBenefitDao = new ProductBenefitDao("beauty_product_benefit",
					pool);
			productTagDao = new ProductTagDao("beauty_product_tag", pool);
			tagDao = new TagDao("beauty_tag", pool);
			dealDao = new DealDao("beauty_deal", pool);
			retailDao = new RetailDao("beauty_retail", pool);
			productRetailDao = new ProductRetailDao("beauty_product_retail",
					pool);
			userDao = new UserDao("beauty_user", pool);
			inviteDao = new InviteCodeDao("beauty_invite", pool);

		} catch (DAOException e) {
			throw new ServletException(e);
		}
	}

	/**
	 * @return the benefitDao
	 */
	public BenefitDao getBenefitDao() {
		return benefitDao;
	}

	/**
	 * @param benefitDao
	 *            the benefitDao to set
	 */
	public void setBenefitDao(BenefitDao benefitDao) {
		this.benefitDao = benefitDao;
	}

	/**
	 * @return the brandDao
	 */
	public BrandDao getBrandDao() {
		return brandDao;
	}

	/**
	 * @param brandDao
	 *            the brandDao to set
	 */
	public void setBrandDao(BrandDao brandDao) {
		this.brandDao = brandDao;
	}

	/**
	 * @return the categoryDao
	 */
	public CategoryDao getCategoryDao() {
		return categoryDao;
	}

	/**
	 * @param categoryDao
	 *            the categoryDao to set
	 */
	public void setCategoryDao(CategoryDao categoryDao) {
		this.categoryDao = categoryDao;
	}

	/**
	 * @return the commentDao
	 */
	public CommentDao getCommentDao() {
		return commentDao;
	}

	/**
	 * @param commentDao
	 *            the commentDao to set
	 */
	public void setCommentDao(CommentDao commentDao) {
		this.commentDao = commentDao;
	}

	/**
	 * @return the photoDao
	 */
	public PhotoDao getPhotoDao() {
		return photoDao;
	}

	/**
	 * @param photoDao
	 *            the photoDao to set
	 */
	public void setPhotoDao(PhotoDao photoDao) {
		this.photoDao = photoDao;
	}

	/**
	 * @return the productDao
	 */
	public ProductDao getProductDao() {
		return productDao;
	}

	/**
	 * @param productDao
	 *            the productDao to set
	 */
	public void setProductDao(ProductDao productDao) {
		this.productDao = productDao;
	}

	/**
	 * @return the productTagDao
	 */
	public ProductTagDao getProductTagDao() {
		return productTagDao;
	}

	/**
	 * @param productTagDao
	 *            the productTagDao to set
	 */
	public void setProductTagDao(ProductTagDao productTagDao) {
		this.productTagDao = productTagDao;
	}

	/**
	 * @return the tagDao
	 */
	public TagDao getTagDao() {
		return tagDao;
	}

	/**
	 * @param tagDao
	 *            the tagDao to set
	 */
	public void setTagDao(TagDao tagDao) {
		this.tagDao = tagDao;
	}

	public ProductBenefitDao getProductBenefitDao() {
		return productBenefitDao;
	}

	public void setProductBenefitDao(ProductBenefitDao productBenefitDao) {
		this.productBenefitDao = productBenefitDao;
	}

	/**
	 * @return the dealDao
	 */
	public DealDao getDealDao() {
		return dealDao;
	}

	/**
	 * @param dealDao
	 *            the dealDao to set
	 */
	public void setDealDao(DealDao dealDao) {
		this.dealDao = dealDao;
	}

	/**
	 * @return the retailDao
	 */
	public RetailDao getRetailDao() {
		return retailDao;
	}

	/**
	 * @param retailDao
	 *            the retailDao to set
	 */
	public void setRetailDao(RetailDao retailDao) {
		this.retailDao = retailDao;
	}

	public ProductRetailDao getProductRetailDao() {
		return this.productRetailDao;
	}

	/**
	 * @return the userDao
	 */
	public UserDao getUserDao() {
		return userDao;
	}

	/**
	 * @param userDao
	 *            the userDao to set
	 */
	public void setUserDao(UserDao userDao) {
		this.userDao = userDao;
	}

	/**
	 * @return the inviteDao
	 */
	public InviteCodeDao getInviteDao() {
		return inviteDao;
	}

	/**
	 * @param inviteDao the inviteDao to set
	 */
	public void setInviteDao(InviteCodeDao inviteDao) {
		this.inviteDao = inviteDao;
	}

	/**
	 * @param productRetailDao the productRetailDao to set
	 */
	public void setProductRetailDao(ProductRetailDao productRetailDao) {
		this.productRetailDao = productRetailDao;
	}
}
