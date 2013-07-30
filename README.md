Samifier
========

  Tools to enable a nexus between proteomic and genomic analysis.
  See https://github.com/IntersectAustralia/ap11_samifier/wiki for building,
  deployment and user guide. For scientific background, see
  http://intersectaustralia.github.com/ap11/ for details.
  
  The code is licensed under the GNU GPL v3 license - see LICENSE.txt

  The documentation (contained in the Github wiki and this README) is licensed under [Creative Commons Attribution-Share Alike](http://creativecommons.org/licenses/by-sa/2.5/au/)


Building
========

    $ ant dist

This builds 4 command line toold and 2 helpers (undocumented). The following
describes briefly each tool parameters. We encourage users to download the
user guide and read it.

Samifier
========

Converts a search result from the Mascot protein search engine (or compatible)
into SAM format, so it can be displayed in a genomics viewer.

    $ java -jar dist/samifier.jar 
    usage: samifier  -r <searchResultsFile> -c <chromosomeDir> -g <genomeFile>   
           -m <mappingFile> -o <outputFile> [-l <logFile>] [-b <bedFile>]
           [-s <Confidence Score thresold>]
     -r <searchResultsFile>           Mascot search results file in txt format           
     -c <chromosomeDir>               Directory containing the chromosome
                                      files in FASTA format for the given
                                      genome
     -m <mappingFile>                 File mapping protein identifier to
                                      ordered locus name                                      
     -g <genomeFile>                  Genome file in gff format
     -o <outputFile>                  Filename to write the SAM format file to     
     -l <logFile>                     Filename to write the log into
     -b <bedFile>                     Filename to write IGV regions of
                                      interest (BED) file to
     -s <Confidence Score thresold>   Minimum confidence score for peptides to
                                      be included

E.g.

    $ java -jar samifier.jar -r results.txt -c saccharomyces_cerevisiae -g saccharomyces_cerevisiae_R64-1-1_20110208.gff -m accession.txt -o test.sam

Results analyser
================

Similar to *samifier* but instead of generating a SAM file, it generates a column with
found peptides. This table that can be queried using SQL to extract a number of reports.


    $ java -jar dist/samifier.jar 
    usage: result_analyser -c <chromosomeDir> -g <genomeFile> -m <mappingFile>
           -o <outputFile> -r <searchResultsFile> [-rep <reportId>] [-replist
           <reportList>] [-sql <sqlQuery>]
     -c <chromosomeDir>       Directory containing the chromosome files in
                              FASTA format for the given genome
     -g <genomeFile>          Genome file in gff format
     -m <mappingFile>         File mapping protein identifier to ordered locus
                              name
     -o <outputFile>          Filename to write the SAM format file to
     -r <searchResultsFile>   Mascot search results file in txt format
     -rep <reportId>          Access a built in report query
     -replist <reportList>    A file containing all the pre-built SQL queries
     -sql <sqlQuery>          Filters the result through the use of a SQL
                              statement to the output file


Protein generator
=================

Having as input a genome, it generates a FASTA file with "proteins" suitable to be
used as a database in Mascot. It operates in two modes, using Glimmer predicted
genes, or simply by splitting the genome into overlaping regions of given length.

Both _Predicted Protein Generator_ (Glimmer gene prediction) and _Virtual Protein 
Generator_ (six-frame translation) are implemented under the command line tool 
‘protein_generator.jar’ as both tools shares similar input files. However, the 
Predicted Protein Generator can be accessed via command line parameter 
‘-g <Glimmer File>’ to identify the input Glimmer prediction file. The Virtual 
Protein Generator is accessed using the command line parameter ‘-i <Split Interval>’, 
which indicates the length of the overlapping virtual proteins.   
 
    $ java -jar dist/protein_generator.jar
    usage: protein_generator -d <Database Name> -f <Genome File> [-g <Glimmer
           File>] [-i <Split Interval>] -o <Output File> [-p <GFF File>] [-q
           <Accession File>] [-t <Translation Table File>]
     -d <Database Name>            Database name
     -f <Genome File>              Genome file in FASTA format
     -g <Glimmer File>             Glimmer txt file. Can't be used with the -i
                                   option.
     -i <Split Interval>           Size of the intervals (number of codons)
                                   into which the genome will be split. Can't
                                   be used with the -g option.
     -o <Output File>              Filename to write the FASTA format file to
     -p <GFF File>                 Filename to write the GFF file to
     -q <Accession File>           Filename to write the accession file to
     -t <Translation Table File>   File containing a mapping of codons to
                                   amino acids, in the format used by NCBI.

Virtual protein merger
======================

When using the database generated by the Protein Generator in interval mode the
generated "proteins" are most likely wrong. However, Mascot still uses them and
can report back found peptides in such sequences. The Virtual Protein Merger
takes such search result against a "virtual protein" database and tries to rebuild
the intervals by searching for stop and start codons in the sequence.


    $ java -jar dist/virtual_protein_merger.jar
    usage: virtual_protein_merger -c <chromosomeDir> -g <genomeFile> -o
           <outputFile> -r <searchResultsFile> [-t <Translation Table File>]
     -c <chromosomeDir>            Directory containing the chromosome files
                                   in FASTA format for the given genome
     -g <genomeFile>               Genome file in gff format
     -o <outputFile>               Filename to write the gff file to
     -r <searchResultsFile>        Mascot search results file in txt format
     -t <Translation Table File>   File containing a mapping of codons to
                                   amino acids, in the format used by NCBI.

