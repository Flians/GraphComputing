import os
import re
import sys

'''
    Correct errors of type SystemPrintln, AvoidPrintStackTrace and 
    AlipayGuardLogStatementRule in blocker
'''


def obtain_files(in_path):
    with open(in_path, 'r') as fin:
        line = fin.readline()
        dirs = set()
        while line:
            line = line.strip()
            if line.endswith('.java'):
                dirs.add(line)
            line = fin.readline()
    return dirs


def write_lines(fout, lines):
    for line in lines:
        fout.write(line)
    lines.clear()


def alter_file(file, old_str, new_str):
    class_name = os.path.basename(file).split('.')[0]
    print('>>> alter', old_str, 'in class', class_name,
          'with', new_str, 'in file', file)
    with open(file, "r", encoding="utf-8") as original, open("%s.bak" % file,
                                                             "w",
                                                             encoding="utf-8") as fout:
        import_pattern = re.compile(r'^\s*import(\s+static)?\s+[\w.]+;$')
        class_pattern = re.compile(
            r'^\s*(public|protected|private)?(\s(final|abstract))?\s+class\s+' +
            class_name + r'[\s\w.<,>{]+$')
        null_vary_pattern = re.compile(r'^(\s+[\w<,>]+)+;$')
        incomplete_pattern = re.compile(r'^\s*[\w.]+\s*$')
        sout_pattern = re.compile(
            r'System\s*\.\s*out\s*\.\s*println\((.+)\);?', re.M | re.S)
        epst_pattern = re.compile(r'(\w+)\.printStackTrace\(\);')
        logger_name = 'LOGGER'
        logger_def = '    private static final Logger LOGGER = ' \
                     'LoggerFactory.getLogger(%s.class);\n ' % class_name
        import_arr = []
        variable_arr = []
        others = []
        line = ''
        flag = 0
        # record whether 'if (LOGGER.isDebugEnabled())' exists
        flag_if = 0
        for fline in original:
            line += fline
            # if ('=' in fline or re.match(incomplete_pattern, line)) and \
            if not fline.strip().endswith(';') and \
                not fline.strip().endswith('}') and \
                not fline.strip().endswith('{') and \
                not fline.strip().startswith('//') and \
                not fline.strip().endswith('*/') and \
                not fline.strip().startswith('@'):
                continue
            if flag == 0:
                if re.match(import_pattern, line):
                    write_lines(fout, others)
                    flag = 1
                    import_arr.append(line)
                else:
                    others.append(line)
            elif flag <= 2:
                if re.match(class_pattern, line):
                    if flag == 1:
                        import_arr.insert(0, 'import org.slf4j.Logger;\n')
                        import_arr.insert(0,
                                          'import org.slf4j.LoggerFactory;\n')
                    write_lines(fout, import_arr)
                    others.append(line)
                    if '{' in line:
                        write_lines(fout, others)
                        flag = 4
                    else:
                        flag = 3
                else:
                    import_arr.append(line)
                    if 'org.slf4j.Logger' in line:
                        flag = 2
            elif flag == 3:
                others.append(line)
                if '{' in line:
                    write_lines(fout, others)
                    flag = 4
            elif flag <= 5:
                if len(line.strip()) == 0 or line.strip().startswith('//') \
                   or re.match(null_vary_pattern, line) or '=' in line:
                    if 'Logger' in line:
                        logger_name = line[line.find('Logger') + 6: line.find(
                            '=')].strip()
                        flag = 5
                    variable_arr.append(line)
                else:
                    if flag == 4:
                        variable_arr.insert(0, logger_def)
                    write_lines(fout, variable_arr)
                    flag = 6
                    others.append(line)
            else:
                # Correct errors of type SystemPrintln
                # For '.(Integre -> LOGGER.info());' in lambda, it's unsolved
                search_sout = re.search(sout_pattern, line)
                if search_sout:
                    print(search_sout.group(1))
                    if '"' in search_sout.group(1):
                        rep_str = '%s.info(%s);' % (logger_name,
                                                    search_sout.group(1))
                    else:
                        rep_str = '%s.info("log: ", %s);' % (logger_name,
                                                             search_sout.group(
                                                                 1))
                    line = re.sub(sout_pattern, rep_str, line)

                # Correct errors of type AvoidPrintStackTrace
                search_epst = re.search(epst_pattern, line)
                if search_epst:
                    line = re.sub(epst_pattern,
                                  '%s.error("%s error, {} ", %s.getMessage());'
                                  'throw new RuntimeException(%s);'
                                  % (logger_name, class_name,
                                     search_epst.group(1),
                                     search_epst.group(1)),
                                  line)

                if re.search(
                    r'if \({}\.is(Info|Error|Debug|Trace|Warn)Enabled\(\)\)'.
                        format(logger_name), line):
                    flag_if = 2
                else:
                    if flag_if > 1:
                        flag_if = 1
                    else:
                        flag_if = 0

                # Correct errors of type AlipayGuardLogStatementRule
                log_pattern = re.compile(
                    r'{}\s*\.\s*(info|error|debug|trace|warn)\((.+)\)'.format(
                        logger_name), re.M | re.S)
                search_log = re.search(log_pattern, line)
                if search_log and flag_if == 0:
                    log_type = search_log.group(1).capitalize()
                    log_content = search_log.group(2)
                    '''
                    if re.search(r'"\s*\+\s*"', log_content):
                        log_content = re.sub(r'"\s*\+\s*"', '', line)
                        # line = re.sub(r'"\s*\+\s*"', '', line)
                    if '" +' in log_content:
                        log_content = re.sub(r'"\s*\+', '', line)
                        # line = re.sub(r'"\s*\+', '",', line)
                    if '+ "' in log_content:
                        log_content = re.sub(r'\+\s*"', '', line)
                        # line = re.sub(r'\s*\+ "', ', "', line)
                    line = line.replace('\n', '').\
                        replace(search_log.group(1), log_content)
                    '''
                    if '+' in log_content:
                        ll = list(line)
                        ll.insert(
                            search_log.span()[0],
                            'if (%s.is%sEnabled()) ' % (logger_name, log_type))
                        line = ''.join(ll)
                others.append(line)
            line = ''
        if len(line) > 0:
            others.append(line)
        write_lines(fout, others)
        os.remove(file)
        os.rename('%s.bak' % file, file)


def fix_code(files, old_str, new_str, root='../../'):
    for fpath in files:
        item = os.path.join(root, fpath)
        if os.path.exists(item):
            alter_file(item, old_str, new_str)


if __name__ == '__main__':
    if len(sys.argv) >= 2:
        if sys.argv[1].upper() == 'APST':
            fix_code(obtain_files(
                '../input/AvoidPrintStackTrace.txt'), 'printStackTrace',
                'log.error')
        elif sys.argv[1].upper() == 'AGLSR':
            fix_code(obtain_files(
                '../input/AlipayGuardLogStatementRule.txt'), 'log.fine(+)',
                'log.fine(,)')

    fix_code(obtain_files('../input/SystemPrintln.txt'),
             'System.out.println', 'log.fine')
