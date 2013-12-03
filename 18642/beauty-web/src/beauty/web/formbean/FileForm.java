package beauty.web.formbean;

import java.util.*;

import org.mybeans.form.*;

public abstract class FileForm extends BaseForm {

	private FileProperty file;

	public ArrayList<String> getValidationErrors() {
		ArrayList<String> errors = new ArrayList<String>();

		if (file == null || file.getBytes().length == 0) {
			errors.add("file is Zero length file");
		}
		return errors;
	}

	/**
	 * @return the file
	 */
	public FileProperty getFile() {
		return file;
	}

	/**
	 * @param file
	 *            the file to set
	 */
	public void setFile(FileProperty file) {
		this.file = file;
	}

}
