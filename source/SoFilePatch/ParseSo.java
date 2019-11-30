package com.happy;

import com.happy.ElfType32.*;
import com.happy.ElfType64.*;
import com.happy.Test;

public class ParseSo {

    /**
     *
     * @param fileByteArys
     */
    protected static void parseSo32(byte[] fileByteArys) {

        //��ȡͷ������
        System.out.println("+++++++++++++++++++Elf Header+++++++++++++++++");
        parseHeader32(fileByteArys, 0);
//		System.out.println("header:\n"+Test.type_32.hdr);

        //��ȡ����ͷ��Ϣ
        System.out.println();
        System.out.println("+++++++++++++++++++Program Header+++++++++++++++++");
        int p_header_offset = Utils.byte2Int(Test.type_32.hdr.e_phoff);
//		System.out.println("offset:"+p_header_offset);
        parseProgramHeaderList32(fileByteArys, p_header_offset);
//		Test.type_32.printPhdrList();

        //��ȡ��ͷ��Ϣ
        System.out.println();
        System.out.println("+++++++++++++++++++Section Header++++++++++++++++++");
        int s_header_offset = Utils.byte2Int(Test.type_32.hdr.e_shoff);
//		System.out.println("offset:"+s_header_offset);
        parseSectionHeaderList32(fileByteArys, s_header_offset);
//		Test.type_32.printShdrList();

		/*//��ȡ���ű���Ϣ(Symbol Table)
		System.out.println();
		System.out.println("+++++++++++++++++++Symbol Table++++++++++++++++++");
		int offset_sym = 0;
		int total_sym = 0;
		for(elf32_shdr shdr : type_32.shdrList){
			if(Utils.byte2Int(shdr.sh_type) == ElfType32.SHT_DYNSYM){
				total_sym = Utils.byte2Int(shdr.sh_size);
				offset_sym = Utils.byte2Int(shdr.sh_offset);
				break;
			}
		}
		int num_sym = total_sym / 16;
		System.out.println("sym num="+num_sym);
		parseSymbolTableList(fileByteArys, num_sym, offset_sym);
		type_32.printSymList();

		//��ȡ�ַ�������Ϣ(String Table)
		System.out.println();
		System.out.println("+++++++++++++++++++Symbol Table++++++++++++++++++");
		int prename_len = 0;
		int[] lens = new int[type_32.shdrList.size()];
		int total = 0;
		for(int i=0;i<type_32.shdrList.size();i++){
			if(Utils.byte2Int(type_32.shdrList.get(i).sh_type) == ElfType32.SHT_STRTAB){
				int curname_offset = Utils.byte2Int(type_32.shdrList.get(i).sh_name);
				lens[i] = curname_offset - prename_len - 1;
				if(lens[i] < 0){
					lens[i] = 0;
				}
				total += lens[i];
				System.out.println("total:"+total);
				prename_len = curname_offset;
				if(i == (lens.length - 1)){
					System.out.println("size:"+Utils.byte2Int(type_32.shdrList.get(i).sh_size));
					lens[i] = Utils.byte2Int(type_32.shdrList.get(i).sh_size) - total - 1;
				}
			}
		}
		for(int i=0;i<lens.length;i++){
			System.out.println("len:"+lens[i]);
		}
       */
    }

    /**
     *
     * @param header
     */
    protected static void parseHeader32(byte[] header, int offset) {
        if (header == null) {
            System.out.println("header is null");
            return;
        }
        /**
         *  public byte[] e_ident = new byte[16];
         public short e_type;
         public short e_machine;
         public int e_version;
         public int e_entry;
         public int e_phoff;
         public int e_shoff;
         public int e_flags;
         public short e_ehsize;
         public short e_phentsize;
         public short e_phnum;
         public short e_shentsize;
         public short e_shnum;
         public short e_shstrndx;
         */
        Test.type_32.hdr.e_ident = Utils.copyBytes(header, 0, 16);//ħ��
        Test.type_32.hdr.e_type = Utils.copyBytes(header, 16, 2);
        Test.type_32.hdr.e_machine = Utils.copyBytes(header, 18, 2);
        Test.type_32.hdr.e_version = Utils.copyBytes(header, 20, 4);
        Test.type_32.hdr.e_entry = Utils.copyBytes(header, 24, 4);
        Test.type_32.hdr.e_phoff = Utils.copyBytes(header, 28, 4);
        Test.type_32.hdr.e_shoff = Utils.copyBytes(header, 32, 4);
        Test.type_32.hdr.e_flags = Utils.copyBytes(header, 36, 4);
        Test.type_32.hdr.e_ehsize = Utils.copyBytes(header, 40, 2);
        Test.type_32.hdr.e_phentsize = Utils.copyBytes(header, 42, 2);
        Test.type_32.hdr.e_phnum = Utils.copyBytes(header, 44, 2);
        Test.type_32.hdr.e_shentsize = Utils.copyBytes(header, 46, 2);
        Test.type_32.hdr.e_shnum = Utils.copyBytes(header, 48, 2);
        Test.type_32.hdr.e_shstrndx = Utils.copyBytes(header, 50, 2);
    }

    /**
     * ��������ͷ��Ϣ
     *
     * @param header
     */
    public static void parseProgramHeaderList32(byte[] header, int offset) {
        int header_size = 32;//32���ֽ�
        int header_count = Utils.byte2Short(Test.type_32.hdr.e_phnum);
        byte[] des = new byte[header_size];
        Test.type_32.phdrList.clear();
        for (int i = 0; i < header_count; i++) {
            System.arraycopy(header, i * header_size + offset, des, 0, header_size);
            Test.type_32.phdrList.add(parseProgramHeader32(des));
        }
    }

    protected static ElfType32.elf32_phdr parseProgramHeader32(byte[] header) {
        /**
         *  public int p_type;
         public int p_offset;
         public int p_vaddr;
         public int p_paddr;
         public int p_filesz;
         public int p_memsz;
         public int p_flags;
         public int p_align;
         */
        ElfType32.elf32_phdr phdr = new ElfType32.elf32_phdr();
        phdr.p_type = Utils.copyBytes(header, 0, 4);
        phdr.p_offset = Utils.copyBytes(header, 4, 4);
        phdr.p_vaddr = Utils.copyBytes(header, 8, 4);
        phdr.p_paddr = Utils.copyBytes(header, 12, 4);
        phdr.p_filesz = Utils.copyBytes(header, 16, 4);
        phdr.p_memsz = Utils.copyBytes(header, 20, 4);
        phdr.p_flags = Utils.copyBytes(header, 24, 4);
        phdr.p_align = Utils.copyBytes(header, 28, 4);
        return phdr;

    }

    /**
     * ������ͷ��Ϣ����
     */
    public static void parseSectionHeaderList32(byte[] header, int offset) {
        int header_size = 40;//40���ֽ�
        int header_count = Utils.byte2Short(Test.type_32.hdr.e_shnum);
        byte[] des = new byte[header_size];
        Test.type_32.shdrList.clear();
        for (int i = 0; i < header_count; i++) {
            System.arraycopy(header, i * header_size + offset, des, 0, header_size);
            Test.type_32.shdrList.add(parseSectionHeader32(des));
        }
    }

    protected static ElfType32.elf32_shdr parseSectionHeader32(byte[] header) {
        ElfType32.elf32_shdr shdr = new ElfType32.elf32_shdr();
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
        shdr.sh_name = Utils.copyBytes(header, 0, 4);
        shdr.sh_type = Utils.copyBytes(header, 4, 4);
        shdr.sh_flags = Utils.copyBytes(header, 8, 4);
        shdr.sh_addr = Utils.copyBytes(header, 12, 4);
        shdr.sh_offset = Utils.copyBytes(header, 16, 4);
        shdr.sh_size = Utils.copyBytes(header, 20, 4);
        shdr.sh_link = Utils.copyBytes(header, 24, 4);
        shdr.sh_info = Utils.copyBytes(header, 28, 4);
        shdr.sh_addralign = Utils.copyBytes(header, 32, 4);
        shdr.sh_entsize = Utils.copyBytes(header, 36, 4);
        return shdr;
    }

    /**
     * Symbol Table
     */
    public static void parseSymbolTableList32(byte[] header, int header_count, int offset) {
        int header_size = 16;//16���ֽ�
        byte[] des = new byte[header_size];
        Test.type_32.symList.clear();
        for (int i = 0; i < header_count; i++) {
            System.arraycopy(header, i * header_size + offset, des, 0, header_size);
            Test.type_32.symList.add(parseSymbolTable32(des));
        }
    }

    protected static ElfType32.elf32_sym parseSymbolTable32(byte[] header) {
        /**
         *  public byte[] st_name = new byte[4];
         public byte[] st_value = new byte[4];
         public byte[] st_size = new byte[4];
         public byte st_info;
         public byte st_other;
         public byte[] st_shndx = new byte[2];
         */
        ElfType32.elf32_sym sym = new ElfType32.elf32_sym();
        sym.st_name = Utils.copyBytes(header, 0, 4);
        sym.st_value = Utils.copyBytes(header, 4, 4);
        sym.st_size = Utils.copyBytes(header, 8, 4);
        sym.st_info = header[12];
        // FIXME
        sym.st_other = header[13];
        sym.st_shndx = Utils.copyBytes(header, 14, 2);
        return sym;
    }


    /**
     * 64 so
     */
    protected static void parseSo64(byte[] fileByteArys) {

        //��ȡͷ������
        System.out.println("+++++++++++++++++++Elf Header+++++++++++++++++");
        parseHeader64(fileByteArys, 0);
//		System.out.println("header:\n"+Test.type_64.hdr);

        //��ȡ����ͷ��Ϣ
        System.out.println();
        System.out.println("+++++++++++++++++++Program Header+++++++++++++++++");
        int p_header_offset = Utils.byte2Int(Test.type_64.hdr.e_phoff);
//		System.out.println("offset:"+p_header_offset);
        parseProgramHeaderList64(fileByteArys, p_header_offset);
//		Test.type_64.printPhdrList();

        //��ȡ��ͷ��Ϣ
        System.out.println();
        System.out.println("+++++++++++++++++++Section Header++++++++++++++++++");
        int s_header_offset = Utils.byte2Int(Test.type_64.hdr.e_shoff);
//		System.out.println("offset:"+s_header_offset);
        parseSectionHeaderList64(fileByteArys, s_header_offset);
//		Test.type_64.printShdrList();

		/*//��ȡ���ű���Ϣ(Symbol Table)
		System.out.println();
		System.out.println("+++++++++++++++++++Symbol Table++++++++++++++++++");
		//������Ҫע����ǣ���Elf����û���ҵ�SymbolTable����Ŀ������������ϸ�۲�Section�е�Type=DYNSYM�ε���Ϣ���Եõ�������εĴ�С��ƫ�Ƶ�ַ����SymbolTable�Ľṹ��С�ǹ̶���16���ֽ�
		//��ô�������Ŀ=��С/�ṹ��С
		//������SectionHeader�в��ҵ�dynsym�ε���Ϣ
		int offset_sym = 0;
		int total_sym = 0;
		for(elf32_shdr shdr : type_32.shdrList){
			if(Utils.byte2Int(shdr.sh_type) == ElfType32.SHT_DYNSYM){
				total_sym = Utils.byte2Int(shdr.sh_size);
				offset_sym = Utils.byte2Int(shdr.sh_offset);
				break;
			}
		}
		int num_sym = total_sym / 16;
		System.out.println("sym num="+num_sym);
		parseSymbolTableList(fileByteArys, num_sym, offset_sym);
		type_32.printSymList();

		//��ȡ�ַ�������Ϣ(String Table)
		System.out.println();
		System.out.println("+++++++++++++++++++Symbol Table++++++++++++++++++");
		//������Ҫע����ǣ���Elf����û���ҵ�StringTable����Ŀ������������ϸ�۲�Section�е�Type=STRTAB�ε���Ϣ�����Եõ�������εĴ�С��ƫ�Ƶ�ַ������������ʱ�����ǲ�֪���ַ����Ĵ�С�����Ծͻ�ȡ������Ŀ��
		//�������ǿ��Բ鿴Section�ṹ�е�name�ֶΣ���ʾƫ��ֵ����ô���ǿ���ͨ�����ֵ����ȡ�ַ����Ĵ�С
		//������ô��⣺��ǰ�ε�nameֵ ��ȥ ��һ�ε�name��ֵ = (��һ�ε�name�ַ����ĳ���)
		//���Ȼ�ȡÿ���ε�name���ַ�����С
		int prename_len = 0;
		int[] lens = new int[type_32.shdrList.size()];
		int total = 0;
		for(int i=0;i<type_32.shdrList.size();i++){
			if(Utils.byte2Int(type_32.shdrList.get(i).sh_type) == ElfType32.SHT_STRTAB){
				int curname_offset = Utils.byte2Int(type_32.shdrList.get(i).sh_name);
				lens[i] = curname_offset - prename_len - 1;
				if(lens[i] < 0){
					lens[i] = 0;
				}
				total += lens[i];
				System.out.println("total:"+total);
				prename_len = curname_offset;
				//������Ҫע����ǣ����һ���ַ����ĳ��ȣ���Ҫ���ܳ��ȼ�ȥǰ��ĳ����ܺ�����ȡ��
				if(i == (lens.length - 1)){
					System.out.println("size:"+Utils.byte2Int(type_32.shdrList.get(i).sh_size));
					lens[i] = Utils.byte2Int(type_32.shdrList.get(i).sh_size) - total - 1;
				}
			}
		}
		for(int i=0;i<lens.length;i++){
			System.out.println("len:"+lens[i]);
		}
		//������Ǹ��������ã����Ƿ���StringTable�е�ÿ���ַ�������������һ��00(��˵�е��ַ���������)����ô����ֻҪ֪��StringTable�Ŀ�ʼλ�ã�Ȼ��Ϳ��Զ�ȡ��ÿ���ַ�����ֵ��
       */
    }

    /**
     * ����Elf��ͷ����Ϣ
     *
     * @param header
     */
    protected static void parseHeader64(byte[] header, int offset) {
        if (header == null) {
            System.out.println("header is null");
            return;
        }
        /**
         * typedef struct elf64_hdr {
         unsigned char	e_ident[EI_NIDENT];	// ELF "magic number"
         Elf64_Half e_type;
         Elf64_Half e_machine;
         Elf64_Word e_version;
         Elf64_Addr e_entry;	// Entry point virtual address
         Elf64_Off e_phoff;	// Program header table file offset
         Elf64_Off e_shoff;	// Section header table file offset
         Elf64_Word e_flags;
         Elf64_Half e_ehsize;
         Elf64_Half e_phentsize;
         Elf64_Half e_phnum;
         Elf64_Half e_shentsize;
         Elf64_Half e_shnum;
         Elf64_Half e_shstrndx;
         } Elf64_Ehdr;
         */
        Test.type_64.hdr.e_ident = Utils.copyBytes(header, 0, 16);//ħ��
        Test.type_64.hdr.e_type = Utils.copyBytes(header, 16, 2);
        Test.type_64.hdr.e_machine = Utils.copyBytes(header, 18, 2);
        Test.type_64.hdr.e_version = Utils.copyBytes(header, 20, 4);
        Test.type_64.hdr.e_entry = Utils.copyBytes(header, 24, 8);
        Test.type_64.hdr.e_phoff = Utils.copyBytes(header, 32, 8);
        Test.type_64.hdr.e_shoff = Utils.copyBytes(header, 40, 8);
        Test.type_64.hdr.e_flags = Utils.copyBytes(header, 48, 4);
        Test.type_64.hdr.e_ehsize = Utils.copyBytes(header, 52, 2);
        Test.type_64.hdr.e_phentsize = Utils.copyBytes(header, 54, 2);
        Test.type_64.hdr.e_phnum = Utils.copyBytes(header, 56, 2);
        Test.type_64.hdr.e_shentsize = Utils.copyBytes(header, 58, 2);
        Test.type_64.hdr.e_shnum = Utils.copyBytes(header, 60, 2);
        Test.type_64.hdr.e_shstrndx = Utils.copyBytes(header, 62, 2);
    }

    /**
     * ��������ͷ��Ϣ
     *
     * @param header
     */
    public static void parseProgramHeaderList64(byte[] header, int offset) {
        int header_size = 56;//56���ֽ�
        int header_count = Utils.byte2Short(Test.type_64.hdr.e_phnum);//ͷ���ĸ���
        byte[] des = new byte[header_size];
        Test.type_64.phdrList.clear();
        for (int i = 0; i < header_count; i++) {
            System.arraycopy(header, i * header_size + offset, des, 0, header_size);
            Test.type_64.phdrList.add(parseProgramHeader64(des));
        }
    }

    protected static elf64_phdr parseProgramHeader64(byte[] header) {
        /**
         *
         *  public byte[] p_type = new byte[Elf64_Word];
         public byte[] p_offset = new byte[Elf64_Off];
         public byte[] p_vaddr = new byte[Elf64_Addr];
         public byte[] p_paddr = new byte[Elf64_Addr];
         public byte[] p_filesz = new byte[Elf64_Xword];
         public byte[] p_memsz = new byte[Elf64_Xword];
         public byte[] p_flags = new byte[Elf64_Word];
         public byte[] p_align = new byte[Elf64_Xword];
         */
        ElfType64.elf64_phdr phdr = new ElfType64.elf64_phdr();
        phdr.p_type = Utils.copyBytes(header, 0, 4);
        phdr.p_offset = Utils.copyBytes(header, 4, 8);
        phdr.p_vaddr = Utils.copyBytes(header, 12, 8);
        phdr.p_paddr = Utils.copyBytes(header, 20, 8);
        phdr.p_filesz = Utils.copyBytes(header, 28, 8);
        phdr.p_memsz = Utils.copyBytes(header, 36, 8);
        phdr.p_flags = Utils.copyBytes(header, 44, 4);
        phdr.p_align = Utils.copyBytes(header, 48, 8);
        return phdr;

    }

    /**
     * ������ͷ��Ϣ����
     */
    public static void parseSectionHeaderList64(byte[] header, int offset) {
        int header_size = 64;//64���ֽ�
        int header_count = Utils.byte2Short(Test.type_64.hdr.e_shnum);//ͷ���ĸ���
        byte[] des = new byte[header_size];
        Test.type_64.shdrList.clear();
        for (int i = 0; i < header_count; i++) {
            System.arraycopy(header, i * header_size + offset, des, 0, header_size);
            Test.type_64.shdrList.add(parseSectionHeader64(des));
        }
    }

    protected static elf64_shdr parseSectionHeader64(byte[] header) {
        ElfType64.elf64_shdr shdr = new ElfType64.elf64_shdr();
        /**
         *
         *  public byte[] sh_name = new byte[Elf64_Word];
         public byte[] sh_type = new byte[Elf64_Word];
         public byte[] sh_flags = new byte[Elf64_Xword];
         public byte[] sh_addr = new byte[Elf64_Addr];
         public byte[] sh_offset = new byte[Elf64_Off];
         public byte[] sh_size = new byte[Elf64_Xword];
         public byte[] sh_link = new byte[Elf64_Word];
         public byte[] sh_info = new byte[Elf64_Word];
         public byte[] sh_addralign = new byte[Elf64_Xword];
         public byte[] sh_entsize = new byte[Elf64_Xword];
         */
        shdr.sh_name = Utils.copyBytes(header, 0, 4);
        shdr.sh_type = Utils.copyBytes(header, 4, 4);
        shdr.sh_flags = Utils.copyBytes(header, 8, 8);
        shdr.sh_addr = Utils.copyBytes(header, 16, 8);
        shdr.sh_offset = Utils.copyBytes(header, 24, 8);
        shdr.sh_size = Utils.copyBytes(header, 32, 8);
        shdr.sh_link = Utils.copyBytes(header, 40, 4);
        shdr.sh_info = Utils.copyBytes(header, 44, 4);
        shdr.sh_addralign = Utils.copyBytes(header, 48, 8);
        shdr.sh_entsize = Utils.copyBytes(header, 56, 8);
        return shdr;
    }

    /**
     * ����Symbol Table����
     */
    public static void parseSymbolTableList64(byte[] header, int header_count, int offset) {
        int header_size = 24;//24���ֽ�
        byte[] des = new byte[header_size];
        Test.type_64.symList.clear();
        for (int i = 0; i < header_count; i++) {
            System.arraycopy(header, i * header_size + offset, des, 0, header_size);
            Test.type_64.symList.add(parseSymbolTable64(des));
        }
    }

    protected static ElfType64.elf64_sym parseSymbolTable64(byte[] header) {
        /**
         *  public byte[] st_name = new byte[Elf64_Word];
         public byte[] st_value = new byte[Elf64_Addr];
         public byte[] st_size = new byte[Elf64_Xword];
         public byte st_info;
         public byte st_other;
         public byte[] st_shndx = new byte[Elf64_Half];
         */
        elf64_sym sym = new elf64_sym();
        sym.st_name = Utils.copyBytes(header, 0, 4);
        sym.st_value = Utils.copyBytes(header, 4, 8);
        sym.st_size = Utils.copyBytes(header, 12, 8);
        sym.st_info = header[20];
        //FIXME ������һ�����⣬��������ֶζ�������ֵʼ����0
        sym.st_other = header[21];
        sym.st_shndx = Utils.copyBytes(header, 22, 2);
        return sym;
    }

}
