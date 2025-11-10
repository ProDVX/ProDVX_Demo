package android_serialport_api;

import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class PledDataUtil {
    private static final String TAG = "PledDataUtil";

    private static final String[] _10PD_RM = {"1-1.6:1.0","1-1.2:1.0","1-1.3:1.0"};
    private static final String[] _10PD_RS = {"1-1.1:1.0","1-1.2:1.0","1-1.4:1.0"};
    private static final String[] _10P_RM = {"1-1.2:1.0","1-1.5:1.0","1-1.7:1.0"};

    private static final String[] _10P_RS = {"2-1:1.0","4-1.3:1.0","4-1.2:1.0"};
    private static final String[] _10P_RS_1 = {"2-1:1.0","3-1.3:1.0","3-1.2:1.0"};
    private static final String[] _10P_RS_2 = {"2-1:1.0","5-1.3:1.0","5-1.2:1.0"};
    private static final String[] _10P_RS_3 = {"2-1:1.0","7-1.3:1.0","7-1.2:1.0"};
    private static final String[] _12P_RM = {"0-0.0:0.0","0-0.0:0.0","1-1.7:1.0"};

    private static final String[] _12P_RS_0 = {"3-1.3:1.0","2-1:1.0","3-1.2:1.0"};
    private static final String[] _12P_RS_1 = {"5-1.3:1.0","2-1:1.0","5-1.2:1.0"};
    private static final String[] _12P_RS_2 = {"7-1.3:1.0","2-1:1.0","7-1.2:1.0"};
    private static final String[] _12P_RS_3 = {"2-1:1.0","3-1.3:1.0","3-1.2:1.0"};
    private static final String[] _12P_RS_4 = {"2-1:1.0","5-1.3:1.0","5-1.2:1.0"};
    private static final String[] _12P_RS_5 = {"2-1:1.0","7-1.3:1.0","7-1.2:1.0"};
    private static final String[] _15N_RM = {"1-1.1:1.0","1-1.4.1:1.0","1-1.6:1.0"};

    private static final String[] _15N_RS = {"1-1.2:1.0","5-1.5:1.0","5-1.2:1.0"};
    private static final String[] _15N_RS_1 = {"1-1.2:1.0","3-1.5:1.0","3-1.2:1.0"};
    private static final String[] _15N_RS_2 = {"1-1.2:1.0","7-1.5:1.0","7-1.2:1.0"};
    private static final String[] _15N_RS_3 = {"1-1.2:1.0","5-1.5:1.0","5-1.6:1.0"};
    private static final String[] _22P_RM = {"1-1.1:1.0","2-1.1:1.0","1-1.6:1.0"};

    private static final String[] _22P_RS = {"1-1.1:1.0","0-0.0:0.0","3-1.7:1.0"};
    private static final String[] _22P_RS_1 = {"1-1.1:1.0","0-0.0:0.0","5-1.7:1.0"};
    private static final String[] _22P_RS_2 = {"1-1.1:1.0","0-0.0:0.0","7-1.7:1.0"};

    private static final String[] _32PB_RS = {"1-1.1:1.0","0-0.0:0.0","3-1.7:1.0"};
    private static final String[] _32PB_RS_1 = {"1-1.1:1.0","0-0.0:0.0","5-1.7:1.0"};
    private static final String[] _32PB_RS_2 = {"1-1.1:1.0","0-0.0:0.0","7-1.7:1.0"};
    private static final String[] _32PB_RM = {"3-1:1.0","0-0.0:0.0","1-1.4.2:1.0"};


    private static String currentLeftPath;  //current left path
    private static String currentRightPaht; //current right path
    private static String currentBottomPath;//current bottom path
    private static HashMap<String, String> allPaths = new HashMap<>();  //store all paths of current device
    //private static HashSet<String> allPathMap = new HashSet<>();
    //private static HashSet<String> allPathMap_2 = new HashSet<>();
    public static String leftUSB;
    public static String rightUSB;
    public static String bottomUSB;

    private static ArrayList<String[]> allDevices = new ArrayList<>();  //Information for all devices containing three interfaces
    private static ArrayList<String[]> allDevicesWithTwo = new ArrayList<>(); //Information for all devices containing two interfaces
    private static ArrayList<String[]> allDevicesWithOne = new ArrayList<>(); //Information for all devices containing one interface

    // initialize data
    static {
        //Currently, the path only includes devices with A8.1, A9, and A11
        //Add devices with three interfaces to 'allDevices'
        addDevice(allDevices,_10P_RM,_10P_RS,_10PD_RM,_10PD_RS,_12P_RS_0,
                _22P_RM,_10P_RS_1,_10P_RS_2,_10P_RS_3,
                _15N_RM,_10P_RS,_12P_RS_2,_12P_RS_3,_12P_RS_4,_12P_RS_5,
                _12P_RS_1,_15N_RS,_15N_RS_1,_15N_RS_2,_15N_RS_3);
        //Add devices with only two interfaces to 'addDevicesWithTwo'
        addDevice(allDevicesWithTwo,_22P_RS,_22P_RS_1,_22P_RS_2,
                _32PB_RS,_32PB_RS_1,_32PB_RS_2,_32PB_RM);
        //Add devices with only one interface to 'addDevicesWithOne'
        addDevice(allDevicesWithOne,_12P_RM);
    }

    private static void addDevice(ArrayList<String[]> deviceInfo, String[] ...devices){
        deviceInfo.addAll(Arrays.asList(devices));
    }

    //Add the obtained path to allPaths
    public static void addPath(String path, String deviceName, int index){

        allPaths.put(path,deviceName);
        //allPathMap.add(path+"to"+index);
    }

    //judging current device's path base on allPaths value
    public static boolean judgeDeviceInfo(){

        if (allPaths.size()==3){
            if (currentBottomPath!=null && currentRightPaht!=null && currentLeftPath!=null){
                return false;
            }
            String[] paths = new String[3];
            int index = 0;
            Iterator<Map.Entry<String, String>> it = allPaths.entrySet().iterator();
            while (it.hasNext()){
                Map.Entry<String, String> value = it.next();
                paths[index++] = value.getKey();
            }

            for (String[] allDevice : allDevices) {
                if (
                        Arrays.toString(allDevice).contains(paths[0])
                        && ( Arrays.toString(allDevice).contains(paths[1]))
                        && (Arrays.toString(allDevice).contains(paths[2])/* || paths[2].contains(Arrays.toString(allDevice))*/)
                ){
                    currentLeftPath = allDevice[0];
                    currentRightPaht = allDevice[1];
                    currentBottomPath = allDevice[2];
                    leftUSB = allPaths.get(currentLeftPath);
                    rightUSB = allPaths.get(currentRightPaht);
                    bottomUSB = allPaths.get(currentBottomPath);
                }
            }
            return currentBottomPath != null && currentRightPaht != null && currentLeftPath != null;
        }
        else if (allPaths.size() == 2){
            if (currentBottomPath!=null && currentLeftPath!=null
            || (currentRightPaht!=null && currentLeftPath!=null)
            || (currentBottomPath!=null && currentRightPaht!=null)
            ){
                return false;
            }
            String[] paths = new String[2];
            int index = 0;
            for (Map.Entry<String, String> value : allPaths.entrySet()) {
                paths[index++] = value.getKey();
            }
            for (String[] allDevice : allDevicesWithTwo) {
                if (Arrays.toString(allDevice).contains(paths[0])
                        && Arrays.toString(allDevice).contains(paths[1])){
                    Log.d(TAG, Arrays.toString(allDevice));
                    currentLeftPath = allDevice[0];
                    //currentRightPaht = allDevice[1];
                    currentBottomPath = allDevice[2];
                    leftUSB = allPaths.get(currentLeftPath);
                    //rightUSB = allPaths.get(currentRightPaht);
                    bottomUSB = allPaths.get(currentBottomPath);
                }
            }
            if (currentBottomPath != null  && currentLeftPath != null)
                return true;
            else
                for (String[] device: allDevices){
                    if (Arrays.toString(device).contains(paths[0])
                            && Arrays.toString(device).contains(paths[1])){
                        Log.d(TAG, Arrays.toString(device));
                        if (allPaths.containsKey(device[0])){
                            currentLeftPath = device[0];
                            leftUSB = allPaths.get(currentLeftPath);
                        }
                        if (allPaths.containsKey(device[1])){
                            currentRightPaht = device[1];
                            rightUSB= allPaths.get(currentRightPaht);
                        }
                        if (allPaths.containsKey(device[2])){
                            currentBottomPath = device[2];
                            bottomUSB = allPaths.get(currentBottomPath);
                        }
                        return true;
                    }
                }

            return false;
        }
        else if (allPaths.size() == 1){
            if (currentBottomPath!=null){
                return false;
            }
            String[] paths = new String[1];
            int index = 0;
            Iterator<Map.Entry<String, String>> it = allPaths.entrySet().iterator();
            while (it.hasNext()){
                Map.Entry<String, String> value = it.next();
                paths[index++] = value.getKey();
            }
            for (String[] allDevice : allDevicesWithOne) {
                if (Arrays.toString(allDevice).contains(paths[0])){
                    currentBottomPath = allDevice[2];
                    bottomUSB = allPaths.get(currentBottomPath);
                }
            }
            return currentBottomPath != null;
        }
        return false;
    }

    /**
     * judge whether the path is left
     * @param li
     * @return
     */
    public static boolean is3399Left(String li){
        String[] lines = {"5-1.3:1.0","2-1:1.0","1-1.2:1.0","1-1.1:1.0","3-1.3:1.0","5-1.3:1.0","7-1.3:1.0"};
        for (String line : lines) {
            if (li.contains(line)){
                return true;
            }
        }
        return false;
    }
    public static boolean is3399Right(String li){
        String[] lines = {"1-1.2:1.0","5-1.3:1.0","4-1.3:1.0","7-1.3:1.0","3-1.3:1.0","2-1:1.0","7-1.5:1.0","5-1.5:1.0","3-1.5:1.0"};
        for (String line : lines) {
            if (li.contains(line)){
                return true;
            }
        }
        return false;
    }
    public static boolean is3399Bottom(String li){
        String[] lines = {"1-1.4:1.0","5-1.2:1.0","4-1.2:1.0","7-1.2:1.0","3-1.2:1.0","5-1.7:1.0","7-1.7:1.0",
                "5-1.6:1.0"};
        for (String line : lines) {
            if (li.contains(line)){
                return true;
            }
        }
        return false;
    }
    public static boolean isLeft(String li){
        ArrayList<String> lines = new ArrayList<>();

        String[] path_1 = {"1-1.6:1.0","1-1.2:1.0","1-1.1:1.0","3-1:1.0"
        };

        /*String[] path_2 = {"2-1.2:1.0","3-1.2:1.0","3-1.1.2:1.0","3-1.5.1.2:1.0"
                ,"3-1.1:1.0","4-1.2:1.0","3-1.4:1.0","4-1.1:1.0","1-1.1:1.0"
                ,"1-1.2:1.0","4-1.7:1.0","1-1.7:1.0","1-1.4:1.0","3-1:1.0"};
        */
        lines.addAll(Arrays.asList(path_1));

        //lines.addAll(Arrays.asList(path_2));
        for (String line : lines) {
            if (li.contains(line)){
                return true;
            }
        }
        return false;
    }
    public static boolean isRight(String li){
        ArrayList<String> lines = new ArrayList<>();

        String[] path_1 = {"1-1.2:1.0","1-1.5:1.0","1-1.4.1:1.0","2-1.1:1.0"};

        /*String[] path_2 = {"2-1.5:1.0","3-1.6:1.0","3-1.5:1.0","3-1.4.1.2:1.0",
                "3-1.4.2:1.0","3-1.4.1:1.0","4-1.5:1.0","4-1.4.1:1.0",
                "3-1.5.1:1.0","1-1.4.1:1.0", "4-1.6:1.0","1-1.6:1.0","1-1.5:1.0","2-1.1:1.0"};
        */
        lines.addAll(Arrays.asList(path_1));

        for (String line : lines) {
            if (li.contains(line)){
                return true;
            }
        }
        return false;
    }
    public static boolean isBottom(String li){
        String[] lines = {"1-1.3:1.0","1-1.7:1.0","1-1.6:1.0","1-1.4.2:1.0"};
        for (String line : lines) {
            if (li.contains(line)){
                return true;
            }
        }
        return false;
    }

    //Determine whether the obtained path is the left interface of the current device
    public static boolean isCurrentLeft(String pled){
        if (currentLeftPath==null){
            return true;
        } else return currentLeftPath.equals(pled) || pled.contains(currentLeftPath);
    }
    //Determine whether the obtained path is the right interface of the current device
    public static boolean isCurrentRight(String pled){
        if (currentRightPaht==null){
            return true;
        } else return currentRightPaht.equals(pled) || pled.contains(currentRightPaht);
    }
    //Determine whether the obtained path is the bottom interface of the current device
    public static boolean isCurrentBottom(String pled){
        if (currentBottomPath==null){
            return true;
        } else return currentBottomPath.equals(pled) || pled.contains(currentBottomPath);
    }

    public static String getCurrentLeftPath(){
        if (currentLeftPath==null || currentLeftPath.equals("0-0.0:0.0")) {
            return "11";
        } else {
            return currentLeftPath;
        }
    }

    public static String getCurrentRightPath(){
        if (currentRightPaht==null || currentRightPaht.equals("0-0.0:0.0")) {
            return "11";
        } else {
            return currentRightPaht;
        }
    }
    public static String getCurrentBottomPath(){
        if (currentBottomPath==null || currentBottomPath.equals("0-0.0:0.0")) {
            return "11";
        } else {
            return currentBottomPath;
        }
    }

    public static String getCurrentDeviceInfo(){
        return "left->"+currentLeftPath+" right->"+currentRightPaht+" bottom->"+currentBottomPath;
    }

}
