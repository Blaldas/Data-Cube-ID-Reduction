package invertedIndex;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InvertedIndex {

    Map<String, List<Integer>> map;

    public InvertedIndex(String text) {
        map = new HashMap<>();
        mapText(text);
    }

    /**
     *
     * @param text -> String with the text to map
     *
     * This method recieves a text and records it's words and their position into an HashMap
     */
    private void mapText(String text) {
        //obtains the list of words
        String[] wordList = text.split(" ");

        //indicates the sentence being tested
        int sentenceCounter = 1;

        //for each Word int the list
        for (String str : wordList) {

            //if the word is not yest mapped
            if (!map.containsKey(str)) {
                //adds the new word and creates a list for it, saving this sentence position in the list
                map.put(str.replace(".", ""), new ArrayList<>());
                map.get(str.replace(".", "")).add(sentenceCounter);
            }
            //if the word is already mapped
            else
                //adds this line to the list of lines related to the word
                map.get(str.replace(".", "")).add(sentenceCounter);

            //if the word contains a character that indicates being the end of the sentence
            if (str.contains("."))
                //increments the sentence counter
                sentenceCounter++;

           // System.out.println(str + " " + map.get(str.replace(".", "")).get(0) );
        }
    }

    /**
     * This method shows word by word the sentences' number where they appear
     */
    public void showInvertedIndex(){
        List<String> words = new ArrayList<>(map.keySet());

        for(String str : words){
            List<Integer> posList = map.get(str);
            String positionListString = new String();
            for(int lineNumber : posList)
                //positionListString.add(lineNumber).append("\t");
                positionListString += lineNumber + " ";

            System.out.println(str + "\t" + positionListString);
        }
    }

    /**
     *
     * @param word -> word to seach in the text
     * @return null if the word doesn't exist in the text or an int array with the list of the index of the sentences where the word appeared
     */
    public int[] searchWord(String word){
        if(!map.containsKey(word))
            return null;
        
        int counter = 0;
        int posList[] = new int[map.get(word).size()];
        for(int pos : map.get(word))
        {
            posList[counter] = pos;
            counter++;
        }

        return posList;
    }

    public static void main(String [] args){

        String str = "olá, o meu nome é João. O João é alguem parvo, porém o João é uma pessoa fixe com nome de João";

        InvertedIndex iv = new InvertedIndex(str);

        iv.showInvertedIndex();
        System.out.println("procura palavra:");
        int [] listapos =iv.searchWord("parvo,");

        String resultado = "parvo,\t";
        for (int listapo : listapos)
            resultado += listapo + " ";

        System.out.println(resultado);
    }




}
