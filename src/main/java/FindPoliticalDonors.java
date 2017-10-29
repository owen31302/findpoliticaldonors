import java.io.*;
import java.util.*;

/**
 * Created by Yu-Cheng Lin on 10/25/17.
 *
 * FindPoliticalDonors is the main logic program.
 * It divided into two parts: (1) input argument part, (2) maintaining data pool.
 * (1)
 *  It will take the input argument, read files, loop over the input files, and output the result in txt.
 * (2)
 *  It will hold the ZIPCODE container in Map of Map of TransactionManager structure,
 *  so that we can identify the recipient in the specified zipcode in O(1) time.
 *  It will also hold the TX_DT container in the same structure but in different implementation.
 *  It uses TreeMap to make the recipient name in alphabetical order, and chronologically by date.
 *  When we insert one row in the TX_DT container, it takes O(logN) time.
 *  When we save the TX_DT container to the txt file, it take O(N) time without sorting.
 */
public class FindPoliticalDonors {

    private BufferedReader bufferedReader;
    private FileWriter zipcodeWriter;
    private FileWriter dateWriter;
    private Map<String, Map<String, TransactionManager>> zipcodeContainer;
    private Map<String, Map<String, TransactionManager>> dateContainer;

    public FindPoliticalDonors(){
        zipcodeContainer = new HashMap<>();
        dateContainer = new TreeMap<>(Comparator.naturalOrder());
    }

    public static void main(String[] args){

        // check input argument
        if(args == null || args.length != 3){
            System.out.println("Please enter the correct input format:");
            System.out.println("<Input txt file path> <Output path for zipcode file> <Output path for date file>");
            return;
        }

        FindPoliticalDonors findPoliticalDonors = new FindPoliticalDonors();
        if(!findPoliticalDonors.setBufferedReader(args[0])){
            return;
        }
        if(!findPoliticalDonors.setOutputTXT(args[1], args[2])){
            return;
        }

        // while loop
        // Read line and parse the data, and it will return a transaction object.
        // Use this object to update the containers and save it to file.
        String line;
        try{

            while ((line = findPoliticalDonors.getBufferedReader().readLine()) != null){
                TransactionObject transactionObject = new TransactionObject(line);

                // update the zipcode part
                if(!transactionObject.isSkipZIP()){
                    findPoliticalDonors.zipcodeUpdate(transactionObject);
                }

                // update the date part
                if(!transactionObject.isSkipDT()){
                    findPoliticalDonors.dateUpdate(transactionObject);
                }
            }
            findPoliticalDonors.writeDateToFile();
        }catch (IOException e){
            e.printStackTrace();
        }
        findPoliticalDonors.close();
        System.out.println("Program finished.");
    }

    /**
     * zipcodeUpdate will take TransactionObject as input, update the ZIPCODE container,
     * and save the result to the file in each input TransactionObject.
     * @param transactionObject
     */
    public void zipcodeUpdate(TransactionObject transactionObject){
        if(!this.zipcodeContainer.containsKey(transactionObject.getCMTE_ID())){
            this.zipcodeContainer.put(transactionObject.getCMTE_ID(), new HashMap<>());
        }
        if(!this.zipcodeContainer.get(transactionObject.getCMTE_ID()).containsKey(transactionObject.getZIPCODE())){
            this.zipcodeContainer.get(transactionObject.getCMTE_ID()).put(transactionObject.getZIPCODE(), new TransactionManager());
        }

        this.zipcodeContainer.get(transactionObject.getCMTE_ID()).get(transactionObject.getZIPCODE()).deposit(transactionObject.getTX_AMT());
        String result = transactionObject.getCMTE_ID() + "|" + transactionObject.getZIPCODE() + "|" +
                this.zipcodeContainer.get(transactionObject.getCMTE_ID()).get(transactionObject.getZIPCODE()).getMedian() + "|" +
                this.zipcodeContainer.get(transactionObject.getCMTE_ID()).get(transactionObject.getZIPCODE()).getTotalTX() + "|" +
                this.zipcodeContainer.get(transactionObject.getCMTE_ID()).get(transactionObject.getZIPCODE()).getTotalAMT() + "\n";
        writeToFile(result, true);
    }

    /**
     * dateUpdate will take TransactionObject as input, update the TX_DT container,
     * but it won't save the result to file at each input.
      * @param transactionObject
     */
    public void dateUpdate(TransactionObject transactionObject){
        if(!this.dateContainer.containsKey(transactionObject.getCMTE_ID())){
            this.dateContainer.put(transactionObject.getCMTE_ID(), new TreeMap<String , TransactionManager>(
                    (date1, date2) -> {
                        int result = date1.substring(4, 8).compareTo(date2.substring(4,8));
                        if(result == 0){
                            return -date1.substring(0, 4).compareTo(date2.substring(0, 4));
                        }
                        return -result;
                    }
                    )
            );
        }
        if(!this.dateContainer.get(transactionObject.getCMTE_ID()).containsKey(transactionObject.getTX_DT())){
            this.dateContainer.get(transactionObject.getCMTE_ID()).put(transactionObject.getTX_DT(), new TransactionManager());
        }

        this.dateContainer.get(transactionObject.getCMTE_ID()).get(transactionObject.getTX_DT()).deposit(transactionObject.getTX_AMT());
    }

    /**
     * writeDateToFile will take all the data in TX_DT container,
     * and write the result to the file once.
     * @return
     */
    public boolean writeDateToFile(){
        if(this.dateContainer == null || this.dateWriter == null){
            return false;
        }

        StringBuilder sb = new StringBuilder();
        for(Map.Entry<String, Map<String, TransactionManager>> nameEntry : this.dateContainer.entrySet()){
            for(Map.Entry<String, TransactionManager> dateEntry : nameEntry.getValue().entrySet()){
                sb.append(nameEntry.getKey() + "|" + dateEntry.getKey() + "|" + dateEntry.getValue().getMedian() + "|" +
                    dateEntry.getValue().getTotalTX() + "|" + dateEntry.getValue().getTotalAMT() + "\n"
                );
            }
        }

        return writeToFile(sb.toString(), false);
    }

    /**
     * writeToFile will write to different file based on isZipCodeFile.
     * @param string
     * @param isZipCodeFile
     * @return
     */
    public boolean writeToFile(String string, boolean isZipCodeFile){
        FileWriter fileWriter = isZipCodeFile ? this.zipcodeWriter : this.dateWriter;

        if(fileWriter == null){
            return false;
        }

        try{
            fileWriter.append(string);
            fileWriter.flush();
        }catch (IOException e){
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public BufferedReader getBufferedReader(){
        return bufferedReader;
    }

    public boolean setBufferedReader(String inputPath){
        try{
            this.bufferedReader = new BufferedReader(new FileReader(inputPath));
        }catch (FileNotFoundException e){
            System.out.println("[ERROR] File not found.");
            return false;
        }
        return true;
    }

    public boolean setOutputTXT(String zipcodePath, String datePath){
        if(zipcodePath == null || datePath == null){
            return false;
        }
        try {
            this.zipcodeWriter = new FileWriter(zipcodePath);
            this.dateWriter = new FileWriter(datePath);
        }catch (IOException e){
            System.out.println("[ERROR] Invalid path.");
            return false;
        }
        return true;
    }

    public boolean close(){
        try{
            if(this.zipcodeWriter != null){
                this.zipcodeWriter.close();
            }
            if(this.dateWriter != null){
                this.dateWriter.close();
            }
            if(this.bufferedReader != null){
                this.bufferedReader.close();
            }
        }catch (IOException e){
            e.printStackTrace();
            return false;
        }
        return true;
    }

}
