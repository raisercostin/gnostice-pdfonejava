/**
****************************************************
*  Java based PDF creation and manipulation Library      
****************************************************
*
*  Project Title: Gnostice PDFOne Java
*  Copyright © 2002-2008 Gnostice Information Technologies Private Limited, Bangalore, India
*  http://www.gnostice.com
*
*  This file is part of PDFOne Java Library.
*
*  This program is free software: you can redistribute it and/or modify
*  it under the terms of the GNU General Public License as published by
*  the Free Software Foundation, either version 3 of the License, or
*  (at your option) any later version.
*
*  This program is distributed in the hope that it will be useful,
*  but WITHOUT ANY WARRANTY; without even the implied warranty of
*  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
*  GNU General Public License for more details.

*  You should have received a copy of the GNU General Public License
*  along with this program.  If not, see <http://www.gnu.org/licenses/>.
*
*/

package com.gnostice.pdfone;

import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.util.ArrayList;

/**
 * This class is used to specify the encryption settings and access
 * permissions for a document.
 * 
 * @since 1.0
 * @version 1.0
 */
public class PdfEncryption
{
    /**
     * Encryption key lenth of 40 bits. 
     */
    public static final int LEVEL_40_BIT = 0;

    /**
     * Encryption key lenth of 128 bits.
     */
    public static final int LEVEL_128_BIT = 1;

    protected byte ownerKey[] = new byte[32];

    protected byte userKey[] = new byte[32];

    protected byte key[];

    protected int keySize;

    protected byte globalKey[];

    protected static final byte paddingStr[] = { (byte) 0x28,
        (byte) 0xBF, (byte) 0x4E, (byte) 0x5E, (byte) 0x4E,
        (byte) 0x75, (byte) 0x8A, (byte) 0x41, (byte) 0x64,
        (byte) 0x00, (byte) 0x4E, (byte) 0x56, (byte) 0xFF,
        (byte) 0xFA, (byte) 0x01, (byte) 0x08, (byte) 0x2E,
        (byte) 0x2E, (byte) 0x00, (byte) 0xB6, (byte) 0xD0,
        (byte) 0x68, (byte) 0x3E, (byte) 0x80, (byte) 0x2F,
        (byte) 0x0C, (byte) 0xA9, (byte) 0xFE, (byte) 0x64,
        (byte) 0x53, (byte) 0x69, (byte) 0x7A };

    protected byte state[] = new byte[256];

    protected MessageDigest md5;

    protected int permissions;

    protected byte documentID[];

    protected int x;

    protected int y;

    protected byte extra[] = new byte[5];

    protected static long timer = System.currentTimeMillis();
    
    protected String userPwd;
    
    protected String ownerPwd;
    
    protected int level;
    
    /**
     * User access permission that allows for printing of document,
     * but not necessarily at quality level specified by {@link
     * #AllowHighResPrint}.
     */
    public static final int AllowPrinting = 4 ; //3'rd bit

    /**
     * User access permission that allows for modification of 
     * document through means other than those specified by
     * {@link #AllowModifyAnnotations}, {@link #AllowFormFill}, and
     * {@link #AllowAssembly}.
     */
    public static final int AllowModifyContents = 8; //4

    /**
     * User access permission that allows for copying or extracting 
     * of all text and graphics, but not those covered by 
     * {@link #AllowAccessibility}.
     */
    public static final int AllowCopy = 16; //5

    /**
     * User access permission that allows for adding or modifying
     * text annotations and filling in interactive form fields, and, 
     * in conjunction with {@link #AllowModifyContents}, allows for
     * creating or modifying interactive form fields (including
     * signature fields).
     */
    public static final int AllowModifyAnnotations = 32; //6

    /**
     * User access permission that allows for filling in existing
     * form fields (including signature fields) even when 
     * {@link #AllowModifyAnnotations} has not been specified.  
     */
    public static final int AllowFormFill = 8 + 256; // 9

    /**
     * User access permission that allows for extracting text and 
     * graphics meant for applications such as providing 
     * accessibility to persons with disabilities.
     */
    public static final int AllowAccessibility = 16 + 512; //10

    /**
     * User access permission that allows for assembling the 
     * document, including inserting, rotating, or deleting pages and 
     * creating bookmarks or thumbnail images, even when 
     * {@link #AllowModifyContents} has not been specified.
     */
    public static final int AllowAssembly = /*8 + */1024; //11

    /**
     * User access permission that allows for printing a document 
     * with quality of appearance high enough to be able to reproduce 
     * an exact digital copy. When not specified, the document is 
     * likely to be printed with a lesser quality of appearance.
     */
    public static final int AllowHighResPrint = 4 + 2048; //12

    protected PdfEncryption()
    {
        try
        {
            md5 = MessageDigest.getInstance("MD5");
            userPwd = "";
            ownerPwd = "";
            level = LEVEL_40_BIT;
            permissions = -3904;//-44;
        }
        catch (Exception e)
        {
            //throw new PdfException("Invalid PDF encryption");
        }
    }

    private synchronized byte[] padPassword(byte userPassword[])
    {
        byte userPad[] = new byte[32];
        if (userPassword == null)
        {
            System.arraycopy(paddingStr, 0, userPad, 0, 32);
        }
        else
        {
            System.arraycopy(userPassword, 0, userPad, 0, Math.min(
                userPassword.length, 32));
            if (userPassword.length < 32)
                System.arraycopy(paddingStr, 0, userPad,
                    userPassword.length, 32 - userPassword.length);
        }

        return userPad;
    }

    protected synchronized byte[] computeOwnerKey(byte userPad[],
        byte ownerPad[], boolean is128Bits)
    {
        byte ownerKey[] = new byte[32];

        byte digest[] = md5.digest(ownerPad);
        if (is128Bits)
        {
            byte globalKey[] = new byte[16];
            for (int k = 0; k < 50; ++k)
                digest = md5.digest(digest);
            System.arraycopy(userPad, 0, ownerKey, 0, 32);
            for (int i = 0; i < 20; ++i)
            {
                for (int j = 0; j < globalKey.length; ++j)
                    globalKey[j] = (byte) (digest[j] ^ i);
                setRC4Key(globalKey);
                encryptRC4(ownerKey);
            }
        }
        else
        {
            setRC4Key(digest, 0, 5);
            encryptRC4(userPad, ownerKey);
        }

        return ownerKey;
    }

    private synchronized void setupGlobalEncryptionKey(
        byte[] documentID, byte userPad[], byte ownerKey[],
        int permissions, boolean is128Bits)
    {
        this.documentID = documentID;
        this.ownerKey = ownerKey;
        this.permissions = permissions;
        globalKey = new byte[is128Bits ? 16 : 5];

        md5.reset();
        md5.update(userPad);
        md5.update(ownerKey);

        byte ext[] = new byte[4];
        ext[0] = (byte) permissions;
        ext[1] = (byte) (permissions >> 8);
        ext[2] = (byte) (permissions >> 16);
        ext[3] = (byte) (permissions >> 24);
        md5.update(ext, 0, 4);
        if (documentID != null)
            md5.update(documentID);

        byte digest[] = md5.digest();

        if (globalKey.length == 16)
        {
            for (int k = 0; k < 50; ++k)
                digest = md5.digest(digest);
        }

        System.arraycopy(digest, 0, globalKey, 0, globalKey.length);
    }

    private synchronized void setupUserKey()
    {
        if (globalKey.length == 16)
        {
            md5.update(paddingStr);
            byte digest[] = md5.digest(documentID);
            System.arraycopy(digest, 0, userKey, 0, 16);
            for (int k = 16; k < 32; ++k)
                userKey[k] = 0;
            for (int i = 0; i < 20; ++i)
            {
                for (int j = 0; j < globalKey.length; ++j)
                    digest[j] = (byte) (globalKey[j] ^ i);
                setRC4Key(digest, 0, globalKey.length);
                encryptRC4(userKey, 0, 16);
            }
        }
        else
        {
            setRC4Key(globalKey);
            encryptRC4(paddingStr, userKey);
        }
    }

    protected synchronized void setupAllKeys(byte userPassword[],
        byte ownerPassword[], int permissions, boolean is128Bits)
    {
        if (ownerPassword == null || ownerPassword.length == 0)
        {
            ownerPassword = md5.digest(createDocumentId());
        }
        //permissions |= is128Bits ? 0xfffff0c0 : 0xffffffc0;
        //permissions &= 0xfffffffc;
        
        byte userPad[] = padPassword(userPassword);
        byte ownerPad[] = padPassword(ownerPassword);
        this.ownerKey = computeOwnerKey(userPad, ownerPad,
            is128Bits);
        documentID = createDocumentId();
        setupByUserPad(this.documentID, userPad, this.ownerKey,
            permissions, is128Bits);
    }

    protected synchronized static byte[] createDocumentId()
    {
        MessageDigest md5 = null;
        try
        {
            md5 = MessageDigest.getInstance("MD5");
        }
        catch (Exception e)
        {
            //throw some exception
        }
        long time = System.currentTimeMillis();
        long mem = Runtime.getRuntime().freeMemory();
        String s = time + "+" + mem + "+" + (timer++);
        return md5.digest(s.getBytes());
    }

    protected synchronized void setupByUserPassword(
        byte[] documentID, byte userPassword[], byte ownerKey[],
        int permissions, boolean is128Bits)
    {
        setupByUserPad(documentID, padPassword(userPassword),
            ownerKey, permissions, is128Bits);
    }

    private synchronized void setupByUserPad(byte[] documentID,
        byte userPad[], byte ownerKey[], int permissions,
        boolean is128Bits)
    {
        setupGlobalEncryptionKey(documentID, userPad, ownerKey,
            permissions, is128Bits);
        setupUserKey();
    }

    protected synchronized void setupByOwnerPassword(
        byte[] documentID, byte ownerPassword[], byte userKey[],
        byte ownerKey[], int permissions, boolean is128Bits)
    {
        setupByOwnerPad(documentID, padPassword(ownerPassword),
            userKey, ownerKey, permissions, is128Bits);
    }

    private synchronized void setupByOwnerPad(byte[] documentID,
        byte ownerPad[], byte userKey[], byte ownerKey[],
        int permissions, boolean is128Bits)
    {
        byte userPad[] = computeOwnerKey(ownerKey, ownerPad,
            is128Bits);
        setupGlobalEncryptionKey(documentID, userPad, ownerKey,
            permissions, is128Bits);
        setupUserKey();
    }

    protected synchronized void setKey()
    {
        setRC4Key(key, 0, keySize);
    }

    protected synchronized void setHashKey(int number, int generation)
    {
        md5.reset();
        extra[0] = (byte) number;
        extra[1] = (byte) (number >> 8);
        extra[2] = (byte) (number >> 16);
        extra[3] = (byte) generation;
        extra[4] = (byte) (generation >> 8);
        md5.update(globalKey);
        key = md5.digest(extra);
        keySize = globalKey.length + 5;
        if (keySize > 16)
            keySize = 16;
    }

    protected static ArrayList createId(byte id[])
    {
        String fileID;
        fileID = PdfString.fromBytes(id);
        ArrayList arr = new ArrayList();
        arr.add(new PdfString(fileID, true));
        arr.add(new PdfString(fileID, true));
        return arr;
    }

    protected synchronized void setRC4Key(byte key[])
    {
        setRC4Key(key, 0, key.length);
    }

    protected synchronized void setRC4Key(byte key[], int off, int len)
    {
        int index1 = 0;
        int index2 = 0;
        for (int k = 0; k < 256; ++k)
            state[k] = (byte) k;
        x = 0;
        y = 0;
        byte tmp;
        for (int k = 0; k < 256; ++k)
        {
            index2 = (key[index1 + off] + state[k] + index2) & 255;
            tmp = state[k];
            state[k] = state[index2];
            state[index2] = tmp;
            index1 = (index1 + 1) % len;
        }
    }

    protected synchronized void encryptRC4(byte dataIn[], int off, int len,
        byte dataOut[])
    {
        int length = len + off;
        byte tmp;
        for (int k = off; k < length; ++k)
        {
            x = (x + 1) & 255;
            y = (state[x] + y) & 255;
            tmp = state[x];
            state[x] = state[y];
            state[y] = tmp;
            dataOut[k] = (byte) (dataIn[k] ^ state[(state[x] + state[y]) & 255]);
        }
    }

    protected synchronized void encryptRC4(byte data[], int off,
        int len)
    {
        encryptRC4(data, off, len, data);
    }

    protected synchronized void encryptRC4(byte dataIn[],
        byte dataOut[])
    {
        encryptRC4(dataIn, 0, dataIn.length, dataOut);
    }

    protected synchronized void encryptRC4(byte data[])
    {
        encryptRC4(data, 0, data.length, data);
    }

    protected static byte[] encryptRC4(ByteBuffer bb, PdfEncryption e)
    {
        if (bb == null)
        {
            return new byte[0];
        }
        int length = bb.capacity();
        byte[] ba = new byte[length];
        bb.position(0);
        bb.get(ba);
        e.encryptRC4(ba, 0, length, ba);
        
        return ba;
    }
    
    protected static ByteBuffer encryptBufferRC4(ByteBuffer bb,
        PdfEncryption e)
    {
        if (bb == null)
        {
            return ByteBuffer.allocate(0);
        }
        int length = bb.capacity();
        byte[] ba = new byte[length];
        bb.position(0);
        bb.get(ba);
        e.encryptRC4(ba, 0, length, ba);
        
        return ByteBuffer.wrap(ba);
    }

    protected static void decryptRC4(byte data[], PdfEncryption e)
    {
        e.encryptRC4(data, 0, data.length, data);
    }
    
    protected static void decryptRC4(byte data[], int offset,
        int len, PdfEncryption e)
    {
        e.encryptRC4(data, offset, len, data);
    }
    
    protected synchronized ArrayList getFileID()
    {
        return createId(documentID);
    }

    protected synchronized int getLevel()
    {
        return level;
    }
    
    /**
     * Specifies encryption key length for this 
     * <code>PdfEncryption</code> object.
     * 
     * @param level
     *            constant specifying the encryption key length for  
     *            this <code>PdfEncryption</code> object
     * @since 1.0
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfEncryption.ExampleSyntax.htm#setLevel">example</a>.
     */
    public synchronized void setLevel(int level)
    {
        this.level = level;
    }
    
    /**
     * Returns owner password specified for this
     * <code>PdfEncryption</code> object.
     * 
     * @return owner password specified for the
     *         <code>PdfEncryption</code> object
     * @since 1.0
     */
    public synchronized String getOwnerPwd()
    {
        return ownerPwd;
    }

    
    /**
     * Specifies owner password for this <code>PdfEncryption</code>
     * object.
     * 
     * @param ownerPwd
     *            owner password for the <code>PdfEncryption</code>
     *            object
     * @since 1.0
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfEncryption.ExampleSyntax.htm#setOwnerPwd">example</a>.
     */
    public synchronized void setOwnerPwd(String ownerPwd)
    {
        this.ownerPwd = ownerPwd;
    }
    
    /**
     * Returns user access permissions specified for this 
     * <code>PdfEncryption</code> object.
     * 
     * @return constant or combined value of constants representing
     *         user access permissions specified for the 
     *         <code>PdfEncryption</code> object
     * @since 1.0
     */
    public synchronized int getPermissions()
    {
        return permissions;
    }
    
    /**
     * Specifies user access permissions for this
     * <code>PdfEncryption</code> object.
     * 
     * @param permissions
     *            constants or combined value of constant specifying 
     *            user access permissions for this
     *            <code>PdfEncryption</code> object
     * @since 1.0
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfEncryption.ExampleSyntax.htm#setPermissions">example</a>.
     */
    public synchronized void setPermissions(int permissions)
    {
        if (level == LEVEL_40_BIT)
        {
            if ((permissions & AllowFormFill) == AllowFormFill)
            {
                this.permissions |= AllowModifyContents;
            }
            if ((permissions & AllowAccessibility) == AllowAccessibility)
            {
                this.permissions |= AllowCopy;
            }
            if ((permissions & AllowAssembly) == AllowAssembly)
            {
                this.permissions |= AllowModifyContents;
            }
            if ((permissions & AllowHighResPrint) == AllowHighResPrint)
            {
                this.permissions |= AllowPrinting;
            }
        }
        this.permissions |= permissions;
    }
    
    /**
     * Returns user password specified for this
     * <code>PdfEncryption</code> object.
     * 
     * @return user password specified for the
     *         <code>PdfEncryption</code> object
     * @since 1.0
     */
    public synchronized String getUserPwd()
    {
        return userPwd;
    }
    
    /**
     * Specifies user password for this <code>PdfEncryption</code>
     * object.
     * 
     * @param userPwd
     *            user password for the <code>PdfEncryption</code>
     *            object
     * @since 1.0
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfEncryption.ExampleSyntax.htm#setUserPwd">example</a>.
     */
    public synchronized void setUserPwd(String userPwd)
    {
        this.userPwd = userPwd;
    }
}