/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.dwo.dwojapplet.REST;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 *
 * @author G.A.J. van der Plas
 */
public class SimpleDigestHttpClient {

    public SimpleDigestHttpClient(String username, String password) throws IOException {
    }
    
    public HttpURLConnection digestGet(URL url) throws IOException{
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        //test if we have a none. then return conn
        
        //if no none then get it
        
        //and open an authenticated connection and return
        
        return conn;
    }

}
