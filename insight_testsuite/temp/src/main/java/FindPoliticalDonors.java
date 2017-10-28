import java.io.*;
import java.util.*;

/**
 * Created by Yu-Cheng Lin on 10/25/17.
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


        // check input
        if(args == null || args.length != 3){
            System.out.println("Please enter the correct input format:");
            System.out.println("<Input txt file path> <Output path for zipcode file> <Output path for date file>");
            return;
        }

        for(String s : args){
            System.out.println(s);
        }

        //System.out.println("Current: " + System.getProperty("user.dir"));

        FindPoliticalDonors findPoliticalDonors = new FindPoliticalDonors();
        if(!findPoliticalDonors.setBufferedReader(args[0])){
            return;
        }
        if(!findPoliticalDonors.setOutputTXT(args[1], args[2])){
            return;
        }

        // while loop
        // Read line and parse the data, I will get an object
        // use this object to update the container and save it to file
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

    public void dateUpdate(TransactionObject transactionObject){
        if(!this.dateContainer.containsKey(transactionObject.getCMTE_ID())){
            this.dateContainer.put(transactionObject.getCMTE_ID(), new TreeMap<>(Comparator.naturalOrder()));
        }
        if(!this.dateContainer.get(transactionObject.getCMTE_ID()).containsKey(transactionObject.getTX_DT())){
            this.dateContainer.get(transactionObject.getCMTE_ID()).put(transactionObject.getTX_DT(), new TransactionManager());
        }

        this.dateContainer.get(transactionObject.getCMTE_ID()).get(transactionObject.getTX_DT()).deposit(transactionObject.getTX_AMT());
    }

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

    private BufferedReader getBufferedReader(){
        return bufferedReader;
    }

    private boolean setBufferedReader(String inputPath){
        try{
            this.bufferedReader = new BufferedReader(new FileReader(inputPath));
        }catch (FileNotFoundException e){
            System.out.println("[ERROR] File not found.");
            return false;
        }
        return true;
    }

    private boolean setOutputTXT(String zipcodePath, String datePath){
        if(zipcodePath == null || datePath == null){
            return false;
        }
        try {
            this.zipcodeWriter = new FileWriter(zipcodePath);
            this.dateWriter = new FileWriter(datePath);
        }catch (IOException e){
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

    public boolean writeToFile(String string, boolean isZipCodeFile){
        FileWriter fileWriter = isZipCodeFile ? this.zipcodeWriter : this.dateWriter;

        if(fileWriter == null){
            return false;
        }

        try{
            fileWriter.append(string);
            if(isZipCodeFile){
                fileWriter.flush();
            }
        }catch (IOException e){
            e.printStackTrace();
            return false;
        }
        return true;
    }

}
