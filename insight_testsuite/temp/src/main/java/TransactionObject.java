import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Yu-Cheng Lin on 10/28/17.
 */
public class TransactionObject {
    private String CMTE_ID;
    private String ZIPCODE;
    private String TX_DT;
    private double TX_AMT;
    private boolean skipZIP;
    private boolean skipDT;

    public TransactionObject(String line){
        String[] columns = line.split("\\|", -1);

        this.CMTE_ID = columns[0];
        this.ZIPCODE = columns[10];
        this.TX_DT = columns[13];
        String TX_AMT = columns[14];
        String OTHERID = columns[15];

        // Terminate parsing if OTHERID contains value or CMTE_ID/TX_AMT is missing.
        if(!OTHERID.equals("") || this.CMTE_ID.equals("") || TX_AMT.equals("")){
            this.skipDT = true;
            this.skipZIP = true;
            return;
        }

        // Check if ZIPCODE is missing or the length is less than 5.
        if(this.ZIPCODE.equals("") || this.ZIPCODE.length() < 5){
            this.skipZIP = true;
        }else{
            if(this.ZIPCODE.length() > 5){
                this.ZIPCODE = this.ZIPCODE.substring(0, 5);
            }
        }

        // Check if TX_DT is missing or TX_DT is in invalid format.
        if(TX_DT.equals("") || !isValidDate(TX_DT)){
            this.skipDT = true;
        }

        this.TX_AMT = Double.parseDouble(TX_AMT);
    }

    private boolean isValidDate(String dateString){
        Date date = null;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMddyyyy");
        try{
            date = simpleDateFormat.parse(dateString);
            if (!dateString.equals(simpleDateFormat.format(date))) {
                date = null;
            }
        }catch (ParseException e){
            e.printStackTrace();
        }
        return date != null;
    }

    public String getCMTE_ID(){
        return this.CMTE_ID;
    }

    public String getZIPCODE(){
        return this.ZIPCODE;
    }

    public String getTX_DT(){
        return this.TX_DT;
    }

    public double getTX_AMT(){
        return this.TX_AMT;
    }

    public boolean isSkipZIP(){
        return this.skipZIP;
    }

    public boolean isSkipDT(){
        return this.skipDT;
    }
}
