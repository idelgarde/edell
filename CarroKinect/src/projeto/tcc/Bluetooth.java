package projeto.tcc;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import javax.bluetooth.LocalDevice;
import javax.bluetooth.RemoteDevice;
import javax.bluetooth.UUID;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
import javax.microedition.io.StreamConnectionNotifier;
import javax.swing.JOptionPane;

public class Bluetooth
{
    private void startServer() throws IOException
    {
    	int op = 0;
    //Create a UUID for SPP
    UUID uuid = new UUID("1101", true);
    //Create the servicve url
    String connectionString = "btspp://localhost:" + uuid +";name=Magic Merlin Server";

    //open server url
    StreamConnectionNotifier streamConnNotifier = (StreamConnectionNotifier)Connector.open(connectionString);

    //Wait for client connection
    System.out.println("\nServer Started. Waiting for clients to connect...");
    StreamConnection connection = streamConnNotifier.acceptAndOpen();

    RemoteDevice dev = RemoteDevice.getRemoteDevice(connection);
    System.out.println("Remote device address: "+dev.getBluetoothAddress());
    System.out.println("Remote device name: "+dev.getFriendlyName(true));


    //send response to spp client
    OutputStream outStream = connection.openOutputStream();
    PrintWriter pWriter=new PrintWriter(new OutputStreamWriter(outStream));
    while(op !=9){
		System.out.println("while");
		String valor = JOptionPane.showInputDialog("n8 - Frente \n4 - Esquerda \n6- Direita \5 - trás \n 3 - Parar \n 9 sair");
		System.out.println("Mandando valor " + valor);
		op = new Integer(valor);
		System.out.println("valor recebido " + valor);
		outStream.write(op);
		System.out.println(valor + " Enviado");

	}

    pWriter.write("Response String from SPP Server\r\n");
    pWriter.flush();
    pWriter.close();

    streamConnNotifier.close();
    }

    public static void main(String[] args) throws IOException
    {
    LocalDevice localDevice = LocalDevice.getLocalDevice();
    System.out.println("Address: "+localDevice.getBluetoothAddress());
    System.out.println("Name: "+localDevice.getFriendlyName());

    Bluetooth sampleSPPServer = new Bluetooth();
    sampleSPPServer.startServer();
    }
}