package com.company;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Main {

    public static void write_accountmap(String username, String password, String Account, String type) {
        try {
            HashMap<String, LinkedList<String>> accountmapnew = new HashMap<>();
            accountmapnew = read_accountmap();
            BufferedWriter bw = new BufferedWriter(new FileWriter("Accountdetails.txt"));
            LinkedList<String> l1 = new LinkedList();
            l1.add(password);
            l1.add(Account);
            l1.add(type);
            accountmapnew.put(username, l1);
            Set<String> set = accountmapnew.keySet();
            int n = set.size();
            List<String> lnew = new ArrayList<String>(n);
            for (String x : set) {
                lnew.add(x);
            }

            for (int j = 0; j < lnew.size(); j++) {
                bw.write(lnew.get(j) + ":");
                LinkedList<String> l2 = new LinkedList<>();
                l2 = accountmapnew.get(lnew.get(j));

                for (int k = 0; k < l2.size() - 1; k++) {
                    bw.write(l2.get(k) + ":");
                }
                if (l2.size() > 0) {
                    bw.write(l2.get(l2.size() - 1));
                }
                bw.newLine();
            }
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static HashMap<String, LinkedList<String>> read_accountmap() {

        HashMap<String, LinkedList<String>> hp = new HashMap<>();

        try {
            BufferedReader br = new BufferedReader(new FileReader("Accountdetails.txt"));
            String filedata;
            String username = null;
            while ((filedata = br.readLine()) != null) {
                String[] strParts = filedata.split(":");
                List<String> listParts = Arrays.asList(strParts);
                username = listParts.get(0);
                LinkedList<String> templist = new LinkedList<>();
                for (int i = 1; i < listParts.size(); i++) {
                    templist.add(listParts.get(i));
                }
                hp.put(username, templist);

            }
            return hp;


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return hp;
    }


    public static boolean verifyusername(String username) {

        HashMap<String, LinkedList<String>> tempmap = new HashMap<>();
        tempmap = read_accountmap();

        Set<String> usernamelist = tempmap.keySet();
        int size = usernamelist.size();
        List<String> aList = new ArrayList<String>(size);
        for (String x : usernamelist)
            aList.add(x);
        for (int i = 0; i < size; i++) {
            if (username.equals(aList.get(i))) {
                System.out.println("Enter new username. This username is not avialble");
                return false;
            }
        }
        return true;
    }

    public static boolean verifypassword(String username, String password) {
        HashMap<String, LinkedList<String>> tempmap = new HashMap<>();
        tempmap = read_accountmap();
        LinkedList<String> l1 = new LinkedList<>();
        l1 = tempmap.get(username);
        if (l1.get(0).equals(password)) {
            return true;
        } else
            return false;
    }

    public static void write_transaction(String username, LinkedList<String> l1) {

        HashMap<String, LinkedList<String>> tempmap = new HashMap<>();

        tempmap = read_transaction();
        tempmap.put(username,l1);


        try (BufferedWriter bw = new BufferedWriter(new FileWriter("src/Transaction.txt"))) {
            Set<String> set = tempmap.keySet();
            int n = set.size();
            List<String> lnew = new ArrayList<String>(n);

            for (String x : set) {
                lnew.add(x);
            }

            for (int j = 0; j < lnew.size(); j++) {
                bw.write(lnew.get(j) + ":");

                LinkedList<String> l2 = new LinkedList<>();
                l2 = tempmap.get(lnew.get(j));

                if (l2.size() == 1) {
                    bw.write(l2.get(0));
                    bw.newLine();
                } else {
                    for (int k = 0; k < l2.size() - 1; k++) {
                        bw.write(l2.get(k) + ":");
                    }
                    if (l2.size() > 1) {
                        bw.write(l2.get(l2.size() - 1));
                    }
                    bw.newLine();
                }
            }
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static HashMap<String, LinkedList<String>> read_transaction() {
        HashMap<String, LinkedList<String>> tempmap = new HashMap<>();
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
                LinkedList<String> templist = new LinkedList<>();
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
        HashMap<String, LinkedList<String>> transactionmap = new HashMap<>();
        LinkedList<String> l1 = new LinkedList<>();
        transactionmap=read_transaction();
        l1=transactionmap.get(username);
        if(l1==null)
        {
            return 0 ;
        }
        int size = l1.size();
        int amount = 0;
        int tamount = 0;
        for (int i = 0; i < size; i++) {
            String tempamount = l1.get(i);

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


    public static Date validateDate(String sdate) {
        Date date1;
        while (true) {
            String date = sdate;
            try {
                date1 = new SimpleDateFormat("yyyy/MM/dd").parse(date);
                break;
            } catch (ParseException e) {
                System.out.println("Enter date in particular format : YYYY/MM/DD");
            }
        }
        return date1;
    }

    public static String searchtransaction(String username , Date startdate , Date Enddate) throws ParseException {
        HashMap<String, LinkedList<String>> transactionmap = new HashMap<>();
        LinkedList<String> l1 = new LinkedList<>();
        l1 = transactionmap.get(username);
        String s1 = null;
        for(int i =0 ;i<l1.size() ;i++)
        {
            String wholedate = l1.get(i);
            String tempyear = wholedate.substring(0,4);
            String teampmonth = wholedate.substring(5,7);
            String tempdate = wholedate.substring(8,10);
            String sdate = tempyear+"/" +teampmonth +"/" +tempdate;

            Date d1 = new SimpleDateFormat("yyyy/MM/dd").parse(sdate);



            if((d1.before(Enddate) && d1.after(startdate)) || d1.equals(startdate) || d1.equals(Enddate))
            {
                s1 = s1+ l1.get(i) + ":";
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

    public static void main(String[] args) {
        ServerSocket server = null;

        try {

            // server is listening on port 1234
            server = new ServerSocket(1234);
            server.setReuseAddress(true);


            while (true) {
                Socket client = server.accept();
                System.out.println("New client connected"
                        + client.getInetAddress()
                        .getHostAddress());


                ClientHandler clientSock
                        = new ClientHandler(client);
                new Thread(clientSock).start();
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            if (server != null) {
                try {
                    server.close();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // ClientHandler class
    private static class ClientHandler implements Runnable  {
        private final Socket clientSocket;

        // Constructor
        public ClientHandler(Socket socket)
        {
            this.clientSocket = socket;
        }

        public void run()  {
            Scanner sc = new Scanner(System.in);
            ServerSocket serverSocket = null;
            InputStreamReader ir = null;
            OutputStreamWriter os = null;
            BufferedReader br = null;
            BufferedWriter bw = null;
            Socket s = null;
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd-HH-mm-ss");
            LocalDateTime now = LocalDateTime.now();
            try {
                ir = new InputStreamReader(clientSocket.getInputStream());
                os = new OutputStreamWriter(clientSocket.getOutputStream());
                br = new BufferedReader(ir);
                bw = new BufferedWriter(os);
            } catch (IOException e) {
                e.printStackTrace();
            }

            while (true) {
                try{
                while (true) {
                    String choice = br.readLine();
                    if (choice == null) {
                        break;
                    }

                    if (choice.equals("newuser")) {

                        String username;
                        while (true) {
                            username = br.readLine();
                            Boolean flag = verifyusername(username);

                            if (flag) {
                                bw.write("valid");
                                bw.newLine();
                                bw.flush();
                                break;
                            } else if (flag == false) {
                                bw.write("Invalid");
                                bw.newLine();
                                bw.flush();
                            }
                        }



                        String password = br.readLine();

                        String accountnumber = br.readLine();

                        String accountype = br.readLine();

                        LinkedList<String> l1 = new LinkedList<>();
                        l1.add(0, dtf.format(now) + " " + 0 + "+");

                        if (accountype.equals("1")) {
                            write_accountmap(username, password, accountnumber, "Saving");
                            write_transaction(username, l1);
                        } else if (accountype.equals("2")) {
                            write_accountmap(username, password, accountnumber, "Current");
                            write_transaction(username, l1);
                        }


                        bw.write(String.valueOf("Login Successful"));
                        bw.newLine();
                        bw.flush();
                        break;
                    }

                    if (choice.equals("existinguser")) {
                        String username = br.readLine();
                        String password;
                        while (true) {
                            password = br.readLine();
                            boolean flag = verifypassword(username, password);
                            if (flag == true) {
                                bw.write("valid");
                                bw.newLine();
                                bw.flush();
                                break;
                            } else {
                                bw.write("invalid");
                                bw.newLine();
                                bw.flush();
                            }

                        }

                        bw.write("Log in successfull");
                        bw.newLine();
                        bw.flush();

                        break;

                    }

                    if (choice.equals("forget")) {
                        HashMap<String, LinkedList<String>> accountmap = new HashMap<>();
                        accountmap = read_accountmap();
                        LinkedList l1 = new LinkedList<>();
                        l1 = accountmap.get(br.readLine());
                        String number = (String) l1.get(1);
                        int otp = (int) (Math.random() * (201) + 200);
                        sendsms(("Your otp is:" + String.valueOf(otp)+" \n" + "Please don't share OTP with anyone"), number);

                        if (Integer.parseInt(br.readLine()) == otp) {
                            bw.write("Your password is : " + l1.get(0));
                            bw.newLine();
                            bw.flush();
                        }
                        break;

                    }

                    if (choice.equals("deposit")) {
                        String username = br.readLine();
                        String amount = br.readLine();

                        LinkedList<String> l1 = new LinkedList<>();
                        LinkedList<String> l2 = new LinkedList<>();
                        HashMap<String, LinkedList<String>> hp = new HashMap<>();
                        hp = read_transaction();

                        l1 = hp.get(username);
                        if (l1 == null || l1.isEmpty()) {

                            l2.add(dtf.format(now) + " " + amount + "+");
                            hp.put(username, l2);
                            l1 = l2;


                        } else {
                            l1.add(dtf.format(now) + " " + amount + "+");
                            hp.put(username, l1);

                        }

                        write_transaction(username, l1);
                        bw.write("Amount Added Successfully.   " + "  Your new balance is:" + getBalance(username));
                        bw.newLine();
                        bw.flush();
                        break;
                    }

                    if (choice.equals("withdraw")) {
                        HashMap<String, LinkedList<String>> transactionmap = new HashMap<>();
                        String amount = br.readLine();
                        String username = br.readLine();
                        int balance = getBalance(username);
                        if (Integer.parseInt(amount) > balance) {
                            bw.write("Not sufficient balance");
                            bw.newLine();
                            bw.flush();
                        } else {
                            transactionmap = read_transaction();
                            LinkedList<String> l1 = new LinkedList<>();
                            l1 = transactionmap.get(username);
                            l1.add(dtf.format(now) + " " + amount + "-");

                            transactionmap.put(username, l1);
                            write_transaction(username, l1);
                            bw.write("Your new balance is: " + getBalance(username));
                            bw.newLine();
                            bw.flush();
                        }
                        break;


                    }

                    if (choice.equals("balance")) {
                        HashMap<String, LinkedList<String>> accountmap = new HashMap<>();
                        String username= br.readLine();
                        accountmap = read_accountmap();
                        LinkedList l1 = new LinkedList<>();
                        l1 = accountmap.get(username);
                        String number = (String) l1.get(1);
                        sendsms(("Your account balance is: Rs." + getBalance(username)), number);
                        bw.write(String.valueOf(getBalance(username)));
                        bw.newLine();
                        bw.flush();
                        break;

                    }

                    if (choice.equals("generatestatement")) {
                        String username= br.readLine();
                        HashMap<String, LinkedList<String>> transactionmap = new HashMap<>();

                        LinkedList<String> l1 = new LinkedList<>();
                        transactionmap = read_transaction();
                        l1 = transactionmap.get(username);

                        String s1 = l1.get(0) + "@";

                        if (l1 == null) {
                            bw.write("No transactions found");
                            bw.newLine();
                            bw.flush();
                        } else {
                            for (int i = 0; i < l1.size(); i++) {
                                s1 = s1 + l1.get(i) + "@";

                            }
                            s1 = s1 + String.valueOf(getBalance(username));

                            bw.write(s1);
                            bw.newLine();
                            bw.flush();

                        }
                        break;

                    }

                    if (choice.equals("search")) {
                        String username= br.readLine();
                        String options = br.readLine();

                        if (options.equals("particular")) {
                            String startdate = dtf.format(now.minusMonths(1));
                            String enddate = dtf.format(now);
                            Date startDate = validateDate(startdate);
                            Date endDate = validateDate(enddate);
                            String search = searchtransaction(username, startDate, endDate);
                            if (search == null) {
                                bw.write("No transaction to print");
                                bw.newLine();
                                bw.flush();
                            } else {
                                bw.write(search);
                                bw.newLine();
                                bw.flush();
                            }
                        } else if (options.equals("particularsix")) {
                            String startdate = dtf.format(now.minusMonths(6));
                            String enddate = dtf.format(now);
                            Date startDate = validateDate(startdate);
                            Date endDate = validateDate(enddate);
                            String search = searchtransaction(username, startDate, endDate);
                            if (search == null) {
                                bw.write("No transaction to print");
                                bw.newLine();
                                bw.flush();
                            } else {
                                bw.write(search);
                                bw.newLine();
                                bw.flush();
                            }
                        } else {
                            String start = br.readLine();

                            String end = br.readLine();
                            System.out.println(start);
                            System.out.println(end);

                            Date startdate = validateDate(start);
                            Date enddate = validateDate(end);
                            String search = searchtransaction(username, startdate, enddate);
                            if (search == null) {
                                bw.write("No transaction to print");
                                bw.newLine();
                                bw.flush();
                            } else {
                                bw.write(search);
                                bw.newLine();
                                bw.flush();
                            }
                        }
                        break;

                    }

                    if (choice.equals("transfer")) {
                        String userusername= br.readLine();
                        String username = br.readLine();
                        String amount = br.readLine();
                        HashMap<String, LinkedList<String>> transactionmap = new HashMap<>();
                        if (verifyusername(username) == true) {
                            bw.write("Username not found. Invalid transaction. Directing you to the main menu");
                            bw.newLine();
                            bw.flush();

                        } else if (getBalance(userusername) < Integer.parseInt(amount)) {
                            bw.write("Not enough balance to tranfer");
                            bw.newLine();
                            bw.flush();
                        } else if (verifyusername(username) == false) {
                            LinkedList<String> l1 = new LinkedList<>();
                            LinkedList<String> l2 = new LinkedList<>();
                            transactionmap = read_transaction();
                            l1 = transactionmap.get(userusername);
                            l1.add(dtf.format(now) + " " + amount + "-");
                            transactionmap.put(userusername, l1);
                            l2 = transactionmap.get(username);
                            l2.add(dtf.format(now) + " " + amount + "+");
                            transactionmap.put(username, l2);
                            //System.out.println(l1);
                            write_transaction(userusername, l1);
                            write_transaction(username, l2);
                            bw.write("Amount transferred successfully");
                            bw.newLine();
                            bw.flush();
                        }

                        break;
                    }

                }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ParseException e) {
                    e.printStackTrace();
                }


            }
        }
    }

    }

