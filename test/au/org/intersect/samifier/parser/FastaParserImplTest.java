package au.org.intersect.samifier.parser;
import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.regex.Pattern;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class FastaParserImplTest {
    private static final String GENBANK_HEADER = ">gi|1234|gb|GENE_NAME|ABCDEFGH";
    private static final String EMBL_HEADER = ">gi|1234|emb|GENE_NAME|ABCDEFGH";
    private static final String DDBJ_HEADER = ">gi|1234|dbj|GENE_NAME|ABCDEFGH";
    private static final String REFERENCE_HEADER = ">gi|1234|ref|GENE_NAME|ABCDEFGH";    
    private static final String SWISS_PROT_HEADER = ">sp|GENE_NAME|ABCDEFGH";
    private static final String GENERAL_DB_IDENTIFIER_HEADER = ">gnl|ABCDEFGH|GENE_NAME"; 
    private static final String NCBI_HEADER = ">ref|GENE_NAME|ABCDEFGH";
    private static final String LOCAL_SEQUENCE_HEADER = ">lcl|GENE_NAME";
    private static final String WRONG_HEADER = ">this|is|wrong";
    
    
    private static final String GENE_NAME = "GENE_NAME";
    
    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void testHeaderParser() throws Exception {
        FastaParserImpl parser = new FastaParserImpl(new File("test/resources/protein_generator/contig_fasta.fa"));
        assertEquals("Proper gen name extracted from header", GENE_NAME, parser.parseHeader(GENBANK_HEADER));
        assertEquals("Proper gen name extracted from header", GENE_NAME, parser.parseHeader(EMBL_HEADER));
        assertEquals("Proper gen name extracted from header", GENE_NAME, parser.parseHeader(DDBJ_HEADER));
        assertEquals("Proper gen name extracted from header", GENE_NAME, parser.parseHeader(REFERENCE_HEADER));
        assertEquals("Proper gen name extracted from header", GENE_NAME, parser.parseHeader(SWISS_PROT_HEADER));
        assertEquals("Proper gen name extracted from header", GENE_NAME, parser.parseHeader(GENERAL_DB_IDENTIFIER_HEADER));
        assertEquals("Proper gen name extracted from header", GENE_NAME, parser.parseHeader(NCBI_HEADER));
        assertEquals("Proper gen name extracted from header", GENE_NAME, parser.parseHeader(LOCAL_SEQUENCE_HEADER));
        exception.expect(FastaParserException.class);
        exception.expectMessage(WRONG_HEADER+" is not supported FASTA header.");
        parser.parseHeader(WRONG_HEADER);
        
    }
    
    @Test
    public void testDOSEnding() throws Exception {
        FastaParserImpl parser = new FastaParserImpl(new File("test/resources/DOS-example.fa"));
        assertEquals("proper length of chromosome", 327, parser.getChromosomeLength("contig_2_875916"));
        assertEquals("proper length of chromosome", 222, parser.getChromosomeLength("contig_4_409559"));
    }

}
