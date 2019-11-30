package com.happy;


import com.happy.ElfType32.*;
import com.happy.ElfType64.*;
import com.happy.Utils;
import com.happy.EncodeSection;
import com.happy.ParseSo;

public class Test {
	
	public static ElfType32 type_32 = new ElfType32();
	public static ElfType64 type_64 = new ElfType64();
	public static String direc = "YOUR_PROJECT_PATH\\Machine\\app\\build\\intermediates\\transforms\\stripDebugSymbol\\debug\\0\\lib\\x86\\";
	public static String srcfile = direc + "libnative-lib.so";
	public static String desfile = direc + "libnative-lib_.so";
	static String release_dir="YOUR_PROJECT_PATH\\Machine\\app\\release\\lib\\";
	public static void main(String[] args){
		release_dir="YOUR_PROJECT_PATH\\Machine\\app\\build\\intermediates\\transforms\\stripDebugSymbol\\release\\0\\lib\\";

		work(release_dir+"armeabi-v7a\\");
		work(release_dir+"x86\\");
	}
	static void work(String dir){
		String srcfile = dir + "libnative-lib.so";
		String desfile = dir + "libnative-lib_.so";
		enc(srcfile,desfile);
	}

	public static void enc(String srcfile, String desfile){
		byte[] fileByteArys = Utils.readFile(srcfile);

		if(fileByteArys == null){
			System.out.println("read file byte failed...");
			return;
		}

		byte ei_class = fileByteArys[4];
		if(ei_class == 1){
			System.out.println("32bit elf");
			ParseSo.parseSo32(fileByteArys);
			EncodeSection.encodeSection32(fileByteArys);
			ParseSo.parseSo32(fileByteArys);
		}
		else if(ei_class == 2){
			System.out.println("64bit elf");
			ParseSo.parseSo64(fileByteArys);
			EncodeSection.encodeSection64(fileByteArys);
			ParseSo.parseSo64(fileByteArys);
		}

		Utils.saveFile(desfile, fileByteArys);	}
}
