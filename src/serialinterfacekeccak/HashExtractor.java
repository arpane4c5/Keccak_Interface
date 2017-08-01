/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package serialinterfacekeccak;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
/**
 *
 * @author Bloodhound
 */
public class HashExtractor
{
    private byte hash[]=new byte[64];
    private boolean correctHash=false;
    public byte[] extractHash(String path)
    {
        try
        {
            String s="";
            correctHash=false;
            BufferedReader br=new BufferedReader(new InputStreamReader(new FileInputStream(path)));
            while((s=br.readLine())!=null && !correctHash)
            {
                //s=br.readLine();
                System.out.println(s);
                correctHash=processString(s);
            }
            br.close();
        }
        catch(Exception e)
        {
            System.out.println(e.getMessage());
        }
        finally
        {
            return hash;
        }
    }

    public boolean processString(String s)
    {
        //check whether string is hex representation and process to convert into bytes
        int i,j;
        boolean found=false;
        char ch;
        byte b=0x00;
        String temp="";
        if(s.length()>54 && s.length()<200)
        {
            //construct the hash string form by removing spaces and special symbols
            for(i=0;i<s.length();i++)
            {
                if((s.charAt(i)>='0' && s.charAt(i)<='9') || (s.charAt(i)>='A' && s.charAt(i)<='F'))
                        temp+=s.charAt(i);
            }
        }
        //check length of string is 512/4 = 128 chars
        System.out.println(temp);
        if(temp.length()==128)
        {
            for(i=0,j=0;i<temp.length();i++)
            {
                ch=temp.charAt(i);
                if(ch>='0' && ch<='9')
                    b|=(byte)(ch-48);      //convert from char '0' to integer 0
                else if(ch>='A' && ch<='F')
                    b|=(byte)(ch-55);

                if(i%2!=0)      //2 chars=1 byte processed
                {
                    hash[j++]=b;
                    b=0x00;
                }
                else
                {
                    b<<=4;      //shifted to MSB 4 bits
                }
            }
            if(j==64)
                found=true;
        }
        else
            found=false;

        return found;
    }

    public boolean isCorrectHash()
    {
        return correctHash;
    }
}
