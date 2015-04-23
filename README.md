# dita-tools [![Build Status](https://secure.travis-ci.org/greenelk/dita-tools.png?branch=master)](http://travis-ci.org/greenelk/dita-tools)

The GREENELK DITA Tools Eclipse plugin provides some useful facilities for DITA authors and editors.
To perform the available actions:

## Source &lt;=&gt; web mapping

Tools that provide mapping between source files and their web equivalents.

### Source file finder

<p>
If you are looking at your documentation on-line and you want to find where source for the web page, or image, is located in your workspace, just.
<ul>
<li>Display the page in your favourite web browser</li>
<li>Copy the URL for the webpage, or image, to the clipboard</li>
<li>Click on the <image src="images/internet.gif"/> icon on your eclipse toolbar and the file (if found) will be located in all open navigation panels</li>
</ul> 
</p>

### DITA Web file finder
<p>
Right click on one or more dita, htm, or html files in your source tree and look for the "DITA Web File Finder" action. After the preferences are set,
 it will open up the equivalent file on a site it's been published to. You just have to supply one, or more, URL prefixes so the action 
 knows how to construct the full URL. Examples of URL prefixes are:  
<ul>
<li>External URL - http://www.company.com/v123</li>
<li>Internal URL for docs build output - http://intranet.company.com:9080/v124/index.jsp?topic=</li>
</ul> 
Clicking on this action will open up 3 web browser tabs with views on the respective webpages.
<img src="images/wff.png" />
</p>
##Checking tools
<ul>
<li>Select a number of resources (projects, folders and files) in the Package Explorer or Project Explorer.</li>
<li>Right click, and then select <b>DITA Tools</b></li>
</ul>
</p>

###Where Used Checker
<p>The selected <b>dita/jpg/gif/png files</b> files are checked to see if they are referred to by any other file in the workspace. 
Informational, Warning, and Error markers are created and may be viewed in the <a href="./problemview.html">Problem view</a>.
<ul>
<li>Informational - jpg/gif/png file is referenced by at least one other file</li>
<li>Informational - dita file is referenced by at least one file, which is a <b>ditamap</b></li>
<li>Error - dita file is referenced by at least one file, but no <b>ditamap</b>s</li>
<li>Error - file is not referenced by any other file in the workspace</li>
</ul>
</p>
<p>The default is to scan <b>ditamap</b>, <b>dita</b>, <b>htm</b> and <b>html</b> filetypes, though 
this may be configured through the Preferences page.</p> 
<p>Note: As this needs to cross-reference the entire workspace it may take some time to complete, even if you've only selected a single file to check.</p> 

###HREF Checker
<p>Selected resources are scanned and an attempt is made to resolve all <b>href</b> attributes. Check is also made that a title exists for scope="peer" references.
Informational, Warning, and Error markers are created and may be viewed in the <a href="./problemview.html">Problem view</a>.
The default is to scan <b>ditamap</b>, <b>dita</b>, <b>htm</b> and <b>html</b> filetypes, though 
this may be configured through the Preferences page. 
</p>

###CONREF Checker
<p>Selected resources are scanned and an attempt is made to resolve all <b>conref</b> attributes. 
Informational, Warning, and Error markers are created and may be viewed in the <a href="./problemview.html">Problem view</a>.
Only <b>dita</b> files are scanned. 
</p>