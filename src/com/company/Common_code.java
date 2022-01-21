package com.company;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.URL;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class Common_code {
    public static void write_accountmap(String username, String password, String Account, String type) {
        try {
           var accountmapnew = new HashMap<String, ArrayList<String>>();
            accountmapnew = read_accountmap();
            BufferedWriter bw = new BufferedWriter(new FileWriter("Accountdetails.txt"));
            ArrayList<String> newtransaction = new ArrayList<>();
            newtransaction.add(password);
            newtransaction.add(Account);
            newtransaction.add(type);
            accountmapnew.put(username, newtransaction);
            Set<String> set = accountmapnew.keySet();
            int n = set.size();
            List<String> lnew = new ArrayList<String>(n);
            for (String x : set) {
                lnew.add(x);
            }

            for (int j = 0; j < lnew.size(); j++) {
                bw.write(lnew.get(j) + ":");
                ArrayList<String> alltransaction = new ArrayList<>();
                alltransaction = accountmapnew.get(lnew.get(j));

                for (int k = 0; k < alltransaction.size() - 1; k++) {
                    bw.write(alltransaction.get(k) + ":");
                }
                if (alltransaction.size() > 0) {
                    bw.write(alltransaction.get(alltransaction.size() - 1));
                }
                bw.newLine();
            }
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static HashMap<String, ArrayList<String>> read_accountmap() {

       var transactionmap = new HashMap<String, ArrayList<String>>();

        try {
            BufferedReader br = new BufferedReader(new FileReader("Accountdetails.txt"));
            String filedata;
            String username = null;
            while ((filedata = br.readLine()) != null) {
                String[] strParts = filedata.split(":");
                List<String> listParts = Arrays.asList(strParts);
                username = listParts.get(0);
                var templist = new ArrayList<String>();
                for (int i = 1; i < listParts.size(); i++) {
                    templist.add(listParts.get(i));
                }
                transactionmap.put(username, templist);

            }
            return transactionmap;


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return transactionmap;
    }

    public static boolean verifyusername(String username) {

        HashMap<String, ArrayList<String>> transactionmap = new HashMap<>();
        transactionmap = read_accountmap();

        Set<String> usernameset = transactionmap.keySet();
        int size = usernameset.size();
        List<String> usernameList = new ArrayList<String>(size);
        for (String x : usernameset)
            usernameList.add(x);
        for (int i = 0; i < size; i++) {
            if (username.equals(usernameList.get(i))) {
                System.out.println("Enter new username. This username is not available");
                return false;
            }
        }
        return true;
    }

    public static boolean verifypassword(String username, String password) {
        HashMap<String, ArrayList<String>> tempmap = new HashMap<>();
        tempmap = read_accountmap();
        ArrayList<String> transactionlist = new ArrayList<>();
        transactionlist = tempmap.get(username);
        if (transactionlist.get(0).equals(password)) {
            return true;
        } else
            return false;
    }

    public static void write_transaction(String username, ArrayList<String> userlist) {

        HashMap<String, ArrayList<String>> tempmap = new HashMap<>();

        tempmap = read_transaction();
        tempmap.put(username,userlist);
        try (BufferedWriter bw = new BufferedWriter(new FileWriter("src/Transaction.txt"))) {
            Set<String> set = tempmap.keySet();
            int n = set.size();
            List<String> usernamelist = new ArrayList<String>(n);

            for (String x : set) {
                usernamelist.add(x);
            }

            for (int j = 0; j < usernamelist.size(); j++) {
                bw.write(usernamelist.get(j) + ":");

                ArrayList<String> transactionlist = new ArrayList<>();
                transactionlist = tempmap.get(usernamelist.get(j));

                if (transactionlist.size() == 1) {
                    bw.write(transactionlist.get(0));
                    bw.newLine();
                } else {
                    for (int k = 0; k < transactionlist.size() - 1; k++) {
                        bw.write(transactionlist.get(k) + ":");
                    }
                    if (transactionlist.size() > 1) {
                        bw.write(transactionlist.get(transactionlist.size() - 1));
                    }
                    bw.newLine();
                }
            }
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static HashMap<String, ArrayList<String>> read_transaction() {
        HashMap<String, ArrayList<String>> tempmap = new HashMap<>();
        try {
            BufferedReader br = new BufferedReader(new FileReader("src/Transaction.txt"));
            String filedata;
            String username = null;
            while ((filedata = br.readLine()) != null) {
                String[] strParts = filedata.split(":");
                List<String> listParts = Arrays.asList(strParts);
                if (listParts.isEmpty()) {
                    return tempmap;
                }
                username = listParts.get(0);
                ArrayList<String> templist = new ArrayList<>();
                for (int i = 1; i < listParts.size(); i++) {
                    templist.add(listParts.get(i));
                }
                tempmap.put(username, templist);
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return tempmap;
    }

    public static int getBalance(String username) {
        HashMap<String, ArrayList<String>> transactionmap = new HashMap<>();
        ArrayList<String> transactionlist = new ArrayList<>();
        transactionmap=read_transaction();
        transactionlist=transactionmap.get(username);
        if(transactionlist==null)
        {
            return 0 ;
        }
        int size = transactionlist.size();
        int amount = 0;
        int tamount = 0;
        for (int i = 0; i < size; i++) {
            String tempamount = transactionlist.get(i);

            for (int j = 20; j < tempamount.length() - 1; j++) {
                tamount = 10 * tamount + Integer.parseInt(String.valueOf(tempamount.charAt(j)));

            }

            if (tempamount.charAt(tempamount.length() - 1) == '+') {
                amount = amount + tamount;
                tamount = 0;
            } else {
                amount = amount - tamount;
                tamount = 0;
            }

        }
        return amount;
    }

    public static Date validateDate(String date) {
        Date NewDate;
        while (true) {
            String datetocheck = date;
            try {
                NewDate = new SimpleDateFormat("yyyy/MM/dd").parse(date);
                break;
            } catch (ParseException e) {
                System.out.println("Enter date in particular format : YYYY/MM/DD");
            }
        }
        return NewDate;
    }

    public static String searchtransaction(String username , Date startdate , Date Enddate) throws ParseException {
        HashMap<String, ArrayList<String>> transactionmap = new HashMap<>();
        transactionmap = read_transaction();
        ArrayList<String> transactionlist = new ArrayList<>();
        transactionlist = transactionmap.get(username);
        String s1 = null;
        for(int i =0 ;i<transactionlist.size() ;i++)
        {
            String wholedate = transactionlist.get(i);
            String tempyear = wholedate.substring(0,4);
            String teampmonth = wholedate.substring(5,7);
            String tempdate = wholedate.substring(8,10);
            String sdate = tempyear+"/" +teampmonth +"/" +tempdate;

            Date d1 = new SimpleDateFormat("yyyy/MM/dd").parse(sdate);

            if((d1.before(Enddate) && d1.after(startdate)) || d1.equals(startdate) || d1.equals(Enddate))
            {
                s1 = s1+ transactionlist.get(i) + ":";
            }
        }
        return s1;
    }

    public static void sendsms(String message , String number) throws IOException {
        String apikey= "8kJw2hMUfp3co5BCnjP0AHDTlgLFNR1dbi6OtEZyaVS7vqemxrR4HwZEOCiY28SqGXp9ajo5zvIAFJgy" ;
        String sendId= "TXTIND" ;
        message= URLEncoder.encode(message, "UTF-8");
        String  language = "english";
        String route = "v3";

        String myurl = "https://www.fast2sms.com/dev/bulkV2?authorization=" + apikey +"&sender_id="+sendId +"&message="+message+"&route=" + route +"&numbers=" + number;
        System.out.println(myurl);

        URL url = new URL(myurl);
        HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("user-Agent", "Mozilla/5.0");
        con.setRequestProperty("cache-control", "no-cache");
        System.out.println("wait....................");
        int code = con.getResponseCode();
        System.out.println(code);

        StringBuffer response = new StringBuffer();
        BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
        while(true)
        {
            String line = br.readLine();
            if(line==null)
            {
                break;
            }
            response.append(line);
        }
        br.close();

        System.out.println(response);

        System.out.println(message);

    }
}
