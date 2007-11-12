//  This software code is made available "AS IS" without warranties of any
//  kind.  You may copy, display, modify and redistribute the software
//  code either by itself or as incorporated into your code; provided that
//  you do not remove any proprietary notices.  Your use of this software
//  code is at your own risk and you waive any claim against Amazon
//  Digital Services, Inc. or its affiliates with respect to your use of
//  this software code. (c) 2006 Amazon Digital Services, Inc. or its
//  affiliates.

package org.lastbamboo.common.amazon.stack;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Iterator;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.httpclient.Header;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Utility methods for using Amazon S3.  This is a modified version of the
 * utilities class that Amazon distributes as example code.
 */
public class AmazonWsUtils 
    {
    
    private static final Log LOG = LogFactory.getLog(AmazonWsUtils.class);
    
    /**     
     * HMAC/SHA1 Algorithm per RFC 2104.     
     */    
    private static final String HMAC_SHA1_ALGORITHM = "HmacSHA1";
    
    /**
     * Calculate the HMAC/SHA1 on a string.
     * @param canonicalString Data to sign
     * @param awsSecretAccessKey The secret access key to sign it with.
     * @param urlEncode Whether or not to URL encode the string.
     * @return The base64-encoded RFC 2104-compliant HMAC signature.
     * @throws RuntimeException If the algorithm does not exist or if the key
     * is invalid -- both should never happen.
     */
    public static String encode(final String awsSecretAccessKey, 
        final String canonicalString, final boolean urlEncode)
        {
        // The following HMAC/SHA1 code for the signature is taken from the
        // AWS Platform's implementation of RFC2104 
        // (amazon.webservices.common.Signature)
        //
        // Acquire an HMAC/SHA1 from the raw key bytes.
        final SecretKeySpec signingKey =
            new SecretKeySpec(awsSecretAccessKey.getBytes(), HMAC_SHA1_ALGORITHM);

        // Acquire the MAC instance and initialize with the signing key.
        final Mac mac;
        try 
            {
            mac = Mac.getInstance(HMAC_SHA1_ALGORITHM);
            } 
        catch (final NoSuchAlgorithmException e) 
            {
            // should not happen
            throw new RuntimeException("Could not find sha1 algorithm", e);
            }
        try 
            {
            mac.init(signingKey);
            } 
        catch (final InvalidKeyException e) 
            {
            // also should not happen
            throw new RuntimeException("Could not initialize the MAC algorithm", e);
            }

        // Compute the HMAC on the digest, and set it.
        final String b64 = 
            Base64.encodeBytes(mac.doFinal(canonicalString.getBytes()));

        if (urlEncode) 
            {
            return urlEncode(b64);
            } 
        else 
            {
            return b64;
            }
        }
    
    private static String urlEncode(final String unencoded) 
        {
        try
            {
            return URLEncoder.encode(unencoded, "UTF-8");
            }
        catch (final UnsupportedEncodingException e)
            {
            LOG.error("Could not find encoding?", e);
            throw new RuntimeException("Could not url encode to UTF-8", e);
            }
        }
    }
