import os
import shutil


# get all paths of the file under the specified directory
def get_file_paths_under_directory(directory):
  file_path_list = []
  for filename in os.listdir(directory):
    absolute_path = directory + "\\" + filename
    if os.path.isfile(absolute_path):
      file_path_list.append(absolute_path)
    elif os.path.isdir(absolute_path):
      sub_list = get_file_paths_under_directory(absolute_path)
      file_path_list.extend(sub_list)
  return file_path_list


# remove all file and directory under the specified directory
def remove_directory(directory_absolute_path):
  for class_file in os.listdir(directory_absolute_path):
    absolute_path = directory_absolute_path + "\\" + class_file
    if os.path.isfile(absolute_path):
      os.remove(absolute_path)
    elif os.path.isdir(absolute_path):
      shutil.rmtree(absolute_path)


# java compile
def java_compile(javac_path, source_directory, class_directory, library_directory):

  # write all java file path into a temporary file
  classnames = get_file_paths_under_directory(source_directory)
  with open(class_directory + "\\temp.txt", mode="w", encoding="UTF-8") as file:
    for each in classnames:
      file.write(each + "\n")

  # get all library absolute path
  library_paths = get_file_paths_under_directory(library_directory)

  # construct cmd str
  cmd = javac_path \
        + " @" + class_directory + "\\temp.txt" \
        + " -encoding UTF-8" \
        + " -d " + class_directory \
        + " -classpath "
  for lib in library_paths:
    cmd += lib + ";"

  # execute cmd
  os.system(cmd)

  os.remove( class_directory + "\\temp.txt" )


# main function
def main():

  # java compiler
  javac_path = "D:\\0-application\\1-portable\\8-jdk\\bin\\javac.exe"

  # some directories about project
  source_directory = "D:\\2-project\\1-graduate\\src"
  class_file_directory = "D:\\2-project\\1-graduate\\web\\WEB-INF\\classes"
  library_directory = "D:\\2-project\\1-graduate\\web\\WEB-INF\\lib"

  # execute
  remove_directory(class_file_directory)
  java_compile(javac_path, source_directory, class_file_directory, library_directory)

  # start tomcat
  cmd = "cd D:\\0-application\\1-portable\\9-tomcat\\bin && startup.bat"
  os.system(cmd)


main()
