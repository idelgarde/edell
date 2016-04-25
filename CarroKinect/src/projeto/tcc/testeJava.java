package projeto.tcc;

import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;

import java.io.InputStream;
import java.io.OutputStream;

import javax.swing.JOptionPane;

public class testeJava{

	public static void main ( String[] args )
	{
//		 new ListAvaliablePorts().list();  
		CommPortIdentifier portIdentifier = null;
		CommPort commPort = null;
		int op = 0;

		try{
			System.out.println("Lendo Porta");
			portIdentifier = CommPortIdentifier.getPortIdentifier("COM4");
			System.out.println("Porta Identificada");
			commPort = portIdentifier.open("TesteArduino",2000);

			if ( commPort instanceof SerialPort )
			{
				SerialPort serialPort = (SerialPort) commPort;
				System.out.println(serialPort.getName());
				serialPort.setSerialPortParams(9600,SerialPort.DATABITS_8,SerialPort.STOPBITS_1,SerialPort.PARITY_NONE);

				InputStream in = serialPort.getInputStream();
				OutputStream out = serialPort.getOutputStream();
				while(op !=9){
					System.out.println("while");
					String valor = JOptionPane.showInputDialog("8/4/5/6 3/9");
					System.out.println("Mandando valor " + valor);
					op = new Integer(valor);
					System.out.println("valor recebido " + valor);
					out.write(op);
					System.out.println(valor + " Enviado");

				}
				commPort.close();

			}
			else
			{
				System.out.println("Error: Only serial ports are handled by this example.");
			}
		}
		catch(Exception e){
			e.printStackTrace();
			commPort.close();
		}

	}
	
}
