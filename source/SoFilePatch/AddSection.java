package com.happy;

/**
 * ���һ��Section��
 * 1���޸�elfͷ���е�section��������Ϣ
 * 2����section header�����һ��section header��Ϣ
 * 3���޸�strtab�ĳ��ȣ�����section header������
 * @author i
 *
 */
public class AddSection {
	
	private final static String newSectionName = ".mytext";
	private final static int newSectionSize = 1000;
	private final static int newSectionNameLen = 0x10;//new section name�ĳ��Ȳ��ܳ���0x10
	
	private final static int sectionSize = 40;//һ��Section�Ĵ�С
	private final static int stringSectionSizeIndex = 20;//String section�е�size�ֶε�index
	private final static int programFileSizeIndex = 16;//program header�е�file size��index
	private final static int elfHeaderSize = 0x34;//elf header�Ĵ�С
	private final static int programHeaderSize = 0x20;//Program Header�Ĵ�С
	private final static int elfHeaderSectionCountIndex = 48;//elf header�е�section����
	
	public static int sectionHeaderOffset;//section header��ƫ��ֵ
	public static short stringSectionInSectionTableIndex;//string section��section list�е�index
	public static int stringSectionOffset;//string section�е�ƫ��ֵ
	public static int firstLoadInPHIndex;//��һ��Load���͵�Program Header����Program Header List�е�index ��0��ʼ
	public static int lastLoadInPHIndex;
	public static int addSectionStartAddr = 0;//���Section�εĿ�ʼ��ַ
	
	/**
	 * �޸�elfͷ���ܵ�section��������Ϣ
	 */
	public static byte[] changeElfHeaderSectionCount(byte[] src){
		byte[] count = Utils.copyBytes(src, elfHeaderSectionCountIndex, 2);
		short counts = Utils.byte2Short(count);
		counts++;
		count = Utils.short2Byte(counts);
		src = Utils.replaceByteAry(src, elfHeaderSectionCountIndex, count);
		return src;
	}
	
	/**
	 * ���section header��Ϣ
	 * ԭ��
	 * �ҵ�String Section��λ�ã�Ȼ���ȡ��ƫ��ֵ
	 * ��section��ӵ��ļ�ĩβ
	 */
	public static byte[] addSectionHeader(byte[] src){
		/**
		 *  public byte[] sh_name = new byte[4];
			public byte[] sh_type = new byte[4];
			public byte[] sh_flags = new byte[4];
			public byte[] sh_addr = new byte[4];
			public byte[] sh_offset = new byte[4];
			public byte[] sh_size = new byte[4];
			public byte[] sh_link = new byte[4];
			public byte[] sh_info = new byte[4];
			public byte[] sh_addralign = new byte[4];
			public byte[] sh_entsize = new byte[4];
		 */
		byte[] newHeader = new byte[sectionSize];
		
		//����һ��New Section Header
		newHeader = Utils.replaceByteAry(newHeader, 0, Utils.int2Byte(addSectionStartAddr - stringSectionOffset));
		newHeader = Utils.replaceByteAry(newHeader, 4, Utils.int2Byte(ElfType32.SHT_PROGBITS));//type=PROGBITS
		newHeader = Utils.replaceByteAry(newHeader, 8, Utils.int2Byte(ElfType32.SHF_ALLOC));
		newHeader = Utils.replaceByteAry(newHeader, 12, Utils.int2Byte(0x5010));
		newHeader = Utils.replaceByteAry(newHeader, 16, Utils.int2Byte(0x5010));
		newHeader = Utils.replaceByteAry(newHeader, 20, Utils.int2Byte(newSectionSize));
		newHeader = Utils.replaceByteAry(newHeader, 24, Utils.int2Byte(0));
		newHeader = Utils.replaceByteAry(newHeader, 28, Utils.int2Byte(0));
		newHeader = Utils.replaceByteAry(newHeader, 32, Utils.int2Byte(4));
		newHeader = Utils.replaceByteAry(newHeader, 36, Utils.int2Byte(0));
		
		//��ĩβ����Section
		byte[] newSrc = new byte[src.length + newHeader.length];
		newSrc = Utils.replaceByteAry(newSrc, 0, src);
		newSrc = Utils.replaceByteAry(newSrc, src.length, newHeader);
		
		return newSrc;
	}
	
	/**
	 * �޸�.strtab�εĳ���
	 */
	public static byte[] changeStrtabLen(byte[] src){
		
		//��ȡ��String��size�ֶεĿ�ʼλ��
		int size_index = sectionHeaderOffset + (stringSectionInSectionTableIndex)*sectionSize + stringSectionSizeIndex;
		
		//����һ��Section Header + ����һ��Section��name��16���ֽ�
		byte[] newLen_ary = Utils.int2Byte(addSectionStartAddr - stringSectionOffset + newSectionNameLen);
		src = Utils.replaceByteAry(src, size_index, newLen_ary);
		return src;
	}
	
	/**
	 * ���ļ�ĩβ��ӿհ׶�+���Ӷ���String
	 * @param src
	 * @return
	 */
	public static byte[] addNewSectionForFileEnd(byte[] src){
		byte[] stringByte = newSectionName.getBytes();
		byte[] newSection = new byte[newSectionSize + newSectionNameLen];
		newSection = Utils.replaceByteAry(newSection, 0, stringByte);
		//�½�һ��byte[]
		byte[] newSrc = new byte[0x5000 + newSection.length];
		newSrc = Utils.replaceByteAry(newSrc, 0, src);//����֮ǰ���ļ�src
		newSrc = Utils.replaceByteAry(newSrc, addSectionStartAddr, newSection);//����section
		return newSrc;
	}
	
	/**
	 * �޸�Program Header�е���Ϣ
	 * �������Ķ����ݼ��뵽LOAD Segement��
	 * �����޸ĵ�һ��LOAD���͵�Segement��filesize��memsizeΪ�ļ����ܳ���
	 */
	public static byte[] changeProgramHeaderLoadInfo(byte[] src){
		//Ѱ�ҵ�LOAD���͵�Segementλ��
		int offset = elfHeaderSize + programHeaderSize * firstLoadInPHIndex + programFileSizeIndex;
		//file size�ֶ�
		byte[] fileSize = Utils.int2Byte(src.length);
		src = Utils.replaceByteAry(src, offset, fileSize);
		//mem size�ֶ�
		offset = offset + 4;
		byte[] memSize = Utils.int2Byte(src.length);
		src = Utils.replaceByteAry(src, offset, memSize);
		//flag�ֶ�
		offset = offset + 4;
		byte[] flag = Utils.int2Byte(7);
		src = Utils.replaceByteAry(src, offset, flag);
		return src;
	}
	
}
