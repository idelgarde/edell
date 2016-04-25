package projeto.tcc;


import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;

import java.io.IOException;
import java.io.OutputStream;

import ch.aplu.jaw.NativeHandler;
import ch.aplu.kinect.Kinect;
import ch.aplu.kinect.Point3D;
import ch.aplu.kinect.SkeletonJoint;
import ch.aplu.util.ModelessOptionPane;

public class CarroKinect
{
	private final String PORTA = "COM63";
	private final int PONTO_ARTICULACAO = 20;
	private String dllPath = Kinect.is64bit() ? "KinectHandler64" : "KinectHandler";
	private boolean flgEnviarDados = true;
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
							exibirStatusMop("Aguardando um usuário válido...\n (Ficar a uma distância de 2m)\n" + rightHand.z + "/" + leftHand.z);
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
								pararExecucao();
								maosLevantadas = true;
								enviarDadosValidos(flgEnviarDados);
							}
						}else{
							maosLevantadas = false;
							executaLeituraCorporal(rightHand, leftHand);
							exibirStatusMop("Levante os dois braços para executar\n" + rightHand.z + "/" + leftHand.z);
						}
					}
				}
				
				
				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			commPort.close();
		}

	}

	private void executaLeituraCorporal(Point3D rightHand , Point3D leftHand) throws IOException{
		System.out.println(flgEnviarDados);
		if(!flgEnviarDados){
			if (rightHand.y < 400){
				estadoFT = Estado.MOVE_TRAS;
				executarComandosFT();
			}else if(rightHand.y >1000){
				estadoFT = Estado.MOVE_FRENTE;
				executarComandosFT();
			}else if(rightHand.y <1000 && rightHand.y >400){
				estadoFT = Estado.PARA;
				executarComandosFT();
			}
			if (leftHand.x < 700){
				estadoED = Estado.VIRA_ESQUERDA;
				executarComandosED();
			}else if(leftHand.x >1000){
				estadoED = Estado.VIRA_DIREITA;
				executarComandosED();
			}else if(leftHand.x <1000 && leftHand.x >850){
				estadoED = Estado.PARA;
				executarComandosED();
			}
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

	private void executarComandosFT() throws IOException
	{
		switch (estadoFT){
		case MOVE_FRENTE:
			//        envia arduino frente
			out.write(8);
			System.out.println("escreveu 8");
			break;
		case MOVE_TRAS:
			//        envia arduino tras
			out.write(2);
			System.out.println("escreveu 2");
			break;
		case PARA:
			//        envia arduino parar
			out.write(0);
			System.out.println("escreveu 0");
			break;
		}
	}

	private void executarComandosED() throws IOException
	{
		switch (estadoED){
		case VIRA_ESQUERDA:
			//        envia arduino esquerda
			out.write(4);
			System.out.println("escreveu 4");
			break;
		case VIRA_DIREITA:
			//        envia arduino direita
			out.write(6);
			System.out.println("escreveu 6");
			break;
		case PARA:
			//        envia arduino parar
			out.write(5);
			System.out.println("escreveu 5");
			break;
		}
	}

	private void abrirPortas(){
		System.out.println("Lendo Porta");
		try {
			portIdentifier = CommPortIdentifier.getPortIdentifier(PORTA);
			commPort = portIdentifier.open("CarroArduino",2000);
			System.out.println("Porta Identificada");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void pararExecucao() throws IOException{
		out.write(0);
	}

	public static void main(String args[])
	{
		CarroKinect carroKinect = new CarroKinect();
		carroKinect.executar();
	}
}
