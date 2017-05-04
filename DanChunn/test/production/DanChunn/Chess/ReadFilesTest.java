package production.DanChunn.Chess;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;

/**
 * Created by Pix on 4/18/2017.
 */
public class ReadFilesTest {

    @Test
    public void fileToArrayListTest(){
        String testFilename = "read.txt";
        TrashTalk myT = new TrashTalk();
        myT.fileToArrayList(testFilename);
        ArrayList<String> testList = myT.trashTalk;
        String TrashString = myT.randTrashTalk();

        Assert.assertEquals(8, testList.size());
        Assert.assertEquals("You have no skills", testList.get(0));
        //Assert.assertEquals("You have no skills 2", testList.get(0));
        Assert.assertEquals("Thats a silly move", testList.get(1));
        Assert.assertEquals("What were you thinking?", testList.get(2));
        Assert.assertEquals("Do you know how to play this game?", testList.get(3));
        Assert.assertEquals("Thats what noobs do", testList.get(4));
        Assert.assertEquals("You are playing like a 10 year old", testList.get(5));
        Assert.assertEquals("You play for fun, I play to win", testList.get(6));
        Assert.assertEquals("My skills > Your skills", testList.get(7));
    }



}