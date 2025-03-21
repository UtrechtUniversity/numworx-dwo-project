package fi.dwo.server.db;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.WriteListener;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

public class RangeFilter implements Filter {
	
	class HttpResp extends HttpServletResponseWrapper {
		
		ByteArrayOutputStream out = new ByteArrayOutputStream(1024);
		ServletOutputStream sout = new ServletOutputStream() {
			
			@Override
			public void write(int b) throws IOException {
				out.write(b);				
			}
			
			@Override
			public void setWriteListener(WriteListener writeListener) {
			}
			
			@Override
			public boolean isReady() {
				return true;
			}
		};
		PrintWriter writer = new PrintWriter( new OutputStreamWriter(sout, StandardCharsets.UTF_8) );

		public HttpResp(HttpServletResponse response) {
			super(response);
		}

		@Override
		public ServletOutputStream getOutputStream() throws IOException {
			return sout;
		}

		@Override
		public PrintWriter getWriter() throws IOException {
			return writer;
		}

		Long contentLength;
		
		@Override
		public void setContentLength(int len) {
			contentLength = Long.valueOf(len);
		}

		@Override
		public void setContentLengthLong(long len) {
			contentLength = Long.valueOf(len);			
		}

		@Override
		public void flushBuffer() throws IOException {
		}

		int status = 200;
		String sm = "";
		@Override
		public void setStatus(int sc) {
			status = sc;
			sm = "";
		}

		@Override
		public void setStatus(int sc, String sm) {
			this.sm = sm;
			this.status = status;		
		}
		
	}
 	

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse resp = (HttpServletResponse) response;
		String range = req.getHeader("Range");
		if (range != null && range.startsWith("bytes=")) {
			range = range.substring(6);
			String[] split = range.split("-");
			int start = 0;
			int end = Integer.MAX_VALUE;
			if (split.length == 2) {
				start = Integer.parseInt(split[0]);
				end   = Integer.parseInt(split[1]);
			}
			// n-m
			HttpResp wrap = new HttpResp(resp);
			chain.doFilter(request, wrap);
			wrap.writer.close();
			byte[] bytes = wrap.out.toByteArray();
			resp.addHeader("Accept-Ranges", "bytes");
			end = Math.min(end, bytes.length-1);
			resp.addHeader("Content-Range", "bytes " + start  + "-" + end + "/" + bytes.length);
			resp.getOutputStream().write(bytes, start, end-start+1);
			resp.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT);
			return;
		}
// no range		
		chain.doFilter(request, response);

	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub

	}

}
