package nl.uu.fi.dwo.rest.dom.entities;

import java.util.Objects;

public class DomStudentModelMethodInfo {

	private String method, book;
	private Integer chapter;
	private String variant;
	
	private Integer x, y;
	
	public DomStudentModelMethodInfo() {
	}

	public DomStudentModelMethodInfo(String methodeName, String leerjaarName, Integer i) {
		method = methodeName;
		book = leerjaarName;
		chapter = i;
	}
	
	public DomStudentModelMethodInfo(DomStudentModelMethodInfo source) {
		this(source.method, source.book, source.chapter);
		x = source.x;
		y = source.y;
		variant = source.variant;
	}

	/**
	 * @return the method
	 */
	public String getMethod() {
		return method;
	}

	/**
	 * @param method the method to set
	 */
	public void setMethod(String method) {
		this.method = method;
	}

	/**
	 * @return the book
	 */
	public String getBook() {
		return book;
	}

	/**
	 * @param book the book to set
	 */
	public void setBook(String book) {
		this.book = book;
	}

	/**
	 * @return the chapter
	 */
	public Integer getChapter() {
		return chapter;
	}

	/**
	 * @param chapter the chapter to set
	 */
	public void setChapter(Integer chapter) {
		this.chapter = chapter;
	}

	/**
	 * @return the x
	 */
	public Integer getX() {
		return x;
	}

	/**
	 * @param x the x to set
	 */
	public void setX(Integer x) {
		this.x = x;
	}

	/**
	 * @return the y
	 */
	public Integer getY() {
		return y;
	}

	/**
	 * @param y the y to set
	 */
	public void setY(Integer y) {
		this.y = y;
	}

	public String getVariant() {
		return variant;
	}

	public void setVariant(String variant) {
		this.variant = variant;
	}

	@Override
	public int hashCode() {
		return Objects.hash(book, chapter, method, x, y, variant);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DomStudentModelMethodInfo other = (DomStudentModelMethodInfo) obj;
		return Objects.equals(book, other.book) && Objects.equals(chapter, other.chapter)
				&& Objects.equals(variant, other.variant)
				&& Objects.equals(method, other.method) && Objects.equals(x, other.x) && Objects.equals(y, other.y);
	}

	public String key() {
		return method + "-" + book + "-" + chapter;
	}
	
}
