# This script is responsible for taking a statically-linked binary and
# populating the functions table inside it.
#
# This is done with a python library responsible for parsing the DWARF format;
# see elftools/ for details.
#
# Ryan Pearl <rpearl@andrew.cmu.edu>
# Andrew Bresticker <abrestic@andrew.cmu.edu>

import sys
import io
import struct

from collections import namedtuple

# If elftools is not installed, maybe we're running from the root or examples
# dir of the source distribution
try:
    import elftools
except ImportError:
    sys.path.extend(['.', '..'])

from elftools.elf.elffile import ELFFile
from elftools.elf.sections import SymbolTableSection
from elftools.dwarf.dwarf_expr import (DW_OP_name2opcode, DW_OP_opcode2name)
from elftools.dwarf.locationlists import LocationEntry
from elftools.dwarf.descriptions import describe_DWARF_expr

symtab = dict()
types = dict()

Sym = namedtuple('Sym', ['offset', 'size', 'args'])
Arg = namedtuple('Arg', ['typ', 'name', 'slot'])
Typ = namedtuple('Typ', ['name', 'size'])

FTABLE = 'functions'

FUNCTS_MAX_NAME = '60'
FUNCTS_MAX_NUM = 4096
ARGS_MAX_NAME = '24'
ARGS_MAX_NUM = 6

header_struct = 'i' + (FUNCTS_MAX_NAME+'s')
arg_struct = 'ii' + (ARGS_MAX_NAME+'s')

type_enum = {
    'char': 0,
    'int': 1,
    'float': 2,
    'double': 3,
    'char*': 4,
    'char**' : 5,
    'void*' : 6
}

def write_func(f, name, func):
    f.write(struct.pack(header_struct, func.offset, name))
    for i in xrange(0, ARGS_MAX_NUM):
        if i < len(func.args):
            arg = func.args[i]
        else:
            arg = Arg('', '', 0)
        typ = type_enum[arg.typ] if arg.typ in type_enum else -1
        f.write(struct.pack(arg_struct, typ, arg.slot, arg.name))

def get_symtab(elf):
    section = elf.get_section_by_name('.symtab')
    symtab = dict()

    if isinstance(section, SymbolTableSection):
        for symbol in section.iter_symbols():
            if symbol['st_info']['type'] == 'STT_FUNC':
                symtab[symbol.name] = Sym(symbol['st_value'],
                                          symbol['st_size'],
                                          list())
            elif symbol.name == FTABLE:
                ftable_addr = symbol['st_value']
    return symtab, ftable_addr

def find_rodata(elf):
    section = elf.get_section_by_name('.rodata')
    assert section
    return section['sh_addr'], section['sh_offset']

def process_file(filename):
    f = open(filename, 'r+b')

    elffile = ELFFile(f)
    symtab, ftable_addr = get_symtab(elffile)

    if symtab is None:
        print "Cannot find symbol table. Compiled without debug symbols?"
        sys.exit(1)

    if ftable_addr is None:
        print "The provided file does not contain symbol `%s'" % FTABLE
        print "Please ensure there is a reference to `%s' in traceback.c" % FTABLE
        sys.exit(1)

    rodata_addr, rodata_off = find_rodata(elffile)

    # get_dwarf_info returns a DWARFInfo context object, which is the
    # starting point for all DWARF-based processing in pyelftools.
    dwarfinfo = elffile.get_dwarf_info()

    typemap = dict()

    process_types(dwarfinfo, typemap)
    process_funcs(dwarfinfo, symtab, typemap)

    i = 0
    f.seek(ftable_addr - rodata_addr + rodata_off)
    for func in sorted(symtab, key=lambda x : symtab[x].offset):
        if len(func) == 0:
            continue
        if i > FUNCTS_MAX_NUM:
            break
        write_func(f, func, symtab[func])
        i += 1
    f.close()

def get_name(die):
    if 'DW_AT_name' in die.attributes:
        return die.attributes['DW_AT_name'].value
    else:
        return 'UNKNOWN'

BASE_TYPES = [
    'DW_TAG_base_type',
    'DW_TAG_structure_type',
    'DW_TAG_union_type',
]

POINTER_TYPES = {
    'DW_TAG_pointer_type' : '*',
}

INDIRECT_TYPES = [
    'DW_TAG_typedef',
    'DW_TAG_const_type',
    'DW_TAG_volatile_type',
    'DW_TAG_restrict_type',
]

def get_type(typemap, die):
    k = die.cu.cu_offset + die.attributes['DW_AT_type'].value
    return typemap[k]

def get_size(die):
    if 'DW_AT_byte_size' in die.attributes:
        return die.attributes['DW_AT_byte_size'].value
    else:
        return -1 

def process_types(dwarf, typemap):
    def resolve_direct(die):
        if die.tag in BASE_TYPES:
            name = get_name(die)
            size = get_size(die)
            assert(size > 0)
            typemap[die.offset] = Typ(name = name, size = size)
    def resolve_pointers(die):
        if die.tag in POINTER_TYPES:
            if 'DW_AT_type' in die.attributes:
                offset = die.attributes['DW_AT_type'].value + die.cu.cu_offset
                indirect = POINTER_TYPES[die.tag]
                name = (typemap[offset].name if offset in typemap \
                            else 'UNKNOWN') + indirect
            else:
                name = 'void*'
            typemap[die.offset] = Typ(name = name, size = 4)
    def resolve_indirect(die):
        if die.tag in INDIRECT_TYPES:
            if 'DW_AT_type' in die.attributes:
                offset = die.attributes['DW_AT_type'].value + die.cu.cu_offset
                if offset in typemap:
                    size = typemap[offset].size
                    name = typemap[offset].name
                else:
                    # Assume size is 4 if we can't derive the base type
                    size = 4
                    name = 'UNKNOWN'
                typemap[die.offset] = Typ(name = name, size = size)

    map_dwarf(dwarf, resolve_direct)
    map_dwarf(dwarf, resolve_pointers)
    map_dwarf(dwarf, resolve_indirect)

# The 'frame base' is an offset from EBP.  This is the default value during
# the body of a funciton.
FRAME_BASE_OFFSET = 8

def process_funcs(dwarf, symtab, typemap):
    def process_func(die):
        if die.tag != 'DW_TAG_subprogram':
            return
        f = get_name(die)
        entry = symtab[f]
        i = FRAME_BASE_OFFSET
        for child in die.iter_children():
            if child.tag == 'DW_TAG_formal_parameter':
                typ = get_type(typemap, child)
                # XXX: We should be using DWARF's location attributes to
                # find the argument slot, but we can't always resolve the
                # location entry to an EBP offset.
                symtab[f].args.append(Arg(typ = typ.name, 
                                          name = get_name(child),
                                          slot = i))
                if typ.size < 4:
                    i += 4
                else:
                    i += typ.size

    map_dwarf(dwarf, process_func)

def map_dies(die, fn):
    fn(die)
    for child in die.iter_children():
        map_dies(child, fn)

def map_dwarf(dwarf, fn):
    for CU in dwarf.iter_CUs():
        top = CU.get_top_DIE()
        map_dies(top, fn)

if __name__ == '__main__':
    for filename in sys.argv[1:]:
        process_file(filename)
