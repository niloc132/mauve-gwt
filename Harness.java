// Copyright (c) 2006  Red Hat, Inc.
// Written by Anthony Balkissoon <abalkiss@redhat.com>

// This file is part of Mauve.

// Mauve is free software; you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation; either version 2, or (at your option)
// any later version.

// Mauve is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.

// You should have received a copy of the GNU General Public License
// along with Mauve; see the file COPYING.  If not, write to
// the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, 
// Boston, MA 02110-1301 USA.

/*
 * See the README file for information on how to use this
 * file and what it is designed to do.
 */

import gnu.testlet.config;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.StringTokenizer;
import java.util.Vector;

/**
 * The Mauve Harness.  This class parses command line input and standard
 * input for tests to run and runs them in a separate process.  It detects
 * when that separate process is hung and restarts the process.
 * @author Anthony Balkissoon abalkiss at redhat dot com
 *
 */
public class Harness
{    
  // The compile method for the embedded ecj
  private static Method ecjMethod = null;
  
  // The string that will be passed to the compiler containing the options
  // and the file(s) to compile
  private static String compileString = null;
  
  // The options to pass to the compiler, needs to be augmented by the
  // bootclasspath, which should be the classpath installation directory
  private static String compileStringBase = "-proceedOnError -nowarn";
  
  // The writers for ecj's out and err streams.
  private static PrintWriter ecjWriterOut = null;
  private static PrintWriter ecjWriterErr = null;
  
  // The constructor for the embedded ecj
  private static Constructor ecjConstructor = null;

  // The classpath installation location, used for the compiler's bootcalsspath
  private static String classpathInstallDir = null;
  
  // The location of the eclipse-ecj.jar file
  private static String ecjJarLocation = null;
  
  // How long the bootclasspath finder program can run before termination
  private static long bcp_timeout = 60000;
  
  // How long a test may run before it is considered hung
  private static long runner_timeout = 60000;

  // The command to invoke for the VM on which we will run the tests.
  private static String vmCommand = null;
  
  // Whether or not we should recurse into directories when a folder is
  // specified to be tested
  private static boolean recursion = true;

  // Whether we should run in noisy mode
  private static boolean verbose = false;
  
  // Whether we should display one-line summaries for passing tests
  private static boolean showPasses = false;
  
  // Whether we should compile tests before running them
  private static boolean compileTests = true;
  
  // Whether we should display information for failing compilations
  private static boolean showCompilationErrors = true;
  
  // The total number of tests run
  private static int total_tests = 0;

  // The total number of failing tests (not harness.check() calls)
  private static int total_test_fails = 0;

  // All the tests that were specified on the command line rather than
  // through standard input or an input file
  private static Vector commandLineTests = null;
  
  // The input file (possibly) supplied by the user
  private static String inputFile = null;

  // All the tests that were explicitly excluded via the -exclude option
  private static Vector excludeTests = new Vector();
  
  // A way to speak to the runner process
  private static PrintWriter runner_out = null;

  // A way to listen to the runner process
  private static BufferedReader runner_in = null;

  // The process that will run the tests for us
  private static Process runnerProcess = null;

  // A watcher to determine if runnerProcess is hung
  private static TimeoutWatcher runner_watcher = null;
  
  // A flag indicating whether or not runnerProcess is hung
  private static boolean testIsHung = false;
  
  // A lock used for synchronizing access to testIsHung
  private static Object runner_lock = new Object();
  
  // The arguments used when this Harness was invoked, we use this to create an
  // appropriate RunnerProcess
  private static String[] harnessArgs = null;
  
  // A convenience String for ensuring tests all have the same name format
  private static final String gnuTestletHeader1 = "gnu" + File.separatorChar
                                                  + "testlet";
  
  // A convenience String for ensuring tests all have the same name format
  private static final String gnuTestletHeader2 = gnuTestletHeader1
                                                  + File.separatorChar;
  
  /**
   * The main method for the Harness.  Parses through the compile line
   * options and sets up the internals, sets up the compiler options, 
   * and then runs all the tests.  Finally, prints out a summary
   * of the test run.
   * 
   * @param args the compile line options 
   * @throws Exception
   */
  public static void main(String[] args) throws Exception
  {
    // Create a new Harness and set it up based on args.
    Harness harness = new Harness();
    harness.setupHarness(args);
    
    // Start the runner process and run all the tests.
    initProcess(args);
    runAllTests();

    // If more than one test was run, print a summary.
    if (total_tests > 1)
      System.out.println("\nTEST RESULTS:\n" + total_test_fails + " of "
                         + total_tests + " tests failed.");
    else if (total_tests == 0)
      {
        // If no tests were run, try to help the user out by suggesting what
        // the problem might have been.
        System.out.println ("No tests were run.  Possible reasons " +
                "may be listed below.");
        if (compileTests == false)
          {
            System.out.println("Autocompilation is not enabled, so the " +
                    "tests need to be compiled manually.  You can enable " +
                    "autocompilation via configure, see the README for more " +
                    "info.\n");
          }
        else if (recursion == false)
          {
            System.out.println ("-norecursion was specified, did you " +
                    "specify a folder that had no tests in it?\n");
          }
        else if (excludeTests != null && excludeTests.size() > 0)
          {
            System.out.println ("Some tests were excluded.\nDid you use " +
                    "-exclude and exclude all tests (or all specified " +
                    "tests)? \n");
          }
        else
          {
            System.out.println ("Did you specify a test that " +
                                "doesn't exist or a folder that contains " +
                                "no tests? \n");
          }          
      }
    else if (total_test_fails == 0 && !showPasses && !verbose)
      // Finally, if a single test was run and the passing result wasn't
      // displayed, display it for the user.
      System.out.println ("TEST RESULT: pass");
    harness.finalize();
    System.exit(total_test_fails > 0 ? 1 : 0);
  }

  /**
   * Sets up the harness internals before the tests are run.  Parses through
   * the compile line options and then sets up the compiler options.
   * @param args
   * @throws Exception
   */
  private void setupHarness(String[] args) throws Exception
  {
    // Save the arguments, we'll pass them to the RunnerProcess so it can
    // set up its internal properties.
    harnessArgs = args;    
    
    // Find out from configuration whether auto-compilation is enabled or not.
    // This can be changed via the options to Harness (-compile true or
    // -compile false).
    compileTests = config.autoCompile.equals("yes");
    
    // Find out from configuration which VM we're testing.  This can be changed
    // via the options to Harness (-vm VM_TO_TEST). 
    vmCommand = config.testJava;
    
    // Now parse all the options to Harness and set the appropriate internal
    // properties.
    for (int i = 0; i < args.length; i++)
      {        
        if (args[i].equals("-norecursion"))
          recursion = false;
        else if (args[i].equals("-verbose"))
          verbose = true;
        else if (args[i].equals("-showpasses"))
          showPasses = true;
        else if (args[i].equals("-compile"))
          {
            // User wants to use an input file to specify which tests to run.
            if (++i >= args.length)
              throw new RuntimeException("No file path after '-file'.  Exit");
            if (args[i].equals("yes") || args[i].equals("true"))
              compileTests = true;
            else if (args[i].equals("no") || args[i].equals("false"))
              compileTests = false;
          }
        else if (args[i].equals("-hidecompilefails"))
          showCompilationErrors = false;
        else if (args[i].equals("-help") || args[i].equals("--help")
                 || args[i].equals("-h"))
          printHelpMessage();
        else if (args[i].equalsIgnoreCase("-file"))
          {
            // User wants to use an input file to specify which tests to run.
            if (++i >= args.length)
              throw new RuntimeException("No file path after '-file'.  Exit");
            inputFile = args[i];
          }
        else if (args[i].equalsIgnoreCase("-bootclasspath"))
          {
            // User is specifying the classpath installation folder to use
            // as the compiler's bootclasspath.
            if (++i >= args.length)
              throw new RuntimeException("No file path " +
                    "after '-bootclasspath'.  Exit");
            classpathInstallDir = args[i];
          }
        else if (args[i].equalsIgnoreCase("-ecj-jar"))
          {
            // User is specifying the location of the eclipse-ecj.jar file
            // to use for compilation.
            if (++i >= args.length)
              throw new RuntimeException("No file path " +
                    "after '-ecj-jar'.  Exit");
            ecjJarLocation = args[i];
          }
        else if (args[i].equals("-exclude"))
          {
            // User wants to exclude some tests from the run.
            if (++i >= args.length)
              throw new RuntimeException ("No test or directory " +
                    "given after '-exclude'.  Exit");
            excludeTests.add(startingFormat(args[i]));
          }
        else if (args[i].equals("-vm"))
          {
            // User wants to exclude some tests from the run.
            if (++i >= args.length)
              throw new RuntimeException ("No VMPATH" +
                    "given after '-vm'.  Exit");
            vmCommand = args[i];
          }
        else if (args[i].equals("-timeout"))
          {
            // User wants to change the timeout value.
            if (++i >= args.length)
              throw new RuntimeException ("No timeout value given " +
                    "after '-timeout'.  Exit");
            runner_timeout = Long.parseLong(args[i]);
          }
        else if (args[i].charAt(0) == '-')
          {
            // One of the ignored options (handled by RunnerProcess)
            // such as -debug.  Do nothing here but don't let it fall
            // through to the next branch which would consider it a 
            // test or folder name
          }
        else if (args[i] != null)
          {
            // This is a command-line (not standard input) test or directory.
            if (commandLineTests == null)
              commandLineTests = new Vector();
            commandLineTests.add(startingFormat(args[i]));
          }          
      }

    // If ecj-jar wasn't specified, use the configuration value.
    if (ecjJarLocation == null)
      ecjJarLocation = config.ecjJar;

    // If auto-compilation is enabled, verify that the ecj-jar location is 
    // valid.
    if (compileTests)
      {
        if (ecjJarLocation == null || ecjJarLocation.equals(""))
          compileTests = false;
        else
          {
            File testECJ = new File(ecjJarLocation);
            if (!testECJ.exists())
              compileTests = false;
          }
      }
    
    // If auto-compilation is enabled and the ecj-jar location was fine, 
    // set up the compiler options and PrintWriters
    if (compileTests)
      setupCompiler();
    
    // If vmCommand is "java" it is likely that it defaulted to this value, 
    // so it wasn't set in configure (--with-vm) and it wasn't set
    // on the command line (-vm TESTVM), so we should print a warning.
    if (vmCommand.equals("java"))
      System.out.println("WARNING: running tests on 'java'.  To set the " +
                         "test VM, use --with-vm when\nconfiguring " +
                         "or specify -vm when running the Harness.\n");
  }    
    
  /**
   * Sets up the compiler by reflection, sets up the compiler options,
   * and the PrintWriters to get error messages from the compiler.
   * 
   * @throws Exception
   */
  private void setupCompiler() throws Exception
  {
    String classname = "org.eclipse.jdt.internal.compiler.batch.Main";
    Class klass = null;
    try
    {
      klass = Class.forName(classname);
    }
    catch (ClassNotFoundException e)
    {
      File jar = new File(ecjJarLocation);
      if (! jar.exists() || ! jar.canRead())
        throw e;
      
      ClassLoader loader = new URLClassLoader(new URL[] { jar.toURL() });
      try
      {
        klass = loader.loadClass(classname);
      }
      catch (ClassNotFoundException f)
      {
        throw e;
      }
    }
    
    // Set up the compiler and the PrintWriters for the compile errors.
    ecjConstructor = 
      klass.getConstructor 
      (new Class[] { PrintWriter.class, PrintWriter.class, Boolean.TYPE});
    ecjMethod = 
      klass.getMethod
      ("compile", new Class[] 
          { String.class, PrintWriter.class, PrintWriter.class });    
    ecjWriterOut = new PrintWriter(new FileOutputStream(".ecjOut"));
    ecjWriterErr = new PrintWriter(new FileOutputStream(".ecjErr"));
    
    
    // Set up the compiler options now that we know whether or not we are
    // compiling, and print a header to the compiler error file.
    compileStringBase += getClasspathInstallString();
    ecjWriterErr.println("This file lists the compiler errors.\n" +
                         "Weird things will happen if the compiler's " +
                         "bootclasspath isn't properly set.\nIf the errors" +
                         " listed here seem odd, like:\n\n" +
                         "    'The type java.lang.Object cannot be resolved." +
                         " It is indirectly\n    referenced from required " +
                         ".class files,'\n\nthen try setting the " +
                         "bootclasspath by using the\n" +
                         "--with-bootclasspath=CPINSTALLDIR option " +
                         "in configure.");
    ecjWriterErr.println("\nThe compiler command used was: \n    " +
                         compileStringBase + "\n");
    
  }
  
  /**
   * Removes the "gnu.testlet." from the start of a String.
   * @param val the String
   * @return the String with "gnu.testlet." removed
   */
  private static String stripPrefix(String val)
  {
    if (val.startsWith("gnu" + File.separatorChar + "testlet")
        || val.startsWith("gnu.testlet."))
      val = val.substring(12);
    return val;
  }
  
  /**
   * Get the bootclasspath from the configuration so it can be added
   * to the string passed to the compiler.
   * @return the bootclasspath for the compiler, in String format
   */
  private static String getClasspathInstallString()
  {
    String temp = classpathInstallDir;
    
    // If classpathInstallDir is null that means no bootclasspath was 
    // specified on the command line using -bootclasspath.  In this case
    // check if anything was supplied to configure with --with-bootclasspath.
    if (temp == null)
      {
        temp = config.cpInstallDir;
        
        // If temp is the empty string then nothing was supplied to configure
        // so auto-detect the bootclasspath using getBootClasspath().
        if (temp.equals(""))
          {
            temp = getBootClassPath();
            
            // If auto-detect returned null we cannot auto-detect the 
            // bootclasspath and we should try invoking the compiler without
            // specifying the bootclasspath.  Otherwise, we should add
            // " -bootclasspath " followed by the detected path.
            if (temp != null)              
              return " -bootclasspath " + temp;
            return temp;
          }
      }
    
    // This section is for bootclasspath's specified with
    // -bootclasspath or --with-bootclasspath (in configure), we need
    // to add "/share/classpath/glibj.zip" onto the end and
    // " -bootclasspath onto the start".
    temp = " -bootclasspath " + temp;
    if (!temp.endsWith(File.separator))
      temp += File.separator;
    temp += "share" + File.separator + "classpath";
    
    // If (for some reason) there is no glibj.zip file in the specified
    // folder, just use the folder as the bootclasspath, perhaps the folder
    // contains an expanded view of the resources.
    File f = new File (temp.substring(16) + File.separator + "glibj.zip");
    if (f.exists())
      temp += File.separator + "glibj.zip";
    return temp;
  }
  
  /**
   * Forks a process to run gnu/testlet/DetectBootclasspath on the VM that is
   * being tested.  This program detects the bootclasspath so we can use
   * it for the compiler's bootclasspath.
   * @return the bootclasspath as found, or null if none could be found.
   */
  private static String getBootClassPath()
  {
    try
    {
      String c = vmCommand + " gnu" + File.separator + "testlet"
                   + File.separator + "DetectBootclasspath";
      Process p = Runtime.getRuntime().exec(c);      
      BufferedReader br = 
        new BufferedReader
        (new InputStreamReader(p.getInputStream()));
      String bcpOutput = null;
      // Create a timer to watch this new process.
      TimeoutWatcher tw = new TimeoutWatcher(bcp_timeout);
      tw.start();
      while (true)
        {
          // If for some reason the process hangs, return null, indicating we
          // cannot auto-detect the bootclasspath.
          if (testIsHung)
            {
              synchronized (runner_lock)
              {
                testIsHung = false;
              }
              br.close();
              p.destroy();
              return null;
            }
          if (br.ready())
            {
              bcpOutput = br.readLine();
              if (bcpOutput.startsWith("BCP_FINDER:"))
                return bcpOutput.substring(11);
              else
                {
                  // This means the auto-detection failed.
                  System.out.println(bcpOutput);
                  return null;
                }
            }
        }
    }
    catch (IOException ioe)
    {
      // Couldn't auto-fetch the bootclasspath.
      return null;
    }
  }

  /**
   * This method takes a String and puts it into a consistent format so we can
   * deal with all test names in the same way. It ensures that tests start with
   * "gnu/testlet" and that '.' are replaced by the file separator character. 
   * It also strips the .java or .class extensions if they are present, 
   * and removes single trailing dots.
   * 
   * @param val the input String
   * @return the formatted String
   */
  private static String startingFormat(String val)
  {
    if (val != null)
      {
        if (val.endsWith(".class"))
          val = val.substring(0, val.length() - 6);
        if (val.endsWith(".java"))
          val = val.substring(0, val.length() - 5);
        val = val.replace('.', File.separatorChar);
        if (val.endsWith("" + File.separatorChar))
          val = val.substring(0, val.length() - 1);
        if (! val.startsWith(gnuTestletHeader1))
          val = gnuTestletHeader2 + val;        
      }
    return val;
  }
  
  /**
   * This method prints a help screen to the console and then exits.
   */
  static void printHelpMessage()
  {
    String align = "\n                           ";
    String message = 
      "This is the Mauve Harness.  Usage:\n\n" +
            
      " JAVA Harness <options> <testcase | folder>\n" +      
      "  If no testcase or folder is given, all the tests will be run. \n" +

      // Example invocation.
      "\nExample: 'jamvm Harness -vm jamvm -showpasses javax.swing'\n" +
      "  will use jamvm (located in your path) to run all the tests in the\n" +
      "  gnu.testlet.javax.swing folder and will display PASSES\n" +
      "  as well as FAILS.\n\nOPTIONS:\n\n" +
      
      // Test Run Options.
      "Test Run Options:\n" +      
      "  -vm [vmpath]:        specify the vm on which to run the tests." +
      "It is strongly recommended" + align + "that you use this option or " +
      "use the --with-test-java option when running" + align + "configure.  " +
      "See the README file for more details.\n" +      
      "  -compile [yes|no]:       specify whether or not to compile the " +
      "tests before running them.  This" + align + "overrides the configure" +
      "option --disable-auto-compilation but requires an ecj jar" + align + 
      "to be in /usr/share/java/eclipse-ecj.jar or specified via the " +
      "--with-ecj-jar" + align + "option to configure.  See the README" +
      " file for more details.\n" +      
      "  -timeout [millis]:       specifies a timeout value for the tests " +
      "(default is 60000 milliseconds)" +

      // Testcase Selection Options.
      "\n\nTestcase Selection Options:\n" +
      "  -exclude [test|folder]:  specifies a test or a folder to exclude " +
      "from the run\n" +
      "  -norecursion:            if a folder is specified to be run, don't " +
      "run the tests in its subfolders\n" +
      "  -file [filename]:        specifies a file that contains the names " +
      "of tests to be run (one per line)" +

      // Output Options.
      "\n\nOutput Options:\n" +
      "  -showpasses:             display passing tests as well as failing " +
      "ones\n" +
      "  -hidecompilefails:       hide errors from the compiler when " +
      "tests fail to compile\n" +
      "  -exceptions:             print stack traces for uncaught " +
      "exceptions\n" +
      "  -verbose:                run in noisy mode, displaying extra " +
      "information\n" +
      "  -debug:                  displays some extra information for " +
      "failing tests that " +
      "use the" + align + "harness.check(Object, Object) method\n" +
      "  -xmlout [filename]:      specifies a file to use for xml output\n" +
      "\nOther Options:\n" +
      "  -help:                   display this help message\n";
      System.out.println(message);
    System.exit(0);
  }
  
  protected void finalize()
  {
    //Clean up 
    try
      {
        runner_in.close();
        runner_out.close();
        runnerProcess.destroy();
      } 
    catch (IOException e) 
      {
        System.err.println("Could not close the interprocess pipes.");
        System.exit(-1);
      }
  }
  
  /**
   * This method sets up our runner process - the process that actually
   * runs the tests.  This needs to be done once initially and also
   * every time a test hangs.
   * @param args the compile line options for Harness
   */
  private static void initProcess(String[] args)
  {    
    StringBuffer sb = new StringBuffer(" RunnerProcess");
    for (int i = 0; i < args.length; i++)      
      sb.append(" " + args[i]);      
    sb.insert(0, vmCommand);
    
    try
      {
        // Exec the process and set up in/out communications with it.
        runnerProcess = Runtime.getRuntime().exec(sb.toString());
        runner_out = new PrintWriter(runnerProcess.getOutputStream(), true);
        runner_in = 
          new BufferedReader
          (new InputStreamReader(runnerProcess.getInputStream()));
        
        // Create a timer to watch this new process.
        runner_watcher = new TimeoutWatcher(runner_timeout);
      }
    catch (IOException e)
      {
        System.err.println("Problems invoking RunnerProcess: " + e);
        System.exit(1);
      }
  }
  
  /**
   * This method runs all the tests, both from the command line and from
   * standard input.  This is so the legacy method of running tests by 
   * echoing the classname and piping it to the Harness works, but so does
   * a more natural "jamvm Harness <TESTNAME>".
   */
  private static void runAllTests()
  {   
    // Run the commandLine tests.  These were assembled into 
    // <code>commandLineTests</code> in the setupHarness method.
    if (commandLineTests != null)
      {
        for (int i = 0; i < commandLineTests.size(); i++)
          {
            String cname = null;
            cname = (String) commandLineTests.elementAt(i);
            if (cname == null)
              break;
            processTest(cname);
          }
      }
    
    
    // Now run the standard input tests.  First we determine if the input is
    // coming from a file (if the -file option was used) or from stdin.
    BufferedReader r = null;
    if (inputFile != null)
      // The -file option was used, so set up our BufferedReader to use the
      // input file.
      try
        {
          r = new BufferedReader(new FileReader(inputFile));
        }
      catch (FileNotFoundException x)
        {
          throw new 
            RuntimeException("Cannot find \"" + inputFile + "\".  Exit");
        }
    else
      {
        // The -file option was not used, so use stdin instead.
        r = new BufferedReader(new InputStreamReader(System.in));
        try
          {
            if (! r.ready())
              {
                // If no tests were specified to be run, we will run all the 
                // tests (except those explicitly excluded).
                if (commandLineTests == null || commandLineTests.size() == 0)
                  processTest("gnu/testlet");
                return;
              }
          }
        catch (IOException ioe)
          {
          }
      }

    // Now process all the tests specified in the file or from stdin.
    while (true)
      {
        String cname = null;
        try
          {
            cname = r.readLine();
            if (cname == null)
              break;
          }
        catch (IOException x)
          {
            // Nothing.
          }
        processTest(startingFormat(cname));
      }
  }
  
  /**
   * This method runs a single test in a new Harness and increments the
   * total tests run and total failures, if the test fails.  Prints
   * PASS and adds to the report, if the appropriate options are enabled.
   * @param testName the name of the test
   */
  private static void runTest(String testName)
  {
    String tn = stripPrefix(testName.replace(File.separatorChar, '.'));
    String outputFromTest;
    int temp = -1;

    // Start the timeout watcher
    if (runner_watcher.isAlive())
      runner_watcher.reset();
    else
      runner_watcher.start();
    
    // Tell the RunnerProcess to run test with name testName
    runner_out.println(testName);
    
    while (true)
      {
        // This while loop polls for output from the test process and 
        // passes it to System.out unless it is the signal that the 
        // test finished properly.  Also checks to see if the watcher
        // thread has declared the test hung and if so ends the process.
        if (testIsHung)
          {
            synchronized (runner_lock)
            {
              testIsHung = false;
            }
            try
              {
                runner_in.close();
                runner_out.close();
                runnerProcess.destroy();
              }
            catch (IOException e)
              {
                System.err.println("Could not close the interprocess pipes.");
                System.exit(- 1);
              }
            initProcess(harnessArgs);
            break;
          }
        try
        {
          if (runner_in.ready())
            {
              outputFromTest = runner_in.readLine();              
              if (outputFromTest.startsWith("RunnerProcess:"))
                {
                  // This means the test finished properly, now have to see if
                  // it passed or failed.
                  if (outputFromTest.endsWith("pass"))
                    temp = 0;
                  else if (outputFromTest.endsWith("fail"))
                    temp = 1;
                  else if (outputFromTest.endsWith("not-a-test"))
                    {
                      // Temporarily decrease the total number of tests,
                      // because it will be incremented later even 
                      // though the test was not a real test.
                      total_tests--;
                      temp = 0;
                    }
                  break;
                }                
              else
                // This means it was just output from the test, like a 
                // System.out.println within the test itself, we should
                // pass these on to stdout.
                System.out.println(outputFromTest);
            }
        }
        catch (IOException e)
        {
        }
      }
    if (temp == -1)
      {        
        // This means the watcher thread had to stop the process 
        // from running.  So this is a fail.
        if (verbose)
          System.out.println("  FAIL: timed out. \nTEST FAILED: timeout " + 
                             tn);
        else
        System.out.println("FAIL: " + tn
                           + "\n  Test timed out.  Use -timeout [millis] " +
                                "option to change the timeout value.");
        
        total_test_fails++;
      }
    else
      total_test_fails += temp;
    total_tests ++;
    
    // If the test passed and the user wants to know about passes, tell them.
    if (showPasses && temp == 0 && !verbose)
      System.out.println ("PASS: "+tn);
  }  
  
  /**
   * This method handles the input, whether it is a single test or a folder
   * and calls runTest on the appropriate .class files.  Will also compile
   * tests that haven't been compiled or that have been changed since last
   * being compiled.
   * @param cname the input file name - may be a directory
   */
  private static void processTest(String cname)
  {
    if (cname.equals("CVS") || cname.endsWith(File.separatorChar + "CVS")
        || excludeTests.contains(cname)
        || (cname.lastIndexOf("$") > cname.lastIndexOf(File.separator)))
      return;

    // If processSingleTest returns -1 then this test was explicitly 
    // excluded with the -exclude option, and if it returns 0 then 
    // the test was successfully run and we should stop here.  Only
    // if it returns 1 should we try to process cname as a directory.
      if (processSingleTest(cname) == 1)
        processFolder(cname);    
  }
  
  /**
   * This method is used to potentially run a single test.  If runAnyway is
   * false we've reached here as a result of processing a directory and we
   * should only run tests if they end in ".java" to avoid running tests
   * multiple times.
   *  
   * @param cname the name of the test to run
   * @return -1 if the test was explicitly excluded via the -exclude option,
   * 0 if cname represents a single test, 1 if cname does not represent a 
   * single test
   */  
  private static int processSingleTest(String cname)
  {
    // If the test should be excluded return -1, this is a signal
    // to processTest that it should quit.    
    if (excludeTests.contains(cname))
      return -1;

    // If it's not a single test, return 1, processTest will then try
    // to process it as a directory.
    File jf = new File(cname + ".java");
    if (!jf.exists())
      return 1;
    
    if (!compileTests)
      {
        File cf = new File(cname + ".class");
        if (!cf.exists())
          {
            // There is an uncompiled test, but the -nocompile option was given
            // so we just skip it
            return -1;
          }
      }
    else
      {
        // If compilation of the test fails, don't try to run it.
        if (compileTest(cname + ".java") != 0)
          return -1;
      }
       
    runTest(cname);
    return 0;
  }
  
  /**
   * This method processes all the tests in a folder.  It does so in
   * 3 steps: 1) compile a list of all the .java files in the folder,
   * 2) compile those files unless compileTests is false, 
   * 3) run those tests.
   * @param folderName
   */
  private static void processFolder(String folderName)
  {
    File dir = new File(folderName);
    String dirPath = dir.getPath();    
    File[] files = dir.listFiles();
    StringBuffer sb = new StringBuffer();
    String fullPath = null;
    boolean compilepassed = true;
    
    // If files is null, it is likely that the user input an incorrect
    // Harness command (like -test-vm TESTVM instead of -vm TESTVM).
    if (files == null)
      return;
    
    // First, compile the list of .java files.    
    int count = 0;
    for (int i = 0; i < files.length; i++)
      {        
        // Ignore the CVS folders.
        String name = files[i].getName();
        fullPath = dirPath + File.separatorChar + name;
        if (name.equals("CVS") || excludeTests.contains(fullPath))
          continue;
                
        if (name.endsWith(".java") && 
            !excludeTests.contains(fullPath.
                                   substring(0, fullPath.length() - 5)))
          {            
            count ++;
            sb.append(' ' + fullPath);
          }
        else
          {
            // Check if it's a folder, if so, call this method on it.
            if (files[i].isDirectory() && recursion
                && ! excludeTests.contains(fullPath))
              processFolder(fullPath);
          }
      }
    
    // Exit if there were no .java files in this folder.
    if (count == 0)
      return;
    
    // Ignore the .java files in top level gnu/teslet folder.
    if (dirPath.equals("gnu" + File.separatorChar + "testlet"))
      return;
    
    // Now compile all those tests in a batch compilation, unless the
    // -nocompile option was used.
    if (compileTests)
      compilepassed = compileFolder(sb, folderName);
    
    // And now run those tests.
    runFolder(sb, compilepassed);
  }
  
  private static boolean compileFolder(StringBuffer sb, String folderName)
  {
    int result = - 1;
    int compileFailsInFolder = 0;
    compileString = compileStringBase + sb.toString();
    try
      {
        result = compile();
      }
    catch (Exception e)
      {
        System.err.println("compilation exception");
        e.printStackTrace();
        result = - 1;
      }

    if (result != 0)
      {
        // The compilation was not successful. Since the -nowarn option was
        // used to compile the tests and output was sent to the ".ecjErr"
        // file, we can parse that file, exclude the tests that did not 
        // compile properly, and print out the errors to the user if they
        // asked to see them.
        try
        {
          BufferedReader errReader = 
            new BufferedReader(new FileReader(".ecjErr"));
          String lastFailingTest = null;
          String temp;
          int loc;
          
          // Go to the part in the file that relates to this folder 
          // specifically.
          temp = errReader.readLine();
          int len = folderName.length();
          int index;
          while (temp != null)
            {
              index = temp.indexOf(folderName);
              if (index != -1 && 
                  (temp.lastIndexOf((int)File.separatorChar) == len + index))
                break;
              temp = errReader.readLine();
            }            
          
          // If temp is null, we didn't find it.  Otherwise, look for each
          // individual failing compilation, count it as a fail, exclude it
          // from the test run, and print out the info.
          while (temp != null)
            {
              // If we've reached a part of the file that pertains to another
              // folder then break out of the loop.
              if (temp.indexOf("gnu" + File.separatorChar + "testlet") != - 1
                  && temp.indexOf(folderName) == - 1)
                break;
              
              
              // Look for test names for failing tests, so we can exclude
              // them from the run.  
              loc = temp.indexOf("gnu" + File.separatorChar + "testlet");
              if (loc != - 1)
                {
                  String name = temp.substring(loc);
                  String shortName = 
                    stripPrefix(name).replace(File.separatorChar, '.');
                  if (shortName.endsWith(".java"))
                    shortName = shortName.substring(0, shortName.length() - 5);
                  if (verbose && lastFailingTest != null)
                    System.out.println
                      ("TEST FAILED: compilation failed " + lastFailingTest);
                  lastFailingTest = shortName;

                  if (verbose)
                    System.out.println
                      ("TEST: " + shortName + "\n  FAIL: compilation failed.");
                  else
                    System.out.println("FAIL: " + shortName
                                       + ": compilation failed");
                  if (!showCompilationErrors)
                    System.out.println
                      ("  Read .ecjErr for details or run with " +
                            "-showcompilefails");

                  // When a test fails to compile, we count it as failing
                  // but we do not run it.
                  compileFailsInFolder++;
                  total_test_fails++;
                  total_tests++;
                  excludeTests.add(name);
                }
              
              // If -showcompilefails was used, print out the info. Do not
              // print the compiler summer (e.g. '3 problems (3 errors)').
              if (showCompilationErrors && 
                  temp.indexOf(problemsString(compileFailsInFolder)) == -1)
                System.out.println("  " + temp);
              
              // Read the next line in the file.
              temp = errReader.readLine();
            }
          if (verbose && lastFailingTest != null)
            System.out.println
              ("TEST FAILED: compilation failed " + lastFailingTest);
        }
        catch (FileNotFoundException fnfe)
        {
        }
        catch (IOException ioe)
        {          
        }
      }
    return result == 0;
  }
  
  /**
   * This method returns a String that the compiler prints as a summary
   * after a batch compile (e.g. '3 problems (3 errors)').  This is so 
   * we can ignore it when printing compiler errors to the screen.
   * @param fails the number of fails in the batch compilation
   * @return the summary String
   */
  private static String problemsString(int fails)
  {
    if (fails == 1)
      return "1 problem (1 error)";
    else
      return fails + " problems (" + fails + " errors)";
  }
  
  /**
   * Runs all the tests in a folder.  If the tests were compiled by 
   * compileFolder, and the compilation failed, then we must check to 
   * see if each individual test compiled before running it.
   * @param sb the StringBuffer holding a space delimited list of all the 
   * tests to run
   * @param compilepassed true if the compilation step happened and all 
   * tests passed or if compilation didn't happen (because of -nocompile).
   */
  private static void runFolder(StringBuffer sb, boolean compilepassed)
  {
    StringTokenizer st = new StringTokenizer(sb.toString());
    String nextTest = null;
    boolean classExists;
    while (st.hasMoreTokens())
      {
        nextTest = st.nextToken();
        nextTest = nextTest.substring(0, nextTest.length() - 5);
        classExists = (new File(nextTest + ".class")).exists();
        if (classExists
            && (compilepassed || ! excludeTests.contains(nextTest + ".java")))
          runTest(nextTest);
      } 
  }
  
  /**
   * This method invokes the embedded ECJ compiler to compile a single
   * test, which is stored in compileArgs[2].
   * @return the return value from the compiler
   * @throws Exception
   */
  public static int compile () throws Exception
  {
    /*
     * This code depends on the patch in Comment #10 in this bug
     * report:
     *
     * https://bugs.eclipse.org/bugs/show_bug.cgi?id=88364
     */
    
    Object ecjInstance = ecjConstructor.newInstance (new Object[] {
      new PrintWriter (System.out),
      new PrintWriter (System.err),
      Boolean.FALSE});
    return ((Boolean) ecjMethod.invoke (ecjInstance, new Object[] {
        compileString, ecjWriterOut, ecjWriterErr})).booleanValue() ? 0 : -1;
  }
  
  private static int compileTest(String testName)  
  {
    int result = -1;
    // Compile the tests before running them, and if compilation fails report
    // it as a test failure.
    try
      {
        compileString = compileStringBase + " " + testName;
        result = compile();
      }
    catch (Exception e)
      {
        result = - 1;
      }
    if (result != 0)
      {
        String shortName = stripPrefix(testName);
        if (verbose)
          System.out.println
            ("TEST: " + shortName + "\n  FAIL: compilation failed.");
        else
          System.out.println("FAIL: " + stripPrefix(testName)
                             + ": compilation failed");
        if (!showCompilationErrors)
          System.out.println
            ("  Read .ecjErr for details or run with -showcompilefails");
        else
          {
            try
            {
              BufferedReader errReader = 
                new BufferedReader(new FileReader(".ecjErr"));
              String temp = errReader.readLine();
              while(temp != null && temp.indexOf(testName) == -1)
                temp = errReader.readLine();
              System.out.println("  " + temp);
              temp = errReader.readLine();
              while (temp != null && 
                     temp.indexOf("gnu" + File.separatorChar + "teslet") == -1)
                {
                  System.out.println("  " + temp);
                  temp = errReader.readLine();
                }
            }
            catch (FileNotFoundException fnfe)
            {          
            }
            catch (IOException ioe)
            {              
            }
          }
        if (config.cpInstallDir.equals(""))
          System.out.println("  Try setting --with-bootclasspath " +
                "when running configure.\n  See the README file for details");
        if (verbose)
          System.out.println("TEST FAILED: compilation failed " + shortName);
        
        total_test_fails++;
        total_tests++;
        result = - 1;
      }
    return result;
  }  

  /**
   * This class is used for our timer to cancel tests that have hung.
   * @author Anthony Balkissoon abalkiss at redhat dot com
   *
   */
  private static class TimeoutWatcher implements Runnable
  {
    private long millisToWait;
    private Thread watcherThread;
    boolean loop = true;
    
    /**
     * Creates a new TimeoutWatcher that will wait for <code>millis</code>
     * milliseconds once started.
     * @param millis the number of milliseconds to wait before declaring the 
     * test as hung
     */
    public TimeoutWatcher(long millis)
    {
      millisToWait = millis;
      watcherThread = new Thread(this);
    }
    
    /**
     * Start the watcher thread, ie start the countdown.     
     */
    public void start()
    {
      watcherThread.start();      
    }
    
    /**
     * Return true if the watcher thread is currently counting down.
     * @return true if the watcher thread is alive
     */
    public boolean isAlive()
    {
      return watcherThread.isAlive();
    }
    
    /**
     * Reset the counter and wait another <code>millisToWait</code>
     * milliseconds before declaring the test as hung.     
     */
    public synchronized void reset()
    {
      loop = true;
      notify();
    }
    
    public synchronized void run()
    {
      Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
      while (loop)
        {
          // We set loop to false here, it will get reset to true if 
          // reset() is called from the main Harness thread.
          loop = false;
          try
          {
            wait(millisToWait);
          }
          catch (InterruptedException ie)
          {}
        }
      // The test is hung, set testIsHung to true so the process will be 
      // destroyed and restarted.
      synchronized (runner_lock)
        {
          testIsHung = true;
        }
    }
  }
}
