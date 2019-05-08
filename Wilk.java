import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class Wilk {

    private List<List<String>> recordsCsv = new ArrayList<>();
    private double amountOfFuel;
    private List<Double> tapFlowList = new ArrayList<Double>();
    private List<Double> temperatureList = new ArrayList<Double>();
    private static Wilk INSTANCE;

    private Wilk(){}

    public static Wilk getINSTANCE(){
        if(INSTANCE == null){
            INSTANCE = new Wilk();
        }
        return INSTANCE;
    }

    public void readCsv () throws IncorrectInputData{

        try (BufferedReader br = new BufferedReader(new InputStreamReader(System.in))) {
            String line;
            boolean checkFirstLine = false;
            while ((line = br.readLine()) != null) {
                String[] values = line.split("\\s+");
                if(Arrays.asList(values).size() > 2){
                    throw new IncorrectInputData("klops");
                }
                else if(!checkFirstLine && Arrays.asList(values).size() != 1){
                    throw new IncorrectInputData("klops");
                }
                recordsCsv.add(Arrays.asList(values));
                checkFirstLine = true;
            }
        } catch (IOException e) {
            System.out.println("klops");
            System.exit(0);
        }
    }

    public void checkDataIsOk() throws IncorrectInputData{

        if(tapFlowList.size() > 1_000_000 ){
            throw new IncorrectInputData("klops");
        }
        if(amountOfFuel > 100_000_000){
            throw new IncorrectInputData("klops");
        }
        if(tapFlowList.size() != temperatureList.size()){
            throw new IncorrectInputData("klops");
        }
        if(temperatureOfWater() > 90 && temperatureOfWater() < 1){
            throw new IncorrectInputData("klops");
        }
    }

    private static double round(double value, int places) {

        if (places < 0) throw new IllegalArgumentException();
        BigDecimal bd = new BigDecimal(Double.toString(value));
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    public void setParameters(){

        try {
            if(temperatureList.isEmpty() && tapFlowList.isEmpty()) {
                boolean fuel = false;
                for (List<String> csv : recordsCsv) {
                    if (!csv.isEmpty()) {
                        if(fuel == true){
                            tapFlowList.add(round(Double.parseDouble(csv.get(0)),5));
                        }
                        else{
                            amountOfFuel = round(Double.parseDouble(csv.get(0)),5);
                        }
                        for (int i = 1; i < csv.size(); i++) {
                            temperatureList.add(round(Double.parseDouble(csv.get(i)),5));
                        }
                    }
                    fuel=true;
                }
            }
        }
        catch(IllegalArgumentException e){
            System.out.println("klops");
            System.exit(0);
        }
    }

    public double fillTime(){

        double time = 0, sum = 0;
        for(double t : tapFlowList){
            sum += t/1000/60;
        }
        time = amountOfFuel / sum;
        return time;
    }

    public double temperatureOfWater(){

        double temperature = 0, sum = 0, sum_2 = 0;
        for (int i = 0; i < temperatureList.size(); i++){
            sum += temperatureList.get(i) * (tapFlowList.get(i) * fillTime());
            sum_2 += (tapFlowList.get(i) * fillTime());
        }
        temperature = sum/sum_2;
        return temperature;
    }

    public List<List<String>> getRecords() {
        return recordsCsv;
    }

    public List<Double> getTapFlowList() {
        return tapFlowList;
    }

    public List<Double> getTemperatureList() {
        return temperatureList;
    }

    public double getAmountOfFuel() {
        return amountOfFuel;
    }

    public void setAmountOfFuel(float amountOfFuel) {
        this.amountOfFuel = amountOfFuel;
    }

    @Override
    public String toString() {
        return String.format("%.5f \n%.5f", fillTime(), temperatureOfWater());
    }


    public static void main(String[] args) throws Exception{

        Wilk wilk = Wilk.getINSTANCE();

        try {
            wilk.readCsv();
            wilk.setParameters();
            wilk.checkDataIsOk();
        }
        catch (IncorrectInputData ex){
            System.out.println("klops");
            System.exit(0);
        }
        System.out.println(wilk);
    }
}

class IncorrectInputData extends Exception {

    public IncorrectInputData(String description){
        super(description);
    }
}

