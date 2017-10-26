#!/usr/bin/env python3.4

import os, os.path, subprocess
import random, string
from subprocess import Popen, PIPE
from io import StringIO
import sys
def make():
    try: 
        subprocess.call(['make', 'clean'])
        return subprocess.call(['make', 'compiler'])
    except:
        print("Make compiler doesn't work when running from the main folder")

def run_tests():
    grade = 0
    try:
        RANDOMSTRING = ''.join(random.choice(string.ascii_uppercase + string.ascii_lowercase + string.digits) for _ in range(32))
        RESULT = './test/' + RANDOMSTRING
        subprocess.call(['mkdir', RESULT])
        INPUT='./test/input'
        OUTPUT='./test/output'
    except:
        print("Error in creating output folder")
        return 0;

    for microfile in os.listdir(INPUT):
        basename=os.path.splitext(microfile)[0]
        print("Testing.....", microfile)
        try:
            subprocess.call("chmod +x runme", shell=True)
            subprocess.call(['./runme',INPUT+"/"+microfile, RESULT+"/"+basename+".out"])
            if(os.path.isfile(RESULT+"/"+basename+".out")):
                writer = open("test_in.txt", "w")
                writer.write(str(random.randint(1,100)))
                writer.write(" ")
                writer.write(str(random.randint(1,100)))
                writer.close()
                with open("test_in.txt", "r") as test1, open("test_in.txt", "r") as test2:
                    process1 = Popen(['./tiny', OUTPUT+"/"+basename+".out"], stdout=subprocess.PIPE, stderr=subprocess.PIPE, stdin=test1,universal_newlines=True)
                    process1.wait()
                    fullKeyOut = process1.stdout.read()
                    process2 = Popen(['./tiny', RESULT+"/"+basename+".out"], stdout=subprocess.PIPE, stderr=subprocess.PIPE, stdin=test2,universal_newlines=True)
                    process2.wait()
                    fullStuOut = process2.stdout.read()
                    keyOut = fullKeyOut.split("STATISTICS")[0]
                    print("Key output:\n", keyOut)
                    stuOut = fullStuOut.split("STATISTICS")[0]
                    print("Student output:\n", stuOut)
                    if(keyOut == stuOut):
                        grade+=(100/len(os.listdir(INPUT)))
                    else:
                        print("Number of lines to be diffd from students output is different than solution")
                        print("Number of student lines before STATISTICS: ", stu_diff_lines)
                        print("Student simulation output: ", fullStuOut)
                        print("Number of solution lines before STATISTICS: ", key_diff_lines)
                        print("Solution simulation output: ", fullKeyOut)
            else:
                print("runme didn't create ", RESULT+"/"+basename+".out")
	except Exception as e:
		print('Failed to upload to ftp: '+ str(e))
    return grade

def make_simulator():
    return subprocess.call("g++ -o tiny tinyNew.c", shell=True)

if __name__ == '__main__':
    #change this to correct path
    #os.environ['CLASSPATH'] = "/lib/antlr.jar:" + os.getcwd()
    os.environ['CLASSPATH'] =  os.getcwd() + "/lib/antlr.jar:"
    print(os.environ['CLASSPATH'])
    grade=0
    make_failure = make()
    sim_failure = make_simulator()
    if(not(sim_failure or make_failure)): 
        grade=run_tests()
    file = open("grade-tmp.txt", "w")
    file.write(str(grade))
    file.close()
