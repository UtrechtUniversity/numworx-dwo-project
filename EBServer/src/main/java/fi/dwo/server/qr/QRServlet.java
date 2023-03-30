package fi.dwo.server.qr;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

@SuppressWarnings("serial")
public class QRServlet extends HttpServlet {

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {

    String text = req.getParameter("qr");
    resp.setContentType("image/png");
    try {
      generateQRCodeImage(text, resp.getOutputStream());
    } catch (WriterException e) {
      throw new ServletException("failed", e);
    }
  }

  @Override
  public void init() throws ServletException {
  }

  public static void generateQRCodeImage(String barcodeText, OutputStream out) throws WriterException, IOException  {
    QRCodeWriter barcodeWriter = new QRCodeWriter();
    BitMatrix bitMatrix =  barcodeWriter.encode(barcodeText, BarcodeFormat.QR_CODE, 200, 200);
    MatrixToImageWriter.writeToStream(bitMatrix, "png", out);
}
}
