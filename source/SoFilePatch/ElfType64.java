package com.happy;

import java.util.ArrayList;

public class ElfType64{
	/**
	 *  64-bit ELF base types.
		typedef uint64_t Elf64_Addr;
		typedef uint16_t Elf64_Half;
		typedef int16_t	 Elf64_SHalf;
		typedef uint64_t Elf64_Off;
		typedef int32_t	 Elf64_Sword;
		typedef uint32_t Elf64_Word;
		typedef uint64_t Elf64_Xword;
		typedef int64_t  Elf64_Sxword;
	 */
	
	public static final int Elf64_Addr = 8;
	public static final int Elf64_Half = 2;
	public static final int Elf64_SHalf = 2;
	public static final int Elf64_Off = 8;
	public static final int Elf64_Sword = 4;
	public static final int Elf64_Word = 4;
	public static final int Elf64_Xword = 8;
	public static final int Elf64_Sxword = 8;
	public static final int EI_NIDENT = 16;
	
	public elf64_rel rel;
	public elf64_rela rela;
	public elf64_hdr hdr;
	public ArrayList<elf64_sym> symList = new ArrayList<elf64_sym>();
	public ArrayList<elf64_phdr> phdrList = new ArrayList<elf64_phdr>();//���ܻ��ж������ͷ
	public ArrayList<elf64_shdr> shdrList = new ArrayList<elf64_shdr>();//���ܻ��ж����ͷ
	public ArrayList<elf64_strtb> strtbList = new ArrayList<elf64_strtb>();//���ܻ��ж���ַ���ֵ
	
	public ElfType64() {
		rel = new elf64_rel();
		rela = new elf64_rela();
		hdr = new elf64_hdr();
	}
	
	
	/**
	 *  typedef struct elf64_rel {
		  Elf64_Addr r_offset;	// Location at which to apply the action 
		  Elf64_Xword r_info;	// index and type of relocation
		} Elf64_Rel;
	 */
	public class elf64_rel{
		public byte[] r_offset = new byte[8];
		public byte[] r_info = new byte[8];
		@Override
		public String toString(){
			return "r_offset:"+Utils.bytes2HexString(r_offset)+";r_info:"+Utils.bytes2HexString(r_info);
		}
	}
	
	/**
	 *  typedef struct elf64_rela {
		  Elf64_Addr r_offset;	// Location at which to apply the action
		  Elf64_Xword r_info;	// index and type of relocation
		  Elf64_Sxword r_addend;	// Constant addend used to compute value
		} Elf64_Rela;
	 */
	public class elf64_rela{
		public byte[] r_offset = new byte[Elf64_Addr];
		public byte[] r_info = new byte[Elf64_Xword];
		public byte[] r_addend = new byte[Elf64_Sxword];
		@Override
		public String toString(){
			return "r_offset:"+Utils.bytes2HexString(r_offset)+";r_info:"+Utils.bytes2HexString(r_info)+";r_addend:"+Utils.bytes2HexString(r_info);
		}
	}
	
	/**
	 *  typedef struct elf64_sym {
		  Elf64_Word st_name;	// Symbol name, index in string tbl
		  Elf64_Addr st_value;	// Value of the symbol
		  Elf64_Xword st_size;	// Associated symbol size
		  unsigned char	st_info;	// Type and binding attributes
		  unsigned char	st_other;	// No defined meaning, 0
		  Elf64_Half st_shndx;	// Associated section index
		
		} Elf64_Sym;
	 */
	public static class elf64_sym{
		public byte[] st_name = new byte[Elf64_Word];
		public byte[] st_value = new byte[Elf64_Addr];
		public byte[] st_size = new byte[Elf64_Xword];
		public byte st_info;
		public byte st_other;
		public byte[] st_shndx = new byte[Elf64_Half];
		
	
		
		@Override
		public String toString(){
			return "st_name:"+Utils.bytes2HexString(st_name)
					+"\nst_value:"+Utils.bytes2HexString(st_value)
					+"\nst_size:"+Utils.bytes2HexString(st_size)
					+"\nst_info:"+(st_info/16)
					+"\nst_other:"+(((short)st_other) & 0xF)
					+"\nst_shndx:"+Utils.bytes2HexString(st_shndx);
		}
	}
	
	public void printSymList(){
		for(int i=0;i<symList.size();i++){
			System.out.println();
			System.out.println("The "+(i+1)+" Symbol Table:");
			System.out.println(symList.get(i).toString());
		}
	}
	
	//Bind�ֶ�==��st_info
	public static final int STB_LOCAL = 0;
	public static final int STB_GLOBAL = 1;
	public static final int STB_WEAK = 2;
	//Type�ֶ�==��st_other
	public static final int STT_NOTYPE = 0;
	public static final int STT_OBJECT = 1;
	public static final int STT_FUNC = 2;
	public static final int STT_SECTION = 3;
	public static final int STT_FILE = 4;
	/**
	 * ������Ҫע����ǻ���Ҫ��һ��ת��
	 *  #define ELF_ST_BIND(x)	((x) >> 4)
		#define ELF_ST_TYPE(x)	(((unsigned int) x) & 0xf)
	 */
	
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
	public class elf64_hdr{
		public byte[] e_ident = new byte[EI_NIDENT];
		public byte[] e_type = new byte[Elf64_Half];
		public byte[] e_machine = new byte[Elf64_Half];
		public byte[] e_version = new byte[Elf64_Word];
		public byte[] e_entry = new byte[Elf64_Addr];
		public byte[] e_phoff = new byte[Elf64_Off];
		public byte[] e_shoff = new byte[Elf64_Off];
		public byte[] e_flags = new byte[Elf64_Word];
		public byte[] e_ehsize = new byte[Elf64_Half];
		public byte[] e_phentsize = new byte[Elf64_Half];
		public byte[] e_phnum = new byte[Elf64_Half];
		public byte[] e_shentsize = new byte[Elf64_Half];
		public byte[] e_shnum = new byte[Elf64_Half];
		public byte[] e_shstrndx = new byte[Elf64_Half];
		
		@Override
		public String toString(){
			return  "magic:"+ Utils.bytes2HexString(e_ident) 
					+"\ne_type:"+Utils.bytes2HexString(e_type)
					+"\ne_machine:"+Utils.bytes2HexString(e_machine)
					+"\ne_version:"+Utils.bytes2HexString(e_version)
					+"\ne_entry:"+Utils.bytes2HexString(e_entry)
					+"\ne_phoff:"+Utils.bytes2HexString(e_phoff)
					+"\ne_shoff:"+Utils.bytes2HexString(e_shoff)
					+"\ne_flags:"+Utils.bytes2HexString(e_flags)
					+"\ne_ehsize:"+Utils.bytes2HexString(e_ehsize)
					+"\ne_phentsize:"+Utils.bytes2HexString(e_phentsize)
					+"\ne_phnum:"+Utils.bytes2HexString(e_phnum)
					+"\ne_shentsize:"+Utils.bytes2HexString(e_shentsize)
					+"\ne_shnum:"+Utils.bytes2HexString(e_shnum)
					+"\ne_shstrndx:"+Utils.bytes2HexString(e_shstrndx);
		}
	}
	
	public void printPhdrList(){
		for(int i=0;i<phdrList.size();i++){
			System.out.println();
			System.out.println("The "+(i+1)+" Program Header:");
			System.out.println(phdrList.get(i).toString());
		}
	}
	
	/**
	 *  typedef struct elf64_phdr {
		  Elf64_Word p_type;
		  Elf64_Word p_flags;
		  Elf64_Off p_offset;	// Segment file offset 
		  Elf64_Addr p_vaddr;	// Segment virtual address 
		  Elf64_Addr p_paddr;	// Segment physical address
		  Elf64_Xword p_filesz;	// Segment size in file 
		  Elf64_Xword p_memsz;	// Segment size in memory
		  Elf64_Xword p_align;	// Segment alignment, file & memory
		} Elf64_Phdr;
	 */
	public static class elf64_phdr{
		public byte[] p_type = new byte[Elf64_Word];
		public byte[] p_offset = new byte[Elf64_Off];
		public byte[] p_vaddr = new byte[Elf64_Addr];
		public byte[] p_paddr = new byte[Elf64_Addr];	
		public byte[] p_filesz = new byte[Elf64_Xword];
		public byte[] p_memsz = new byte[Elf64_Xword];
		public byte[] p_flags = new byte[Elf64_Word];
		public byte[] p_align = new byte[Elf64_Xword];
		
		@Override
		public String toString(){
			return "p_type:"+ Utils.bytes2HexString(p_type)
					+"\np_offset:"+Utils.bytes2HexString(p_offset)
					+"\np_vaddr:"+Utils.bytes2HexString(p_vaddr)
					+"\np_paddr:"+Utils.bytes2HexString(p_paddr)
					+"\np_filesz:"+Utils.bytes2HexString(p_filesz)
					+"\np_memsz:"+Utils.bytes2HexString(p_memsz)
					+"\np_flags:"+Utils.bytes2HexString(p_flags)
					+"\np_align:"+Utils.bytes2HexString(p_align);
		}
	}
	
	
	/**
	 * typedef struct elf64_shdr {
		  Elf64_Word sh_name;	// Section name, index in string tbl 
		  Elf64_Word sh_type;	// Type of section 
		  Elf64_Xword sh_flags;	// Miscellaneous section attributes 
		  Elf64_Addr sh_addr;	// Section virtual addr at execution 
		  Elf64_Off sh_offset;	// Section file offset 
		  Elf64_Xword sh_size;	// Size of section in bytes 
		  Elf64_Word sh_link;	// Index of another section 
		  Elf64_Word sh_info;	// Additional section information 
		  Elf64_Xword sh_addralign;	// Section alignment 
		  Elf64_Xword sh_entsize;	// Entry size if section holds table 
		} Elf64_Shdr;
	 */
	public static class elf64_shdr{
		public byte[] sh_name = new byte[Elf64_Word];
		public byte[] sh_type = new byte[Elf64_Word];
		public byte[] sh_flags = new byte[Elf64_Xword];
		public byte[] sh_addr = new byte[Elf64_Addr];
		public byte[] sh_offset = new byte[Elf64_Off];
		public byte[] sh_size = new byte[Elf64_Xword];
		public byte[] sh_link = new byte[Elf64_Word];
		public byte[] sh_info = new byte[Elf64_Word];
		public byte[] sh_addralign = new byte[Elf64_Xword];
		public byte[] sh_entsize = new byte[Elf64_Xword];
		
		@Override
		public String toString(){
			return "sh_name:"+Utils.bytes2HexString(sh_name)/*Utils.byte2Int(sh_name)*/
					+"\nsh_type:"+Utils.bytes2HexString(sh_type)
					+"\nsh_flags:"+Utils.bytes2HexString(sh_flags)
					+"\nsh_add:"+Utils.bytes2HexString(sh_addr)
					+"\nsh_offset:"+Utils.bytes2HexString(sh_offset)
					+"\nsh_size:"+Utils.bytes2HexString(sh_size)
					+"\nsh_link:"+Utils.bytes2HexString(sh_link)
					+"\nsh_info:"+Utils.bytes2HexString(sh_info)
					+"\nsh_addralign:"+Utils.bytes2HexString(sh_addralign)
					+"\nsh_entsize:"+ Utils.bytes2HexString(sh_entsize);
		}
	}
	
	/****************sh_type********************/
	public static final int SHT_NULL = 0;
	public static final int SHT_PROGBITS = 1;
	public static final int SHT_SYMTAB = 2;
	public static final int SHT_STRTAB = 3;
	public static final int SHT_RELA = 4;
	public static final int SHT_HASH = 5;
	public static final int SHT_DYNAMIC = 6;
	public static final int SHT_NOTE = 7;
	public static final int SHT_NOBITS = 8;
	public static final int SHT_REL = 9;
	public static final int SHT_SHLIB = 10;
	public static final int SHT_DYNSYM = 11;
	public static final int SHT_NUM = 12;
	public static final int SHT_LOPROC = 0x70000000;
	public static final int SHT_HIPROC = 0x7fffffff;
	public static final int SHT_LOUSER = 0x80000000;
	public static final int SHT_HIUSER = 0xffffffff;
	public static final int SHT_MIPS_LIST = 0x70000000;
	public static final int SHT_MIPS_CONFLICT = 0x70000002;
	public static final int SHT_MIPS_GPTAB = 0x70000003;
	public static final int SHT_MIPS_UCODE = 0x70000004;
	
	/*****************sh_flag***********************/
	public static final int SHF_WRITE = 0x1;
	public static final int SHF_ALLOC = 0x2;
	public static final int SHF_EXECINSTR = 0x4;
	public static final int SHF_MASKPROC = 0xf0000000;
	public static final int SHF_MIPS_GPREL = 0x10000000;
	
	public void printShdrList(){
		for(int i=0;i<shdrList.size();i++){
			System.out.println();
			System.out.println("The "+(i+1)+" Section Header:");
			System.out.println(shdrList.get(i));
		}
	}
	
	public static class elf64_strtb{
		public byte[] str_name;
		public int len;
		
		@Override
		public String toString(){
			return "str_name:"+str_name
					+"len:"+len;
		}
	}

}
