/*
* Elliptic Curve Equation used
* E(1,1) -> y^2 = x^3 + x + 1
* n is 256
* na, nb < 256
* Global Variable G -> Order of n is 333 => E((333^2 + 333 + 1)^(1/2))
* 1 <= k <= 1000
* Authors: Abhijith Ajith, Aravind Krishnan, Amoghavarsh SH
*/

import java.util.*;
import java.io.*;
import java.net.*;

class ClientFunctions{

    //ECC Function
    public double ECC(int x){
        int ysq = (x*x*x) + (x*x) + 1;
        double E = Math.sqrt(ysq);
        return E;
    }
    
    //Client utilities & K Random generation
    public int randomK(){
        int l = 1, h = 1000;
        int k = (int) (Math.random() * (h - l)) + l;
        return k;
    }
    
    public String ConvertString(int x){
        String s = String.valueOf(x);
        return s;
    }

    public String ConvertString(double x){
        String s = String.valueOf(x);
        return s;
    }

    public double DoubleConvert(String s){
        return Double.parseDouble(s);
    }
}

public class client{

    //Initializing parameters & variables
    static int globalX = 333;
    static Socket soc = null;
    static DataInputStream input = null;
    static DataOutputStream output = null;
    static BufferedReader bufRead = null;
    public static void main(String[] args) {
        try{
            //Using sockets to ensure a connection between Client & Server
            soc = new Socket("localhost",3333);
            input = new DataInputStream(soc.getInputStream());
            output = new DataOutputStream(soc.getOutputStream());
            bufRead = new BufferedReader(new InputStreamReader(System.in));

            //Initializing functions object & Global variable G
            ClientFunctions f = new ClientFunctions();
            double G = f.ECC(globalX);
            while(true){
                int k = f.randomK();
                String publicKey = input.readUTF();
                System.out.println("Received Servers Public Key: " + publicKey);
                double Pb = f.DoubleConvert(publicKey);
                System.out.print("Enter your message (Enter end to stop): ");
                String message = bufRead.readLine();
                if(message.equals("end")) break;

                //Message Encryption (Character Wise)
                for (int i = 0; i < message.length(); i++) {
                    char c = message.charAt(i);
                    int ascii = (int) c;
                    double Pm = f.ECC(ascii);
                    int chidx = i + 1;
                    System.out.println("Encryption of character " + chidx + ": " + Pm);
                    double Encryption = k * Pb;
                    double L = k*G, R = Pm + Encryption;
                    String KV = f.ConvertString(L) + "," + f.ConvertString(R);
                    System.out.println("Sending to Server: {" + KV + "}");
                    output.writeUTF(KV);
                    output.flush();
                }
                output.writeUTF("MessageSent");
                output.flush();
            }

            //closing all objects
            soc.close();
            input.close();
            output.close();
            bufRead.close();
        }
        catch(Exception e){
            System.out.println(e);
        }
    }
}
