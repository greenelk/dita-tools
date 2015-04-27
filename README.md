# dita-tools [![Build Status](https://secure.travis-ci.org/greenelk/dita-tools.png?branch=master)](http://travis-ci.org/greenelk/dita-tools)

The DITA Tools Eclipse plugin provides some useful facilities for DITA authors and editors.
To perform the available actions:

## Quick start

Link to the the plugin download, installation and build instructions can be found on the [dita-tools wiki](https://github.com/greenelk/dita-tools/wiki)

## Checking tools

 - Select some resources (projects, folders and files) in the Package Explorer or Project Explorer.
 - Right click, and then select **DITA Tools** 

### Where Used Checker
 
The selected **dita/jpg/gif/png files** files are checked to see if they are referred to by any other file in the workspace. 
Informational, Warning, and Error markers are created and may be viewed in the Problem view.

  - Informational - jpg/gif/png file is referenced by at least one other file
  - Informational - dita file is referenced by at least one file, which is a **ditamap**
  - Error - dita file is referenced by at least one file, but no **ditamap**s
  - Error - file is not referenced by any other file in the workspace

The default is to scan **ditamap**, **dita**, **htm** and **html** filetypes, though 
this may be configured through the Preferences page. 
Note: As this needs to cross-reference the entire workspace it may take some time to complete, even if you've only selected a single file to check. 

### HREF Checker

Selected resources are scanned and an attempt is made to resolve all **href** attributes. Check is also made that a title exists for scope="peer" references.
Informational, Warning, and Error markers are created and may be viewed in the Problem view.
The default is to scan **ditamap**, **dita**, **htm** and **html** filetypes, though 
this may be configured through the Preferences page. 

### CONREF Checker

Selected resources are scanned and an attempt is made to resolve all **conref** attributes. 
Informational, Warning, and Error markers are created and may be viewed in the Problem view.
Only **dita** files are scanned. 

## Source &lt;-&gt; web mapping

Tools that provide mapping between source files and their web equivalents.

### Source file finder

If you are looking at your documentation on-line and you want to find where source for the web page, or image, is located in your workspace, just.

   - Display the page in your favourite web browser.
   - Copy the URL for the webpage, or image, to the clipboard.
   - Click on the <image src="images/internet.gif"/> icon on your eclipse toolbar and the file (if found) will be located in all open navigation panels.
 

### DITA Web file finder

Right click on one or more dita, htm, or html files in your source tree and look for the "DITA Web File Finder" action. After the preferences are set,
 it will open up the equivalent file on a site it's been published to. You just have to supply one, or more, URL prefixes so the action 
 knows how to construct the full URL. Examples of URL prefixes are:  

  - External URL - http://www.company.com/v123
  - Internal URL for docs build output - http://intranet.company.com:9080/v124/index.jsp?topic=
 
Clicking on this action will open up 3 web browser tabs with views on the respective webpages.

![WebFileFinder](https://raw.githubusercontent.com/greenelk/dita-tools/master/images/wff.png "Using the Web File Finder")
