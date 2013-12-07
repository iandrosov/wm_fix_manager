wm_fix_manager
==============

webMethods Platform Fix Manager Utility

Getting started

Welcome to webMethods Platform Fix Manager Professional 1.0 This utility program has been designed
to manage multiple fixes to webMethods components on multiple environments. 
webMethods Fix Manager automates fix install and uninstall including fix details such as
package updates, database script execution to change tables required for some fixes. 
It includes analyzer, uninstaller, backup and restore features.

Analyzer component can test user configuration against actual installed
components and visually display which fixes are installed, not installed or partially installed.
Uninstaller allows user to remove any fix from webMethods environment or reinstall it.
Backup/Restore features allow users to take package backup in order to restore or uninstall
changes made by fix installation. These features are  useful during testing and development phases
to verify sets of fixes in actual environments.
 
The tool can manage fixes for following webMethods components:
Integration Server
Broker
Developer

webMethods Fix Manager is Eclipse based application that uses Eclipse SWT in GUI mode and requires 
Eclipse SWT binaries for target platform. 
It can also be used in console mode where Display is not available only to install or uninstall fixes.


NOTE: When using console mode the configuration of fixes must be performed manually by updates to 
ini.xml file. Visual GUI configuration only available on OS with windowing display capability.
Analyzer is not available in console mode.

Installation instructions

webMethods Fix Manager is distributed as executable jar and supported on all platforms which support
webMethods Integration Server and JVM. The distribution includes only application binary and
basic configuration files.
Other dependent libraries must be downloaded and installed separately.

1. Unzip WmFixManagerInstaller.zip into some temporary directory on local drive.

2. Run WmFixManagerInstaller.exe windows installer on the system where webMethods platform is installed.

3. During installation select target directory where WmFixManager is to be installed. Recommended location
is webMethods platform home directory.

4. Installer will create required directories and copy application files to target system and create start
menu shortcut to run the program from Windows Start menu.
NOTE: WmFIxManager is java program and in order to execute it in Windows OS directly, Windows Explorer must be
configured to run java jar files. If this configuration is not available for any reason user can run the
utility from command line using run.cmd command file provided with installation.

5. After setup is completed user may need to add required dependency libraries to new installation.
Depending on distribution selected by user installer may or may not have complete set of required
libraries.
Following set of libraries must be available to run this program:

webMethdos Client libraries: 
client.jar,
entbase.jar, 
entcertlist.jar, 
entcms.jar, 
entjsse.jar, 
entmisc.jar

JDBC Driver: 
Oracle - classes12.jar or other JDBC driver.

Apache XML parser: xerses.jar

Eclipse SWT library: swt.jar

All jar files must be placed in <WmFixManager App>/lib directory
webMethods libraries can be copied directly from WM platform installation, example: <IS dir>/lib/client.jar.
JDBC driver for target database can be downloaded or copied form database installation directory.
Apache XML parser xerces.jar <xerces-J-bin.1.0.0.zip> is open source library and can be downloaded 
from: http://xerces.apache.org/xerces2-j/download.cgi or archive: http://archive.apache.org/dist/xml/xerces-j/old_xerces1
Current version of Eclipse SWT support library is 3.1.2
Eclipse SWT library is open source and can be downloaded from: 
http://download.eclipse.org/eclipse/downloads/drops/R-3.1.2-200601181600/index.php
SWT provides several components, native binaries and jar files. Only swt.jar and native binaries files are required 
for this tool.

6. Eclipse SWT requires target platform OS native library for GUI components. For windows it is .dll
for Unix .so files. Following Eclipse library must be installed:
Native Eclipse SWT library:  swt-win32-3139.dll for windows or libswt-motif-2049.so for Linux or their latest versions.
For other platforms support refers to Eclipse website http://www.eclipse.org
Native library must be placed in application home directory where WmFixManager_1.0.jar is located.


7. At this point the installation is complete and user can proceed to set up his fix configurations for target environment.
As part of installation a sample fix and WM directory structure is set for testing the utility.

Updates V1.0

No current updates

User Notes

webMethods Fix Manager provides intuitive GUI interface to manage fixes.
Each webMethods fix must be configured using installation instructions that are provided with fix distribution.
Interface arranges fixes into logical Profiles meant to serve as containers for specific webMethods installation
target environment. Each Profile requires environment specific properties to be set such as target and source 
directories where all fixes are located and where they are to be installed.
User can add profiles and fixes by using popup menu. Each fix must be configured individually based on its
instructions. The general rule to select fix target directory is to use same directory indicated in fix
installation instructions.

WARNING: Selecting incorrect target path for the fix and executing install operation may corrupt the 
webMethods platform installation. Therefore configure each fix installation with care!