Samifier
========

  Mascot (peptide) search results converted into SAM format.

Building
========

    $ ant dist

Running
=======

    $ java -jar dist/samifier.jar 
    usage: samifier -c <chromosomeDir> -g <genomeFile> -m <mappingFile> -o <outputFile> -r <searchResultsFile>
     -c <chromosomeDir>       Directory containing the chromosome files in
                              FASTA format for the given genome
     -g <genomeFile>          Genome file in gff format
     -m <mappingFile>         File mapping protein identifier to ordered locus
                              name
     -o <outputFile>          Filename to write the SAM format file to
     -r <searchResultsFile>   Mascot search results file in txt format

E.g.

    $ java -jar samifier.jar -r results.txt -c saccharomyces_cerevisiae -g saccharomyces_cerevisiae_R64-1-1_20110208.gff -m accession.txt -o test.sam
