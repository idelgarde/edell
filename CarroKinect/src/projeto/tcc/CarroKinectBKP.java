package projeto.tcc;


import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.UnsupportedCommOperationException;

import java.io.IOException;
import java.io.OutputStream;

import ch.aplu.jaw.NativeHandler;
import ch.aplu.kinect.Kinect;
import ch.aplu.kinect.Point3D;
import ch.aplu.kinect.SkeletonJoint;
import ch.aplu.util.ModelessOptionPane;

public class CarroKinectBKP
{
	private final String PORTA = "COM29";
	private final int PONTO_ARTICULACAO = 20;
	private String dllPath = Kinect.is64bit() ? "KinectHandler64" : "KinectHandler";
	private boolean flgEnviarDados = false;
	private boolean maosLevantadas = false;
	private Estado estadoFT = null;
	private Estado estadoED = null;
	private Point3D leftHand;
	private Point3D rightHand;
	private ModelessOptionPane mop = null;
	private CommPortIdentifier portIdentifier = null;
	private CommPort commPort = null;
	private OutputStream out = null;

	public void executar(){
		try {
			Point3D[] joints = new Point3D[PONTO_ARTICULACAO];

			Kinect kinect = new Kinect(dllPath, "CarroKinect", 0, 0, 640, 480, NativeHandler.WS_DEFAULT);
			mop = new ModelessOptionPane(150,485, "Inicializando...");
			mop.setTitle("CarroArduino");
			// Se não foi inicializado, não deveria exibir uma mensagem e encerrar o programa?
			if (!kinect.isInitialized()) {
				exibirStatusMop("Kinect não encontrado");
				kinect.setVisible(false);
			}else{

				kinect.setWindowScaleFactor(4);
				abrirPortas();

				for (int i = 0; i < PONTO_ARTICULACAO; i++){
					joints[i] = new Point3D();
				}

				if (!(commPort instanceof SerialPort)){
					exibirStatusMop("Erro na porta serial");
				}else{
					exibirStatusMop("Aguardando um usuário válido...");
					kinect.getJoints(joints, 0);

					SerialPort serialPort = (SerialPort) commPort;
					System.out.println(serialPort.getName());

					serialPort.setSerialPortParams(9600,SerialPort.DATABITS_8,SerialPort.STOPBITS_1,SerialPort.PARITY_NONE);
					out = serialPort.getOutputStream();

					while (true){
						int articulacoes = kinect.getJoints(joints, 20);

						if (articulacoes == -1) {
							exibirStatusMop("Aguardando um usuário válido...");
							estadoFT = Estado.PARA;
							estadoED = Estado.PARA;
							continue;
						}

						leftHand = joints[SkeletonJoint.HAND_LEFT.ordinal()];
						rightHand = joints[SkeletonJoint.HAND_RIGHT.ordinal()];
						System.out.println(leftHand);
						System.out.println(rightHand);
						System.out.println(maosLevantadas);

						if(leftHand.y<=300 && rightHand.y <=300 ){
							if (!maosLevantadas) {
								maosLevantadas = true;
								enviarDadosValidos(flgEnviarDados);
							}
						}else{
							maosLevantadas = false;
							executaLeituraCorporal(rightHand, leftHand);
							exibirStatusMop("Levante os dois braços para executar");
							pararExecucao();
						}
					}
				}
			}
		} catch (UnsupportedCommOperationException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			commPort.close();
		}

	}

	private void executaLeituraCorporal(Point3D rightHand , Point3D leftHand) throws IOException{
		System.out.println(flgEnviarDados);
		if(flgEnviarDados){
			if(rightHand.y < 400) {
				estadoFT = Estado.MOVE_TRAS;
			} else if(rightHand.y >1000) {
				estadoFT = Estado.MOVE_FRENTE;
			} else {
				estadoFT = Estado.PARA;
			}

			if (leftHand.x < 700) {
				estadoED = Estado.VIRA_ESQUERDA;
			} else if(leftHand.x >1000) {
				estadoED = Estado.VIRA_DIREITA;
			} else {
				estadoED = Estado.PARA;
			}

			executarComandosFT(estadoFT);
			executarComandosED(estadoED);
		}
	}

	private boolean enviarDadosValidos(boolean flg){
		return flgEnviarDados = !flg;
	}

	private void exibirStatusMop(String msg){
		System.out.println(msg);
		if(!flgEnviarDados&&leftHand!=null && rightHand!=null && estadoED!=null && estadoFT!=null){
			String text = String.format("\nEsquerda: %s\nDireita:%s\nEstado F_T: %s\nEstado D_E: %s",
					leftHand, rightHand, estadoFT.getDescricao(), estadoED.getDescricao());
			mop.setText(text, true);
		} else {
			mop.setText(msg);
		}
	}

	private void executarComandosFT(Estado estado) throws IOException
	{
		switch (estado){
		case MOVE_FRENTE:
			out.write(estadoED.getValorArduino());
			System.out.println("escreveu "+estadoED.getValorArduino());
			break;
		case MOVE_TRAS:
			out.write(estadoED.getValorArduino());
			System.out.println("escreveu "+estadoED.getValorArduino());
			break;
		case PARA:
			out.write(estadoED.getValorArduino());
			System.out.println("escreveu "+estadoED.getValorArduino());
			break;
		}
	}

	private void executarComandosED(Estado estado) throws IOException
	{
		switch (estado){
		case VIRA_ESQUERDA:
			out.write(estadoED.getValorArduino());
			System.out.println("escreveu "+estadoED.getValorArduino());
			break;
		case VIRA_DIREITA:
			out.write(estadoED.getValorArduino());
			System.out.println("escreveu "+estadoED.getValorArduino());
			break;
		case PARA_RODA_DIANTEIRA:
			out.write(estadoED.getValorArduino());
			System.out.println("escreveu "+estadoED.getValorArduino());
			break;
		}
	}

	private void abrirPortas(){
		System.out.println("Lendo Porta");
		try {
			portIdentifier = CommPortIdentifier.getPortIdentifier(PORTA);
			commPort = portIdentifier.open("TesteArduino",2000);
			System.out.println("Porta Identificada");
		} catch (PortInUseException e) {
			e.printStackTrace();
		} catch (NoSuchPortException e) {
			e.printStackTrace();
		}
	}

	private void pararExecucao() throws IOException{
		out.write(0);
	}

	public static void main(String args[])
	{
		CarroKinectBKP carroKinect = new CarroKinectBKP();
		carroKinect.executar();
	}
}
