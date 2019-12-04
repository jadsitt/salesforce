/** ========================================================================= *
 * Copyright (C)  2017, 2018 Salesforce Inc ( http://www.salesforce.com/      *
 *                            All rights reserved.                            *
 *                                                                            *
 *  @author     Stephan H. Wissel (stw) <swissel@salesforce.com>              *
 *                                       @notessensei                         *
 * @version     1.0                                                           *
 * ========================================================================== *
 *                                                                            *
 * Licensed under the  Apache License, Version 2.0  (the "License").  You may *
 * not use this file except in compliance with the License.  You may obtain a *
 * copy of the License at <http://www.apache.org/licenses/LICENSE-2.0>.       *
 *                                                                            *
 * Unless  required  by applicable  law or  agreed  to  in writing,  software *
 * distributed under the License is distributed on an  "AS IS" BASIS, WITHOUT *
 * WARRANTIES OR  CONDITIONS OF ANY KIND, either express or implied.  See the *
 * License for the  specific language  governing permissions  and limitations *
 * under the License.                                                         *
 *                                                                            *
 * ========================================================================== *
 */
package net.wissel.tool.findStrings;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import com.google.common.base.Charsets;
import com.google.common.io.ByteStreams;
import com.google.common.io.Files;

/**
 * @author swissel
 *
 */
public class StringFinder {

    public static final String DIRNAME               = "d";
    public static final String DIRNAME_LONGNAME      = "dir";
    public static final String STRINGFILE            = "s";
    public static final String STRINGFILE_LONGNAME   = "stringfile";
    public static final String OUTPUT                = "o";
    public static final String OUTPUT_LONGNAME       = "output";
    public static final String NOUNZIP               = "nz";
    public static final String NOUNZIP_LONGNAME      = "nounzip";
    public static final String REPORTFORMAT          = "r";
    public static final String REPORTFORMAT_LONGNAME = "reportformat";
    public static final String DEEPSCAN              = "x";
    public static final String DEEPSCAN_LONGNAME     = "extensivescan";

    /**
     * @param Command
     *            line provides input/output/search
     * @throws Exception
     */
    public static void main(final String[] args) throws Exception {
        final StringFinder sf = new StringFinder();
        if (sf.parseCommandLine(args)) {
            sf.run();
        }
        System.out.println("Done");

    }

    private final Options      options   = new Options();
    private final List<String> knownZips = new ArrayList<>(Arrays.asList("zip", "xlsx", "docx", "pptx"));
    private File             startDir;
    private String             stringFileName;

    private final Map<String, String>      keys           = new HashMap<>();
    private final Map<String, Set<String>> results        = new HashMap<>();
    private boolean                        extractFiles   = true;
    private String                         outputFileName = null;
    private ReportType                     reportType     = ReportType.MARKDOWN;
    private boolean                        deepScan;

    public StringFinder() {
        this.setupOptions();
    }

    public boolean parseCommandLine(final String[] args) throws FileNotFoundException {
        boolean canProceed = false;
        final CommandLineParser parser = new DefaultParser();
        CommandLine line = null;
        try {
            // parse the command line arguments
            line = parser.parse(this.options, args);
        } catch (final ParseException exp) {
            // oops, something went wrong
            System.err.println("Command line parsing failed.  Reason: " + exp.getMessage());
            System.exit(-1);
        }

        if (line != null) {
            canProceed = (line.hasOption(StringFinder.DIRNAME) && line.hasOption(StringFinder.STRINGFILE));
            if (line.hasOption(StringFinder.OUTPUT)) {
                this.outputFileName = line.getOptionValue(StringFinder.OUTPUT);
            }
            if (line.hasOption(StringFinder.NOUNZIP)) {
                this.extractFiles = false;
            }
            if (line.hasOption(StringFinder.DEEPSCAN)) {
                this.deepScan = true;
            }
            if (line.hasOption(StringFinder.REPORTFORMAT)) {
                this.setReportFormat(line.getOptionValue(StringFinder.REPORTFORMAT));
            }
        }

        if (!canProceed) {
            this.printHelp();

        } else {
            this.startDir = new File(line.getOptionValue(StringFinder.DIRNAME));
            this.stringFileName = line.getOptionValue(StringFinder.STRINGFILE);
        }

        return canProceed;
    }

    public void run() throws Exception {
        this.populateKeys();

        if (!this.startDir.isDirectory()) {
            throw new Exception("Input is not a directory");
        }

        final File[] dirs = this.startDir.listFiles();
        for (final File d : dirs) {
            this.processEntry(d, this.extractFiles, this.deepScan);
        }

        if (!this.results.isEmpty()) {
            final ReportRenderer r = this.getReportRenderer();
            r.render(this.results, this.keys, this.getOutput());
        }
    }

    /**
     * Expands a ZIP file, but only if the target directory doesn't exist
     * already
     *
     * @param the
     *            ZIP file
     * @param targetDir
     * @return
     * @throws IOException
     */
    private boolean expandFile(final File f, final File targetDir) throws IOException {
        if (targetDir.exists()) {
            return false;
        }

        final ZipInputStream zis = new ZipInputStream(new FileInputStream(f));
        ZipEntry zipEntry = zis.getNextEntry();
        while (zipEntry != null) {
            final File outFile = this.newFile(targetDir, zipEntry);

            final FileOutputStream out = new FileOutputStream(outFile);
            ByteStreams.copy(zis, out);
            out.close();
            zipEntry = zis.getNextEntry();
        }

        zis.close();
        // Unpacking worked!
        return true;
    }

    private void findKeyInFile(final File targetDirOrFile) throws IOException {
        String fileName = targetDirOrFile.getAbsolutePath().substring(this.startDir.getAbsolutePath().length()+1);
        final String source = Files.asCharSource(targetDirOrFile, Charsets.UTF_8).read().toLowerCase();
        this.keys.keySet().forEach(k -> {
            if (source.indexOf(k) > -1) {
                final Set<String> thisResult = this.results.containsKey(k) ? this.results.get(k)
                        : new HashSet<>();
                thisResult.add(fileName);
                this.results.put(k, thisResult);
            }
        });

    }

    private PrintStream getOutput() throws FileNotFoundException {
        if (this.outputFileName == null) {
            return System.out;
        }
        return new PrintStream(this.outputFileName);
    }

    private ReportRenderer getReportRenderer() {
        switch (this.reportType) {
            case JSON:
                return new ReportRendererJSON();
            case XML:
                return new ReportRendererXML();
            default: /* MD */
                return new ReportRendererMD();
        }
    }

    /**
     * Checks if a file is a ZIP file,
     *
     * @param f
     *            - the file to check
     * @param deep
     *            if true tries to open ZIPStream, if false uses common entries
     * @return
     */
    private boolean isZipFile(final File f, final boolean deep) {
        if (deep) {
            return this.isZipFileDeep(f);
        }
        final String someName = f.getName();
        final String extension = someName.substring(someName.lastIndexOf(".") + 1);
        return this.knownZips.contains(extension);
    }

    /**
     * Tests a file for being a ZIP file by actually opening the file using a
     * ZIPInputStream
     *
     * @param zipCandidate
     *            - the file we suspect to be a zip file
     * @return true/false if this is a zip file
     */
    private boolean isZipFileDeep(final File zipCandidate) {
        boolean result = false;

        FileInputStream in = null;
        ZipInputStream zis = null;

        try {
            in = new FileInputStream(zipCandidate);
            zis = new ZipInputStream(in);
            @SuppressWarnings("unused")
            final ZipEntry zipEntry = zis.getNextEntry();
            // We got here, we have a ZIP file
        } catch (final IOException e) {
            result = false;
        } finally {
            try {
                if (zis != null) {
                    zis.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (final IOException ex) {
                // Not interested in this
            }
        }

        return result;
    }

    private File newFile(final File destinationDir, final ZipEntry zipEntry) throws IOException {
        final File destFile = new File(destinationDir, zipEntry.getName());
        Files.createParentDirs(destFile);
        final String destDirPath = destinationDir.getCanonicalPath();
        final String destFilePath = destFile.getCanonicalPath();

        if (!destFilePath.startsWith(destDirPath + File.separator)) {
            throw new IOException("Entry is outside of the target dir: " + zipEntry.getName());
        }

        return destFile;
    }

    private void populateKeys() throws FileNotFoundException {
        final File keyFile = new File(this.stringFileName);
        final Scanner c = new Scanner(keyFile);
        while (c.hasNextLine()) {
            final String curLine = c.nextLine().trim();
            if (!curLine.equals("") && !curLine.startsWith("#")) {
                this.keys.put(curLine.toLowerCase(), curLine);
            }
        }
        c.close();
    }

    private void printHelp() {
        final HelpFormatter formatter = new HelpFormatter();
        formatter.setOptionComparator(null);

        formatter.printHelp(
                "java -jar findString.jar ",
                this.options);
    }

    private void processEntry(final File d, final boolean extract, final boolean deep) throws IOException {
        if (d.isDirectory()) {
            final File[] children = d.listFiles();
            for (final File f : children) {
                this.processEntry(f, extract, deep);
            }
        } else if (this.isZipFile(d, deep)) {
            if (extract) {
                final String zipName = d.getAbsolutePath();
                final String newDirName = zipName.substring(0, zipName.lastIndexOf("."));
                final File newTarget = new File(newDirName);
                if (this.expandFile(d, newTarget)) {
                    final File[] children = newTarget.listFiles();
                    for (final File f : children) {
                        this.processEntry(f, extract, deep);
                    }
                }
            }
        } else {
            // Scanning a file
            this.findKeyInFile(d);
        }

    }

    private void setReportFormat(final String optionValue) {
        final String compareString = String.valueOf(optionValue).trim().toLowerCase();
        switch (compareString) {
            case "xml":
                this.reportType = ReportType.XML;
                break;
            case "json":
                this.reportType = ReportType.JSON;
                break;

            default:
                this.reportType = ReportType.MARKDOWN;
                break;
        }

    }

    private void setupOptions() {
        this.options.addOption(Option.builder(StringFinder.DIRNAME).longOpt(StringFinder.DIRNAME_LONGNAME)
                .desc("directory with all zip files")
                .hasArg()
                .build());
        this.options.addOption(Option.builder(StringFinder.STRINGFILE).longOpt(StringFinder.STRINGFILE_LONGNAME)
                .desc("Filename with Strings to search, one per line")
                .hasArg()
                .build());

        this.options.addOption(Option.builder(StringFinder.OUTPUT).longOpt(StringFinder.OUTPUT_LONGNAME)
                .desc("Output file name for report")
                .hasArg()
                .build());

        this.options.addOption(Option.builder(StringFinder.REPORTFORMAT).longOpt(StringFinder.REPORTFORMAT_LONGNAME)
                .desc("Format for the Report: markdown, xml, json")
                .hasArg()
                .build());

        this.options.addOption(Option.builder(StringFinder.NOUNZIP).longOpt(StringFinder.NOUNZIP_LONGNAME)
                .desc("Rerun find operation on a ready unzipped structure - good for alternate finds")
                .build());

        this.options.addOption(Option.builder(StringFinder.DEEPSCAN).longOpt(StringFinder.DEEPSCAN_LONGNAME)
                .desc("Test every file for Zipped content (catches office formats too) - Warning SLOW!!!")
                .build());
    }

}
