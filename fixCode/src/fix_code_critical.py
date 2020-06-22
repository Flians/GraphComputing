import os
import re
import sys

'''
    Correct errors of type AlipayUnusedImportsRule, UnusedLocalVariable, and 
    UnusedFormalParameter in critical
'''


def obtain_files(in_path):
    with open(in_path, 'r') as fin:
        line = fin.readline()
        map_unused = {}
        file_path = ''
        var_name = ''
        var_name_pattern = re.compile(
            r'^Avoid unused '
            r'(?:local|method|constructor|imports)(?: (variables|parameters))? such as \'([\w.]+)\'\.?$')
        while line:
            line = line.strip()
            if line.endswith('.java'):
                file_path = line
                if file_path not in map_unused:
                    map_unused[file_path] = []
            else:
                match_var = re.match(var_name_pattern, line)
                if match_var:
                    var_name = match_var.group(2)
                    line_index = int(fin.readline().strip())
                    line_var = fin.readline().strip()

                    map_unused[file_path].append([line_index, line_var, var_name])
            line = fin.readline()
    return map_unused


def write_lines(fout, lines):
    for line in lines:
        fout.write(line)
    lines.clear()


def unused_local_variable(file, unused):
    class_name = os.path.basename(file).split('.')[0]
    with open(file, "r", encoding="utf-8") as original, \
        open("%s.bak" % file, "w", encoding="utf-8") as fout:
        i = 0
        line_index = 0
        content = []
        line = ''
        flag = False
        for fline in original:
            line_index += 1
            if (i < len(unused) and line_index == unused[i][0] and
               unused[i][1] in fline and
               '// {}'.format(unused[i][1]) not in fline) or flag:
                index = fline.find(';', 0 if flag else fline.find(unused[i][1]))
                if index == -1:
                    if flag:
                        line += fline.strip()
                    else:
                        line += fline.rstrip()
                    flag = True
                    continue
                else:
                    if flag:
                        line += fline.lstrip()
                        flag = False
                    else:
                        line += fline

                print('>>> Comment', unused[i][1], 'in class', class_name,
                      '[', unused[i][0], '] in file', file)
                index = line.find(unused[i][1])
                ll = list(line)
                ll.insert(index, '// ')
                line = ''.join(ll)
                # line = line[0:index] + line[line.find(';', index) + 1:]
                if len(line.strip()) != 0:
                    content.append(line)
                i += 1
                line = ''
            else:
                content.append(fline)
        write_lines(fout, content)
    os.remove(file)
    os.rename('%s.bak' % file, file)


def unused_formal_parameter(file, unused):
    class_name = os.path.basename(file).split('.')[0]
    with open(file, "r", encoding="utf-8") as original, \
       open("%s.bak" % file, "w", encoding="utf-8") as fout:
        i = 0
        line_index = 0
        content = []
        line = ''
        flag = False
        for fline in original:
            line_index += 1
            if (i < len(unused) and line_index == unused[i][0] and
               unused[i][1] in fline) or flag:
                index = fline.find('{', 0 if flag else fline.find(unused[i][1]))
                if index == -1:
                    if flag:
                        line += fline.strip()
                    else:
                        line += fline.rstrip()
                    flag = True
                    continue
                else:
                    if flag:
                        line += fline.lstrip()
                        flag = False
                    else:
                        line += fline

                print('>>> sout', unused[i][1], 'in class', class_name,
                      '[', unused[i][0], '] in file', file)
                index = line.find('{', 0 if flag else line.find(unused[i][1]))
                ll = list(line)
                ll.insert(index + 1,
                          '\nSystem.out.println(%s);\n' % unused[i][2])
                line = ''.join(ll)
                if len(line.strip()) != 0:
                    content.append(line)
                i += 1
                line = ''
            else:
                content.append(fline)
        write_lines(fout, content)
    os.remove(file)
    os.rename('%s.bak' % file, file)


def alipay_unused_imports_rule(file, unused):
    class_name = os.path.basename(file).split('.')[0]
    with open(file, "r", encoding="utf-8") as original, \
        open("%s.bak" % file, "w", encoding="utf-8") as fout:
        i = 0
        line_index = 0
        content = []
        for fline in original:
            line_index += 1
            if i < len(unused) and line_index == unused[i][0] and \
               unused[i][1] in fline and '//' not in fline.strip():
                print('>>> Comment', unused[i][1], 'in class', class_name,
                      '[', unused[i][0], '] in file', file)
                index = fline.find(unused[i][1])
                ll = list(fline)
                ll.insert(index, '// ')
                fline = ''.join(ll)
                if len(fline.strip()) != 0:
                    content.append(fline)
                i += 1
            else:
                content.append(fline)
        write_lines(fout, content)
    os.remove(file)
    os.rename('%s.bak' % file, file)


def fix_code(files, type, root='../../'):
    for fpath, unused in files.items():
        item = os.path.join(root, fpath)
        if os.path.exists(item):
            if type == 'ULV':
                unused_local_variable(item, unused)
            elif type == 'UFP':
                unused_formal_parameter(item, unused)
            elif type == 'AUIR':
                alipay_unused_imports_rule(item, unused)


if __name__ == '__main__':
    if len(sys.argv) >= 2:
        if sys.argv[1].upper() == 'UFP':
            fix_code(obtain_files(
                '../input/UnusedFormalParameter.txt'), 'UFP')
        elif sys.argv[1].upper() == 'AUIR':
            fix_code(obtain_files(
                '../input/AlipayUnusedImportsRule.txt'), 'AUIR')

    fix_code(obtain_files('../input/UnusedLocalVariable.txt'), 'ULV')
