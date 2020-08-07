#!/usr/bin/env python
# coding=utf-8


import getopt
import sys
import os
import re
import shutil

from git import Repo

businessName = ''

projectName = ''

template_dir_name = 'template'

template_file_name = 'Template'

rename_file_list = ['settings.gradle', 'nexus.properties', 'AndroidManifest.xml']


# Exit with specified error message
def exit_error(err_code):
    print 'exit_error err_code is={}'.format(err_code)
    sys.exit(err_code)


# Clone git repository
def clone_repo(local_dir, git_url):
    if not git_url.endswith('.git'):
        print 'git git_url is={}'.format(git_url)
        return

    repo_dir = '{}/{}'.format(local_dir, git_url[git_url.rfind('/') + 1:git_url.rfind('.git')])
    print 'git repo_dir is={}'.format(repo_dir)

    # 二次执行python脚本的预处理
    target_dir = '{}/{}'.format(local_dir, projectName)

    if os.path.exists(target_dir):
        print 'do not run template.py twice ,clean project first'
        return
    else:
        if os.path.exists(repo_dir):
            print 'git repo \'{}\' has existed'.format(repo_dir)
            repo = Repo(repo_dir)
        else:
            print 'create git repo \'{}\''.format(repo_dir)
            print 'git clone {}'.format(git_url)
            repo = Repo.clone_from(git_url, repo_dir)

    pull_repo(git_url, repo)
    rename_business(repo_dir, target_dir)


def pull_repo(git_url, repo):
    try:
        repo.remote().pull()
    except Exception:
        raise RuntimeError('FAILURE : git pull on {}'.format(git_url))


def rename_business(repo_dir, target_dir):
    print 'git repo_dir is={}'.format(repo_dir)
    print 'rename_business target_dir is={}'.format(target_dir)
    if os.path.exists(repo_dir):
        print 'rename_business repo_dir is={}'.format(repo_dir)
        os.rename(repo_dir, target_dir)

        rename_dir(target_dir)


def rename_dir(template_dir):
    if os.path.exists(template_dir):
        parents = os.listdir(template_dir)

        business_build_gradle = None
        for parent in parents:
            child = os.path.join(template_dir, parent)
            print ('os.path.join(template_dir, parent), child is = ' + os.path.abspath(child))
            if os.path.isdir(child):
                print('isdir===' + child)
                if template_dir_name in os.path.abspath(child):
                    dir_target_name = child.replace(template_dir_name, businessName)
                    os.rename(child, dir_target_name)
                    rename_dir(dir_target_name)
                else:
                    rename_dir(child)

            elif os.path.isfile(child):
                print ('isfile===' + child)
                child_path = os.path.abspath(child)
                rename_file = template_file_name in child_path
                file_target_name = child.replace(template_file_name, businessName.title())

                if ('business' in child_path) & (child_path.endswith('build.gradle')):
                    business_build_gradle = child

                if is_rename_file(child_path):
                    print ('setting_file,file===' + child)
                    replace_file_content(child)
                if rename_file:
                    print ('rename_file,file===' + child)
                    os.rename(child, file_target_name)
                    replace_file_content(file_target_name)

            # init_git(child)

        if business_build_gradle is not None:
            print 'business_build_gradle.path == ' + os.path.abspath(business_build_gradle)
            add_dependency(business_build_gradle)


def is_rename_file(child_path):
    for item in rename_file_list:
        print "is_rename_file == " + item
        if item in child_path:
            return True
    return False


def add_dependency(business_build_gradle):
    dopen = open('./dependency.properties')

    target_line = '\n'
    for line in dopen:
        print ('dependency.properties,line = ' + line)
        target_line = target_line + '    ' + line

    business_service = """implementation project(path:":service_templatebundle")"""

    rename_service = business_service.replace(template_dir_name, businessName)
    target_line += '    ' + rename_service
    print ('target_line == ' + target_line)
    write_infile(business_build_gradle, target_line, get_insert_index(business_build_gradle))
    dopen.close()


def init_git(dir):
    if os.path.exists(dir) & os.path.isdir(dir):

        if os.path.abspath(dir).endswith('.git'):
            shutil.rmtree(dir)
            Repo.init('{}/{}'.format('./', projectName))


def write_infile(path, cont, line=0):
    lines = []
    with open(path, 'r') as r:
        for l in r:
            lines.append(l)

    if line == 0:
        lines.insert(0, '{}\n'.format(cont))
    else:
        lines.insert(line - 1, '{}\n'.format(cont))
    s = ''.join(lines)
    # print(s)
    with open(path, 'w') as m:
        m.write(s)
        print('writeInFile Success!')


def get_insert_index(business_build_gradle):
    f = open(business_build_gradle, 'r')
    lines = []
    i = 0
    for line in f:
        i = i + 1
        lines.append(line)
        if 'fileTree' in line:
            f.close()
            return i + 1


def replace_file_content(old_file):
    fopen = open(old_file, 'r')
    w_str = ""
    for line in fopen:
        if re.search(template_dir_name, line):
            line = re.sub(template_dir_name, businessName, line)
        elif re.search(template_file_name, line):
            line = re.sub(template_file_name, businessName.title(), line)

        w_str += line
    print w_str
    wopen = open(old_file, 'w')
    wopen.write(w_str)
    fopen.close()
    wopen.close()


def update_file_content(fp):
    for s in fp.readlines():
        if template_file_name in s:
            fp.write(str(s.replace(template_file_name, businessName.title)))
            if template_dir_name in s:
                fp.write(str(s.replace(template_dir_name, businessName)))
    fp.close()


# Program entrance
if __name__ == '__main__':
    if len(sys.argv) == 1:
        print ('please enter business name,such as:python template.py --business=taxi\nNote the lowercase initials')
        exit_error(1)

    # Parse input parameters
    try:
        opts, args = getopt.getopt(sys.argv[1:], 'h', ['package=',
                                                       'business=', 'help'])
    except getopt.GetoptError:
        exit_error(1)

    for opt, value in opts:
        if opt == '--business':
            businessName = value.lower()

            print 'businessName is \'{}\' '.format(businessName)
            projectName = 'porjectName'.replace(template_file_name, businessName.title())
            print 'projectName is \'{}\' '.format(projectName)
        elif opt in ('-h', '--help'):
            print ('please enter business name,such as:python template.py --business=taxi\nNote the lowercase initials')
            exit_error(0)

if not businessName.strip() == "":
    print 'start clone repo...'
    # clone_repo('./', 'git@github.com:liqianjiang/template.git')
    clone_repo('./', 'git')
