package au.org.intersect.samifier.generator;

import static org.junit.Assert.fail;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import au.org.intersect.samifier.domain.ProteinLocation;

public class VirtualProteinMascotLocationGeneratorUnitTest {

    private VirtualProteinMascotLocationGenerator generator;
    //test with start on begin of peptide
    @Before
    public void setUp() {
        

    }
    @Test
    public void testForwardWithProperStart(){
        ProteinLocation expectedLocation = new ProteinLocation("q0", 36, 105, "+", "0");
        List<ProteinLocation> expectedArray = new ArrayList<ProteinLocation>();
        expectedArray.add(expectedLocation);
        String [] mascotFiles = {"test/resources/merger/test_mascot_search_results.txt"};
        File genomeFile = new File("test/resources/merger/virtual_protein_proper_start.gff");
        File chromosomeDir = new File("test/resources/merger/");
        File translationTableFile = new File("test/resources/merger/bacterial_translation_table.txt");
        generator = new VirtualProteinMascotLocationGenerator(mascotFiles, translationTableFile, genomeFile, chromosomeDir);
        
        try {
            List<ProteinLocation> locations = generator.generateLocations();
            assertEquals(expectedArray.size(), locations.size());
            assertEquals(expectedArray.get(0), locations.get(0));
           
        } catch(Exception e){
            fail("Unexpected exception: " + e.getMessage());
            e.printStackTrace();
        }
    }
    //test with no start and no end for start
    @Test
    public void testForwardWithNoStart(){
        ProteinLocation expectedLocation = new ProteinLocation("q0", 0, 141, "+", "0");
        List<ProteinLocation> expectedArray = new ArrayList<ProteinLocation>();
        expectedArray.add(expectedLocation);
        String [] mascotFiles = {"test/resources/merger/test_mascot_search_results.txt"};
        File genomeFile = new File("test/resources/merger/virtual_protein.gff");
        File chromosomeDir = new File("test/resources/merger/");
        File translationTableFile = new File("test/resources/merger/bacterial_translation_table.txt");
        generator = new VirtualProteinMascotLocationGenerator(mascotFiles, translationTableFile, genomeFile, chromosomeDir);
        
        try {
            List<ProteinLocation> locations = generator.generateLocations();
            assertEquals(expectedArray.size(), locations.size());
            assertEquals(expectedArray.get(0), locations.get(0));
           
        } catch(Exception e){
            fail("Unexpected exception: " + e.getMessage());
            e.printStackTrace();
        }
        
        
    }
    //test with no end for start
    @Test
    public void testForwardNoEndForStart(){
        ProteinLocation expectedLocation = new ProteinLocation("q0", 36, 105, "+", "0");
        List<ProteinLocation> expectedArray = new ArrayList<ProteinLocation>();
        expectedArray.add(expectedLocation);
        String [] mascotFiles = {"test/resources/merger/test_mascot_search_results.txt"};
        File genomeFile = new File("test/resources/merger/virtual_protein_no_end_for_start.gff");
        File chromosomeDir = new File("test/resources/merger/");
        File translationTableFile = new File("test/resources/merger/bacterial_translation_table.txt");
        generator = new VirtualProteinMascotLocationGenerator(mascotFiles, translationTableFile, genomeFile, chromosomeDir);
        
        try {
            List<ProteinLocation> locations = generator.generateLocations();
            assertEquals(expectedArray.size(), locations.size());
            assertEquals(expectedArray.get(0), locations.get(0));
           
        } catch(Exception e){
            fail("Unexpected exception: " + e.getMessage());
            e.printStackTrace();
        }  
    }
    //test with no start -> end is in place
    @Test
    public void testForwardNoStartProperEnd(){
        ProteinLocation expectedLocation = new ProteinLocation("q0", 33, 108, "+", "0");
        List<ProteinLocation> expectedArray = new ArrayList<ProteinLocation>();
        expectedArray.add(expectedLocation);
        String [] mascotFiles = {"test/resources/merger/test_mascot_search_results.txt"};
        File genomeFile = new File("test/resources/merger/virtual_protein_no_start_proper_end.gff");
        File chromosomeDir = new File("test/resources/merger/");
        File translationTableFile = new File("test/resources/merger/bacterial_translation_table.txt");
        generator = new VirtualProteinMascotLocationGenerator(mascotFiles, translationTableFile, genomeFile, chromosomeDir);
        
        try {
            List<ProteinLocation> locations = generator.generateLocations();
            assertEquals(expectedArray.size(), locations.size());
            assertEquals(expectedArray.get(0), locations.get(0));
           
        } catch(Exception e){
            fail("Unexpected exception: " + e.getMessage());
            e.printStackTrace();
        }  
    }
    //test with no markers
    @Test
    public void testForwardNoMarkers(){
        ProteinLocation expectedLocation = new ProteinLocation("q0", 0, 141, "+", "0");
        List<ProteinLocation> expectedArray = new ArrayList<ProteinLocation>();
        expectedArray.add(expectedLocation);
        String [] mascotFiles = {"test/resources/merger/test_mascot_search_results.txt"};
        File genomeFile = new File("test/resources/merger/virtual_protein_no_markers.gff");
        File chromosomeDir = new File("test/resources/merger/");
        File translationTableFile = new File("test/resources/merger/bacterial_translation_table.txt");
        generator = new VirtualProteinMascotLocationGenerator(mascotFiles, translationTableFile, genomeFile, chromosomeDir);
        
        try {
            List<ProteinLocation> locations = generator.generateLocations();
            assertEquals(expectedArray.size(), locations.size());
            assertEquals(expectedArray.get(0), locations.get(0));
           
        } catch(Exception e){
            fail("Unexpected exception: " + e.getMessage());
            e.printStackTrace();
        }  
    }
}
