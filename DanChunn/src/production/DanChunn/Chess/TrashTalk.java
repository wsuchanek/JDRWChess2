package production.DanChunn.Chess;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Pix on 4/18/2017.
 */
public class TrashTalk {
    public ArrayList<String> trashTalk;

    public TrashTalk(){
        trashTalk = new ArrayList<String>();
    }

    public void fileToArrayList(String filename) {
        //return new ArrayList<String>();

        BufferedReader br = null;
        FileReader fr = null;


        try {

            fr = new FileReader(filename);
            br = new BufferedReader(fr);

            String sCurrentLine;

            br = new BufferedReader(new FileReader(filename));

            while ((sCurrentLine = br.readLine()) != null) {
                //System.out.println(sCurrentLine);
                trashTalk.add(sCurrentLine);
            }
            /*
            Random r = new Random();
            int ridx = r.nextInt(trashTalk.size());

            System.out.println(trashTalk.get(ridx));
            */


        } catch (IOException e) {

            e.printStackTrace();

        } finally {

            try {

                if (br != null)
                    br.close();

                if (fr != null)
                    fr.close();

            } catch (IOException ex) {

                ex.printStackTrace();

            }

        }
        //return trashTalk;
    }

    public String randTrashTalk (){
        Random r = new Random();
        int ridx = r.nextInt(trashTalk.size());

        return (trashTalk.get(ridx));

    }
}
