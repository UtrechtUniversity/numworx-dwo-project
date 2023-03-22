/**
Copyright (c) 2011, Gert van der Plas
All rights reserved.
Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:

    * Redistributions of source code must retain the above copyright
      notice, this list of conditions and the following disclaimer.
    * Redistributions in binary form must reproduce the above copyright
      notice, this list of conditions and the following disclaimer in the
      documentation and/or other materials provided with the distribution.
    * Neither the name of Gert van der Plas nor the
      names of its contributors may be used to endorse or promote products
      derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY GERT VAN DER PLAS AND CONTRIBUTORS "AS IS" AND ANY
EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL GERT VAN DER PLAS AND CONTRIBUTORS BE LIABLE FOR ANY
DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

package com.digitalmolehill.crypto;

import java.security.Security;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;

/**
 *
 * @author Gert van der Plas
 * 
 * Updated 2016. Exception type and classname.
 */
public class SymmetricCryptor {
    private static final Logger LOG = Logger.getLogger(SymmetricCryptor.class.getName());
    static {
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
    }
// Example:
//        String s = null;;
//        s = pbc.encrypt("appelflapappelflapappelflapappelflapappelflapappelflapappelflapappelflap".toCharArray(), "The quick brown fox jumps over the lazy dog.");
//
//        System.err.println(s);
//        s = pbc.decrypt("appelflapappelflapappelflapappelflapappelflapappelflapappelflapappelflap".toCharArray(), "E82DE2D75C8F6603EA1131623B6E5933EE673E41ADC77DEE6CCF7A72FA8340B3B0915A136DB99D9F42FDE6B51A58B079");

// When rewriting code for a service the next 3 comment lines show a way to resalt each service message.
//SecureRandom saltGen = SecureRandom.getInstance(SHA1PRNG);
//this.salt = new byte[SALT_LENGTH];
//saltGen.nextBytes(this.salt);

    private PBEKeySpec pbeKeySpec;
    private PBEParameterSpec pbeParamSpec;
    private SecretKeyFactory keyFac;
    private Cipher pbeCipher;
    private SecretKey pbeKey;
     // Salt
    private byte[] salt = {
        (byte) 0x26, (byte) 0x06, (byte) 0x19, (byte) 0x67,
        (byte) 0x03, (byte) 0x07, (byte) 0x20, (byte) 0x10
    };
    // Iteration count
    private int count = 26;

    public SymmetricCryptor() {
        
    }

    private void initialize(char[] password) throws Exception {
        try {
             // Create PBE parameter set
            pbeParamSpec = new PBEParameterSpec(getSalt(), getCount());

            //Use password based encryption with SHA256 and 256 AES with cypherblockcycling and 
            keyFac = SecretKeyFactory.getInstance("PBEWithSHA256And256BitAES-CBC-BC");
            pbeKeySpec = new PBEKeySpec(password);
            pbeKey = keyFac.generateSecret(pbeKeySpec);

            // Create PBE Cipher
            pbeCipher = Cipher.getInstance("PBEWithSHA256And256BitAES-CBC-BC");

        } catch (Exception ex) {
            LOG.log(Level.SEVERE, null, ex);
            throw ex;
        }
    }

    public String encrypt(char[] password, String text) throws Exception {

        // do general stuff for 
        initialize(password);

        byte[] ciphertext;
        
        try {
            // Initialize PBE Cipher with key and parameters
            pbeCipher.init(Cipher.ENCRYPT_MODE, pbeKey, pbeParamSpec);

            byte[] cleartext = text.getBytes();

            // Encrypt the cleartext
            ciphertext = pbeCipher.doFinal(cleartext);

        } catch (Exception ex) {
            LOG.log(Level.SEVERE, null, ex);
            throw ex;
        }


        return toHex(ciphertext);

    }

    public String decrypt(char[] password, String text) throws Exception {

        // do general stuff for 
        initialize(password);

        byte[] cleartext;
        
        try {
            // Initialize PBE Cipher with key and parameters
        pbeCipher.init(Cipher.DECRYPT_MODE, pbeKey, pbeParamSpec);

           byte[] ciphertext = toByte(text);
        
        // decrypt the cleartext
        cleartext =   pbeCipher.doFinal(ciphertext);

        } catch (Exception ex) {
            LOG.log(Level.SEVERE, null, ex);
            throw ex;
        }

        String result = "";
        for(byte c : cleartext){
            result += (char) c;
        }

        return result;
    }
    
    
    private static byte[] toByte(String hexString) {
        int len = hexString.length() / 2;
        byte[] result = new byte[len];
        for (int i = 0; i < len; i++) {
            result[i] = Integer.valueOf(hexString.substring(2 * i, 2 * i + 2), 16).byteValue();
        }
        return result;
    }

    private static String toHex(byte[] buf) {
        if (buf == null) {
            return "";
        }
        StringBuffer result = new StringBuffer(2 * buf.length);
        for (int i = 0; i < buf.length; i++) {
            appendHex(result, buf[i]);
        }
        return result.toString();
    }
    private final static String HEX = "0123456789ABCDEF";

    private static void appendHex(StringBuffer sb, byte b) {
        sb.append(HEX.charAt((b >> 4) & 0x0f)).append(HEX.charAt(b & 0x0f));
    }

    /**
     * @return the salt
     */
    public byte[] getSalt() {
        return salt;
    }

    /**
     * @param salt the salt to set
     */
    public void setSalt(byte[] salt) {
        for(int i = 0;i<this.salt.length;i++){
            this.salt[i] = salt[i];
        }
    }

    /**
     * @return the count
     */
    public int getCount() {
        return count;
    }

    /**
     * @param count the count to set
     */
    public void setCount(int count) {
        this.count = count;
    }

}    

