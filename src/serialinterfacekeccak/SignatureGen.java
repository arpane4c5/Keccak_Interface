/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package serialinterfacekeccak;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;
import javax.crypto.Cipher;

/**
 *
 * @author Bloodhound
 */
public class SignatureGen
{
    private PublicKey pubKey;
    private PrivateKey priKey;
    Cipher cipher;
    private byte[] cipherData;
    
    void keyGenerateRSA()
    {
        try
        {
            KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
            kpg.initialize(2048);
            KeyPair kp = kpg.genKeyPair();
            pubKey = kp.getPublic();
            priKey = kp.getPrivate();
            
            //saving the key values to a file
            KeyFactory fact = KeyFactory.getInstance("RSA");
            RSAPublicKeySpec pub = fact.getKeySpec(kp.getPublic(), RSAPublicKeySpec.class);
            RSAPrivateKeySpec priv = fact.getKeySpec(kp.getPrivate(), RSAPrivateKeySpec.class);
            saveToFile("public.key", pub.getModulus(), pub.getPublicExponent());
            saveToFile("private.key", priv.getModulus(), priv.getPrivateExponent());

        }
        catch(Exception e)
        {
            System.err.println(e.getMessage());
        }
    }

    public void saveToFile(String fileName, BigInteger mod, BigInteger exp)
    {
        ObjectOutputStream oout=null;
        try
        {
            oout = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(fileName)));
            oout.writeObject(mod);
            oout.writeObject(exp);
        }
        catch (Exception e)
        {
            System.err.println(e.getMessage());
            //throw new IOException("Unexpected error", e);
        }
        finally
        {
            try
            {
                oout.close();
            }
            catch(Exception ignored){}
        }
    }

    public byte[] rsaEncrypt(byte[] data)
    {
        //PrivateKey priKey = readKeyFromFile("/public.key");
        try
        {
            cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, priKey);
            cipherData = cipher.doFinal(data);
        }
        catch(Exception e)
        {
            System.err.println(e.getMessage());
        }
        finally
        {
                return cipherData;
        }
    }


    //Read the public keys from the files and use them to encrypt the data
    PublicKey readKeyFromFile(String keyFileName)
    {
        ObjectInputStream oin=null;
        InputStream in=null;
        try
        {
            in = this.getClass().getClassLoader().getResourceAsStream(keyFileName);
            oin = new ObjectInputStream(new BufferedInputStream(in));
            BigInteger m = (BigInteger) oin.readObject();
            BigInteger e = (BigInteger) oin.readObject();
            RSAPublicKeySpec keySpec = new RSAPublicKeySpec(m, e);
            KeyFactory fact = KeyFactory.getInstance("RSA");
            PublicKey pubKey = fact.generatePublic(keySpec);
        }
        catch (Exception e)
        {
            System.err.println("Spurious serialization error"+e.getMessage());
            //throw new RuntimeException("Spurious serialisation error", e);
        }
        finally
        {
            try
            {
                oin.close();
                in.close();
            }
            catch(Exception ignored){}
            finally
            {
                return pubKey;
            }
        }
    }

}
