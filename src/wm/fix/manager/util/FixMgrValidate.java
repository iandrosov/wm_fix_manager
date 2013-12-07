package wm.fix.manager.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Properties;


import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;

import org.eclipse.swt.widgets.Shell;


public class FixMgrValidate 
{
	private boolean m_console = false;
	private static String lic_file_name = "config/lic.dat";
	private static int EXPIRED = 1;
	private static int INVALID = 2;
	private static long m_local_offset = 1918;

	   private int state[] = new int[5];
	   private long count;
	   public byte[] digestBits;
	   public boolean digestValid;

	   // Define class global constans to set digest array size
	   // format keycode mask to 4 and option mask - 9 characters
	   private int digest_array_size  = 10;

	   /*
	    * The following array forms the basis for the transform
	    * buffer. Update puts bytes into this buffer and then
	    * transform adds it into the state of the digest.
	    */
	   private int block[] = new int[16];
	   private int blockIndex;
	
	   int dd[] = new int[5];
	
	   //private native void generate(char[] keycode, char[] serial, char[] option);
	   private native String generate_hash(String serial, String option);
	   private native void displayHelloWorld();
	  /*
	   static
	      {
	        // The runtime system executes a class's static
	        // initializer when it loads the class.
	        System.loadLibrary("wm_fixmgr_win32");
	      }
	    */     
	public FixMgrValidate(boolean console) 
	{
		m_console = console;
		
        state = new int[5];
        count = 0;
        if (block == null)
             block = new int[16];
         digestBits = new byte[digest_array_size];
         digestValid = false;
		
	}
	
	public boolean system_check()
	{
		boolean b = true;
		if (!isExists())
			b = enter_key();
		else
			b = validate();
		
		return b;
	}
	
	private boolean isExists()
	{
		boolean b = false;
		File f = new File(lic_file_name);
		b = f.exists();
		return b;
	}
	
	private boolean validate()
	{
		String key = getKey();
		int rc = validateLicense(key);
		if (rc == 0)
			return true;
		else
		{
			message();
			return false;
		}
	}
	
	private boolean enter_key()
	{
		String key = "";
		try
		{
			if (m_console)
			{
				System.out.println("Enter License Key: ");
				// enter key for keyboard input
				BufferedReader br = new BufferedReader(
		                          new InputStreamReader(System.in));
				key = br.readLine();			
				saveKey(key);
			}
			else
			{
				key = DefaultMessage("Fix Manager License");
				saveKey(key);
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		boolean b = validate();
		return b;
	}
	
	private void message()
	{
		Display display = new Display();
		Shell shell = new Shell(display);

		MessageBox msg = new MessageBox(shell,SWT.OK|SWT.CANCEL|SWT.ICON_ERROR);
		msg.setText("Fix Manager");
		msg.setMessage("Fix Manager License is not valid!");
		msg.open();		
		
		//clean
		shell.dispose();
		display.dispose();		
	}
	
	private String DefaultMessage(String title)
	{
		String str = null;
		Display display = new Display();
		Shell shell = new Shell(display);
		FixMgrSrs msg = new FixMgrSrs(shell);
		if (msg.open() == 1)
			str = msg.getKey();

		//clean
		shell.dispose();
		display.dispose();
		
		return str;
	}
	
	private String getKey()
	{
		String key = null;
		try
		{
			File f = new File(lic_file_name);
			if (!f.exists())
				return key;
			Properties p = new Properties();
			InputStream in_stream = (InputStream) new FileInputStream(lic_file_name);
			p.load(in_stream);
			in_stream.close();
			key = p.getProperty("key");
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return key;
	}
	private int validateLicense(String key)
	{
		  int rc = 0;
		  if (key == null)
			  return INVALID;
		  if (key.length() < 20)
			  return INVALID;
		  // handle date part
		  String expire = getExpirationDateFromKey(key);
		  if (isExpired(expire))
			  return EXPIRED;
		  if (!checkSystem(key))
			  return INVALID;
		  
		  return rc;
	}
	
	private void saveKey(String key)
	{
		try
		{
			if (key != null && key.length() > 0)
			{
				FileWriter fw = new FileWriter(lic_file_name,false);
				fw.write("key="+key);
				fw.flush();
				fw.close();
				fw = null;
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	  private String getExpirationDateFromKey(String key)
	  {
		  // get offset
		  String str = key.substring(0,4);
		  long ext_offset = Long.parseLong(str);
		  str = getOptionsFromKeyCode(key);
		  long d_date = Long.parseLong(str,16);
		  long res = d_date - (m_local_offset + ext_offset);
		  return Long.toString(res);
	  }
	  
	  private String getOptionsFromKeyCode(String key_code)
	  {
	    String str = "";
	    String tmp = "";
	    // Ensure that data has at least valid options mask size of 9 to do the parsing
	    if (key_code.length() > 9)
	    {
	      str = key_code.substring(key_code.length() - 9, key_code.length());
	      tmp = str.replaceAll("-","");
	      str = tmp;
	    }

	    return str;
	  }
	  
	  private boolean isExpired(String date)
	  {
		  boolean expired = false;
		  try
		  {
			  String s1 = null;
			  if (date.length()==6)
				  s1 = date.substring(0,4);
			  else
				  s1 = "0"+date.substring(0,3);
			  String s2 = date.substring(date.length()-2,date.length());
			  s1+="20"+s2;
			  Date dt1 = new Date();
			  SimpleDateFormat df = new SimpleDateFormat("MMddyyyy");
			  Date dt2 = df.parse(s1);
			  if (dt1.after(dt2))
				  expired = true;
			  else
				  expired = false;
		  }
		  catch(Exception e)
		  {
			  e.printStackTrace();
		  }
		  return expired;
	  }
	
	  /**
	   * checkSystem test if license valid for system to run on
	   * @return
	   */
	  private boolean checkSystem(String key)
	  {
		  boolean b = false;
		  String t = key.substring(6,key.length()-11);
		  String h = t.replaceAll("-","");
		  String tmp = getOptionsFromKeyCode(key);
		  long d_date = Long.parseLong(tmp,16);
		  String opt = Long.toString(d_date); 
		  String[] mac_list = getListSystemID();
		  for (int i = 0; i < mac_list.length; i++)
		  {
			  String z = mac_list[i];
			  String str = generate(z,opt); 
			  if (h.equals(str))
			  {
				  b = true;
			  }
		  }
		  return b;
	  }
	  
	  private String[] getListSystemID()
	  {
		String[] mac = null;
		ArrayList al = new ArrayList();
		try
		{
			Process proc = Runtime.getRuntime().exec("ipconfig /all");
			InputStream in = proc.getInputStream();
			
			int r=0;
			while (r!=-1)
			{
				byte dat[] = new byte[1024];
				r = in.read(dat,0,dat.length);
				String st = new String(dat);
				int idx = st.indexOf("Physical Address");
				while (idx != -1)
				{
					String z = st.substring(idx,idx+53).replaceAll(" ","");
					String zf = z.substring(25,z.length());
					al.add(zf);
					idx = st.indexOf("Physical Address",idx+16);
				}
			}
			mac = new String[al.size()];
			for (int i=0; i < al.size(); i++)
				mac[i]=(String)al.get(i);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		return mac;
	  }
	  
	  public String generate(String serial, String option)
	  {	     
	     String str = serial + option;
	     digest_array_size = 10;
	     this.init();
	     this.updateASCII(str);
	     this.finish();
	     str = this.digout();

	     return str;
	  }

	  public String gen(String ser, String opt)
	  {
		  String res = null;
		  //char[] key = new char[20];
		  String key = "";
		  //System.loadLibrary("WM_FIXMGR_WIN32");
		  //ClassLoader cl = this.getClass().getClassLoader();
		  
		  //generate(key, ser.toCharArray(), opt.toCharArray());
		  //displayHelloWorld();
		  key = generate_hash( ser, opt);
		  
		  res = new String(key);
		  return res;
	  }
	  
	  /**
	  *
	  * KeyCodeBuilderInit - Initialize new context
	  */
	 private void init() {
	 /* Original SHA1 initialization constants */
	 /**
	   state[0] = 0x67452301;
	   state[1] = 0xEFCDAB89;
	   state[2] = 0x98BADCFE;
	   state[3] = 0x10325476;
	   state[4] = 0xC3D2E1F0;
	 **/

	   // Shuffeled driver keys
	   state[0] = 0x98BADCFE;
	   state[1] = 0xC3D2E1F0;
	   state[2] = 0x67452301;
	   state[3] = 0x10325476;
	   state[4] = 0xEFCDAB89;

	   count = 0;
	   digestBits = new byte[digest_array_size];
	   digestValid = false;
	   blockIndex = 0;
	 }

	 /**
	  * Add one byte to the digest. When this is implemented
	  * all of the abstract class methods end up calling
	  * this method for types other than bytes.
	  */
	 private synchronized void update(byte b) {
	   int mask = (8 * (blockIndex & 3));

	   count += 8;
	   block[blockIndex >> 2] &= ~(0xff << mask);
	   block[blockIndex >> 2] |= (b & 0xff) << mask;
	   blockIndex++;
	   if (blockIndex == 64) {
	       transform();
	       blockIndex = 0;
	   }
	 }


	 /**
	  * Complete processing on the message digest.
	  */
	 private void finish() {
	   byte bits[] = new byte[8];
	   int i;

	   for (i = 0; i < 8; i++) {
	       bits[i] = (byte)((count >>> (((7 - i) * 8))) & 0xff);
	   }

	   update((byte) 128);
	   while (blockIndex != 56)
	         update((byte) 0);

	   // This should cause a transform to happen.
	   update(bits);
	   for (i = 0; i < digestBits.length; i++)
	   {
	       digestBits[i] = (byte)((state[i>>2] >> ((3-(i & 3)) * 8) ) & 0xff);
	   }
	   digestValid = true;
	 }
	  /**
	   * Add specific bytes to the digest.
	   * @param input
	   * @param offset
	   * @param len
	   */
	private synchronized void update(byte input[], int offset, int len)
	{
	  for (int i = 0; i < len; i++)
	  {
	      update(input[i+offset]);
	  }
	}

	/**
	* Add an array of bytes to the digest.
	*/
	private synchronized void update(byte input[])
	{
	  update(input, 0, input.length);
	}

	/**
	* Treat the string as a sequence of ISO-Latin1 (8 bit) characters.
	*/
	private void updateASCII(String input)
	{
	     int     i, len;
	     byte    x;

	     len = input.length();
	     for (i = 0; i < len; i++)
	     {
	        x = (byte) (input.charAt(i) & 0xff);
	          update(x);
	     }
	}

	
	/*
	 * These functions are taken out of #defines in C
	 * code. Java doesn't have a preprocessor so the first
	 * step is to just promote them to real methods.
	 * Later we can optimize them out into inline code,
	 * note that by making them final some compilers will
	 * inline them when given the -O flag.
	 */
	final int rol(int value, int bits)
	{
	  int q = (value << bits) | (value >>> (32 - bits));
	  return q;
	}

	final int blk0(int i) {
	  block[i] = (rol(block[i],24)&0xFF00FF00) | (rol(block[i],8)&0x00FF00FF);
	  return block[i];
	}

	final int blk(int i) {
	  block[i&15] = rol(block[(i+13)&15]^block[(i+8)&15]^
	  block[(i+2)&15]^block[i&15], 1);
	  return (block[i&15]);
	}

	final void R0(int data[], int v, int w, int x , int y, int z, int i) {
	  data[z] += ((data[w] & (data[x] ^ data[y] )) ^ data[y]) +
	  blk0(i) + 0x5A827999 + rol(data[v] ,5);
	  data[w] = rol(data[w], 30);
	}

	final void R1(int data[], int v, int w, int x, int y, int z, int i) {
	  data[z] += ((data[w] & (data[x] ^ data[y])) ^ data[y]) +
	              blk(i) + 0x5A827999 + rol(data[v] ,5);
	  data[w] = rol(data[w], 30);
	}

	final void R2(int data[], int v, int w, int x, int y, int z, int i) {
	  data[z] += (data[w] ^ data[x] ^ data[y]) +
	  blk(i) + 0x6ED9EBA1 + rol(data[v] ,5);
	  data[w] = rol(data[w], 30);
	}

	final void R3(int data[], int v, int w, int x, int y, int z, int i) {
	  data[z] += (((data[w] | data[x]) & data[y]) | (data[w] & data[x])) +
	           blk(i) + 0x8F1BBCDC + rol(data[v] ,5);
	  data[w] = rol(data[w], 30);
	}

	final void R4(int data[], int v, int w, int x, int y, int z, int i) {
	  data[z] += (data[w] ^ data[x] ^ data[y]) +
	            blk(i) + 0xCA62C1D6 + rol(data[v] ,5);
	  data[w] = rol(data[w], 30);
	}
	
	/**
	 * Hash a single 512-bit block. This is the core of the algorithm.
	 *
	 * Note that working with arrays is very inefficent in Java as it
	 * does a class cast check each time you store into the array.
	 *
	 */

	void transform() 
	{

		/* Copy context->state[] to working vars */
		dd[0] = state[0];
		dd[1] = state[1];
		dd[2] = state[2];
		dd[3] = state[3];
		dd[4] = state[4];
		/* 4 rounds of 20 operations each. Loop unrolled. */
		R0(dd,0,1,2,3,4, 0);
		R0(dd,4,0,1,2,3, 1);
		R0(dd,3,4,0,1,2, 2);
		R0(dd,2,3,4,0,1, 3);
		R0(dd,1,2,3,4,0, 4);
		R0(dd,0,1,2,3,4, 5);
		R0(dd,4,0,1,2,3, 6);
		R0(dd,3,4,0,1,2, 7);
		R0(dd,2,3,4,0,1, 8);
		R0(dd,1,2,3,4,0, 9);
		R0(dd,0,1,2,3,4,10);
		R0(dd,4,0,1,2,3,11);
		R0(dd,3,4,0,1,2,12);
		R0(dd,2,3,4,0,1,13);
		R0(dd,1,2,3,4,0,14);
		R0(dd,0,1,2,3,4,15);
		R1(dd,4,0,1,2,3,16);
		R1(dd,3,4,0,1,2,17);
		R1(dd,2,3,4,0,1,18);
		R1(dd,1,2,3,4,0,19);
		R2(dd,0,1,2,3,4,20);
		R2(dd,4,0,1,2,3,21);
		R2(dd,3,4,0,1,2,22);
		R2(dd,2,3,4,0,1,23);
		R2(dd,1,2,3,4,0,24);
		R2(dd,0,1,2,3,4,25);
		R2(dd,4,0,1,2,3,26);
		R2(dd,3,4,0,1,2,27);
		R2(dd,2,3,4,0,1,28);
		R2(dd,1,2,3,4,0,29);
		R2(dd,0,1,2,3,4,30);
		R2(dd,4,0,1,2,3,31);
		R2(dd,3,4,0,1,2,32);
		R2(dd,2,3,4,0,1,33);
		R2(dd,1,2,3,4,0,34);
		R2(dd,0,1,2,3,4,35);
		R2(dd,4,0,1,2,3,36);
		R2(dd,3,4,0,1,2,37);
		R2(dd,2,3,4,0,1,38);
		R2(dd,1,2,3,4,0,39);
		R3(dd,0,1,2,3,4,40);
		R3(dd,4,0,1,2,3,41);
		R3(dd,3,4,0,1,2,42);
		R3(dd,2,3,4,0,1,43);
		R3(dd,1,2,3,4,0,44);
		R3(dd,0,1,2,3,4,45);
		R3(dd,4,0,1,2,3,46);
		R3(dd,3,4,0,1,2,47);
		R3(dd,2,3,4,0,1,48);
		R3(dd,1,2,3,4,0,49);
		R3(dd,0,1,2,3,4,50);
		R3(dd,4,0,1,2,3,51);
		R3(dd,3,4,0,1,2,52);
		R3(dd,2,3,4,0,1,53);
		R3(dd,1,2,3,4,0,54);
		R3(dd,0,1,2,3,4,55);
		R3(dd,4,0,1,2,3,56);
		R3(dd,3,4,0,1,2,57);
		R3(dd,2,3,4,0,1,58);
		R3(dd,1,2,3,4,0,59);
		R4(dd,0,1,2,3,4,60);
		R4(dd,4,0,1,2,3,61);
		R4(dd,3,4,0,1,2,62);
		R4(dd,2,3,4,0,1,63);
		R4(dd,1,2,3,4,0,64);
		R4(dd,0,1,2,3,4,65);
		R4(dd,4,0,1,2,3,66);
		R4(dd,3,4,0,1,2,67);
		R4(dd,2,3,4,0,1,68);
		R4(dd,1,2,3,4,0,69);
		R4(dd,0,1,2,3,4,70);
		R4(dd,4,0,1,2,3,71);
		R4(dd,3,4,0,1,2,72);
		R4(dd,2,3,4,0,1,73);
		R4(dd,1,2,3,4,0,74);
		R4(dd,0,1,2,3,4,75);
		R4(dd,4,0,1,2,3,76);
		R4(dd,3,4,0,1,2,77);
		R4(dd,2,3,4,0,1,78);
		R4(dd,1,2,3,4,0,79);
		/* Add the working vars back into context.state[] */
		state[0] += dd[0];
		state[1] += dd[1];
		state[2] += dd[2];
		state[3] += dd[3];
		state[4] += dd[4];
	}
	/**
	 * Print out the digest in a form that can be easily compared
	 * to the test vectors.
	 */
	private String digout() {
	  StringBuffer sb = new StringBuffer();

	  for (int i = 0; i < digestBits.length; i++) {
	        char c1;

	             c1 = (char) ((digestBits[i] >>> 4) & 0xf);
	             //c2 = (char) (digestBits[i] & 0xf);
	             c1 = (char) ((c1 > 9) ? 'a' + (c1 - 10) : '0' + c1);
	             //c2 = (char) ((c2 > 9) ? 'a' + (c2 - 10) : '0' + c2);
	             sb.append(c1);
	             //sb.append(c2);

	             /*            if (((i+1) % 4) == 0)
	                             sb.append(' '); */
	         }

	         return sb.toString().toUpperCase();
	     }
	
}
