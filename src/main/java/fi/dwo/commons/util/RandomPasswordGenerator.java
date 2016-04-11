/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.dwo.commons.util;

import java.security.SecureRandom;

/**
 * RandomPasswordGenerator for DWO allows to set and select characters to be
 * used for generation.
 *
 * @author G.A.J. van der Plas
 */
public class RandomPasswordGenerator {

    private static RandomPasswordGenerator _instance;
    private final char[] chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890".toCharArray();
    StringBuilder sb;
//
//    RandomPasswordGenerator() {
//        //sb = new StringBuilder();
//    }

    public String Generate(int size) {

        SecureRandom random = new SecureRandom();
        sb = new StringBuilder();
        for (int i = 0; i < size; i++) {
            char c = chars[random.nextInt(chars.length)];
            sb.append(c);
        }
        return sb.toString();
    }

    /**
     * Returns an instance of PersistenceFacade.
     *
     * @return fi.dwo.client.persistence.PersistenceFacade
     *
     */
    public static RandomPasswordGenerator instance() {
        if (_instance == null) {
            _instance = new RandomPasswordGenerator();
        }
        return _instance;
    }
}
