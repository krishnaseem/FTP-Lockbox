/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package processes;

/**
 *
 * @author seemanapallik
 */
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.KeySpec;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.DESedeKeySpec;

import org.apache.commons.codec.binary.Base64;

public class ByteEncryptDecrypt
{
	
	public static final String DESEDE_ENCRYPTION_SCHEME = "DESede";
	public static final String DES_ENCRYPTION_SCHEME = "DES";
	public static final String DEFAULT_ENCRYPTION_KEY	= "Washington Post Encrypter, developed for PAS project";
	
	private KeySpec				keySpec;
	private SecretKeyFactory	keyFactory;
	private Cipher				cipher;
	
	private static final String	UNICODE_FORMAT			= "UTF8";


	public ByteEncryptDecrypt() throws EncryptionException
	{
		this( DESEDE_ENCRYPTION_SCHEME, DEFAULT_ENCRYPTION_KEY );
	}


	private ByteEncryptDecrypt(String encryptionScheme) throws EncryptionException
	{
		this( encryptionScheme, DEFAULT_ENCRYPTION_KEY );
	}

	private ByteEncryptDecrypt(String encryptionScheme, String encryptionKey) throws EncryptionException
	{

		if ( encryptionKey == null )
				throw new IllegalArgumentException( "encryption key was null" );
		if ( encryptionKey.trim().length() < 24 )
				throw new IllegalArgumentException(
						"encryption key was less than 24 characters" );

		try
		{
			byte[] keyAsBytes = encryptionKey.getBytes( UNICODE_FORMAT );

			if ( encryptionScheme.equals( DESEDE_ENCRYPTION_SCHEME) )
			{
				keySpec = new DESedeKeySpec( keyAsBytes );
			}
			else if ( encryptionScheme.equals( DES_ENCRYPTION_SCHEME ) )
			{
				keySpec = new DESKeySpec( keyAsBytes );
			}
			else
			{
				throw new IllegalArgumentException( "Encryption scheme not supported: "
													+ encryptionScheme );
			}

			keyFactory = SecretKeyFactory.getInstance( encryptionScheme );
			cipher = Cipher.getInstance( encryptionScheme );

		}
		catch (InvalidKeyException e)
		{
			throw new EncryptionException( e );
		}
		catch (UnsupportedEncodingException e)
		{
			throw new EncryptionException( e );
		}
		catch (NoSuchAlgorithmException e)
		{
			throw new EncryptionException( e );
		}
		catch (NoSuchPaddingException e)
		{
			throw new EncryptionException( e );
		}

	}

	public byte[] encrypt( byte[] toBeEncrypted ) throws EncryptionException
	{
		if ( toBeEncrypted == null || toBeEncrypted.length == 0 )
				throw new IllegalArgumentException(
						"bytes to be encrypted are null or empty" );

		try
		{
			SecretKey key = keyFactory.generateSecret( keySpec );
			cipher.init( Cipher.ENCRYPT_MODE, key );
			return cipher.doFinal( toBeEncrypted );

		}
		catch (Exception e)
		{
			throw new EncryptionException( e );
		}
	}

	public byte[] decrypt( byte[] toBeDecrypted  ) throws EncryptionException
	{
		if ( toBeDecrypted  == null || toBeDecrypted .length <= 0 )
			throw new IllegalArgumentException(
					"bytes to be decrypted are null or empty" );

		try
		{
			SecretKey key = keyFactory.generateSecret( keySpec );
			cipher.init( Cipher.DECRYPT_MODE, key );
			return cipher.doFinal( toBeDecrypted );

		}
		catch (Exception e)
		{
			throw new EncryptionException( e );
		}
	}


	public static class EncryptionException extends Exception
	{
		public EncryptionException( Throwable t )
		{
			super( t );
		}
	}
    
    
    
}
