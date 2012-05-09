package fi.dwo.server.persistence;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

import fi.dwo.client.persistence.DbAccessIF;

public class DbAccessScienceServlet extends DbAccessServlet {

	DbAccessScienceServlet() {
		super(new DbAccessScience());
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see fi.dwo.server.persistence.DbAccessServlet#init(javax.servlet.ServletConfig)
	 */
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
	}

}
