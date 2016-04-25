package projeto.tcc;

import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;

import javax.swing.JOptionPane;

public class testePorta{

	public static void main ( String[] args )
	{
		CommPortIdentifier portIdentifier = null;
		CommPort commPort = null;
		int op = 0;
		
		try{
			System.out.println("Listando as portas");
//			Enumeration ports = CommPortIdentifier.getPortIdentifiers();
//			while (ports.hasMoreElements()) {
//			    CommPortIdentifier port = (CommPortIdentifier) ports.nextElement();
//			    System.out.println(port.getName());
//			} 
			System.out.println("Lendo Porta");
			portIdentifier = CommPortIdentifier.getPortIdentifier("COM4");
			System.out.println("Porta Identificada");
			commPort = portIdentifier.open("TesteArduino",2000);

			if ( commPort instanceof SerialPort )
			{
				SerialPort serialPort = (SerialPort) commPort;
				System.out.println(serialPort.getName());
				serialPort.setSerialPortParams(9600,SerialPort.DATABITS_8,SerialPort.STOPBITS_1,SerialPort.PARITY_NONE);

				OutputStream out = serialPort.getOutputStream();
				while(op !=9){
					System.out.println("while");
					String valor = JOptionPane.showInputDialog("n8 - Frente \n4 - Esquerda \n6- Direita \5 - trás \n 3 - Parar \n 9 sair");
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
